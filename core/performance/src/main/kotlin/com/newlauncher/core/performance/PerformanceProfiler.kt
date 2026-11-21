package com.newlauncher.core.performance

import android.view.Choreographer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Measures frame rendering time using [Choreographer] and maintains a rolling
 * average plus a coarse p95 estimate. The numbers feed [QualityManager]'s
 * adaptive decisions.
 *
 * This is intentionally cheap: it only records the frame callback delta and
 * periodically trims the rolling buffer. It does not poll hardware sensors.
 */
class PerformanceProfiler {

    private val _frameMs = MutableStateFlow(0f)
    /** Most recent measured frame interval (ms). */
    val frameMs: StateFlow<Float> = _frameMs.asStateFlow()

    private val _avgFrameMs = MutableStateFlow(0f)
    /** Rolling average frame interval (ms). */
    val avgFrameMs: StateFlow<Float> = _avgFrameMs.asStateFlow()

    private val _p95FrameMs = MutableStateFlow(0f)
    /** Approximate 95th-percentile frame interval (ms). */
    val p95FrameMs: StateFlow<Float> = _p95FrameMs.asStateFlow()

    private val buffer = ArrayList<Float>(WINDOW_SIZE)
    private var lastFrameNanos: Long = 0L
    private var choreographer: Choreographer? = null
    private var frameCallback: Choreographer.FrameCallback? = null
    private var scope: CoroutineScope? = null
    private var trimJob: Job? = null

    /** Start measuring. Safe to call multiple times. */
    fun start(scope: CoroutineScope) {
        if (choreographer != null) return
        this.scope = scope
        choreographer = Choreographer.getInstance()
        lastFrameNanos = 0L
        frameCallback = object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                if (lastFrameNanos != 0L) {
                    val deltaMs = (frameTimeNanos - lastFrameNanos) / 1_000_000f
                    record(deltaMs)
                }
                lastFrameNanos = frameTimeNanos
                choreographer?.postFrameCallback(this)
            }
        }
        choreographer?.postFrameCallback(frameCallback!!)
        trimJob = scope.launch {
            while (isActive) {
                delay(TRIM_INTERVAL_MS)
                synchronized(buffer) {
                    if (buffer.size > WINDOW_SIZE) {
                        buffer.subList(0, buffer.size - WINDOW_SIZE).clear()
                    }
                }
            }
        }
    }

    /** Stop measuring and release the callback. */
    fun stop() {
        frameCallback?.let { choreographer?.removeFrameCallback(it) }
        frameCallback = null
        choreographer = null
        lastFrameNanos = 0L
        trimJob?.cancel()
        trimJob = null
        synchronized(buffer) { buffer.clear() }
        _frameMs.value = 0f
        _avgFrameMs.value = 0f
        _p95FrameMs.value = 0f
    }

    private fun record(deltaMs: Float) {
        synchronized(buffer) {
            buffer.add(deltaMs)
            if (buffer.size > WINDOW_SIZE) buffer.removeAt(0)
        }
        _frameMs.value = deltaMs
        if (buffer.isNotEmpty()) {
            val sum = buffer.sum()
            _avgFrameMs.value = sum / buffer.size
            // p95 estimate: sort a copy and pick the 95th percentile bucket.
            val sorted = buffer.sorted()
            val idx = ((sorted.size - 1) * 0.95f).toInt().coerceIn(0, sorted.size - 1)
            _p95FrameMs.value = sorted[idx]
        }
    }

    private companion object {
        const val WINDOW_SIZE = 120
        const val TRIM_INTERVAL_MS = 2000L
    }
}

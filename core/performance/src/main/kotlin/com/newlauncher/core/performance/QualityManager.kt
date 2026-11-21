package com.newlauncher.core.performance

import android.content.Context
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Adaptive graphics quality controller.
 *
 * Combines the [PerformanceProfiler]'s frame measurements with light, periodic
 * reads of memory, thermal status and battery, then asks [PerformancePolicy] to
 * resolve the effective [GraphicsProfile]. Sampling runs on a slow cadence
 * (every [SAMPLE_INTERVAL_MS]) — sensors are never polled aggressively.
 *
 * The user's chosen profile is treated as an upper bound: the effective profile
 * never exceeds it, only steps down under pressure.
 */
class QualityManager(
    private val context: Context,
    private val targetProvider: () -> GraphicsProfile,
) {
    private val profiler = PerformanceProfiler()

    private val _targetProfile = MutableStateFlow(targetProvider())
    val targetProfile: StateFlow<GraphicsProfile> = _targetProfile.asStateFlow()

    private val _effectiveProfile = MutableStateFlow(targetProvider())
    val effectiveProfile: StateFlow<GraphicsProfile> = _effectiveProfile.asStateFlow()

    private val _signals = MutableStateFlow(
        PerformancePolicy.Signals(0f, 0f, 1f, PerformancePolicy.ThermalStatus.NOMINAL, 1f)
    )
    val signals: StateFlow<PerformancePolicy.Signals> = _signals.asStateFlow()

    private var scope: CoroutineScope? = null
    private var sampleJob: Job? = null

    /** Begin adaptive sampling. */
    fun start(scope: CoroutineScope) {
        if (sampleJob != null) return
        this.scope = scope
        profiler.start(scope)
        sampleJob = scope.launch {
            while (isActive) {
                val target = targetProvider()
                _targetProfile.value = target
                val signals = collectSignals()
                _signals.value = signals
                val effective = PerformancePolicy.decide(target, signals)
                _effectiveProfile.value = effective
                delay(SAMPLE_INTERVAL_MS)
            }
        }
    }

    /** Stop sampling and release the frame callback. */
    fun stop() {
        profiler.stop()
        sampleJob?.cancel()
        sampleJob = null
    }

    /** Update the user's target profile (e.g. from the customization screen). */
    fun setTarget(profile: GraphicsProfile) {
        _targetProfile.value = profile
    }

    private fun collectSignals(): PerformancePolicy.Signals {
        val avg = profiler.avgFrameMs.value
        val p95 = profiler.p95FrameMs.value
        val memoryFree = readMemoryFreeFraction()
        val thermal = readThermalStatus()
        val battery = readBatteryFraction()
        return PerformancePolicy.Signals(avg, p95, memoryFree, thermal, battery)
    }

    private fun readMemoryFreeFraction(): Float {
        val runtime = Runtime.getRuntime()
        val max = runtime.maxMemory().toFloat().coerceAtLeast(1f)
        val used = (runtime.totalMemory() - runtime.freeMemory()).toFloat()
        val free = (max - used).coerceIn(0f, max)
        return (free / max).coerceIn(0f, 1f)
    }

    private fun readThermalStatus(): PerformancePolicy.ThermalStatus {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return PerformancePolicy.ThermalStatus.NOMINAL
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return PerformancePolicy.ThermalStatus.NOMINAL
        return when (pm.currentThermalStatus) {
            PowerManager.THERMAL_STATUS_NONE,
            PowerManager.THERMAL_STATUS_LIGHT -> PerformancePolicy.ThermalStatus.NOMINAL
            PowerManager.THERMAL_STATUS_MODERATE -> PerformancePolicy.ThermalStatus.FAIR
            PowerManager.THERMAL_STATUS_SEVERE -> PerformancePolicy.ThermalStatus.HIGH
            PowerManager.THERMAL_STATUS_CRITICAL,
            PowerManager.THERMAL_STATUS_EMERGENCY,
            PowerManager.THERMAL_STATUS_SHUTDOWN -> PerformancePolicy.ThermalStatus.SEVERE
            else -> PerformancePolicy.ThermalStatus.NOMINAL
        }
    }

    private fun readBatteryFraction(): Float {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager ?: return 1f
        val capacity = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return (capacity.coerceIn(0, 100) / 100f).coerceIn(0f, 1f)
    }

    private companion object {
        /** Slow sampling cadence — deliberately not aggressive. */
        const val SAMPLE_INTERVAL_MS = 5000L
    }
}

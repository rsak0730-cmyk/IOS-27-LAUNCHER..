package com.newlauncher.feature.dynamicisland

import android.app.Application
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * ViewModel backing the Dynamic Island.
 *
 * - **Music**: registers a [MediaSessionManager] callback so that whenever an
 *   active media session exists, the island surfaces the now-playing title,
 *   artist and playback state. This uses the platform MediaSession API and
 *   respects background-execution limits.
 * - **Timer**: a user-controlled countdown driven by a coroutine ticker.
 * - **Notifications**: surfaced by the (optional) notification listener
 *   service through [DynamicIslandController].
 */
class DynamicIslandViewModel(app: Application) : AndroidViewModel(app) {

    private val mediaSessionManager: MediaSessionManager =
        app.getSystemService(Application.MEDIA_SESSION_SERVICE) as MediaSessionManager

    private val mainHandler = Handler(Looper.getMainLooper())
    private var mediaCallback: MediaSessionManager.Callback? = null

    private var timerJob: Job? = null
    private var timerRemaining: Long = 0L
    private var timerTotal: Long = 0L

    /** Current island state (expanded + activity). */
    val state: StateFlow<IslandState> = DynamicIslandController.state

    init {
        registerMediaListener()
    }

    /** Toggle the island's expanded/collapsed geometry on tap. */
    fun onIslandTap() {
        DynamicIslandController.toggle()
    }

    /** Clear the active activity and collapse. */
    fun clearActivity() {
        DynamicIslandController.clear()
    }

    /** Toggle play/pause on the currently active media session. */
    fun togglePlayback() {
        val session = mediaSessionManager.getActiveSessions()?.firstOrNull() ?: return
        runCatching {
            session.controller?.transportControls?.playPause()
        }
    }

    // ---- Timer controls ----

    fun startTimer(seconds: Long) {
        cancelTimer()
        timerTotal = seconds
        timerRemaining = seconds
        DynamicIslandController.setActivity(
            IslandActivity.Timer(remainingSeconds = seconds, totalSeconds = seconds, isRunning = true)
        )
        timerJob = viewModelScope.launch {
            while (isActive && timerRemaining > 0) {
                delay(1000L)
                timerRemaining -= 1
                DynamicIslandController.setActivity(
                    IslandActivity.Timer(remainingSeconds = timerRemaining, totalSeconds = timerTotal, isRunning = true)
                )
                if (timerRemaining <= 0) {
                    DynamicIslandController.clear()
                    break
                }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        (DynamicIslandController.state.value.activity as? IslandActivity.Timer)?.let {
            DynamicIslandController.setActivity(
                IslandActivity.Timer(it.remainingSeconds, it.totalSeconds, isRunning = false)
            )
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        timerRemaining = 0
        timerTotal = 0
    }

    override fun onCleared() {
        cancelTimer()
        unregisterMediaListener()
    }

    // ---- MediaSession listener ----

    private fun registerMediaListener() {
        val callback = object : MediaSessionManager.Callback() {
            override fun onActiveSessionsChanged(sessions: MutableList<MediaSession>) {
                val session = sessions.firstOrNull() ?: run {
                    DynamicIslandController.clear()
                    return
                }
                val controller = session.controller ?: return
                val metadata = controller.metadata ?: return
                val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE)
                    ?: "Now Playing"
                val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
                val isPlaying = controller.playbackState?.state == PlaybackState.STATE_PLAYING
                DynamicIslandController.setActivity(IslandActivity.Music(title, artist, isPlaying))
            }
        }
        mediaCallback = callback
        runCatching { mediaSessionManager.addCallback(getApplication(), callback, mainHandler) }
    }

    private fun unregisterMediaListener() {
        mediaCallback?.let {
            runCatching { mediaSessionManager.removeCallback(it) }
        }
        mediaCallback = null
    }
}

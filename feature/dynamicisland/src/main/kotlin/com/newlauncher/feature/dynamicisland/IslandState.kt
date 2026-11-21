package com.newlauncher.feature.dynamicisland

/**
 * The type of ongoing activity the Dynamic Island can surface.
 *
 * These map to real Android capabilities: music via MediaSession, timers via
 * user-controlled countdowns, and notifications via an optional
 * NotificationListenerService (granted by the user). This is an original
 * Android interpretation of the Dynamic Island concept — it does **not** use
 * Apple's proprietary Live Activities.
 */
sealed interface IslandActivity {

    /** No ongoing activity. */
    data object None : IslandActivity

    /** Now-playing music, driven by an active Android MediaSession. */
    data class Music(
        val title: String,
        val artist: String?,
        val isPlaying: Boolean,
    ) : IslandActivity

    /** A countdown timer controlled by the user. */
    data class Timer(
        val remainingSeconds: Long,
        val totalSeconds: Long,
        val isRunning: Boolean,
    ) : IslandActivity

    /** An incoming notification (requires user-granted notification access). */
    data class Notification(
        val title: String,
        val text: String?,
    ) : IslandActivity
}

/**
 * The full Dynamic Island state: collapsed/expanded geometry plus the active
 * activity surfaced inside it.
 */
data class IslandState(
    val expanded: Boolean = false,
    val activity: IslandActivity = IslandActivity.None,
) {
    val hasActivity: Boolean get() = activity !is IslandActivity.None
}

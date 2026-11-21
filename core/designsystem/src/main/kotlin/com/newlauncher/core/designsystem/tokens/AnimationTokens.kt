package com.newlauncher.core.designsystem.tokens

import androidx.compose.animation.core.DurationMillis

/**
 * Layer 1 — Animation tokens.
 *
 * Centralised durations and easing so every transition feels consistent.
 * Values favour "fluid" rather than snappy motion for the liquid-glass
 * interactions.
 */
object AnimationTokens {
    // Durations (ms).
    val Instant: DurationMillis = DurationMillis(0)
    val Fast: DurationMillis = DurationMillis(150)
    val Normal: DurationMillis = DurationMillis(250)
    val Slow: DurationMillis = DurationMillis(400)
    val VerySlow: DurationMillis = DurationMillis(600)

    // Icon press feedback.
    val IconPress: DurationMillis = DurationMillis(120)

    // Page swipe / pager settle.
    val PageSettle: DurationMillis = DurationMillis(320)

    // Dynamic Island expand / collapse.
    val IslandExpand: DurationMillis = DurationMillis(360)
    val IslandCollapse: DurationMillis = DurationMillis(240)

    // Control Center reveal.
    val ControlCenterReveal: DurationMillis = DurationMillis(320)

    // App open / close (launcher-to-app).
    val AppOpen: DurationMillis = DurationMillis(280)
}

/** Curve aliases for readability. */
object AnimationCurves {
    val Standard = androidx.compose.animation.core.CubicEaseOut
    val Emphatic = androidx.compose.animation.core.CubicEaseInOut
    val Soft = androidx.compose.animation.core.SpringSpec(dampingRatio = 0.8f)
}

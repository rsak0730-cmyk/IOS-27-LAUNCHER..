package com.newlauncher.core.designsystem.tokens

/**
 * Layer 1 — Animation tokens.
 *
 * Centralised durations and easing so every transition feels consistent.
 * Values favour "fluid" rather than snappy motion for the liquid-glass
 * interactions.
 *
 * Durations are plain `Int` milliseconds (the same underlying type as
 * `androidx.compose.animation.core.DurationMillis`); using `Int` keeps this
 * token file free of a hard dependency on the animation-core artifact while
 * remaining fully compatible with Compose animation specs.
 */
object AnimationTokens {
    // Durations (ms).
    val Instant: Int = 0
    val Fast: Int = 150
    val Normal: Int = 250
    val Slow: Int = 400
    val VerySlow: Int = 600

    // Icon press feedback.
    val IconPress: Int = 120

    // Page swipe / pager settle.
    val PageSettle: Int = 320

    // Dynamic Island expand / collapse.
    val IslandExpand: Int = 360
    val IslandCollapse: Int = 240

    // Control Center reveal.
    val ControlCenterReveal: Int = 320

    // App open / close (launcher-to-app).
    val AppOpen: Int = 280
}

/** Curve aliases for readability. */
object AnimationCurves {
    val Standard = androidx.compose.animation.core.CubicEaseOut
    val Emphatic = androidx.compose.animation.core.CubicEaseInOut
    val Soft = androidx.compose.animation.core.SpringSpec(dampingRatio = 0.8f)
}

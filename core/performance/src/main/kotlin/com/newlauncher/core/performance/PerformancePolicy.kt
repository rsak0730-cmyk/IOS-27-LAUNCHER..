package com.newlauncher.core.performance

/**
 * Pure, deterministic policy that maps measured device signals to a graphics
 * profile. This logic is deliberately separated from Android APIs so it can be
 * unit-tested without a device or instrumentation.
 *
 * Adaptation model: the user selects a **target** profile (the maximum they
 * want). The effective profile is the *lower* (more conservative) of the
 * target and what the current signals permit. This guarantees the system only
 * *downgrades* under pressure and never silently exceeds the user's choice —
 * avoiding aggressive, jittery quality changes.
 */
object PerformancePolicy {

    enum class ThermalStatus { NOMINAL, FAIR, HIGH, SEVERE }

    /** Normalised device signals, all in simple 0..1 or enum ranges. */
    data class Signals(
        val avgFrameMs: Float,
        val p95FrameMs: Float,
        /** Fraction of runtime memory still free (0f..1f). */
        val memoryFreeFraction: Float,
        val thermalStatus: ThermalStatus,
        /** Battery charge fraction (0f..1f). */
        val batteryFraction: Float,
    )

    /** The profile the signals currently permit (ignoring the user target). */
    fun allowedBySignals(signals: Signals): GraphicsProfile {
        // Thermal pressure is the strongest single signal.
        if (signals.thermalStatus == ThermalStatus.SEVERE || signals.thermalStatus == ThermalStatus.HIGH) {
            return GraphicsProfile.PERFORMANCE
        }

        // Very low memory forces the leanest profile.
        if (signals.memoryFreeFraction < 0.12f) return GraphicsProfile.PERFORMANCE
        if (signals.memoryFreeFraction < 0.22f && signals.thermalStatus == ThermalStatus.FAIR) {
            return GraphicsProfile.BALANCED
        }

        // Frame time pressure (60fps target ≈ 16.6ms).
        if (signals.avgFrameMs > 26f || signals.p95FrameMs > 40f) return GraphicsProfile.PERFORMANCE
        if (signals.avgFrameMs > 19f || signals.p95FrameMs > 28f) return GraphicsProfile.BALANCED

        // Low battery caps at BALANCED.
        if (signals.batteryFraction < 0.15f) return GraphicsProfile.BALANCED

        return GraphicsProfile.ULTRA
    }

    /**
     * Resolve the effective profile: the more conservative of [target] and the
     * signal-permitted profile. Uses ordinal ordering (ULTRA=0 < BALANCED=1 <
     * PERFORMANCE=2) so "lower ordinal" means "more effects".
     */
    fun decide(target: GraphicsProfile, signals: Signals): GraphicsProfile {
        val permitted = allowedBySignals(signals)
        return if (permitted.ordinal >= target.ordinal) target else permitted
    }
}

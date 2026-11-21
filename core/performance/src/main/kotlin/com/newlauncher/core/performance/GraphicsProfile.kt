package com.newlauncher.core.performance

import kotlinx.serialization.Serializable

/**
 * The three graphics profiles requested for NEW-LAUNCHER.
 *
 * Each profile is a budget for the glass rendering engine:
 * - [ULTRA]      — maximum glass effects, reflections, dispersion and shaders.
 * - [BALANCED]   — realistic glass effects with optimised rendering.
 * - [PERFORMANCE] — reduced rendering workload that still preserves the visual
 *                   identity (fewer shader passes, no dispersion, lighter blur).
 */
@Serializable
enum class GraphicsProfile(
    /** Whether AGSL shader passes are enabled. */
    val enableShaders: Boolean,
    /** Whether chromatic dispersion is enabled. */
    val enableDispersion: Boolean,
    /** Whether animated specular sheen is enabled. */
    val enableAnimatedSheen: Boolean,
    /** Whether hardware RenderEffect blur is enabled. */
    val enableBlur: Boolean,
    /** Whether liquid deformation is enabled. */
    val enableLiquidDeform: Boolean,
) {
    ULTRA(
        enableShaders = true,
        enableDispersion = true,
        enableAnimatedSheen = true,
        enableBlur = true,
        enableLiquidDeform = true,
    ),
    BALANCED(
        enableShaders = true,
        enableDispersion = true,
        enableAnimatedSheen = true,
        enableBlur = true,
        enableLiquidDeform = false,
    ),
    PERFORMANCE(
        enableShaders = true,
        enableDispersion = false,
        enableAnimatedSheen = false,
        enableBlur = true,
        enableLiquidDeform = false,
    ),
}

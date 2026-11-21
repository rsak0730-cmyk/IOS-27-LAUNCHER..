package com.newlauncher.core.glassengine

import androidx.compose.ui.unit.dp
import com.newlauncher.core.designsystem.tokens.GlassMaterialParameters
import com.newlauncher.core.performance.GraphicsProfile

/**
 * How the glass surface fill is produced.
 */
enum class GlassFillStrategy {
    /** AGSL RuntimeShader fill (fresnel + specular + refraction + dispersion). */
    AGSL_SHADER,

    /** Pure-Compose brush fallback (gradients) when shaders are unavailable. */
    BRUSH_FALLBACK,
}

/**
 * A resolved plan describing exactly how a glass surface should be rendered
 * for the current device capabilities, performance profile and material.
 *
 * This is a pure, testable decision: given the inputs it deterministically
 * produces a plan. The [GlassSurface] composable consumes the plan.
 */
data class GlassRenderPlan(
    val fill: GlassFillStrategy,
    val applyBackdropBlur: Boolean,
    val animatedSheen: Boolean,
    val chromaticDispersion: Boolean,
    val liquidDeform: Boolean,
) {
    companion object {
        /**
         * Resolve a render plan.
         *
         * @param supportsShader whether AGSL RuntimeShader is available (API 31+).
         * @param supportsBlur whether hardware RenderEffect blur is available.
         * @param profile the active graphics profile (caps the effect budget).
         * @param material the glass material parameters.
         */
        fun resolve(
            supportsShader: Boolean,
            supportsBlur: Boolean,
            profile: GraphicsProfile,
            material: GlassMaterialParameters,
        ): GlassRenderPlan {
            val fill = when {
                supportsShader && profile.enableShaders -> GlassFillStrategy.AGSL_SHADER
                else -> GlassFillStrategy.BRUSH_FALLBACK
            }

            return GlassRenderPlan(
                fill = fill,
                applyBackdropBlur = supportsBlur &&
                    profile.enableBlur &&
                    material.blurRadius > 0.dp,
                animatedSheen = supportsShader && profile.enableAnimatedSheen && material.specularStrength > 0f,
                chromaticDispersion = supportsShader &&
                    profile.enableDispersion &&
                    material.chromaticDispersion > 0f,
                liquidDeform = supportsShader &&
                    profile.enableLiquidDeform &&
                    material.liquidDeformation > 0f,
            )
        }
    }
}

package com.newlauncher.core.designsystem.tokens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The five reusable glass materials.
 *
 * Each material is a *preset* of configurable [GlassMaterialParameters]; the
 * rendering engine reads these parameters and produces the corresponding
 * physical-optics look. Adding a new material only requires adding an entry to
 * [GlassMaterialPresets].
 */
enum class GlassMaterialType {
    CLEAR,
    FROSTED,
    LIQUID,
    CRYSTAL,
    HYBRID,
}

/**
 * Layer 1 — Glass material parameters.
 *
 * These are the design tokens that drive the Physical Glass Rendering Engine.
 * Every field is clamped to a sensible physical range by [coerceInRange] so a
 * caller can never produce an invalid or undefined look.
 *
 * Note: these values describe *simulated* physical optics (refraction,
 * reflection, fresnel), not ray-traced rendering. See the architecture doc.
 */
data class GlassMaterialParameters(
    /** Surface transparency: 0f fully transparent, 1f fully opaque. */
    val transparency: Float = 0.15f,
    /** Background blur radius in dp. 0dp means no blur (pure clear glass). */
    val blurRadius: Dp = 0.dp,
    /** Refraction strength: how strongly the background is displaced. 0f–1f. */
    val refractionStrength: Float = 0.0f,
    /** Specular / reflection intensity of the surface. 0f–1f. */
    val reflectionIntensity: Float = 0.5f,
    /** Fresnel edge-highlight intensity. 0f–1f. */
    val fresnelIntensity: Float = 0.6f,
    /** Base specular highlight size/strength. 0f–1f. */
    val specularStrength: Float = 0.7f,
    /** Subtle chromatic dispersion at high-angle edges. 0f–1f. */
    val chromaticDispersion: Float = 0.0f,
    /** Tint applied to the glass surface. */
    val tint: Color = Color.Unspecified,
    /** Edge stroke alpha for the glass border. 0f–1f. */
    val edgeStrokeAlpha: Float = 0.18f,
    /** Inner ambient illumination strength. 0f–1f. */
    val ambientIllumination: Float = 0.25f,
    /** Liquid deformation responsiveness for touch. 0f–1f. */
    val liquidDeformation: Float = 0.0f,
) {
    companion object {
        const val TRANSPARENCY_MIN = 0.0f
        const val TRANSPARENCY_MAX = 1.0f
        const val STRENGTH_MIN = 0.0f
        const val STRENGTH_MAX = 1.0f

        /** Clamp every numeric parameter into a physically sensible range. */
        fun coerceInRange(params: GlassMaterialParameters): GlassMaterialParameters =
            params.copy(
                transparency = params.transparency.coerceIn(TRANSPARENCY_MIN, TRANSPARENCY_MAX),
                refractionStrength = params.refractionStrength.coerceIn(STRENGTH_MIN, STRENGTH_MAX),
                reflectionIntensity = params.reflectionIntensity.coerceIn(STRENGTH_MIN, STRENGTH_MAX),
                fresnelIntensity = params.fresnelIntensity.coerceIn(STRENGTH_MIN, STRENGTH_MAX),
                specularStrength = params.specularStrength.coerceIn(STRENGTH_MIN, STRENGTH_MAX),
                chromaticDispersion = params.chromaticDispersion.coerceIn(STRENGTH_MIN, STRENGTH_MAX),
                edgeStrokeAlpha = params.edgeStrokeAlpha.coerceIn(STRENGTH_MIN, STRENGTH_MAX),
                ambientIllumination = params.ambientIllumination.coerceIn(STRENGTH_MIN, STRENGTH_MAX),
                liquidDeformation = params.liquidDeformation.coerceIn(STRENGTH_MIN, STRENGTH_MAX),
            )
    }
}

/**
 * Layer 1 — Glass material presets.
 *
 * The five reusable materials. Each is already within valid ranges; callers
 * may copy a preset and tweak individual fields via [copy].
 */
object GlassMaterialPresets {

    /** 1. Clear Glass — no blur, strong refraction and fresnel, pure transparency. */
    val Clear = GlassMaterialParameters.coerceInRange(
        GlassMaterialParameters(
            transparency = 0.10f,
            blurRadius = 0.dp,
            refractionStrength = 0.55f,
            reflectionIntensity = 0.75f,
            fresnelIntensity = 0.80f,
            specularStrength = 0.85f,
            chromaticDispersion = 0.18f,
            edgeStrokeAlpha = 0.22f,
            ambientIllumination = 0.30f,
            liquidDeformation = 0.0f,
        )
    )

    /** 2. Frosted Glass — translucent blur, soft reflection, minimal refraction. */
    val Frosted = GlassMaterialParameters.coerceInRange(
        GlassMaterialParameters(
            transparency = 0.30f,
            blurRadius = 24.dp,
            refractionStrength = 0.10f,
            reflectionIntensity = 0.45f,
            fresnelIntensity = 0.40f,
            specularStrength = 0.45f,
            chromaticDispersion = 0.02f,
            edgeStrokeAlpha = 0.14f,
            ambientIllumination = 0.35f,
            liquidDeformation = 0.0f,
        )
    )

    /** 3. Liquid Glass — blurred, deformable, fluid movement and elastic edges. */
    val Liquid = GlassMaterialParameters.coerceInRange(
        GlassMaterialParameters(
            transparency = 0.20f,
            blurRadius = 18.dp,
            refractionStrength = 0.30f,
            reflectionIntensity = 0.65f,
            fresnelIntensity = 0.60f,
            specularStrength = 0.70f,
            chromaticDispersion = 0.08f,
            edgeStrokeAlpha = 0.20f,
            ambientIllumination = 0.40f,
            liquidDeformation = 0.80f,
        )
    )

    /** 4. Crystal Glass — high clarity, strong refraction and dispersion. */
    val Crystal = GlassMaterialParameters.coerceInRange(
        GlassMaterialParameters(
            transparency = 0.08f,
            blurRadius = 0.dp,
            refractionStrength = 0.75f,
            reflectionIntensity = 0.85f,
            fresnelIntensity = 0.90f,
            specularStrength = 0.95f,
            chromaticDispersion = 0.30f,
            edgeStrokeAlpha = 0.28f,
            ambientIllumination = 0.25f,
            liquidDeformation = 0.10f,
        )
    )

    /** 5. Hybrid Glass — balanced blend of frosted softness and crystal clarity. */
    val Hybrid = GlassMaterialParameters.coerceInRange(
        GlassMaterialParameters(
            transparency = 0.18f,
            blurRadius = 12.dp,
            refractionStrength = 0.45f,
            reflectionIntensity = 0.60f,
            fresnelIntensity = 0.65f,
            specularStrength = 0.75f,
            chromaticDispersion = 0.12f,
            edgeStrokeAlpha = 0.20f,
            ambientIllumination = 0.35f,
            liquidDeformation = 0.45f,
        )
    )

    /** Resolve a preset by [GlassMaterialType]. */
    fun of(type: GlassMaterialType): GlassMaterialParameters = when (type) {
        GlassMaterialType.CLEAR -> Clear
        GlassMaterialType.FROSTED -> Frosted
        GlassMaterialType.LIQUID -> Liquid
        GlassMaterialType.CRYSTAL -> Crystal
        GlassMaterialType.HYBRID -> Hybrid
    }
}

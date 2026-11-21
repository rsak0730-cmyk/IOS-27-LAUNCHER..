package com.newlauncher.core.graphics

import androidx.compose.ui.graphics.Color

/**
 * Pure, dependency-light color math used by the glass rendering engine.
 *
 * These functions are deliberately side-effect free and unit-testable; the
 * AGSL shaders mirror the same math on the GPU so that the CPU-side helpers
 * (used for fallbacks and debugging) and the GPU-side shader stay consistent.
 */
object ColorUtils {

    /** Linear interpolation between two colors. */
    fun lerp(start: Color, end: Color, t: Float): Color {
        val clamped = t.coerceIn(0f, 1f)
        return Color(
            red = lerpChannel(start.red, end.red, clamped),
            green = lerpChannel(start.green, end.green, clamped),
            blue = lerpChannel(start.blue, end.blue, clamped),
            alpha = lerpChannel(start.alpha, end.alpha, clamped),
        )
    }

    private fun lerpChannel(a: Float, b: Float, t: Float): Float = a + (b - a) * t

    /**
     * Source-over alpha compositing. Returns the resulting color when [src] is
     * drawn on top of [dst]. Used for layering transparent glass surfaces.
     */
    fun over(src: Color, dst: Color): Color {
        val sa = src.alpha
        if (sa <= 0f) return dst
        if (sa >= 1f) return src
        val inv = 1f - sa
        return Color(
            red = src.red * sa + dst.red * inv * dst.alpha,
            green = src.green * sa + dst.green * inv * dst.alpha,
            blue = src.blue * sa + dst.blue * inv * dst.alpha,
            alpha = sa + dst.alpha * inv,
        )
    }

    /**
     * Fresnel approximation (Schlick's approximation) for a dielectric.
     *
     * Returns reflectance in the range 0f–1f for a given view angle cosine.
     * At grazing angles (cosN ≈ 0) reflectance approaches 1f (bright edges);
     * at normal incidence it stays near the base reflectance [f0].
     *
     * This mirrors the AGSL fresnel term used by the glass shaders.
     */
    fun fresnelSchlick(cosN: Float, f0: Float = 0.04f): Float {
        val c = cosN.coerceIn(0f, 1f)
        return f0 + (1f - f0) * (1f - c) * (1f - c) * (1f - c) * (1f - c) * (1f - c)
    }

    /** ReLU-style soft clamp for highlight falloff. */
    fun softClamp(x: Float): Float = (x.coerceIn(0f, 1f) * x.coerceIn(0f, 1f))

    /** Perceived luminance (0f–1f) using Rec. 709 weights. */
    fun luminance(c: Color): Float =
        0.2126f * c.red + 0.7152f * c.green + 0.0722f * c.blue

    /** Multiply a color's alpha by [factor], keeping RGB. */
    fun withAlpha(c: Color, factor: Float): Color =
        c.copy(alpha = (c.alpha * factor).coerceIn(0f, 1f))

    /** Decompose a color into (r, g, b, a) channels in 0f–1f. */
    fun channels(c: Color): FloatArray = floatArrayOf(c.red, c.green, c.blue, c.alpha)
}

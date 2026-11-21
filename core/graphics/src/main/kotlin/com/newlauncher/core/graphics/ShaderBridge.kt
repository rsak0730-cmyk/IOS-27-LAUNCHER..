package com.newlauncher.core.graphics

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.asAndroidMatrix
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush

/**
 * Bridge between Android's [RuntimeShader] (AGSL) and Compose's [Shader] /
 * [ShaderBrush] pipeline.
 *
 * Compose renders custom shaders through a [Shader] that produces a platform
 * [android.graphics.Shader]. [RuntimeShader] *is* an `android.graphics.Shader`,
 * so we simply forward it and keep its local matrix in sync with the matrix
 * Compose supplies. This lets AGSL glass shaders be used as a brush on any
 * drawable region.
 */
class AgslShader(private val runtimeShader: RuntimeShader) : Shader() {
    override fun createShader(
        size: Size,
        viewport: Rect,
        localMatrix: Matrix,
        alpha: Float,
    ): android.graphics.Shader {
        // Keep the shader's local matrix aligned with Compose's transform so
        // UV-based AGSL math (refraction displacement, fresnel) is correct.
        runtimeShader.localMatrix = localMatrix.asAndroidMatrix()
        return runtimeShader
    }
}

/**
 * Compile an AGSL source string into a Compose [ShaderBrush], or return null
 * when the runtime does not support RuntimeShader (pre-Android 12) or the
 * source fails to compile. The caller falls back to a non-shader strategy.
 *
 * @param source AGSL fragment shader source.
 * @param namespace AGSL namespace declaration (e.g. `"agsl;"`).
 */
fun createAgslBrush(source: String, namespace: String = "agsl;"): ShaderBrush? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return null
    return runCatching {
        val shader = RuntimeShader(source, namespace)
        ShaderBrush.create(AgslShader(shader))
    }.getOrNull()
}

/** Whether the runtime can use AGSL RuntimeShader at all. */
val supportsRuntimeShader: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/** Whether the runtime supports hardware RenderEffect blur. */
val supportsRenderEffect: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/**
 * Build a blur [RenderEffect] in *pixels*. The glass components convert their
 * dp-based blur radius to pixels with the ambient density before calling this.
 * Returns null on unsupported runtimes so the caller can fall back.
 */
fun buildBlurRenderEffect(radiusPx: Float): RenderEffect? {
    if (!supportsRenderEffect) return null
    val radius = radiusPx.coerceIn(0f, 100f)
    if (radius <= 0f) return null
    return runCatching {
        RenderEffect.createBlurEffect(radius, radius)
    }.getOrNull()
}

/**
 * Build a chained blur+chromatic [RenderEffect] for the crystal material.
 * Blends a slight horizontal/vertical offset of the blurred layer to fake
 * chromatic dispersion. Returns null on unsupported runtimes.
 */
fun buildChromaticBlurRenderEffect(radiusPx: Float): RenderEffect? {
    if (!supportsRenderEffect) return null
    val base = buildBlurRenderEffect(radiusPx) ?: return null
    return runCatching {
        val r = radiusPx.coerceIn(0.5f, 100f)
        RenderEffect.createBlurEffect(r, r, base, null)
    }.getOrNull()
}

package com.newlauncher.core.glassengine

import android.graphics.RuntimeShader
import androidx.compose.ui.graphics.ShaderBrush
import com.newlauncher.core.graphics.supportsRuntimeShader

/**
 * Holds a compiled [RuntimeShader] together with its Compose [ShaderBrush],
 * and exposes [apply] to push fresh uniforms before each draw.
 *
 * The AGSL shader source and uniform names live in [GlassShaderSources]; this
 * class is the bridge that feeds material parameters + surface size + elapsed
 * time into the shader.
 */
class GlassShaderBrush(
    val runtimeShader: RuntimeShader,
    val brush: ShaderBrush,
) {
    /** Push all uniforms for the given [uniforms] into the runtime shader. */
    fun apply(uniforms: GlassShaderUniforms) {
        val u = GlassShaderSources.Uniforms
        runtimeShader.setFloatUniform(u.RESOLUTION, uniforms.resolutionWidth, uniforms.resolutionHeight)
        runtimeShader.setFloatUniform(u.TIME, uniforms.time)
        runtimeShader.setFloatUniform(u.FRESNEL, uniforms.fresnel)
        runtimeShader.setFloatUniform(u.SPECULAR, uniforms.specular)
        runtimeShader.setFloatUniform(u.REFRACTION, uniforms.refraction)
        runtimeShader.setFloatUniform(u.DISPERSION, uniforms.dispersion)
        runtimeShader.setFloatUniform(u.AMBIENT, uniforms.ambient)
        runtimeShader.setFloatUniform(u.TINT, uniforms.tintRed, uniforms.tintGreen, uniforms.tintBlue, uniforms.alpha)
        runtimeShader.setFloatUniform(u.ALPHA, uniforms.alpha)
        runtimeShader.setFloatUniform(u.DEFORM, uniforms.deform)
    }

    companion object {
        /**
         * Compile the glass AGSL shader into a [GlassShaderBrush]. Returns null
         * when RuntimeShader is unsupported or compilation fails, so the
         * [GlassSurface] can fall back to the brush strategy.
         */
        fun create(): GlassShaderBrush? {
            if (!supportsRuntimeShader) return null
            return runCatching {
                // RuntimeShader takes the AGSL source string; the namespace is
                // declared inline in the source (AGSL default namespace).
                val shader = RuntimeShader(GlassShaderSources.GlassFragment)
                // RuntimeShader is an android.graphics.Shader; the ShaderBrush
                // constructor wraps it and drives the local matrix from Compose's
                // draw transform.
                val brush = ShaderBrush(shader)
                GlassShaderBrush(shader, brush)
            }.getOrNull()
        }
    }
}

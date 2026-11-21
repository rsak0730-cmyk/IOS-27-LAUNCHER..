package com.newlauncher.core.glassengine

/**
 * AGSL shader sources for the Physical Glass Rendering Engine.
 *
 * These shaders implement *simulated* physical optics: Schlick fresnel edge
 * highlights, a moving specular sheen, lens-like internal refraction, and
 * subtle chromatic dispersion. They are procedural — they do **not** sample a
 * captured backdrop texture, so they are not ray-traced background refraction.
 * See `docs/ARCHITECTURE.md` for the explicit distinction and the roadmap for
 * true backdrop-sampled refraction.
 *
 * The shader is a single AGSL fragment program compiled through
 * `android.graphics.RuntimeShader`. Compose supplies the vertex stage; the
 * fragment stage reads `gl_FragCoord` and the uniforms set by
 * [GlassShaderUniforms].
 */
object GlassShaderSources {

    /** Uniform identifiers shared between Kotlin and the AGSL source. */
    object Uniforms {
        const val RESOLUTION = "uResolution"
        const val TIME = "uTime"
        const val FRESNEL = "uFresnel"
        const val SPECULAR = "uSpecular"
        const val REFRACTION = "uRefraction"
        const val DISPERSION = "uDispersion"
        const val AMBIENT = "uAmbient"
        const val TINT = "uTint"
        const val ALPHA = "uAlpha"
        const val DEFORM = "uDeform"
    }

    /**
     * Primary glass fragment shader. Produces a physically-inspired glass
     * appearance: translucent tint, fresnel edge glow, a moving specular
     * highlight, lens-like internal refraction and chromatic dispersion.
     */
    val GlassFragment: String = """
        precision highp float;

        out vec4 fragColor;

        uniform vec2  ${Uniforms.RESOLUTION};
        uniform float ${Uniforms.TIME};
        uniform float ${Uniforms.FRESNEL};
        uniform float ${Uniforms.SPECULAR};
        uniform float ${Uniforms.REFRACTION};
        uniform float ${Uniforms.DISPERSION};
        uniform float ${Uniforms.AMBIENT};
        uniform vec4  ${Uniforms.TINT};
        uniform float ${Uniforms.ALPHA};
        uniform float ${Uniforms.DEFORM};

        void main() {
            vec2 uv = gl_FragCoord.xy / max(${Uniforms.RESOLUTION}, vec2(1.0));

            // Edge distance used as a grazing-angle proxy for fresnel.
            float edgeDist = min(min(uv.x, 1.0 - uv.x), min(uv.y, 1.0 - uv.y));
            float grazing = clamp(1.0 - edgeDist * 2.0, 0.0, 1.0);
            float fresnel = mix(0.04, 1.0, pow(grazing, 2.0)) * ${Uniforms.FRESNEL};

            // Moving light direction for the specular sheen.
            vec2 light = vec2(
                0.5 + 0.28 * sin(${Uniforms.TIME} * 0.7),
                0.45 - 0.22 * cos(${Uniforms.TIME} * 0.5)
            );
            float spec = pow(clamp(1.0 - length((uv - light) * 3.2), 0.0, 1.0), 10.0)
                * ${Uniforms.SPECULAR};

            // Lens-like internal refraction: displace the tint toward the centre.
            vec2 centre = vec2(0.5);
            vec2 dir = uv - centre;
            float lens = 1.0 + ${Uniforms.REFRACTION} * 0.12 * (1.0 - length(dir));
            vec2 refracted = centre + dir * lens;

            // Base translucent tint with ambient illumination.
            vec3 base = ${Uniforms.TINT}.rgb + ${Uniforms.AMBIENT} * 0.18;

            // Chromatic dispersion: split the refracted tint into RGB offsets.
            float d = ${Uniforms.DISPERSION};
            vec3 dispersed = vec3(
                base.r,
                base.g,
                base.b
            );
            dispersed.r = base.r * (1.0 + d * 0.06 * (refracted.x - 0.5));
            dispersed.b = base.b * (1.0 - d * 0.06 * (refracted.x - 0.5));

            // Compose: tint + ambient + specular + fresnel edge glow.
            vec3 color = dispersed + spec * vec3(1.0) + fresnel * vec3(0.7, 0.8, 1.0);

            // Liquid deformation: a soft radial bulge driven by touch (deform>0).
            float bulge = ${Uniforms.DEFORM} * 0.15 * (1.0 - length(uv - vec2(0.5)) * 1.4);
            color += bulge * vec3(0.6, 0.75, 1.0);

            fragColor = vec4(clamp(color, 0.0, 1.0), ${Uniforms.ALPHA});
        }
    """.trimIndent()

    /** Namespace passed to `RuntimeShader`. */
    const val Namespace: String = "agsl;"
}

/**
 * Uniform values applied to the glass shader before each draw.
 *
 * All values are derived from [com.newlauncher.core.designsystem.tokens.GlassMaterialParameters]
 * so the GPU shader and the CPU fallback share one source of truth.
 */
data class GlassShaderUniforms(
    val resolutionWidth: Float,
    val resolutionHeight: Float,
    val time: Float,
    val fresnel: Float,
    val specular: Float,
    val refraction: Float,
    val dispersion: Float,
    val ambient: Float,
    val tintRed: Float,
    val tintGreen: Float,
    val tintBlue: Float,
    val alpha: Float,
    val deform: Float,
) {
    companion object {
        /** Build uniforms from material params + surface size + elapsed time. */
        fun from(
        params: com.newlauncher.core.designsystem.tokens.GlassMaterialParameters,
        width: Float,
        height: Float,
        timeSeconds: Float,
    ): GlassShaderUniforms = GlassShaderUniforms(
        resolutionWidth = width,
        resolutionHeight = height,
        time = timeSeconds,
        fresnel = params.fresnelIntensity,
        specular = params.specularStrength,
        refraction = params.refractionStrength,
        dispersion = params.chromaticDispersion,
        ambient = params.ambientIllumination,
        tintRed = params.tint.red,
        tintGreen = params.tint.green,
        tintBlue = params.tint.blue,
        alpha = 1f - params.transparency,
        deform = params.liquidDeformation,
    )
    }
}

package com.newlauncher.core.glassengine

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.newlauncher.core.designsystem.tokens.GlassMaterialParameters
import com.newlauncher.core.designsystem.tokens.GlassMaterialPresets
import com.newlauncher.core.designsystem.tokens.GlassMaterialType
import com.newlauncher.core.graphics.supportsRenderEffect
import com.newlauncher.core.graphics.supportsRuntimeShader
import com.newlauncher.core.performance.GraphicsProfile

/**
 * The central, reusable Physical Glass Rendering Engine controller.
 *
 * It holds the active [GlassMaterialParameters] and [GraphicsProfile] and
 * resolves a [GlassRenderPlan] for any surface. Feature modules never render
 * glass directly — they consume [GlassSurface] / [GlassCard], which read the
 * controller from [LocalGlassEngine]. This keeps the rendering API consistent
 * across the whole launcher.
 *
 * Components can override the material per-instance via [withMaterial], which
 * is the supported way to render, e.g., a crystal control toggle inside a
 * frosted panel.
 */
class GlassEngineController(
    material: GlassMaterialParameters = GlassMaterialPresets.Hybrid,
    profile: GraphicsProfile = GraphicsProfile.BALANCED,
) {
    var material: GlassMaterialParameters = material
        private set
    var profile: GraphicsProfile = profile
        private set

    /** Resolve the render plan for the current material + profile. */
    fun plan(): GlassRenderPlan = GlassRenderPlan.resolve(
        supportsShader = supportsRuntimeShader,
        supportsBlur = supportsRenderEffect,
        profile = profile,
        material = material,
    )

    /** Resolve a plan for an explicit material (used by per-instance overrides). */
    fun planFor(material: GlassMaterialParameters): GlassRenderPlan = GlassRenderPlan.resolve(
        supportsShader = supportsRuntimeShader,
        supportsBlur = supportsRenderEffect,
        profile = profile,
        material = material,
    )

    /** Resolve the material for a [GlassMaterialType], applying the active profile. */
    fun materialFor(type: GlassMaterialType): GlassMaterialParameters =
        GlassMaterialPresets.of(type)

    /** Replace the active material (validated/coerced). */
    fun setMaterial(material: GlassMaterialParameters) {
        this.material = GlassMaterialParameters.coerceInRange(material)
    }

    /** Replace the active profile. */
    fun setProfile(profile: GraphicsProfile) {
        this.profile = profile
    }
}

/**
 * CompositionLocal that provides the active [GlassEngineController] to all
 * glass components. The app creates one controller (bound to the user's
 * customization and the [com.newlauncher.core.performance.QualityManager])
 * and provides it at the root.
 */
val LocalGlassEngine = staticCompositionLocalOf { GlassEngineController() }

/** Read the active glass engine from the composition. */
@Composable
fun glassEngine(): GlassEngineController = LocalGlassEngine.current

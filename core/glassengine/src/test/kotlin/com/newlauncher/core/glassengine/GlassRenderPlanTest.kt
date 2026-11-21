package com.newlauncher.core.glassengine

import com.newlauncher.core.designsystem.tokens.GlassMaterialPresets
import com.newlauncher.core.performance.GraphicsProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GlassRenderPlanTest {

    @Test
    fun `shader supported and ultra uses agsl shader`() {
        val plan = GlassRenderPlan.resolve(
            supportsShader = true,
            supportsBlur = true,
            profile = GraphicsProfile.ULTRA,
            material = GlassMaterialPresets.Hybrid,
        )
        assertEquals(GlassFillStrategy.AGSL_SHADER, plan.fill)
        assertTrue(plan.animatedSheen)
        assertTrue(plan.chromaticDispersion)
    }

    @Test
    fun `no shader support falls back to brush`() {
        val plan = GlassRenderPlan.resolve(
            supportsShader = false,
            supportsBlur = false,
            profile = GraphicsProfile.ULTRA,
            material = GlassMaterialPresets.Hybrid,
        )
        assertEquals(GlassFillStrategy.BRUSH_FALLBACK, plan.fill)
    }

    @Test
    fun `performance profile disables dispersion and sheen`() {
        val plan = GlassRenderPlan.resolve(
            supportsShader = true,
            supportsBlur = true,
            profile = GraphicsProfile.PERFORMANCE,
            material = GlassMaterialPresets.Crystal,
        )
        assertEquals(GlassFillStrategy.AGSL_SHADER, plan.fill)
        assertFalse(plan.chromaticDispersion)
        assertFalse(plan.animatedSheen)
        assertFalse(plan.liquidDeform)
    }

    @Test
    fun `clear glass has no backdrop blur`() {
        val plan = GlassRenderPlan.resolve(
            supportsShader = true,
            supportsBlur = true,
            profile = GraphicsProfile.ULTRA,
            material = GlassMaterialPresets.Clear,
        )
        assertFalse(plan.applyBackdropBlur)
    }

    @Test
    fun `frosted glass enables backdrop blur`() {
        val plan = GlassRenderPlan.resolve(
            supportsShader = true,
            supportsBlur = true,
            profile = GraphicsProfile.ULTRA,
            material = GlassMaterialPresets.Frosted,
        )
        assertTrue(plan.applyBackdropBlur)
    }

    @Test
    fun `balanced profile disables liquid deformation`() {
        val plan = GlassRenderPlan.resolve(
            supportsShader = true,
            supportsBlur = true,
            profile = GraphicsProfile.BALANCED,
            material = GlassMaterialPresets.Liquid,
        )
        assertFalse(plan.liquidDeform)
    }
}

package com.newlauncher.core.designsystem

import androidx.compose.ui.unit.dp
import com.newlauncher.core.designsystem.tokens.GlassMaterialParameters
import com.newlauncher.core.designsystem.tokens.GlassMaterialPresets
import com.newlauncher.core.designsystem.tokens.GlassMaterialType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GlassMaterialTest {

    @Test
    fun `all five materials are distinct`() {
        val presets = GlassMaterialPresets.of(GlassMaterialType.CLEAR)
        val set = setOf(
            GlassMaterialPresets.Clear,
            GlassMaterialPresets.Frosted,
            GlassMaterialPresets.Liquid,
            GlassMaterialPresets.Crystal,
            GlassMaterialPresets.Hybrid,
        )
        assertEquals(5, set.size)
        assertEquals(presets, GlassMaterialPresets.of(GlassMaterialType.CLEAR))
    }

    @Test
    fun `coerceInRange clamps transparency`() {
        val bad = GlassMaterialParameters(transparency = 5f)
        val fixed = GlassMaterialParameters.coerceInRange(bad)
        assertEquals(1f, fixed.transparency, 1e-5f)

        val bad2 = GlassMaterialParameters(transparency = -1f)
        assertEquals(0f, GlassMaterialParameters.coerceInRange(bad2).transparency, 1e-5f)
    }

    @Test
    fun `coerceInRange clamps all strength params`() {
        val bad = GlassMaterialParameters(
            refractionStrength = 9f,
            reflectionIntensity = -3f,
            fresnelIntensity = 100f,
            specularStrength = -1f,
            chromaticDispersion = 2f,
            edgeStrokeAlpha = -0.5f,
            ambientIllumination = 9f,
            liquidDeformation = -2f,
        )
        val fixed = GlassMaterialParameters.coerceInRange(bad)
        listOf(
            fixed.refractionStrength,
            fixed.reflectionIntensity,
            fixed.fresnelIntensity,
            fixed.specularStrength,
            fixed.chromaticDispersion,
            fixed.edgeStrokeAlpha,
            fixed.ambientIllumination,
            fixed.liquidDeformation,
        ).forEach { assertTrue("param out of range: $it", it in 0f..1f) }
    }

    @Test
    fun `clear glass has no blur but strong fresnel`() {
        val clear = GlassMaterialPresets.Clear
        assertEquals(0.dp, clear.blurRadius)
        assertTrue(clear.fresnelIntensity > 0.7f)
    }

    @Test
    fun `frosted glass has blur and low refraction`() {
        val frosted = GlassMaterialPresets.Frosted
        assertTrue(frosted.blurRadius > 0.dp)
        assertTrue(frosted.refractionStrength < 0.2f)
    }

    @Test
    fun `liquid glass enables deformation`() {
        assertTrue(GlassMaterialPresets.Liquid.liquidDeformation > 0.5f)
    }

    @Test
    fun `crystal glass has the strongest dispersion`() {
        val dispersion = listOf(
            GlassMaterialPresets.Clear,
            GlassMaterialPresets.Frosted,
            GlassMaterialPresets.Liquid,
            GlassMaterialPresets.Crystal,
            GlassMaterialPresets.Hybrid,
        ).maxOf { it.chromaticDispersion }
        assertEquals(GlassMaterialPresets.Crystal.chromaticDispersion, dispersion, 1e-5f)
    }
}

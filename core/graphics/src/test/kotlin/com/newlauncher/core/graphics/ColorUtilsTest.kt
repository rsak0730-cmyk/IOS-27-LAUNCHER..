package com.newlauncher.core.graphics

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorUtilsTest {

    @Test
    fun `lerp midpoint blends channels`() {
        val result = ColorUtils.lerp(Color.Red, Color.Blue, 0.5f)
        assertEquals(0.5f, result.red, 1e-5f)
        assertEquals(0.0f, result.green, 1e-5f)
        assertEquals(0.5f, result.blue, 1e-5f)
    }

    @Test
    fun `lerp clamps t to range`() {
        // t below 0 returns start; above 1 returns end.
        assertEquals(Color.Red, ColorUtils.lerp(Color.Red, Color.Blue, -1f))
        assertEquals(Color.Blue, ColorUtils.lerp(Color.Red, Color.Blue, 2f))
    }

    @Test
    fun `over composites transparent source over dst`() {
        val dst = Color(0f, 0f, 0f, 1f)
        val src = Color(1f, 1f, 1f, 0.5f)
        val result = ColorUtils.over(src, dst)
        assertEquals(0.5f, result.red, 1e-5f)
        assertEquals(0.5f, result.alpha, 1e-5f)
    }

    @Test
    fun `over with opaque src replaces dst`() {
        val dst = Color(0f, 0f, 0f, 1f)
        val src = Color(0f, 1f, 0f, 1f)
        assertEquals(src, ColorUtils.over(src, dst))
    }

    @Test
    fun `fresnel schlick is low at normal incidence`() {
        // cosN = 1 (normal) -> near f0 (0.04)
        val f = ColorUtils.fresnelSchlick(cosN = 1f)
        assertTrue(f < 0.06f)
    }

    @Test
    fun `fresnel schlick is high at grazing`() {
        // cosN = 0 (grazing) -> near 1
        val f = ColorUtils.fresnelSchlick(cosN = 0f)
        assertTrue(f > 0.95f)
    }

    @Test
    fun `fresnel schlick clamps input`() {
        // Out-of-range cosN is clamped, so extremes don't exceed [0,1].
        val f = ColorUtils.fresnelSchlick(cosN = -5f)
        assertTrue(f in 0f..1f)
    }

    @Test
    fun `softClamp squares and clamps`() {
        assertEquals(0f, ColorUtils.softClamp(-1f), 1e-5f)
        assertEquals(1f, ColorUtils.softClamp(2f), 1e-5f)
        assertEquals(0.25f, ColorUtils.softClamp(0.5f), 1e-5f)
    }

    @Test
    fun `withAlpha scales alpha`() {
        val c = Color(1f, 1f, 1f, 0.5f)
        assertEquals(0.25f, ColorUtils.withAlpha(c, 0.5f).alpha, 1e-5f)
    }
}

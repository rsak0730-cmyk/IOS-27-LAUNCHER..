package com.newlauncher.app

import com.newlauncher.core.designsystem.tokens.GlassMaterialType
import com.newlauncher.core.performance.GraphicsProfile
import com.newlauncher.core.storage.LauncherSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GlassMaterialSettingsTest {

    @Test
    fun `crystal material preserves strong refraction`() {
        val settings = LauncherSettings.DEFAULT.copy(glassMaterial = GlassMaterialType.CRYSTAL)
        val material = glassMaterialFromSettings(settings)
        assertTrue(material.refractionStrength > 0.6f)
    }

    @Test
    fun `zero glass intensity reduces reflection to near zero`() {
        val settings = LauncherSettings.DEFAULT.copy(glassIntensity = 0f, reflectionStrength = 0f)
        val material = glassMaterialFromSettings(settings)
        assertEquals(0f, material.reflectionIntensity, 1e-5f)
    }

    @Test
    fun `result is always within valid range`() {
        val settings = LauncherSettings.DEFAULT.copy(
            glassIntensity = 2f,
            reflectionStrength = 5f,
            refractionStrength = 9f,
            transparency = 2f,
        )
        val material = glassMaterialFromSettings(settings)
        assertTrue(material.reflectionIntensity in 0f..1f)
        assertTrue(material.refractionStrength in 0f..1f)
        assertTrue(material.transparency in 0f..1f)
        assertTrue(material.specularStrength in 0f..1f)
    }

    @Test
    fun `profile is taken from settings unchanged`() {
        val settings = LauncherSettings.DEFAULT.copy(graphicsProfile = GraphicsProfile.PERFORMANCE)
        // The profile is applied via the engine; the material mapping must not
        // corrupt it. Verify the settings round-trips through clamp.
        val clamped = LauncherSettings.clamp(settings)
        assertEquals(GraphicsProfile.PERFORMANCE, clamped.graphicsProfile)
    }
}

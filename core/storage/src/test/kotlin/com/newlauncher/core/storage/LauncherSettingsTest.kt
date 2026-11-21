package com.newlauncher.core.storage

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LauncherSettingsTest {

    @Test
    fun `default settings are valid`() {
        val default = LauncherSettings.DEFAULT
        val clamped = LauncherSettings.clamp(default)
        assertEquals(default, clamped)
    }

    @Test
    fun `clamp brings out-of-range grid into bounds`() {
        val bad = LauncherSettings(gridColumns = 99, gridRows = -1)
        val clamped = LauncherSettings.clamp(bad)
        assertEquals(8, clamped.gridColumns)
        assertEquals(3, clamped.gridRows)
    }

    @Test
    fun `clamp brings out-of-range glass intensity into bounds`() {
        val bad = LauncherSettings(glassIntensity = 5f, transparency = -2f)
        val clamped = LauncherSettings.clamp(bad)
        assertEquals(1f, clamped.glassIntensity, 1e-5f)
        assertEquals(0f, clamped.transparency, 1e-5f)
    }

    @Test
    fun `clamp caps dock size`() {
        val bad = LauncherSettings(dockSize = 0)
        assertEquals(2, LauncherSettings.clamp(bad).dockSize)
    }

    @Test
    fun `clamp clamps animation speed`() {
        val bad = LauncherSettings(animationSpeed = 0f)
        assertEquals(0.2f, LauncherSettings.clamp(bad).animationSpeed, 1e-5f)
    }
}

class HomeLayoutTest {

    @Test
    fun `layout round-trips through json`() {
        val layout = HomeLayout(
            pages = listOf(
                HomePage(
                    id = "page-0",
                    cells = listOf(
                        HomeCell("com.example", "MainActivity", "Example", col = 0, row = 0),
                        HomeCell("com.example2", "MainActivity", "Example2", col = 1, row = 0),
                    ),
                    widgets = listOf(
                        WidgetPlacement(1, "com.example.widget", "WidgetProvider", 0, 0, 2, 2, 2)
                    ),
                )
            ),
            dock = listOf(DockSlot("com.dock", "DockActivity", "Dock App")),
        )

        val json = Json.encodeToString(layout)
        val decoded = Json.decodeFromString<HomeLayout>(json)
        assertEquals(layout, decoded)
    }

    @Test
    fun `empty layout serializes cleanly`() {
        val json = Json.encodeToString(HomeLayout.EMPTY)
        assertTrue(Json.decodeFromString<HomeLayout>(json).pages.isEmpty())
    }
}

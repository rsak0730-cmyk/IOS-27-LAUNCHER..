package com.newlauncher.core.storage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeLayoutFactoryTest {

    private fun app(name: String) = AppPlacement("pkg.$name", "cls.$name", name)

    @Test
    fun `empty app list returns empty layout preserving existing pages`() {
        val existing = listOf(HomePage(id = "page-0"))
        val result = HomeLayoutFactory.populateEmptyLayout(
            apps = emptyList(),
            settings = LauncherSettings.DEFAULT,
            existingPages = existing,
        )
        assertEquals(existing, result.pages)
        assertTrue(result.dock.isEmpty())
    }

    @Test
    fun `first dockSize apps go to the dock`() {
        val apps = listOf(app("a"), app("b"), app("c"), app("d"), app("e"))
        val result = HomeLayoutFactory.populateEmptyLayout(
            apps = apps,
            settings = LauncherSettings.DEFAULT, // dockSize = 4
            existingPages = emptyList(),
        )
        assertEquals(4, result.dock.size)
        assertEquals("a", result.dock[0].label)
        assertEquals("b", result.dock[1].label)
        assertEquals("c", result.dock[2].label)
        assertEquals("d", result.dock[3].label)
    }

    @Test
    fun `remaining apps fill the grid column-major`() {
        // dockSize=4 → grid gets "e".
        val apps = listOf(app("a"), app("b"), app("c"), app("d"), app("e"))
        val result = HomeLayoutFactory.populateEmptyLayout(
            apps = apps,
            settings = LauncherSettings.DEFAULT,
            existingPages = emptyList(),
        )
        val page0 = result.pages[0]
        assertEquals(1, page0.cells.size)
        val cell = page0.cells[0]
        assertEquals("e", cell.label)
        assertEquals(0, cell.col)
        assertEquals(0, cell.row)
    }

    @Test
    fun `grid fills column-major across columns`() {
        // dockSize=0, gridColumns=4, gridRows=5 → 20 cells on page 0.
        val settings = LauncherSettings.DEFAULT.copy(dockSize = 0, gridColumns = 4, gridRows = 5)
        val apps = (1..20).map { app("app$it") }
        val result = HomeLayoutFactory.populateEmptyLayout(
            apps = apps,
            settings = settings,
            existingPages = emptyList(),
        )
        val page0 = result.pages[0]
        assertEquals(20, page0.cells.size)
        // First row (col 0..3) should hold apps 1..4.
        for (c in 0..3) {
            assertEquals("app${c + 1}", page0.cells[c].label)
            assertEquals(c, page0.cells[c].col)
            assertEquals(0, page0.cells[c].row)
        }
        // Second row holds apps 5..8 at col 0..3, row 1.
        for (c in 0..3) {
            assertEquals("app${c + 5}", page0.cells[4 + c].label)
            assertEquals(c, page0.cells[4 + c].col)
            assertEquals(1, page0.cells[4 + c].row)
        }
    }

    @Test
    fun `overflow apps flow to the next page`() {
        // 1 page, 4 cols, 5 rows = 20 cells. Give 22 apps with dockSize=0.
        val settings = LauncherSettings.DEFAULT.copy(dockSize = 0, gridColumns = 4, gridRows = 5, pages = 1)
        val apps = (1..22).map { app("app$it") }
        val result = HomeLayoutFactory.populateEmptyLayout(
            apps = apps,
            settings = settings,
            existingPages = emptyList(),
        )
        // pages count is at least 1, but extra apps beyond page 0 capacity
        // are still placed — the factory creates enough pages to hold them.
        val totalCells = result.pages.sumOf { it.cells.size }
        assertEquals(22, totalCells)
    }

    @Test
    fun `existing page ids are preserved`() {
        val existing = listOf(HomePage(id = "custom-0"), HomePage(id = "custom-1"))
        val apps = listOf(app("a"))
        val result = HomeLayoutFactory.populateEmptyLayout(
            apps = apps,
            settings = LauncherSettings.DEFAULT,
            existingPages = existing,
        )
        assertEquals("custom-0", result.pages[0].id)
        assertEquals("custom-1", result.pages[1].id)
    }

    @Test
    fun `cell key matches package and class`() {
        val apps = listOf(app("a"))
        val result = HomeLayoutFactory.populateEmptyLayout(
            apps = apps,
            settings = LauncherSettings.DEFAULT.copy(dockSize = 0),
            existingPages = emptyList(),
        )
        val cell = result.pages[0].cells.first()
        assertEquals("pkg.a/cls.a", cell.key)
    }
}

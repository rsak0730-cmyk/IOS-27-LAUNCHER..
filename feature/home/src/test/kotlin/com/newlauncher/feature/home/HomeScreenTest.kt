package com.newlauncher.feature.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.newlauncher.core.common.AppInfo
import com.newlauncher.core.storage.DockSlot
import com.newlauncher.core.storage.HomeCell
import com.newlauncher.core.storage.HomeLayout
import com.newlauncher.core.storage.HomePage
import com.newlauncher.core.storage.LauncherSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Compose UI tests for the home screen layout (Phase 6 functional regression).
 *
 * These run on the JVM via Robolectric — no emulator or device required — and
 * exercise the pure [HomeContent] composable with plain data + callbacks:
 * grid rendering with labels, the toolbar controls, app launching, edit-mode
 * removal, the Dynamic Island slot, the always-rendered dock, and pager
 * page-change reporting.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun app(name: String, color: Int = Color.BLUE) = AppInfo(
        packageName = "com.example.$name",
        className = ".MainActivity",
        label = name,
        icon = ColorDrawable(color),
    )

    private fun cell(app: AppInfo, col: Int, row: Int) =
        HomeCell(app.packageName, app.className, app.label, col, row)

    /** Build a layout with [apps] on page 0 and [dock] apps in the dock. */
    private fun layoutOf(
        vararg apps: AppInfo,
        dock: List<AppInfo> = emptyList(),
        pageCount: Int = 1,
    ): HomeLayout {
        val cols = LauncherSettings.DEFAULT.gridColumns
        val pageCells = apps.mapIndexed { i, a -> cell(a, i % cols, i / cols) }
        val pages = mutableListOf(HomePage(id = "page-0", cells = pageCells))
        for (i in 1 until pageCount) {
            pages.add(HomePage(id = "page-$i", cells = emptyList()))
        }
        return HomeLayout(
            pages = pages,
            dock = dock.map { DockSlot(it.packageName, it.className, it.label) },
        )
    }

    private fun setContent(
        layout: HomeLayout,
        editMode: Boolean = false,
        onToggleEdit: () -> Unit = {},
        onOpenAppDrawer: () -> Unit = {},
        onOpenSettings: () -> Unit = {},
        onOpenWidgets: () -> Unit = {},
        onPageChange: (Int) -> Unit = {},
        onLaunch: (AppInfo) -> Unit = {},
        onRemove: (Int, Int) -> Unit = {},
        onMove: (Int, Int, Int) -> Unit = {},
        onMoveToDock: (Int, Int) -> Unit = {},
        onReorderDock: (Int, Int) -> Unit = {},
        island: @Composable () -> Unit = {},
    ) {
        // Rebuild the AppInfo pool so appByKey resolves every placed cell and
        // every docked slot (exercises AppIcon rasterisation too).
        val apps = layout.pages.flatMap { it.cells }.map { app(it.label) } +
            layout.dock.map { app(it.label) }
        composeTestRule.setContent {
            HomeContent(
                layout = layout,
                settings = LauncherSettings.DEFAULT,
                apps = apps,
                editMode = editMode,
                onToggleEdit = onToggleEdit,
                onOpenAppDrawer = onOpenAppDrawer,
                onOpenSettings = onOpenSettings,
                onOpenWidgets = onOpenWidgets,
                onPageChange = onPageChange,
                onLaunch = onLaunch,
                onRemove = onRemove,
                onMove = onMove,
                onMoveToDock = onMoveToDock,
                onReorderDock = onReorderDock,
                islandContent = island,
            )
        }
    }

    @Test
    fun grid_shows_app_labels() {
        setContent(layoutOf(app("Chat"), app("Mail"), app("Photos"), app("Music")))
        listOf("Chat", "Mail", "Photos", "Music").forEach {
            composeTestRule.onNodeWithText(it).assertExists()
        }
    }

    @Test
    fun toolbar_buttons_fire_callbacks() {
        var drawer = false
        var settings = false
        var widgets = false
        var toggles = 0
        setContent(
            layout = layoutOf(app("Chat")),
            onToggleEdit = { toggles++ },
            onOpenAppDrawer = { drawer = true },
            onOpenSettings = { settings = true },
            onOpenWidgets = { widgets = true },
        )
        composeTestRule.onNodeWithContentDescription("App drawer").performClick()
        composeTestRule.onNodeWithContentDescription("Settings").performClick()
        composeTestRule.onNodeWithContentDescription("Widgets").performClick()
        composeTestRule.onNodeWithContentDescription("Edit home screen").performClick()
        assertTrue(drawer)
        assertTrue(settings)
        assertTrue(widgets)
        assertEquals(1, toggles)
    }

    @Test
    fun tapping_grid_app_launches() {
        var launched: AppInfo? = null
        setContent(layoutOf(app("Chat")), onLaunch = { launched = it })
        composeTestRule.onNodeWithText("Chat").performClick()
        assertNotNull(launched)
        assertEquals("Chat", launched?.label)
    }

    @Test
    fun edit_mode_shows_remove_affordance_and_fires_remove() {
        var removed: Pair<Int, Int>? = null
        setContent(
            layout = layoutOf(app("Chat"), app("Mail")),
            editMode = true,
            onRemove = { page, index -> removed = page to index },
        )
        composeTestRule.onNodeWithText("×").performClick()
        assertNotNull(removed)
    }

    @Test
    fun empty_dock_shows_hint() {
        setContent(layoutOf(app("Chat"), dock = emptyList()))
        composeTestRule.onNodeWithText("Dock").assertExists()
    }

    @Test
    fun docked_apps_hide_dock_hint() {
        setContent(
            layout = layoutOf(app("Chat"), dock = listOf(app("Phone"), app("Messages"))),
        )
        composeTestRule.onNodeWithText("Dock").assertDoesNotExist()
    }

    @Test
    fun island_content_is_rendered() {
        setContent(layoutOf(app("Chat")), island = { Text("IslandMarker") })
        composeTestRule.onNodeWithText("IslandMarker").assertExists()
    }

    @Test
    fun pager_reports_initial_page() {
        val reported = mutableListOf<Int>()
        setContent(
            layout = layoutOf(app("Chat"), app("Mail"), app("Photos"), app("Music")),
            onPageChange = { reported.add(it) },
        )
        assertTrue(reported.contains(0))
    }

    @Test
    fun multiple_pages_keep_first_page_visible() {
        setContent(layoutOf(app("Chat"), app("Mail"), pageCount = 3))
        composeTestRule.onNodeWithText("Chat").assertExists()
        composeTestRule.onNodeWithText("Mail").assertExists()
    }
}

package com.newlauncher.feature.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.newlauncher.core.common.AppInfo
import com.newlauncher.core.common.AppProvider
import com.newlauncher.core.storage.HomeLayout
import com.newlauncher.core.storage.HomeLayoutFactory
import com.newlauncher.core.storage.HomePage
import com.newlauncher.core.storage.HomeCell
import com.newlauncher.core.storage.DockSlot
import com.newlauncher.core.storage.LauncherRepository
import com.newlauncher.core.storage.launcherDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the home screen.
 *
 * Owns the persisted [HomeLayout] and [com.newlauncher.core.storage.LauncherSettings],
 * the pool of installed apps (for placement), the current page index, and the
 * rearrangement operations (move, add, remove, dock reorder). Every mutation
 * produces a new immutable layout and persists it via [LauncherRepository], so
 * arrangements survive restarts.
 */
class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = LauncherRepository(launcherDataStore(app))
    private val provider = AppProvider(app)

    /** User customization settings. */
    val settings: StateFlow<com.newlauncher.core.storage.LauncherSettings> = repository.settings

    /** Persisted home layout (pages + dock). */
    val layout: StateFlow<HomeLayout> = repository.layout

    /** All launchable apps, used as the placement pool and to resolve icons. */
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    /** Currently displayed page index. */
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _editMode = MutableStateFlow(false)
    /** Whether the user is in rearrangement (edit) mode. */
    val editMode: StateFlow<Boolean> = _editMode.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initialize()
            _apps.value = provider.getLaunchableApps()
            _currentPage.value = 0
            populateLayoutIfEmpty()
        }
    }

    fun onPageChange(page: Int) {
        _currentPage.value = page.coerceIn(0, (layout.value.pages.size - 1).coerceAtLeast(0))
    }

    fun toggleEditMode(enabled: Boolean? = null) {
        _editMode.value = enabled ?: !_editMode.value
    }

    /** Launch an app immediately. */
    fun launch(app: AppInfo) {
        provider.launch(app)
    }

    /**
     * Swap the cell at [fromIndex] with the cell at [toIndex] on [page].
     */
    fun moveWithinPage(page: Int, fromIndex: Int, toIndex: Int) {
        val current = layout.value
        val newPages = current.pages.toMutableList()
        val pg = newPages.getOrNull(page) ?: return
        val cells = pg.cells.toMutableList()
        if (fromIndex !in cells.indices || toIndex !in cells.indices) return
        cells.add(toIndex, cells.removeAt(fromIndex))
        newPages[page] = pg.copy(cells = cells)
        persist(current.copy(pages = newPages))
    }

    /**
     * Move the cell at [index] on [page] into the dock (respecting [dockSize]).
     */
    fun moveToDock(page: Int, index: Int) {
        val current = layout.value
        val maxDock = settings.value.dockSize
        val newPages = current.pages.toMutableList()
        val pg = newPages.getOrNull(page) ?: return
        val cells = pg.cells.toMutableList()
        if (index !in cells.indices) return
        val cell = cells.removeAt(index)
        newPages[page] = pg.copy(cells = cells)

        val dock = current.dock.toMutableList()
        if (dock.size < maxDock) {
            dock.add(DockSlot(cell.packageName, cell.className, cell.label))
        }
        persist(current.copy(pages = newPages, dock = dock))
    }

    /**
     * Remove the cell at [index] from [page] (back to the app pool).
     */
    fun removeFromPage(page: Int, index: Int) {
        val current = layout.value
        val newPages = current.pages.toMutableList()
        val pg = newPages.getOrNull(page) ?: return
        val cells = pg.cells.toMutableList()
        if (index !in cells.indices) return
        cells.removeAt(index)
        newPages[page] = pg.copy(cells = cells)
        persist(current.copy(pages = newPages))
    }

    /**
     * Add [app] to [page] at the next free grid slot.
     */
    fun addAppToPage(app: AppInfo, page: Int) {
        val current = layout.value
        val cols = settings.value.gridColumns
        val newPages = current.pages.toMutableList()
        val pg = newPages.getOrNull(page) ?: return
        val cells = pg.cells.toMutableList()
        val col = cells.size % cols
        val row = cells.size / cols
        cells.add(HomeCell(app.packageName, app.className, app.label, col, row))
        newPages[page] = pg.copy(cells = cells)
        persist(current.copy(pages = newPages))
    }

    /**
     * Reorder the dock by swapping [fromIndex] and [toIndex].
     */
    fun reorderDock(fromIndex: Int, toIndex: Int) {
        val current = layout.value
        val dock = current.dock.toMutableList()
        if (fromIndex !in dock.indices || toIndex !in dock.indices) return
        dock.add(toIndex, dock.removeAt(fromIndex))
        persist(current.copy(dock = dock))
    }

    /** Add an app to the dock. */
    fun addToDock(app: AppInfo) {
        val current = layout.value
        val maxDock = settings.value.dockSize
        if (current.dock.size >= maxDock) return
        val dock = current.dock.toMutableList()
        dock.add(DockSlot(app.packageName, app.className, app.label))
        persist(current.copy(dock = dock))
    }

    private fun persist(next: HomeLayout) {
        viewModelScope.launch { repository.saveLayout(next) }
    }

    /**
     * Auto-populate the home screen on first launch.
     *
     * A freshly installed launcher has an empty layout (no pages, no cells, no
     * dock), which renders as a blank home screen. To match the behaviour of a
     * real launcher, populate the grid with the installed apps: the first
     * [LauncherSettings.dockSize] apps go to the dock, the rest are distributed
     * across the pages in column-major order.
     *
     * This only runs when the layout is genuinely empty (no placed cells and no
     * dock), so an existing user arrangement is never overwritten.
     */
    private suspend fun populateLayoutIfEmpty() {
        val apps = _apps.value
        if (apps.isEmpty()) return

        val current = layout.value
        // Only auto-populate a genuinely empty layout — never touch a user's
        // existing arrangement.
        if (current.pages.all { it.cells.isEmpty() } && current.dock.isEmpty()) {
            val placements = apps.map {
                HomeLayoutFactory.AppPlacement(it.packageName, it.className, it.label)
            }
            val populated = HomeLayoutFactory.populateEmptyLayout(
                apps = placements,
                settings = settings.value,
                existingPages = current.pages,
            )
            repository.saveLayout(populated)
        }
    }

    /** Ensure there is at least one page. */
    fun ensurePages() {
        val current = layout.value
        if (current.pages.isNotEmpty()) return
        val pages = List(settings.value.pages.coerceAtLeast(1)) { i ->
            HomePage(id = "page-$i")
        }
        persist(current.copy(pages = pages))
    }
}

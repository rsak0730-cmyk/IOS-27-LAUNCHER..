package com.newlauncher.core.storage

/**
 * A lightweight, pure-data reference to an app that can be placed on the home
 * screen. Deliberately free of any Android types (unlike [com.newlauncher.core.common.AppInfo],
 * which carries a [android.graphics.drawable.Drawable]) so the layout-building
 * logic stays unit-testable on a plain JVM.
 */
data class AppPlacement(
    val packageName: String,
    val className: String,
    val label: String,
)

/**
 * Builds a home-screen layout for a freshly installed launcher.
 *
 * A fresh install has an empty [HomeLayout] (no pages, no cells, no dock),
 * which renders as a blank home screen. [populateEmptyLayout] fills it the way
 * a real launcher does: the first [LauncherSettings.dockSize] apps go to the
 * dock, the rest are distributed across the pages in column-major order,
 * up to [LauncherSettings.gridRows] rows per page.
 *
 * This is a pure function with no Android or coroutine dependencies, so the
 * grid/dock distribution logic is fully unit-testable.
 */
object HomeLayoutFactory {

    /**
     * Populate an empty layout with [apps].
     *
     * Only invoked when the layout is genuinely empty (no placed cells and no
     * dock), so a user's existing custom arrangement is never overwritten — the
     * caller is responsible for that guard (see [HomeViewModel.populateLayoutIfEmpty]).
     *
     * @param apps the launchable applications, in the desired placement order.
     * @param settings the user's grid/dock/pages configuration.
     * @param existingPages pages already present (used to preserve page ids);
     *   when empty, fresh pages are created.
     * @return a populated [HomeLayout].
     */
    fun populateEmptyLayout(
        apps: List<AppPlacement>,
        settings: LauncherSettings,
        existingPages: List<HomePage>,
    ): HomeLayout {
        val cols = settings.gridColumns.coerceAtLeast(1)
        val dockSize = settings.dockSize.coerceAtLeast(0)
        val rowsPerPage = settings.gridRows.coerceAtLeast(1)
        val cellsPerPage = cols * rowsPerPage

        if (apps.isEmpty()) {
            return HomeLayout(pages = existingPages, dock = emptyList())
        }

        // Reserve the first dockSize apps for the dock; the rest fill the grid.
        val dockApps = apps.take(dockSize)
        val gridApps = apps.drop(dockSize)

        // Create enough pages to hold every grid app, never fewer than the
        // user's configured page count or the pages already present.
        val neededPages = if (cellsPerPage > 0) {
            (gridApps.size + cellsPerPage - 1) / cellsPerPage
        } else {
            0
        }
        val pagesCount = maxOf(settings.pages, existingPages.size, neededPages).coerceAtLeast(1)
        val pages = List(pagesCount) { i ->
            (existingPages.getOrNull(i) ?: HomePage(id = "page-$i")).copy(cells = emptyList())
        }.toMutableList()

        var appIdx = 0
        for (i in pages.indices) {
            val cells = mutableListOf<HomeCell>()
            var row = 0
            var col = 0
            while (appIdx < gridApps.size && cells.size < cellsPerPage) {
                val app = gridApps[appIdx]
                cells.add(HomeCell(app.packageName, app.className, app.label, col, row))
                appIdx++
                col++
                if (col >= cols) {
                    col = 0
                    row++
                }
            }
            pages[i] = pages[i].copy(cells = cells)
        }

        val dock = dockApps.map { DockSlot(it.packageName, it.className, it.label) }
        return HomeLayout(pages = pages, dock = dock)
    }
}

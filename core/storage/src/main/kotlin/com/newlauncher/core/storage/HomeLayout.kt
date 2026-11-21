package com.newlauncher.core.storage

import kotlinx.serialization.Serializable

/**
 * Persisted home-screen arrangement. Stored as JSON in DataStore and restored
 * after the launcher restarts, so the user's custom layout survives.
 *
 * The layout is a list of [HomePage]; each page holds an ordered list of
 * [HomeCell] (apps placed on the grid) plus any [WidgetPlacement]s. The dock
 * is stored separately as [DockSlot].
 */
@Serializable
data class HomeLayout(
    val pages: List<HomePage> = emptyList(),
    val dock: List<DockSlot> = emptyList(),
) {
    companion object {
        val EMPTY = HomeLayout()
    }
}

/** A single home-screen page. */
@Serializable
data class HomePage(
    val id: String,
    /** Cells in display order (column-major fill). */
    val cells: List<HomeCell> = emptyList(),
    /** Widgets placed on this page. */
    val widgets: List<WidgetPlacement> = emptyList(),
)

/**
 * One app cell on the grid. [col]/[row] are grid coordinates (0-based); [page]
 * is the page index the cell belongs to.
 */
@Serializable
data class HomeCell(
    val packageName: String,
    val className: String,
    val label: String,
    val col: Int = 0,
    val row: Int = 0,
) {
    val key: String get() = "$packageName/$className"
}

/** A dock slot. */
@Serializable
data class DockSlot(
    val packageName: String,
    val className: String,
    val label: String,
)

/** A widget placed on a home-screen page. */
@Serializable
data class WidgetPlacement(
    val appWidgetId: Int,
    val packageName: String,
    /** Component name of the AppWidget provider. */
    val providerName: String,
    val page: Int,
    val col: Int,
    val row: Int,
    /** Span in columns. */
    val spanX: Int = 2,
    /** Span in rows. */
    val spanY: Int = 2,
)

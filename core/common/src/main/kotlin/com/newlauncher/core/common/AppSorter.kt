package com.newlauncher.core.common

/**
 * Pure, testable helpers for organising discovered applications.
 *
 * Separated from [AppProvider] so the ordering/filtering logic can be unit
 * tested without a device or PackageManager.
 */
object AppSorter {

    /**
     * Sort apps alphabetically by label, case-insensitively, with a stable
     * fallback for equal labels (preserves insertion order via the original
     * index embedded in the sort).
     */
    fun byLabel(apps: List<AppInfo>): List<AppInfo> =
        apps.sortedWith(compareBy { it.label.lowercase() })

    /**
     * Filter [apps] by a query string matched against the app label and
     * package name, case-insensitively. A blank query returns all apps.
     */
    fun filter(apps: List<AppInfo>, query: String): List<AppInfo> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return apps
        return apps.filter {
            it.label.lowercase().contains(q) || it.packageName.lowercase().contains(q)
        }
    }

    /** Filter + sort combined: the canonical drawer/list pipeline. */
    fun search(apps: List<AppInfo>, query: String): List<AppInfo> =
        byLabel(filter(apps, query))

    /**
     * Partition apps into buckets by first letter of the label for the
     * sectioned drawer. Letters are uppercase; apps whose label does not start
     * with a letter fall into "#".
     */
    fun sectionByLetter(apps: List<AppInfo>): Map<String, List<AppInfo>> {
        val sorted = byLabel(apps)
        return sorted.groupBy { app ->
            val first = app.label.firstOrNull()
            if (first != null && first.isLetter()) first.uppercase() else "#"
        }
    }
}

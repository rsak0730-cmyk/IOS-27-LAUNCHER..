package com.newlauncher.core.common

/**
 * Central constants shared across the NEW-LAUNCHER application.
 *
 * Keeping these in one place avoids hard-coded strings scattered through the
 * codebase and makes the launcher's intent contract explicit and testable.
 */
object LauncherConstants {

    /** Application package used for internal components. */
    const val APP_PACKAGE = "com.newlauncher.android"

    /** Internal storage key namespace for preferences. */
    const val PREFS_NAME = "new_launcher_prefs"

    /** Intent action that identifies the home/launcher screen. */
    const val ACTION_HOME = android.content.Intent.ACTION_MAIN

    /** Intent category that marks an activity as a home screen. */
    const val CATEGORY_HOME = android.content.Intent.CATEGORY_HOME

    /** Intent category for default/launchable activities. */
    const val CATEGORY_DEFAULT = android.content.Intent.CATEGORY_DEFAULT

    /** Intent category that marks an activity as a launcher entry point. */
    const val CATEGORY_LAUNCHER = android.content.Intent.CATEGORY_LAUNCHER

    /** Number of columns on the default home screen grid. */
    const val DEFAULT_GRID_COLUMNS = 4

    /** Number of rows on the default home screen grid. */
    const val DEFAULT_GRID_ROWS = 5

    /** Number of apps shown in the dock. */
    const val DEFAULT_DOCK_SIZE = 4

    /** Minimum number of home screen pages. */
    const val MIN_PAGES = 1

    /** Fallback page count when no layout has been saved yet. */
    const val DEFAULT_PAGES = 3
}

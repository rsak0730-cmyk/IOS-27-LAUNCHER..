package com.newlauncher.core.common

import android.graphics.drawable.Drawable

/**
 * A single installed application discoverable by the launcher.
 *
 * Instances are produced by [AppProvider] from the Android [android.content.pm.PackageManager].
 * The [icon] is the launcher-provided application icon — never a placeholder.
 */
data class AppInfo(
    val packageName: String,
    val className: String,
    val label: String,
    val icon: Drawable,
    val isSystem: Boolean = false,
) {
    /** Stable key identifying this app instance for layout persistence. */
    val key: String get() = "$packageName/$className"

    companion object {
        /** Sentinel used when an icon cannot be resolved. */
        val NO_ICON: Drawable? = null
    }
}

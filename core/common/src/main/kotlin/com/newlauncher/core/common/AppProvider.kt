package com.newlauncher.core.common

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log

/**
 * Discovers and launches installed applications using Android's standard
 * package-visibility mechanisms.
 *
 * Discovery uses an `ACTION_MAIN` + `CATEGORY_LAUNCHER` intent query. On
 * Android 11+ the launcher's `<queries>` manifest entry grants visibility of
 * all launchable apps without declaring `QUERY_ALL_PACKAGES`, so this is both
 * functional and privacy-respecting. Icons and labels come from the system
 * [PackageManager] — never placeholders.
 */
class AppProvider(private val context: Context) {

    private val pm: PackageManager = context.packageManager

    /**
     * Return all launchable applications, sorted alphabetically by label.
     */
    fun getLaunchableApps(): List<AppInfo> {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolved = runCatching {
            pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        }.getOrDefault(emptyList())

        val apps = resolved.mapNotNull { resolveInfo -> toAppInfo(resolveInfo) }
        return AppSorter.byLabel(apps)
    }

    /**
     * Find a single app by package + activity class name, or null if not
     * launchable.
     */
    fun getApp(packageName: String, className: String): AppInfo? =
        getLaunchableApps().firstOrNull { it.packageName == packageName && it.className == className }

    /**
     * Launch [app] as a new task. Returns true on success, false if no
     * activity could handle the intent (the launcher degrades gracefully).
     */
    fun launch(app: AppInfo): Boolean {
        val intent = Intent().apply {
            component = android.content.ComponentName(app.packageName, app.className)
            addCategory(Intent.CATEGORY_LAUNCHER)
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return runCatching {
            context.startActivity(intent)
            true
        }.getOrElse {
            Log.e(Log.tag("AppProvider"), "Failed to launch ${app.packageName}", it)
            false
        }
    }

    private fun toAppInfo(resolveInfo: ResolveInfo): AppInfo? {
        val activityInfo = resolveInfo.activityInfo ?: return null
        val appInfo = activityInfo.applicationInfo ?: return null
        val label = runCatching { resolveInfo.loadLabel(pm).toString() }
            .getOrDefault(activityInfo.packageName)
        val icon = runCatching { resolveInfo.loadIcon(pm) }
            .getOrNull() ?: return null
        val isSystem = (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
        return AppInfo(
            packageName = activityInfo.packageName,
            className = activityInfo.name,
            label = label,
            icon = icon,
            isSystem = isSystem,
        )
    }
}

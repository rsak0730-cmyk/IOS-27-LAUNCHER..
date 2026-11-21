package com.newlauncher.feature.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/**
 * A discovered, installable widget provider.
 */
data class WidgetInfo(
    val componentName: ComponentName,
    val label: String,
    val icon: android.graphics.drawable.Drawable,
    val minWidth: Int,
    val minHeight: Int,
    val hasConfigure: Boolean,
    val previewLayout: Int,
)

/**
 * Discovers available AppWidget providers and handles binding/allocation.
 *
 * Uses the standard [AppWidgetManager] API — no fake or static widgets. The
 * returned providers are exactly those the system exposes to the launcher.
 */
class WidgetManager(private val context: Context) {

    private val appWidgetManager: AppWidgetManager =
        context.getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager

    /** List all widget providers available on the device. */
    fun getAvailableWidgets(): List<WidgetInfo> =
        appWidgetManager.installedProviders
            .mapNotNull { toWidgetInfo(it) }
            .sortedBy { it.label.lowercase() }

    /** Get the provider info for a [ComponentName], or null. */
    fun getProviderInfo(component: ComponentName): AppWidgetProviderInfo? =
        appWidgetManager.installedProviders.firstOrNull { it.provider == component }

    /**
     * Bind an allocated [appWidgetId] to [provider] so the host can render it.
     * Returns true if binding succeeded.
     */
    fun bind(appWidgetId: Int, provider: ComponentName): Boolean =
        runCatching {
            appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, provider)
        }.getOrDefault(false)

    /** Launch the provider's configuration activity, if it has one. */
    fun configure(appWidgetId: Int, provider: ComponentName): Boolean {
        val info = getProviderInfo(provider) ?: return false
        val configure = info.configure ?: return false
        val intent = Intent().apply {
            component = configure
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return runCatching { context.startActivity(intent); true }.getOrDefault(false)
    }

    /**
     * Request an updated [RemoteViews] for a bound widget (e.g. after resize).
     */
    fun requestUpdate(appWidgetId: Int, provider: ComponentName) {
        try {
            appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetId,
                android.R.id.content,
            )
        } catch (_: Exception) {
            // Best-effort widget update; ignore failures.
        }
    }

    private fun toWidgetInfo(info: AppWidgetProviderInfo): WidgetInfo? {
        val label = info.label?.toString() ?: info.provider?.className ?: return null
        val icon = runCatching { info.loadIcon(appWidgetManager) }.getOrNull() ?: return null
        return WidgetInfo(
            componentName = info.provider ?: return null,
            label = label,
            icon = icon,
            minWidth = info.minWidth,
            minHeight = info.minHeight,
            hasConfigure = info.configure != null,
            previewLayout = info.previewLayout,
        )
    }
}

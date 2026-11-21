package com.newlauncher.feature.widgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.content.ComponentName
import android.content.Context
import com.newlauncher.core.common.Log
import com.newlauncher.core.storage.WidgetPlacement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Owns the [AppWidgetHost] and the live set of widget placements on the home
 * screen.
 *
 * Responsibilities:
 *  - allocate a real AppWidget id for each placed widget
 *  - bind it to its provider
 *  - create/refresh the [AppWidgetHostView] for rendering
 *  - remove widgets cleanly
 *  - resize widgets (re-create the host view at the new span)
 *
 * This preserves actual widget functionality — widgets render their live
 * [android.widget.RemoteViews] content, never a static illustration.
 */
class WidgetHostController(
    context: Context,
    private val widgetManager: WidgetManager,
) {
    private val tag = Log.tag("WidgetHost")
    private val appWidgetHost = AppWidgetHost(context, HOST_ID)

    private val _placements = MutableStateFlow<List<WidgetPlacement>>(emptyList())
    /** Current widget placements (reactive for the UI). */
    val placements: StateFlow<List<WidgetPlacement>> = _placements.asStateFlow()

    /** Begin listening for widget updates. */
    fun start() {
        appWidgetHost.startListening()
    }

    /** Stop listening and release resources. */
    fun stop() {
        appWidgetHost.stopListening()
    }

    /**
     * Place [widget] on [page] at [col]/[row]. Allocates a real AppWidget id,
     * binds it, optionally runs the provider's configure activity, and records
     * the placement. Returns the created [WidgetPlacement], or null on failure.
     */
    fun addWidget(
        widget: WidgetInfo,
        page: Int,
        col: Int,
        row: Int,
        spanX: Int = 2,
        spanY: Int = 2,
    ): WidgetPlacement? {
        val appWidgetId = runCatching { appWidgetHost.allocateAppWidgetId() }.getOrNull() ?: return null
        if (!widgetManager.bind(appWidgetId, widget.componentName)) {
            Log.e(tag, "Failed to bind widget ${widget.componentName}")
            return null
        }
        if (widget.hasConfigure) {
            widgetManager.configure(appWidgetId, widget.componentName)
        }
        val placement = WidgetPlacement(
            appWidgetId = appWidgetId,
            packageName = widget.componentName.packageName,
            providerName = widget.componentName.className,
            page = page,
            col = col,
            row = row,
            spanX = spanX,
            spanY = spanY,
        )
        _placements.value = _placements.value + placement
        return placement
    }

    /** Remove a widget by its AppWidget id and release the host view. */
    fun removeWidget(appWidgetId: Int) {
        runCatching { appWidgetHost.deleteAppWidgetId(appWidgetId) }
        _placements.value = _placements.value.filterNot { it.appWidgetId == appWidgetId }
    }

    /**
     * Resize a widget by updating its span and refreshing the host view.
     */
    fun resizeWidget(appWidgetId: Int, spanX: Int, spanY: Int) {
        val placement = _placements.value.firstOrNull { it.appWidgetId == appWidgetId } ?: return
        _placements.value = _placements.value.map {
            if (it.appWidgetId == appWidgetId) it.copy(spanX = spanX, spanY = spanY) else it
        }
        widgetManager.requestUpdate(appWidgetId, placement.providerName.toComponentName())
    }

    /**
     * Create (or refresh) the [AppWidgetHostView] for [appWidgetId].
     */
    fun createHostView(context: Context, appWidgetId: Int, provider: ComponentName): AppWidgetHostView? {
        return runCatching {
            val info = widgetManager.getProviderInfo(provider) ?: return@runCatching null
            appWidgetHost.createView(context, appWidgetId, info)
        }.onFailure { Log.e(tag, "Failed to create host view for $appWidgetId", it) }
            .getOrNull()
    }

    private fun String.toComponentName(): ComponentName {
        val idx = this.indexOf('/')
        return if (idx > 0) {
            ComponentName(this.substring(0, idx), this.substring(idx + 1))
        } else {
            ComponentName(this, this)
        }
    }

    companion object {
        /** Arbitrary, stable host id for this launcher's AppWidgetHost. */
        private const val HOST_ID = 424242
    }
}

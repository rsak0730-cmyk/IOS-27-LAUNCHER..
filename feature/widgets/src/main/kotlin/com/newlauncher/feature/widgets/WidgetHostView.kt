package com.newlauncher.feature.widgets

import android.appwidget.AppWidgetHostView
import android.content.ComponentName
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toPainter
import com.newlauncher.core.designsystem.tokens.SemanticColors
import com.newlauncher.core.designsystem.tokens.SpacingTokens

/**
 * Renders the placed widgets by embedding real [AppWidgetHostView] instances
 * through [AndroidView]. Each widget keeps its live RemoteViews content and
 * updates automatically (the [WidgetHostController] listens for changes).
 *
 * Widgets are sized by their span; each row shows a remove affordance and a
 * resize stepper. This preserves actual widget functionality.
 */
@Composable
fun WidgetHostView(
    controller: WidgetHostController,
    modifier: Modifier = Modifier,
    onAddWidget: () -> Unit,
) {
    val placements by controller.placements.collectAsState()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.Md),
    ) {
        placements.forEach { placement ->
            WidgetRow(
                placement = placement,
                controller = controller,
            )
        }

        Button(onClick = onAddWidget, modifier = Modifier.padding(top = SpacingTokens.Sm)) {
            Text("Add widget")
        }
    }
}

@Composable
private fun WidgetRow(
    placement: com.newlauncher.core.storage.WidgetPlacement,
    controller: WidgetHostController,
) {
    val cellWidth = 100.dp
    val cellHeight = 100.dp
    val widthDp = cellWidth * placement.spanX
    val heightDp = cellHeight * placement.spanY

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        AndroidView(
            modifier = Modifier
                .width(widthDp)
                .height(heightDp),
            factory = { context ->
                FrameLayout(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        widthDp.value.toInt() * context.resources.displayMetrics.density.toInt(),
                        heightDp.value.toInt() * context.resources.displayMetrics.density.toInt(),
                    )
                }
            },
            update = { container ->
                // Remove stale host views, then add/refresh the current one.
                (container.childCount - 1 downTo 0).forEach { i ->
                    container.removeViewAt(i)
                }
                val hostView: AppWidgetHostView? =
                    controller.createHostView(
                        container.context,
                        placement.appWidgetId,
                        ComponentName(placement.packageName, placement.providerName),
                    )
                if (hostView != null) {
                    container.addView(hostView)
                }
            },
        )

        Column(modifier = Modifier.padding(start = SpacingTokens.L)) {
            Button(onClick = { controller.removeWidget(placement.appWidgetId) }) {
                Text("Remove")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { controller.resizeWidget(placement.appWidgetId, placement.spanX, placement.spanY - 1) },
                    modifier = Modifier.size(36.dp),
                ) { Text("−") }
                Text(
                    text = "${placement.spanX}×${placement.spanY}",
                    modifier = Modifier.padding(horizontal = SpacingTokens.Sm),
                    color = SemanticColors.TextSecondary,
                )
                Button(
                    onClick = { controller.resizeWidget(placement.appWidgetId, placement.spanX, placement.spanY + 1) },
                    modifier = Modifier.size(36.dp),
                ) { Text("+") }
            }
        }
    }
}

/**
 * A simple picker listing available widget providers. Selecting one places it
 * on page 0 at the next free slot via [WidgetHostController].
 */
@Composable
fun WidgetPicker(
    controller: WidgetHostController,
    widgetManager: WidgetManager,
    onDismiss: () -> Unit,
) {
    val widgets = widgetManager.getAvailableWidgets()
    Column(modifier = Modifier.fillMaxWidth().padding(SpacingTokens.L)) {
        Text("Available widgets", color = SemanticColors.TextPrimary)
        widgets.forEach { widget ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        controller.addWidget(widget, page = 0, col = 0, row = 0)
                        onDismiss()
                    }
                    .padding(vertical = SpacingTokens.Sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = remember(widget.icon) { widget.icon.toPainter() },
                    contentDescription = widget.label,
                    modifier = Modifier.size(32.dp),
                )
                Text(
                    text = widget.label,
                    modifier = Modifier.padding(start = SpacingTokens.Sm),
                    color = SemanticColors.TextPrimary,
                )
            }
        }
        if (widgets.isEmpty()) {
            Text("No widgets available on this device.", color = SemanticColors.TextTertiary)
        }
    }
}

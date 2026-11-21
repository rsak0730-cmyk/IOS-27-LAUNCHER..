package com.newlauncher.feature.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.newlauncher.core.designsystem.tokens.SemanticColors
import com.newlauncher.core.designsystem.tokens.SpacingTokens

/**
 * The widget management screen: lists placed widgets (with remove + resize)
 * and a picker to add new ones. Backed by a real [WidgetHostController] so
 * widgets render and update live.
 */
@Composable
fun WidgetsScreen(
    controller: WidgetHostController,
    widgetManager: WidgetManager,
    modifier: Modifier = Modifier,
) {
    var showPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .fillMaxWidth()
            .padding(SpacingTokens.L),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.L),
    ) {
        Text("Widgets", color = SemanticColors.TextPrimary)

        WidgetHostView(
            controller = controller,
            onAddWidget = { showPicker = true },
        )

        if (showPicker) {
            WidgetPicker(
                controller = controller,
                widgetManager = widgetManager,
                onDismiss = { showPicker = false },
            )
        }
    }
}

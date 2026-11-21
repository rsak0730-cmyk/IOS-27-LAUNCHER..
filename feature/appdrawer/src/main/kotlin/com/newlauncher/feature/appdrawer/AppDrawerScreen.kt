package com.newlauncher.feature.appdrawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.newlauncher.core.common.AppInfo
import com.newlauncher.core.designsystem.component.AppIcon
import com.newlauncher.core.designsystem.tokens.SemanticColors
import com.newlauncher.core.designsystem.tokens.SpacingTokens
import com.newlauncher.core.glassengine.GlassSurface

/**
 * The application library screen.
 *
 * Shows a glass search field and a sectioned, alphabetical list of installed
 * applications. Tapping an app launches it via [AppDrawerViewModel]. Scrolling
 * is smooth (LazyColumn) and the layout is responsive.
 */
@Composable
fun AppDrawerScreen(
    viewModel: AppDrawerViewModel,
    onBack: () -> Unit = {},
) {
    val apps by viewModel.apps.collectAsState()
    val sections by viewModel.sections.collectAsState()
    val query by viewModel.query.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SpacingTokens.L),
    ) {
        // Glass search field.
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(SpacingTokens.Xl),
            contentPadding = PaddingValues(horizontal = SpacingTokens.Md, vertical = SpacingTokens.Sm),
        ) {
            TextField(
                value = query,
                onValueChange = viewModel::setQuery,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search apps") },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = "Search", tint = SemanticColors.TextSecondary)
                },
                singleLine = true,
                colors = androidx.compose.material3.TextFieldDefaults.colors(
                    focusedTextColor = SemanticColors.TextPrimary,
                    unfocusedTextColor = SemanticColors.TextSecondary,
                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                ),
            )
        }

        Spacer(Modifier.height(SpacingTokens.L))

        if (apps.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No applications found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SemanticColors.TextTertiary,
                )
            }
        } else {
            AppList(
                sections = sections,
                onAppClick = { viewModel.launch(it) },
            )
        }
    }
}

@Composable
private fun AppList(
    sections: Map<String, List<AppInfo>>,
    onAppClick: (AppInfo) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = SpacingTokens.Sm),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.Sm),
    ) {
        sections.forEach { (letter, apps) ->
            item(key = "header-$letter") {
                Text(
                    text = letter,
                    style = MaterialTheme.typography.labelLarge,
                    color = SemanticColors.TextTertiary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = SpacingTokens.Sm)
                        .fillMaxWidth(),
                )
            }
            items(apps, key = { it.key }) { app ->
                AppRow(app = app, onClick = { onAppClick(app) })
            }
        }
    }
}

@Composable
private fun AppRow(app: AppInfo, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = SpacingTokens.Sm, vertical = SpacingTokens.Md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppIcon(
            drawable = app.icon,
            sizeDp = 48,
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.size(SpacingTokens.Md))
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            color = SemanticColors.TextPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}

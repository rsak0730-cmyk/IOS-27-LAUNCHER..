package com.newlauncher.feature.customization

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.newlauncher.core.designsystem.tokens.GlassMaterialType
import com.newlauncher.core.designsystem.tokens.SemanticColors
import com.newlauncher.core.designsystem.tokens.SpacingTokens
import com.newlauncher.core.glassengine.GlassSurface
import com.newlauncher.core.performance.GraphicsProfile
import com.newlauncher.core.storage.LauncherSettings
import kotlin.ranges.FloatRange

/**
 * The customization screen. Every control edits [LauncherSettings] through
 * [CustomizationViewModel] and persists immediately.
 */
@Composable
fun CustomizationScreen(
    viewModel: CustomizationViewModel,
    onPickWallpaper: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(SpacingTokens.L),
        verticalArrangement = Arrangement.spacedBy(SpacingTokens.Xl),
    ) {
        Text("Appearance", style = MaterialTheme.typography.titleMedium, color = SemanticColors.TextPrimary)

        // Glass material.
        GlassMaterialPicker(
            selected = settings.glassMaterial,
            onSelect = { type -> viewModel.update { s -> s.copy(glassMaterial = type) } },
        )

        SliderSetting(
            label = "Glass intensity",
            value = settings.glassIntensity,
            range = 0f..1f,
            onChange = { viewModel.update { s -> s.copy(glassIntensity = it) } },
        )
        SliderSetting(
            label = "Reflection strength",
            value = settings.reflectionStrength,
            range = 0f..1f,
            onChange = { viewModel.update { s -> s.copy(reflectionStrength = it) } },
        )
        SliderSetting(
            label = "Refraction strength",
            value = settings.refractionStrength,
            range = 0f..1f,
            onChange = { viewModel.update { s -> s.copy(refractionStrength = it) } },
        )
        SliderSetting(
            label = "Surface transparency",
            value = settings.transparency,
            range = 0f..1f,
            onChange = { viewModel.update { s -> s.copy(transparency = it) } },
        )

        Text("Home screen", style = MaterialTheme.typography.titleMedium, color = SemanticColors.TextPrimary)
        SliderSetting(
            label = "Grid columns",
            value = settings.gridColumns.toFloat(),
            range = 2f..8f,
            stepCount = 6,
            format = { it.toInt().toString() },
            onChange = { viewModel.update { s -> s.copy(gridColumns = it.toInt()) } },
        )
        SliderSetting(
            label = "Grid rows",
            value = settings.gridRows.toFloat(),
            range = 3f..8f,
            stepCount = 5,
            format = { it.toInt().toString() },
            onChange = { viewModel.update { s -> s.copy(gridRows = it.toInt()) } },
        )
        SliderSetting(
            label = "App icon size",
            value = settings.appIconSize,
            range = 0.6f..1.6f,
            format = { "${(it * 100).toInt()}%" },
            onChange = { viewModel.update { s -> s.copy(appIconSize = it) } },
        )
        SliderSetting(
            label = "Grid spacing",
            value = settings.gridSpacing,
            range = 0.5f..2f,
            format = { "${(it * 100).toInt()}%" },
            onChange = { viewModel.update { s -> s.copy(gridSpacing = it) } },
        )
        SliderSetting(
            label = "Dock size",
            value = settings.dockSize.toFloat(),
            range = 2f..8f,
            stepCount = 6,
            format = { it.toInt().toString() },
            onChange = { viewModel.update { s -> s.copy(dockSize = it.toInt()) } },
        )
        SliderSetting(
            label = "Number of pages",
            value = settings.pages.toFloat(),
            range = 1f..12f,
            stepCount = 11,
            format = { it.toInt().toString() },
            onChange = { viewModel.update { s -> s.copy(pages = it.toInt()) } },
        )

        Text("Motion", style = MaterialTheme.typography.titleMedium, color = SemanticColors.TextPrimary)
        SliderSetting(
            label = "Animation speed",
            value = settings.animationSpeed,
            range = 0.2f..2f,
            format = { "${(it * 100).toInt()}%" },
            onChange = { viewModel.update { s -> s.copy(animationSpeed = it) } },
        )

        Text("Performance", style = MaterialTheme.typography.titleMedium, color = SemanticColors.TextPrimary)
        ProfilePicker(
            selected = settings.graphicsProfile,
            onSelect = { viewModel.update { s -> s.copy(graphicsProfile = it) } },
        )

        Text("Wallpaper", style = MaterialTheme.typography.titleMedium, color = SemanticColors.TextPrimary)
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(SpacingTokens.L),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpacingTokens.Md)
                    .clickable { onPickWallpaper() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = settings.wallpaperUri?.let { "Wallpaper set" } ?: "Pick wallpaper",
                    color = SemanticColors.TextSecondary,
                )
                Text("Change", color = SemanticColors.Primary)
            }
        }
    }
}

@Composable
private fun GlassMaterialPicker(
    selected: GlassMaterialType,
    onSelect: (GlassMaterialType) -> Unit,
) {
    val options = GlassMaterialType.entries
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.Sm),
    ) {
        options.forEach { type ->
            val isSelected = type == selected
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(SpacingTokens.L))
                    .clickable { onSelect(type) }
                    .padding(SpacingTokens.Sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = type.name,
                    color = if (isSelected) SemanticColors.Primary else SemanticColors.TextSecondary,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun ProfilePicker(
    selected: GraphicsProfile,
    onSelect: (GraphicsProfile) -> Unit,
) {
    val options = GraphicsProfile.entries
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.Sm),
    ) {
        options.forEach { profile ->
            val isSelected = profile == selected
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(SpacingTokens.L))
                    .clickable { onSelect(profile) }
                    .padding(SpacingTokens.Sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = profile.name,
                    color = if (isSelected) SemanticColors.Primary else SemanticColors.TextSecondary,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun SliderSetting(
    label: String,
    value: Float,
    range: FloatRange,
    onChange: (Float) -> Unit,
    stepCount: Int = 0,
    format: (Float) -> String = { "${(it * 100).toInt()}%" },
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = SpacingTokens.Sm)) {
        Text(text = "$label: ${format(value)}", color = SemanticColors.TextSecondary, style = MaterialTheme.typography.bodySmall)
        Slider(
            value = value.coerceIn(range.start, range.endInclusive),
            valueRange = range,
            steps = if (stepCount > 0) stepCount else 0,
            onValueChange = onChange,
        )
    }
}

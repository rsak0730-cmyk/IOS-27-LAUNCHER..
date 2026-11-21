package com.newlauncher.feature.controlcenter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Rotate90DegreesCcw
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.BatteryFull
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.newlauncher.core.designsystem.tokens.SemanticColors
import com.newlauncher.core.designsystem.tokens.SpacingTokens
import com.newlauncher.core.glassengine.GlassPanel

/**
 * The iOS-inspired Control Center: a glass panel of quick-access controls.
 *
 * Toggle tiles (Wi-Fi, Bluetooth, flashlight) act directly where Android
 * allows and open the matching Settings screen otherwise. Brightness and
 * volume are sliders. Rotation and Settings open the relevant system screens.
 * Battery is shown read-only.
 */
@Composable
fun ControlCenter(
    viewModel: ControlCenterViewModel,
    modifier: Modifier = Modifier,
) {
    val wifi by viewModel.wifiEnabled.collectAsState()
    val bluetooth by viewModel.bluetoothEnabled.collectAsState()
    val flashlight by viewModel.flashlightOn.collectAsState()
    val brightness by viewModel.brightness.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val battery by viewModel.batteryLevel.collectAsState()

    GlassPanel(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SpacingTokens.Xxl),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L),
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.L),
        ) {
            // Toggle tiles row.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ToggleTile(
                    icon = Icons.Filled.Wifi,
                    label = "Wi-Fi",
                    active = wifi,
                    onClick = { viewModel.toggleWifi() },
                )
                ToggleTile(
                    icon = Icons.Filled.Bluetooth,
                    label = "Bluetooth",
                    active = bluetooth,
                    onClick = { viewModel.toggleBluetooth() },
                )
                ToggleTile(
                    icon = Icons.Filled.FlashlightOn,
                    label = "Flash",
                    active = flashlight,
                    onClick = { viewModel.toggleFlashlight() },
                )
            }

            // Secondary action row.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ActionTile(
                    icon = Icons.Filled.Rotate90DegreesCcw,
                    label = "Rotation",
                    onClick = { viewModel.openSettings(android.provider.Settings.ACTION_APPLICATION_SETTINGS) },
                )
                ActionTile(
                    icon = Icons.Filled.Settings,
                    label = "Settings",
                    onClick = { viewModel.openSettings(android.provider.Settings.ACTION_SETTINGS) },
                )
            }

            // Brightness slider.
            SliderRow(
                icon = Icons.Filled.BrightnessHigh,
                value = brightness.toFloat() / 255f,
                valueRange = 0f..1f,
                onValueChange = { viewModel.setBrightness((it * 255).toInt()) },
                label = "Brightness",
            )

            // Volume stepper.
            VolumeControl(
                value = volume,
                onDecrease = { viewModel.adjustVolume(-1) },
                onIncrease = { viewModel.adjustVolume(1) },
            )

            // Battery.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Outlined.BatteryFull,
                    contentDescription = "Battery",
                    tint = SemanticColors.TextPrimary,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Battery $battery%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SemanticColors.TextSecondary,
                )
            }
        }
    }
}

@Composable
private fun ToggleTile(
    icon: ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    GlassPanel(
        modifier = Modifier
            .padding(SpacingTokens.Sm)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(SpacingTokens.L),
    ) {
        Column(
            modifier = Modifier.padding(SpacingTokens.Md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.Sm),
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (active) SemanticColors.Primary else SemanticColors.TextSecondary,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = SemanticColors.TextSecondary,
            )
        }
    }
}

@Composable
private fun ActionTile(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    GlassPanel(
        modifier = Modifier
            .padding(SpacingTokens.Sm)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(SpacingTokens.L),
    ) {
        Column(
            modifier = Modifier.padding(SpacingTokens.Md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpacingTokens.Sm),
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = SemanticColors.TextPrimary,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = SemanticColors.TextSecondary,
            )
        }
    }
}

@Composable
private fun SliderRow(
    icon: ImageVector,
    value: Float,
    valueRange: FloatRange,
    onValueChange: (Float) -> Unit,
    label: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = label, tint = SemanticColors.TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.size(SpacingTokens.Sm))
        Slider(
            value = value.coerceIn(valueRange.first, valueRange.last),
            valueRange = valueRange,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "${(value * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = SemanticColors.TextSecondary,
            modifier = Modifier.padding(start = SpacingTokens.Sm),
        )
    }
}

@Composable
private fun VolumeControl(
    value: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onDecrease) {
            Icon(Icons.Filled.Remove, contentDescription = "Decrease volume", tint = SemanticColors.TextPrimary)
        }
        Text(
            text = "$value",
            style = MaterialTheme.typography.bodyMedium,
            color = SemanticColors.TextSecondary,
            modifier = Modifier.padding(horizontal = SpacingTokens.Sm),
        )
        IconButton(onClick = onIncrease) {
            Icon(Icons.Filled.Add, contentDescription = "Increase volume", tint = SemanticColors.TextPrimary)
        }
    }
}

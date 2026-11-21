package com.newlauncher.feature.dynamicisland

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.newlauncher.core.designsystem.tokens.SemanticColors
import com.newlauncher.core.glassengine.GlassPanel

/**
 * The interactive Dynamic Island-inspired glass bubble.
 *
 * Collapsed, it is a compact pill; tapping (or an incoming activity) expands
 * it into a larger glass panel with a liquid spring transition. The interior
 * shows the active activity: now-playing music, a countdown timer, or a
 * notification. Reflections and translucency come from the glass engine.
 *
 * This is an original Android interpretation — it does not use Apple's Live
 * Activities infrastructure.
 */
@Composable
fun DynamicIsland(
    viewModel: DynamicIslandViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    // Animate the expansion factor with a liquid spring.
    val progress by animateFloatAsState(
        targetValue = if (state.expanded) 1f else 0f,
        animationSpec = SpringSpec(dampingRatio = 0.75f, stiffness = 200f),
        label = "islandExpand",
    )

    val collapsedWidth: Dp = 120.dp
    val collapsedHeight: Dp = 36.dp
    val expandedWidth: Dp = 340.dp
    val expandedHeight: Dp = 120.dp

    val width = lerp(collapsedWidth, expandedWidth, progress)
    val height = lerp(collapsedHeight, expandedHeight, progress)

    GlassPanel(
        modifier = modifier
            .width(width)
            .height(height)
            .clickable { viewModel.onIslandTap() },
        shape = RoundedCornerShape(28.dp),
    ) {
        when (val activity = state.activity) {
            IslandActivity.None -> {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "NEW-LAUNCHER",
                        style = MaterialTheme.typography.labelSmall,
                        color = SemanticColors.TextSecondary,
                    )
                }
            }

            is IslandActivity.Music -> {
                MusicContent(activity, viewModel)
            }

            is IslandActivity.Timer -> {
                TimerContent(activity)
            }

            is IslandActivity.Notification -> {
                NotificationContent(activity)
            }
        }
    }
}

@Composable
private fun MusicContent(activity: IslandActivity.Music, viewModel: DynamicIslandViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleSmall,
                color = SemanticColors.TextPrimary,
                maxLines = 1,
            )
            if (!activity.artist.isNullOrBlank()) {
                Text(
                    text = activity.artist.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = SemanticColors.TextSecondary,
                    maxLines = 1,
                )
            }
        }
        IconButton(onClick = { viewModel.togglePlayback() }) {
            Icon(
                if (activity.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (activity.isPlaying) "Pause" else "Play",
                tint = SemanticColors.TextPrimary,
            )
        }
    }
}

@Composable
private fun TimerContent(activity: IslandActivity.Timer) {
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
        Text(
            text = formatDuration(activity.remainingSeconds),
            style = MaterialTheme.typography.headlineSmall,
            color = SemanticColors.TextPrimary,
        )
    }
}

@Composable
private fun NotificationContent(activity: IslandActivity.Notification) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = activity.title,
            style = MaterialTheme.typography.titleSmall,
            color = SemanticColors.TextPrimary,
            maxLines = 1,
        )
        if (!activity.text.isNullOrBlank()) {
            Text(
                text = activity.text.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = SemanticColors.TextSecondary,
                maxLines = 2,
            )
        }
    }
}

private fun formatDuration(totalSeconds: Long): String {
    val s = totalSeconds.coerceAtLeast(0)
    val m = s / 60
    val sec = s % 60
    return String.format("%d:%02d", m, sec)
}

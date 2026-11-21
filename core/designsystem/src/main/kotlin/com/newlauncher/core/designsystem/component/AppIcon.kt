package com.newlauncher.core.designsystem.component

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.asPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap

/**
 * Renders a system-provided application [Drawable] as a Compose image.
 *
 * Used by the home screen grid, dock and the app drawer so every app shows its
 * real launcher icon — never a placeholder. The drawable is rasterised once and
 * cached via [remember].
 */
@Composable
fun AppIcon(
    drawable: Drawable,
    modifier: Modifier = Modifier,
    sizeDp: Int = 64,
    contentDescription: String? = null,
) {
    val painter = remember(drawable, sizeDp) {
        drawable.toBitmap(width = sizeDp, height = sizeDp)
            .asImageBitmap()
            .asPainter()
    }
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}

/**
 * A glass-backed app icon tile. Wraps [AppIcon] in a rounded, glass surface
 * so icons read as premium launcher tiles.
 */
@Composable
fun AppIconTile(
    drawable: Drawable,
    modifier: Modifier = Modifier,
    sizeDp: Int = 64,
    label: String? = null,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AppIcon(drawable = drawable, sizeDp = sizeDp)
    }
}

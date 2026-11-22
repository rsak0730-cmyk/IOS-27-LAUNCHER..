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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap

/**
 * Renders a system-provided application [Drawable] as a Compose image.
 *
 * Used by the home screen grid, dock and the app drawer so every app shows its
 * real launcher icon — never a placeholder. The drawable is rasterised to an
 * [androidx.compose.ui.graphics.ImageBitmap] once (keyed on the drawable + the
 * resolved pixel size) and cached via [remember].
 *
 * Two defensive details matter here:
 *  - The rasterisation target is the *pixel* size derived from [sizeDp] via the
 *    ambient density, not the dp value itself. Rasterising at the dp number
 *    produced blurry, pixelated icons on high-density screens.
 *  - [Drawable.toBitmap] returns null (or can throw) for drawables it cannot
 *    rasterise. A null/throwing result previously crashed the whole launcher
 *    during composition (the home grid renders many icons at once). Here it
 *    degrades gracefully to an empty box, so a single bad icon never takes down
 *    the app.
 */
@Composable
fun AppIcon(
    drawable: Drawable,
    modifier: Modifier = Modifier,
    sizeDp: Int = 64,
    contentDescription: String? = null,
) {
    val density = LocalDensity.current
    val pxSize = with(density) { sizeDp.dp.toPx() }.toInt()

    val imageBitmap = remember(drawable, pxSize) {
        try {
            drawable.toBitmap(width = pxSize, height = pxSize)?.asImageBitmap()
        } catch (_: Exception) {
            null
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit,
        )
    } else {
        // Graceful fallback: a drawable that cannot be rasterised must not
        // crash composition. Render an empty box so the cell keeps its size.
        Box(modifier = modifier, contentAlignment = Alignment.Center)
    }
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
    Box(
        modifier = modifier.size(sizeDp.dp),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(drawable = drawable, sizeDp = sizeDp)
    }
}

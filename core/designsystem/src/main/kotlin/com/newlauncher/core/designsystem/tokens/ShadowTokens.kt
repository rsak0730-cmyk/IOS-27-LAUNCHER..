package com.newlauncher.core.designsystem.tokens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

/**
 * Layer 1 — Shadow tokens.
 *
 * Soft, ambient shadows with a hint of blue to match the dark base. These
 * are consumed by the glass surface components. A [GlassShadow] bundles a
 * shadow color, blur radius and vertical offset so glass panels can share one
 * definition of elevation.
 */
object ShadowTokens {
    /** Ambient, diffuse shadow used under floating glass cards. */
    val Glass = GlassShadow(
        color = Color(0x33000000),
        blurRadius = 24.dp,
        offsetY = 6.dp,
    )

    /** Stronger shadow for the dock and floating panels. */
    val Elevated = GlassShadow(
        color = Color(0x40000000),
        blurRadius = 32.dp,
        offsetY = 10.dp,
    )

    /** Subtle inner sheen shadow for crisp glass edges. */
    val InnerStroke = GlassShadow(
        color = Color(0x14FFFFFF),
        blurRadius = 1.dp,
        offsetY = 0.dp,
    )

    /** Card shadow for non-glass cards. */
    val Card = GlassShadow(
        color = Color(0x22000000),
        blurRadius = 16.dp,
        offsetY = 4.dp,
    )
}

/**
 * A reusable shadow definition. [Shape] defaults to a rounded rectangle so
 * the shadow follows the glass card's corner radius.
 */
data class GlassShadow(
    val color: Color,
    val blurRadius: androidx.compose.ui.unit.Dp,
    val offsetY: androidx.compose.ui.unit.Dp,
    val shape: Shape = RectangleShape,
) {
    val colorArgb: Int get() = color.toArgb()
}

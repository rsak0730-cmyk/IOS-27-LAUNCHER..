package com.newlauncher.core.designsystem.tokens

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Layer 1 — Typography tokens.
 *
 * A premium, SF-inspired scale. The launcher uses a tight, legible scale so
 * that labels remain readable over translucent glass. Sizes are in logical
 * pixels (sp) and weights follow a 400/500/600/700 ramp.
 */
object TypographyTokens {
    val Caption = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, lineHeight = 14.sp)
    val Footnote = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal, lineHeight = 16.sp)
    val Body = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp)
    val BodyStrong = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, lineHeight = 20.sp)
    val Subhead = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, lineHeight = 22.sp)
    val Title = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
    val Headline = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 34.sp)
    val Display = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold, lineHeight = 42.sp)

    /** App-label style used under home-screen / drawer icons. */
    val AppLabel = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, lineHeight = 13.sp)

    /** Dock label — slightly larger for the always-visible dock. */
    val DockLabel = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, lineHeight = 14.sp)
}

/** Layer 1 — font-weight aliases for convenience. */
object FontWeights {
    val Regular = FontWeight.Normal
    val Medium = FontWeight.Medium
    val SemiBold = FontWeight.SemiBold
    val Bold = FontWeight.Bold
}

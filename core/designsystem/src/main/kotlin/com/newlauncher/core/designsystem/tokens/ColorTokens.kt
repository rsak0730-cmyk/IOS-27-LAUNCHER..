package com.newlauncher.core.designsystem.tokens

import androidx.compose.ui.graphics.Color

/**
 * Layer 1 — PRIMITIVE tokens.
 *
 * Raw, semantic-free design values. These are the foundation of the
 * NEW-LAUNCHER design system and are intentionally not tied to any
 * particular meaning. Semantic tokens (see [SemanticColors]) alias these.
 *
 * The palette is a dark, deep base so that translucent glass surfaces can
 * refract and reflect light convincingly. Pure-black backgrounds were avoided
 * in favour of a very dark blue-violet to keep glass highlights soft.
 */
object PrimitiveColors {
    // Backgrounds — deep, desaturated blue-black for glass to read against.
    val BgDeepest: Color = Color(0xFF080A0F)
    val BgBase: Color = Color(0xFF10131A)
    val BgElevated: Color = Color(0xFF181C24)
    val BgSurface: Color = Color(0xFF1E222B)

    // Glass primitive tints (alpha-bearing, intentionally near-transparent).
    val GlassClear: Color = Color(0x0CFFFFFF)
    val GlassClearStrong: Color = Color(0x1AFFFFFF)
    val GlassFrosted: Color = Color(0x26FFFFFF)
    val GlassFrostedStrong: Color = Color(0x33FFFFFF)
    val GlassCrystal: Color = Color(0x18EAF2FF)
    val GlassCrystalStrong: Color = Color(0x2AEAF2FF)
    val GlassHybrid: Color = Color(0x1CFFFFFF)

    // Accent spectrum.
    val AccentBlue: Color = Color(0xFF5EAAFF)
    val AccentViolet: Color = Color(0xFF8B7BFF)
    val AccentCyan: Color = Color(0xFF5EE9E0)
    val AccentWarm: Color = Color(0xFFFFB36B)

    // Text.
    val TextPrimary: Color = Color(0xFFFFFFFF)
    val TextSecondary: Color = Color(0xFFAEB6C4)
    val TextTertiary: Color = Color(0xFF6B7480)
    val TextOnAccent: Color = Color(0xFF08121F)

    // Lines / dividers (white at low alpha).
    val DividerLow: Color = Color(0x14FFFFFF)
    val DividerMid: Color = Color(0x26FFFFFF)
    val StrokeGlass: Color = Color(0x33FFFFFF)

    // Highlight sheen used by reflections.
    val SheenWarm: Color = Color(0x66FFFFFF)
    val SheenCool: Color = Color(0x3DFFFFFF)

    // Status.
    val Success: Color = Color(0xFF3DD68C)
    val Warning: Color = Color(0xFFFFB36B)
    val Error: Color = Color(0xFFFF6B7A)
    val Info: Color = AccentBlue
}

/**
 * Layer 2 — SEMANTIC tokens.
 *
 * Purpose-based aliases over primitive values. These are what components
 * consume, so theme switching only needs to remap this layer.
 */
object SemanticColors {
    // Surfaces.
    val Background = PrimitiveColors.BgBase
    val BackgroundDeep = PrimitiveColors.BgDeepest
    val BackgroundElevated = PrimitiveColors.BgElevated
    val Surface = PrimitiveColors.BgSurface
    val SurfaceVariant = PrimitiveColors.BgElevated

    // Glass surfaces (translucent).
    val GlassClear = PrimitiveColors.GlassClear
    val GlassClearStrong = PrimitiveColors.GlassClearStrong
    val GlassFrosted = PrimitiveColors.GlassFrosted
    val GlassFrostedStrong = PrimitiveColors.GlassFrostedStrong
    val GlassCrystal = PrimitiveColors.GlassCrystal
    val GlassHybrid = PrimitiveColors.GlassHybrid

    // Brand / accent.
    val Primary = PrimitiveColors.AccentBlue
    val PrimaryVariant = PrimitiveColors.AccentViolet
    val Secondary = PrimitiveColors.AccentCyan
    val Tertiary = PrimitiveColors.AccentWarm

    // Text.
    val TextPrimary = PrimitiveColors.TextPrimary
    val TextSecondary = PrimitiveColors.TextSecondary
    val TextTertiary = PrimitiveColors.TextTertiary
    val OnPrimary = PrimitiveColors.TextOnAccent

    // Lines.
    val Divider = PrimitiveColors.DividerLow
    val DividerStrong = PrimitiveColors.DividerMid
    val GlassStroke = PrimitiveColors.StrokeGlass

    // Sheen (reflection highlight).
    val Sheen = PrimitiveColors.SheenWarm
    val SheenCool = PrimitiveColors.SheenCool

    // Status.
    val Success = PrimitiveColors.Success
    val Warning = PrimitiveColors.Warning
    val Error = PrimitiveColors.Error
    val Info = PrimitiveColors.Info
}

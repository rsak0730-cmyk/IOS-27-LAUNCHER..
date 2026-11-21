package com.newlauncher.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.newlauncher.core.designsystem.tokens.PrimitiveColors
import com.newlauncher.core.designsystem.tokens.SemanticColors
import com.newlauncher.core.designsystem.tokens.TypographyTokens

/**
 * The launcher is glass-first and therefore defaults to a dark palette, which
 * lets translucent glass surfaces refract and reflect convincingly. A light
 * scheme is also provided for accessibility, but the home experience is
 * intentionally dark.
 */
private val DarkColorScheme = darkColorScheme(
    primary = SemanticColors.Primary,
    onPrimary = SemanticColors.OnPrimary,
    primaryContainer = SemanticColors.Primary,
    onPrimaryContainer = SemanticColors.TextPrimary,
    secondary = SemanticColors.Secondary,
    onSecondary = SemanticColors.TextPrimary,
    tertiary = SemanticColors.Tertiary,
    onTertiary = SemanticColors.TextPrimary,
    background = SemanticColors.Background,
    onBackground = SemanticColors.TextPrimary,
    surface = SemanticColors.Surface,
    onSurface = SemanticColors.TextPrimary,
    surfaceVariant = SemanticColors.SurfaceVariant,
    onSurfaceVariant = SemanticColors.TextSecondary,
    surfaceTint = SemanticColors.Primary,
    outline = SemanticColors.Divider,
    outlineVariant = SemanticColors.Divider,
    error = SemanticColors.Error,
    onError = SemanticColors.TextPrimary,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimitiveColors.AccentBlue,
    onPrimary = PrimitiveColors.TextOnAccent,
    primaryContainer = Color(0xFFDCE9FF),
    onPrimaryContainer = PrimitiveColors.BgDeepest,
    secondary = PrimitiveColors.AccentViolet,
    onSecondary = Color.White,
    tertiary = PrimitiveColors.AccentCyan,
    onTertiary = PrimitiveColors.TextOnAccent,
    background = Color(0xFFF4F6FB),
    onBackground = PrimitiveColors.BgDeepest,
    surface = Color(0xFFFBFCFE),
    onSurface = PrimitiveColors.BgDeepest,
    surfaceVariant = Color(0xFFE6EAF2),
    onSurfaceVariant = PrimitiveColors.BgDeepest,
    surfaceTint = PrimitiveColors.AccentBlue,
    outline = PrimitiveColors.TextTertiary,
    outlineVariant = PrimitiveColors.DividerMid,
    error = PrimitiveColors.Error,
    onError = Color.White,
)

private val LauncherTypography = Typography(
    displayLarge = TypographyTokens.Display,
    displayMedium = TypographyTokens.Headline,
    headlineLarge = TypographyTokens.Headline,
    headlineMedium = TypographyTokens.Title,
    titleLarge = TypographyTokens.Subhead,
    bodyLarge = TypographyTokens.Body,
    bodyMedium = TypographyTokens.Body,
    bodySmall = TypographyTokens.Footnote,
    labelLarge = TypographyTokens.Subhead,
    labelMedium = TypographyTokens.AppLabel,
    labelSmall = TypographyTokens.Caption,
)

/**
 * CompositionLocal exposing the resolved glass material parameters to the
 * glass engine and feature components. The app sets this from the user's
 * customization + performance profile.
 */
data class GlassTheme(
    val useDarkPalette: Boolean = true,
)

val LocalGlassTheme = staticCompositionLocalOf { GlassTheme() }

/**
 * Entry theme wrapper. [darkTheme] defaults to true because glass reads best
 * on a dark base, but the system setting is respected when explicitly asked.
 */
@Composable
fun NewLauncherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    CompositionLocalProvider(LocalGlassTheme provides GlassTheme(useDarkPalette = darkTheme)) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LauncherTypography,
            content = content,
        )
    }
}

/**
 * Provides the ambient animation tokens. Currently informational; components
 * import [AnimationTokens] directly. Kept here as the single composition
 * entry point for future per-theme animation overrides.
 */
@Composable
fun LauncherAnimationScope(content: @Composable () -> Unit) {
    // Animation tokens are static; this scope exists so animation tuning can
    // be centralised without forcing every call site to import the tokens.
    content
}

/** Whether the runtime supports AGSL RuntimeShader (Android 12+). */
fun supportsAgsl(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

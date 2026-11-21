package com.newlauncher.core.storage

import com.newlauncher.core.designsystem.tokens.GlassMaterialType
import com.newlauncher.core.performance.GraphicsProfile
import kotlinx.serialization.Serializable

/**
 * All user-customizable launcher settings, persisted locally via DataStore.
 *
 * This is the single source of truth for customization. The customization
 * feature module edits it; the home screen, glass engine and performance
 * manager read it. Nothing here leaves the device.
 */
@Serializable
data class LauncherSettings(
    // Wallpaper
    val wallpaperUri: String? = null,

    // Home grid
    val gridColumns: Int = 4,
    val gridRows: Int = 5,
    val appIconSize: Float = 1.0f,
    val gridSpacing: Float = 1.0f,

    // Dock / folders
    val dockSize: Int = 4,
    val dockAppearance: String = "glass",
    val folderAppearance: String = "glass",

    // Home layout
    val pages: Int = 3,

    // Glass appearance
    val glassMaterial: GlassMaterialType = GlassMaterialType.HYBRID,
    /** Global multiplier (0f..1f) applied to reflection/refraction strength. */
    val glassIntensity: Float = 1.0f,
    val reflectionStrength: Float = 1.0f,
    val refractionStrength: Float = 1.0f,
    val transparency: Float = 1.0f,
    val animationSpeed: Float = 1.0f,

    // Performance
    val graphicsProfile: GraphicsProfile = GraphicsProfile.BALANCED,
) {
    companion object {
        /** Default settings used on first launch. */
        val DEFAULT = LauncherSettings()

        /** Clamp all numeric fields into valid ranges (defensive against bad data). */
        fun clamp(settings: LauncherSettings): LauncherSettings = settings.copy(
            gridColumns = settings.gridColumns.coerceIn(2, 8),
            gridRows = settings.gridRows.coerceIn(3, 8),
            appIconSize = settings.appIconSize.coerceIn(0.6f, 1.6f),
            gridSpacing = settings.gridSpacing.coerceIn(0.5f, 2.0f),
            dockSize = settings.dockSize.coerceIn(2, 8),
            pages = settings.pages.coerceIn(1, 12),
            glassIntensity = settings.glassIntensity.coerceIn(0f, 1f),
            reflectionStrength = settings.reflectionStrength.coerceIn(0f, 1f),
            refractionStrength = settings.refractionStrength.coerceIn(0f, 1f),
            transparency = settings.transparency.coerceIn(0f, 1f),
            animationSpeed = settings.animationSpeed.coerceIn(0.2f, 2.0f),
        )
    }
}

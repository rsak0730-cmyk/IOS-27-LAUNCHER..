package com.newlauncher.app

import android.graphics.Bitmap
import android.os.Bundle
import android.wallpapermanager.WallpaperManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.newlauncher.core.common.Log
import com.newlauncher.core.designsystem.theme.NewLauncherTheme
import com.newlauncher.core.designsystem.tokens.GlassMaterialParameters
import com.newlauncher.core.designsystem.tokens.GlassMaterialPresets
import com.newlauncher.core.glassengine.LocalGlassEngine
import com.newlauncher.core.storage.LauncherSettings
import com.newlauncher.feature.appdrawer.AppDrawerScreen
import com.newlauncher.feature.appdrawer.AppDrawerViewModel
import com.newlauncher.feature.controlcenter.ControlCenter
import com.newlauncher.feature.controlcenter.ControlCenterViewModel
import com.newlauncher.feature.customization.CustomizationScreen
import com.newlauncher.feature.customization.CustomizationViewModel
import com.newlauncher.feature.dynamicisland.DynamicIsland
import com.newlauncher.feature.dynamicisland.DynamicIslandViewModel
import com.newlauncher.feature.home.HomeScreen
import com.newlauncher.feature.home.HomeViewModel
import com.newlauncher.feature.widgets.WidgetsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * The launcher's single activity.
 *
 * Wires the [AppContainer] to the UI: starts the adaptive [QualityManager],
 * binds the glass engine to the user's settings, and composes the home screen
 * with the Dynamic Island, Control Center, app drawer and customization
 * overlays. It is exported and declares the `HOME` intent filter so the system
 * can select NEW-LAUNCHER as the default launcher.
 */
class LauncherActivity : ComponentActivity() {

    private val container by lazy { (application as NewLauncherApp).container }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val wallpaperPicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                setWallpaperFromUri(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val homeVm: HomeViewModel = viewModel()
            val drawerVm: AppDrawerViewModel = viewModel()
            val islandVm: DynamicIslandViewModel = viewModel()
            val controlVm: ControlCenterViewModel = viewModel()
            val customVm: CustomizationViewModel = viewModel()

            LauncherRoot(
                container = container,
                homeVm = homeVm,
                drawerVm = drawerVm,
                islandVm = islandVm,
                controlVm = controlVm,
                customVm = customVm,
                onPickWallpaper = { wallpaperPicker.launch(null) },
            )
        }

        container.qualityManager.start(scope)
        container.widgetController.start()
    }

    override fun onDestroy() {
        container.qualityManager.stop()
        container.widgetController.stop()
        scope.cancel()
        super.onDestroy()
    }

    private fun setWallpaperFromUri(uri: android.net.Uri) {
        val context = applicationContext
        runCatching {
            val bitmap: Bitmap? = context.contentResolver.openInputStream(uri)?.use { input ->
                Bitmap.Factory.decodeStream(input)
            }
            if (bitmap != null) {
                WallpaperManager.getInstance(context).setBitmap(bitmap, null, true)
            }
            container.repository.updateSettings { it.copy(wallpaperUri = uri.toString()) }
        }.onFailure { Log.e(Log.tag("Launcher"), "Failed to set wallpaper", it) }
    }
}

/**
 * Root composition: theme + glass engine + overlay orchestration.
 */
@Composable
private fun LauncherRoot(
    container: AppContainer,
    homeVm: HomeViewModel,
    drawerVm: AppDrawerViewModel,
    islandVm: DynamicIslandViewModel,
    controlVm: ControlCenterViewModel,
    customVm: CustomizationViewModel,
    onPickWallpaper: () -> Unit,
) {
    val settings by container.repository.settings.collectAsState()

    // Bind the glass engine to the user's settings whenever they change.
    LaunchedEffect(settings) {
        container.glassEngine.setMaterial(glassMaterialFromSettings(settings))
        container.glassEngine.setProfile(settings.graphicsProfile)
    }

    NewLauncherTheme {
        LocalGlassEngine provides container.glassEngine {
            var showDrawer by remember { mutableStateOf(false) }
            var showControlCenter by remember { mutableStateOf(false) }
            var showCustomization by remember { mutableStateOf(false) }
            var showWidgets by remember { mutableStateOf(false) }

            Box(modifier = Modifier.fillMaxSize()) {
                // Home screen.
                HomeScreen(
                    viewModel = homeVm,
                    onOpenAppDrawer = { showDrawer = true },
                )

                // Dynamic Island, floating at the top centre.
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 24.dp),
                ) {
                    DynamicIsland(viewModel = islandVm)
                }

                // Settings entry (top-right).
                IconButton(
                    onClick = { showCustomization = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 20.dp, end = 56.dp),
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                }

                // Widgets entry (top-right, beside settings).
                IconButton(
                    onClick = { showWidgets = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 20.dp, end = 16.dp),
                ) {
                    Icon(Icons.Filled.Apps, contentDescription = "Widgets", tint = Color.White)
                }

                // Control Center toggle (bottom-right).
                IconButton(
                    onClick = { showControlCenter = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 96.dp),
                ) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Control Center",
                        tint = Color.White,
                    )
                }

                // App drawer overlay.
                if (showDrawer) {
                    Overlay(onDismiss = { showDrawer = false }) {
                        AppDrawerScreen(viewModel = drawerVm, onBack = { showDrawer = false })
                    }
                }

                // Control Center overlay.
                if (showControlCenter) {
                    Overlay(onDismiss = { showControlCenter = false }) {
                        ControlCenter(viewModel = controlVm)
                    }
                }

                // Customization overlay.
                if (showCustomization) {
                    Overlay(onDismiss = { showCustomization = false }) {
                        CustomizationScreen(
                            viewModel = customVm,
                            onPickWallpaper = onPickWallpaper,
                        )
                    }
                }

                // Widgets overlay.
                if (showWidgets) {
                    Overlay(onDismiss = { showWidgets = false }) {
                        WidgetsScreen(
                            controller = container.widgetController,
                            widgetManager = container.widgetManager,
                        )
                    }
                }
            }
        }
    }
}

/**
 * A full-screen overlay with a translucent backdrop that dismisses on tap.
 */
@Composable
private fun Overlay(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onDismiss)
            .background(Color.Black.copy(alpha = 0.4f)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            content()
        }
    }
}

/**
 * Build a [GlassMaterialParameters] from the user's [LauncherSettings],
 * applying the global intensity / transparency multipliers to the chosen
 * material preset.
 */
fun glassMaterialFromSettings(settings: LauncherSettings): GlassMaterialParameters {
    val preset = GlassMaterialPresets.of(settings.glassMaterial)
    return preset.copy(
        reflectionIntensity = (preset.reflectionIntensity * settings.glassIntensity * settings.reflectionStrength)
            .coerceIn(0f, 1f),
        refractionStrength = (preset.refractionStrength * settings.glassIntensity * settings.refractionStrength)
            .coerceIn(0f, 1f),
        transparency = (preset.transparency * (1f - settings.transparency * 0.5f)).coerceIn(0f, 1f),
        specularStrength = (preset.specularStrength * settings.glassIntensity).coerceIn(0f, 1f),
    ).let { GlassMaterialParameters.coerceInRange(it) }
}

package com.newlauncher.app

import android.content.Context
import com.newlauncher.core.glassengine.GlassEngineController
import com.newlauncher.core.performance.QualityManager
import com.newlauncher.core.storage.LauncherRepository
import com.newlauncher.core.storage.launcherDataStore
import com.newlauncher.feature.widgets.WidgetHostController
import com.newlauncher.feature.widgets.WidgetManager

/**
 * Application-level container that owns the shared, singleton services:
 *
 *  - [repository]  — reactive settings + home-layout persistence
 *  - [glassEngine] — the Physical Glass Rendering Engine controller
 *  - [qualityManager] — adaptive graphics profile controller
 *
 * ViewModels and the UI read these so there is exactly one engine, one
 * quality controller and one persistence layer for the whole process.
 */
class AppContainer(context: Context) {

    val repository: LauncherRepository = LauncherRepository(launcherDataStore(context))

    val glassEngine: GlassEngineController = GlassEngineController()

    val qualityManager: QualityManager = QualityManager(context) {
        // Read the user's chosen profile from the persisted settings each sample.
        repository.settings.value.graphicsProfile
    }

    val widgetManager: WidgetManager = WidgetManager(context)

    val widgetController: WidgetHostController =
        WidgetHostController(context, widgetManager)
}

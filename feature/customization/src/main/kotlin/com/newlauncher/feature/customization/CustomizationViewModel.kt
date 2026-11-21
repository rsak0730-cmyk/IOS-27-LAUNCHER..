package com.newlauncher.feature.customization

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.newlauncher.core.storage.LauncherRepository
import com.newlauncher.core.storage.LauncherSettings
import com.newlauncher.core.storage.launcherDataStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the customization screen.
 *
 * Reads and writes [LauncherSettings] through the [LauncherRepository], so
 * every change persists across restarts. All updates are clamped to valid
 * ranges by the repository.
 */
class CustomizationViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = LauncherRepository(launcherDataStore(app))

    /** Current settings (reactive). */
    val settings: StateFlow<LauncherSettings> = repository.settings

    init {
        viewModelScope.launch { repository.initialize() }
    }

    /** Apply a transform to the settings and persist. */
    fun update(transform: (LauncherSettings) -> LauncherSettings) {
        viewModelScope.launch { repository.updateSettings(transform) }
    }

    /** Restore default settings. */
    fun resetToDefaults() {
        viewModelScope.launch { repository.updateSettings { LauncherSettings.DEFAULT } }
    }
}

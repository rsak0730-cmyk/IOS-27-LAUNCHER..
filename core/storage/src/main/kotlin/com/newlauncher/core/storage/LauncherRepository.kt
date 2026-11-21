package com.newlauncher.core.storage

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

/**
 * Reactive repository over [LauncherDataStore].
 *
 * Exposes the current [LauncherSettings] and [HomeLayout] as [StateFlow]s so
 * Compose can collect them and react to changes. All writes go through
 * suspend functions that persist immediately and update the in-memory flow,
 * so the UI stays consistent after a restart.
 */
class LauncherRepository(private val dataStore: LauncherDataStore) {

    private val _settings = MutableStateFlow(LauncherSettings.DEFAULT)
    val settings: StateFlow<LauncherSettings> = _settings.asStateFlow()

    private val _layout = MutableStateFlow(HomeLayout.EMPTY)
    val layout: StateFlow<HomeLayout> = _layout.asStateFlow()

    /** Load persisted values into the flows (called once at startup). */
    suspend fun initialize() {
        _settings.value = dataStore.readSettings()
        _layout.value = dataStore.readLayout()
    }

    /** Update settings via a transform and persist. */
    suspend fun updateSettings(transform: (LauncherSettings) -> LauncherSettings) {
        val next = LauncherSettings.clamp(transform(_settings.value))
        _settings.value = next
        dataStore.saveSettings(next)
    }

    /** Replace and persist the home layout. */
    suspend fun saveLayout(layout: HomeLayout) {
        _layout.value = layout
        dataStore.saveLayout(layout)
    }

    /** Read the current settings (suspending, for non-reactive call sites). */
    suspend fun currentSettings(): LauncherSettings = _settings.value

    /** Read the current layout (suspending). */
    suspend fun currentLayout(): HomeLayout = _layout.value

    /** Convenience: the persisted layout as a one-shot read. */
    suspend fun persistedLayout(): HomeLayout = dataStore.layoutFlow().first()
}

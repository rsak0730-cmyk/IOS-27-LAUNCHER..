package com.newlauncher.core.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Single DataStore instance for the launcher. All preferences (settings +
 * serialized home layout) live here, scoped to the launcher's own file so no
 * other app can read them.
 */
private val Context.launcherDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "new_launcher_prefs",
)

/** Keys used within the DataStore. */
object LauncherDataStoreKeys {
    val SETTINGS_JSON = stringPreferencesKey("settings_json")
    val LAYOUT_JSON = stringPreferencesKey("layout_json")
}

/**
 * Typed accessors over the raw [Preferences] DataStore.
 */
class LauncherDataStore(private val dataStore: DataStore<Preferences>) {

    /** Read the persisted [LauncherSettings], falling back to defaults. */
    fun settingsFlow(): Flow<LauncherSettings> = dataStore.data.map { prefs ->
        val raw = prefs[LauncherDataStoreKeys.SETTINGS_JSON]
        if (raw.isNullOrBlank()) {
            LauncherSettings.DEFAULT
        } else {
            runCatching { Json.decodeFromString<LauncherSettings>(raw) }
                .getOrDefault(LauncherSettings.DEFAULT)
        }
    }

    /** Read the persisted [HomeLayout], falling back to empty. */
    fun layoutFlow(): Flow<HomeLayout> = dataStore.data.map { prefs ->
        val raw = prefs[LauncherDataStoreKeys.LAYOUT_JSON]
        if (raw.isNullOrBlank()) {
            HomeLayout.EMPTY
        } else {
            runCatching { Json.decodeFromString<HomeLayout>(raw) }
                .getOrDefault(HomeLayout.EMPTY)
        }
    }

    /** Persist a [LauncherSettings] (clamped to valid ranges). */
    suspend fun saveSettings(settings: LauncherSettings) {
        val clamped = LauncherSettings.clamp(settings)
        dataStore.edit { it[LauncherDataStoreKeys.SETTINGS_JSON] = Json.encodeToString(clamped) }
    }

    /** Persist a [HomeLayout]. */
    suspend fun saveLayout(layout: HomeLayout) {
        dataStore.edit { it[LauncherDataStoreKeys.LAYOUT_JSON] = Json.encodeToString(layout) }
    }

    /** Read the current settings once. */
    suspend fun readSettings(): LauncherSettings = settingsFlow().first()

    /** Read the current layout once. */
    suspend fun readLayout(): HomeLayout = layoutFlow().first()
}

/** Factory that builds a [LauncherDataStore] from a [Context]. */
fun launcherDataStore(context: Context): LauncherDataStore =
    LauncherDataStore(context.launcherDataStore)

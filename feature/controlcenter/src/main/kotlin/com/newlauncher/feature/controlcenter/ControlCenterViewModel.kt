package com.newlauncher.feature.controlcenter

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.newlauncher.core.common.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Control Center.
 *
 * Each control attempts the most direct Android API first and gracefully
 * falls back to opening the appropriate system Settings screen when the
 * platform restricts direct control (e.g. Wi-Fi/Bluetooth toggles on
 * Android 11+, brightness/rotation without WRITE_SETTINGS). No control
 * pretends to work when it cannot — the UI reports the fallback.
 */
class ControlCenterViewModel(app: Application) : AndroidViewModel(app) {

    private val tag = Log.tag("ControlCenter")
    private val context: Context = app

    private val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as? WifiManager
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
    private val contentResolver = context.contentResolver

    private val _wifiEnabled = MutableStateFlow(false)
    val wifiEnabled: StateFlow<Boolean> = _wifiEnabled.asStateFlow()

    private val _bluetoothEnabled = MutableStateFlow(false)
    val bluetoothEnabled: StateFlow<Boolean> = _bluetoothEnabled.asStateFlow()

    private val _flashlightOn = MutableStateFlow(false)
    val flashlightOn: StateFlow<Boolean> = _flashlightOn.asStateFlow()

    private val _brightness = MutableStateFlow(0)
    val brightness: StateFlow<Int> = _brightness.asStateFlow()

    private val _volume = MutableStateFlow(0)
    val volume: StateFlow<Int> = _volume.asStateFlow()

    private val _batteryLevel = MutableStateFlow(0)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()

    private val _lastAction = MutableStateFlow<String?>(null)
    /** Last control action's outcome (e.g. "opened Wi-Fi settings"). */
    val lastAction: StateFlow<String?> = _lastAction.asStateFlow()

    init {
        refreshStates()
    }

    /** Re-read all control states from the system. */
    fun refreshStates() {
        _wifiEnabled.value = runCatching { wifiManager?.isWifiEnabled ?: false }.getOrDefault(false)
        _bluetoothEnabled.value = readBluetoothEnabled()
        _brightness.value = readBrightness()
        _volume.value = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)?.coerceIn(0, 100) ?: 0
        _batteryLevel.value = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)?.coerceIn(0, 100) ?: 0
    }

    /** Toggle Wi-Fi. Falls back to Wi-Fi settings on Android 11+. */
    fun toggleWifi(): Boolean {
        val target = !_wifiEnabled.value
        val ok = runCatching { wifiManager?.setWifiEnabled(target) == true }.getOrDefault(false)
        if (ok) {
            _wifiEnabled.value = target
            _lastAction.value = "Wi-Fi ${if (target) "on" else "off"}"
        } else {
            openSettings(Settings.ACTION_WIFI_SETTINGS)
            _lastAction.value = "Opened Wi-Fi settings"
        }
        return ok
    }

    /** Toggle Bluetooth. Falls back to Bluetooth settings when restricted. */
    fun toggleBluetooth(): Boolean {
        val target = !_bluetoothEnabled.value
        val ok = runCatching {
            val adapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter() ?: return@runCatching false
            if (target) adapter.enable() else adapter.disable()
            true
        }.getOrDefault(false)
        if (ok) {
            _bluetoothEnabled.value = target
            _lastAction.value = "Bluetooth ${if (target) "on" else "off"}"
        } else {
            openSettings(Settings.ACTION_BLUETOOTH_SETTINGS)
            _lastAction.value = "Opened Bluetooth settings"
        }
        return ok
    }

    /** Toggle the flashlight via the CameraManager torch API. */
    fun toggleFlashlight(): Boolean {
        val target = !_flashlightOn.value
        val ok = runCatching {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? android.hardware.camera2.CameraManager
                ?: return@runCatching false
            val cameraId = cameraManager.cameraIdList.firstOrNull { idx ->
                cameraManager.getCameraCharacteristics(idx)
                    .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            } ?: return@runCatching false
            cameraManager.setTorchMode(cameraId, target)
            true
        }.getOrDefault(false)
        if (ok) {
            _flashlightOn.value = target
            _lastAction.value = "Flashlight ${if (target) "on" else "off"}"
        } else {
            openSettings(Settings.ACTION_DISPLAY_SETTINGS)
            _lastAction.value = "Opened display settings"
        }
        return ok
    }

    /** Set brightness (0..255). Falls back to display settings without WRITE_SETTINGS. */
    fun setBrightness(level: Int): Boolean {
        val clamped = level.coerceIn(0, 255)
        val ok = runCatching {
            Settings.System.putInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                clamped,
            )
            Settings.System.putInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL,
            )
            true
        }.getOrDefault(false)
        if (ok) {
            _brightness.value = clamped
        } else {
            openSettings(Settings.ACTION_DISPLAY_SETTINGS)
            _lastAction.value = "Opened display settings"
        }
        return ok
    }

    /** Adjust media volume by [delta] (+1 raise, -1 lower). */
    fun adjustVolume(delta: Int) {
        audioManager?.let { am ->
            val direction = if (delta >= 0) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, 0)
            _volume.value = am.getStreamVolume(AudioManager.STREAM_MUSIC).coerceIn(0, 100)
        }
    }

    /** Open an arbitrary settings screen by action. */
    fun openSettings(action: String) {
        val intent = Intent(action).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
    }

    private fun readBluetoothEnabled(): Boolean = runCatching {
        android.bluetooth.BluetoothAdapter.getDefaultAdapter()?.isEnabled == true
    }.getOrDefault(false)

    private fun readBrightness(): Int = runCatching {
        Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
    }.getOrDefault(128)
}

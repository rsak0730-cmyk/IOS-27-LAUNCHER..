# Security & Privacy

NEW-LAUNCHER is built privacy-conscious by design: **minimum necessary permissions, no
unnecessary data collection, no hidden background activity, and local-only preferences.**

## Permissions

Every permission declared in `app/src/main/AndroidManifest.xml` is listed here with its
purpose and why it is needed.

| Permission | Purpose | Notes |
|------------|---------|-------|
| `CAMERA` | Control Center flashlight toggle | Used only to drive `CameraManager.setTorchMode`. The camera is never used for imaging or capture. |
| `ACCESS_WIFI_STATE` | Read Wi-Fi status for the Control Center tile | Read-only. |
| `CHANGE_WIFI_STATE` | Toggle Wi-Fi where the platform allows | On Android 11+ direct toggle is restricted; the app falls back to opening Wi-Fi Settings. |
| `BLUETOOTH`, `BLUETOOTH_ADMIN` | Toggle Bluetooth where allowed | Falls back to Bluetooth Settings when restricted. |
| `BLUETOOTH_CONNECT`, `BLUETOOTH_SCAN` | Bluetooth state on Android 12+ | Required for Bluetooth state access on Android 12+. |
| `MODIFY_AUDIO_SETTINGS` | Adjust media volume in the Control Center | Standard media volume control. |
| `WRITE_SETTINGS` | Set brightness / rotation | **Special (signature-level) permission.** The user grants it via system Settings. When not granted, the Control Center opens the relevant Settings screen instead of pretending to control it. |

## Notification access (opt-in)

The Dynamic Island can surface incoming notifications via `IslandNotificationListener`
(`NotificationListenerService`). This is **entirely opt-in**:

- The service is only invoked after the user explicitly grants notification-listener
  access from **Settings → Apps → Special access → Notification access**.
- Without the grant, the service never runs and no notifications are read.
- Notifications are displayed on-device only and are never uploaded or transmitted.

## Package visibility

App discovery uses `PackageManager.queryIntentActivities` with an `ACTION_MAIN` +
`CATEGORY_LAUNCHER` intent. The manifest's `<queries>` element grants visibility of all
launchable apps **without** a blanket `QUERY_ALL_PACKAGES` declaration, so the launcher
can list and launch installed applications while respecting Android's package-visibility
model.

## Data & storage

- **Local preferences only.** Settings and the home layout are stored in a private
  DataStore scoped to the launcher (`new_launcher_prefs`). Nothing leaves the device.
- **No analytics, no telemetry, no tracking.** There are no analytics SDKs or
  advertising dependencies.
- **No hardcoded API keys or credentials.** The project contains no secrets.
- **No background data upload.** No personal information is uploaded without consent
  (there is no upload path at all).
- `allowBackup="false"` disables Android Auto Backup of launcher data.

## Background execution

The launcher does not run persistent background services. The only background component
is the opt-in notification listener, which the system invokes only when the user grants
access. The `QualityManager` and `PerformanceProfiler` run lightweight frame/sensor
sampling only while the launcher is foregrounded and stop when it is destroyed.

## Secure coding practices

- All file/network-sensitive operations are wrapped in `runCatching` and degrade
  gracefully.
- No credentials are stored or exposed.
- Preference writes are clamped to valid ranges before persistence.

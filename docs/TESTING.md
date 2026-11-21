# Testing

NEW-LAUNCHER uses a layered testing strategy: **pure unit tests** for logic that can be
verified without a device, and **instrumented/UI tests** (configured in the app module)
that run on-device or in CI.

## Running tests

```bash
# Unit tests (core modules + app)
./gradlew :app:testDebugUnitTest :core:test

# Instrumented tests (requires a device/emulator)
./gradlew :app:connectedDebugAndroidTest

# Lint
./gradlew :app:lintDebug
```

## Unit tests (pure JVM)

These run on the JVM without an Android device and cover the core logic:

| Module | Test | Covers |
|--------|------|--------|
| `core:graphics` | `ColorUtilsTest` | Color lerp/compositing, Schlick fresnel, soft-clamp, alpha |
| `core:designsystem` | `GlassMaterialTest` | Five material presets, parameter clamping, material properties |
| `core:glassengine` | `GlassRenderPlanTest` | Render-plan resolution across profiles/capabilities |
| `core:performance` | `PerformancePolicyTest` | Adaptive profile decisions (thermal, frame, memory, battery) |
| `core:storage` | `LauncherSettingsTest` / `HomeLayoutTest` | Settings clamping, layout JSON round-trip |
| `core:common` | `AppSorterTest` | App sorting, filtering, sectioning |
| `app` | `GlassMaterialSettingsTest` | Settings → glass-material mapping and clamping |

## Instrumented / UI tests

The app module is configured with Compose UI test and Espresso dependencies
(`androidTestImplementation`). Instrumented tests are intended to verify, on a real
device or emulator:

1. The launcher **installs** and **starts**.
2. NEW-LAUNCHER appears as a selectable **default home** application.
3. It **displays installed applications** (AppProvider discovery).
4. It **launches applications** (real intents).
5. It **saves** a home-screen arrangement and **restores** it after restart.
6. It **displays widgets** and preserves widget functionality.
7. It handles **orientation / configuration changes**.
8. It **recovers gracefully** from errors (permission denials, missing providers).

> **Note:** The instrumented tests are authored and wired for the CI/device workflow but
> have **not yet been executed on a device** in the authoring environment. See
> [`KNOWN-LIMITATIONS.md`](KNOWN-LIMITATIONS.md).

## CI

The GitHub Actions workflow (`.github/workflows/build.yml`) runs on push and pull
request:

1. Sets up JDK 17 and the Android SDK (platform 35, build-tools 35.0.0).
2. Builds the debug APK (`:app:assembleDebug`).
3. Runs unit tests (`:app:testDebugUnitTest`, `:core:test`).
4. Runs lint (`:app:lintDebug`).
5. Uploads the **debug APK** and **test reports** as artifacts.

## Performance benchmarks

The `PerformanceProfiler` exposes frame-time statistics (average, p95) at runtime.
Frame-time targets are **not claimed without measurement**; the profiler is the
measurement mechanism. No FPS guarantee is made without real device data.

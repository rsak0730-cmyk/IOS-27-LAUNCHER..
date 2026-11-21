# NEW-LAUNCHER 2.0

**An advanced Android launcher featuring photorealistic glass physics, liquid glass
interactions, a premium iPhone-inspired UI and advanced Android customization.**

NEW-LAUNCHER is an original, premium Android home-screen replacement that combines an
iPhone-inspired interaction model with Android's customization flexibility. Its visual
identity is built on a dedicated **Physical Glass Rendering Engine** that simulates
refraction, reflections, fresnel edge highlights, transparency and liquid deformation —
not ordinary backdrop blur.

> This is an original Android experience. It does **not** copy Apple's proprietary
> Dynamic Island / Live Activities infrastructure; the Dynamic Island here is an original
> Android interpretation built on MediaSession, notifications and foreground-service APIs.

---

## Highlights

- **Physical Glass Rendering Engine** — AGSL shaders for refraction, specular
  reflections, schlick fresnel, chromatic dispersion and liquid deformation, with
  graceful fallbacks on older devices.
- **Five reusable glass materials** — Clear, Frosted, Liquid, Crystal and Hybrid —
  each with configurable parameters.
- **iPhone-inspired home screen** — paged app grid, customizable dock, page indicators,
  drag-to-rearrange, smooth icon animations.
- **App drawer** — alphabetical, sectioned, searchable library of real installed apps
  (no placeholder icons, real launch intents).
- **Dynamic Island-inspired bubble** — expandable/collapsible glass surface with music
  (MediaSession), timer and notification activities.
- **Control Center** — Wi-Fi, Bluetooth, flashlight, brightness, volume, battery,
  rotation and settings, using direct APIs where allowed and system Settings otherwise.
- **Real AppWidget support** — place, resize, manage and host live Android widgets.
- **Customization** — wallpaper, grid, dock, glass intensity, reflection/refraction,
  transparency, animation speed, layout and widget placement, all persisted.
- **Adaptive performance** — ULTRA / BALANCED / PERFORMANCE graphics profiles with
  frame-time, memory, thermal and battery-aware adaptation.
- **Privacy-first** — minimal permissions, no analytics, no background data collection,
  local-only preferences.

## Target devices

Primary testing targets:

1. **Samsung Galaxy A14 5G** (Android 13)
2. **OPPO K14x 5G** (Android 13+)

`minSdk = 26` (Android 8.0), `targetSdk = 35` (Android 15), `compileSdk = 35`.

## Tech stack

| Concern         | Choice |
|-----------------|--------|
| Language        | Kotlin 2.0.21 |
| UI              | Jetpack Compose (BOM 2024.10.01), Material 3 |
| Build           | Gradle 8.9, AGP 8.7.2 |
| Graphics        | Compose `GraphicsLayer`, `RenderEffect`, AGSL `RuntimeShader` |
| Persistence     | Jetpack DataStore (Preferences) + kotlinx.serialization |
| Toolchain       | Java 17 |
| CI              | GitHub Actions (build, test, lint, APK artifact) |

## Project structure

```
new-launcher-android/
├── app/                       # Application module: manifest, activity, wiring
├── core/
│   ├── common/                # Constants, app discovery (AppProvider), utilities
│   ├── designsystem/          # Three-layer design tokens + Compose theme + AppIcon
│   ├── glassengine/           # Physical Glass Rendering Engine (shaders, materials, GlassSurface)
│   ├── graphics/              # Color math + AGSL shader bridge (RuntimeShader/RenderEffect)
│   ├── performance/           # GraphicsProfile, frame profiler, adaptive QualityManager
│   └── storage/               # LauncherSettings, HomeLayout, DataStore, repository
├── feature/
│   ├── home/                  # Home screen (pager, grid, dock, rearrangement)
│   ├── appdrawer/             # App library
│   ├── dynamicisland/         # Dynamic Island-inspired bubble
│   ├── controlcenter/         # Quick-access controls
│   ├── widgets/               # AppWidget hosting
│   └── customization/         # Settings screen
├── docs/                      # Architecture, roadmap, security, testing, limitations
└── .github/workflows/         # CI
```

## Build

### Prerequisites

- JDK 17
- Android SDK with **platform 35** and **build-tools 35.0.0**
- (Recommended) Android Studio Ladybug or newer

### Build the debug APK

```bash
./gradlew :app:assembleDebug
```

The APK is written to:

```
app/build/outputs/apk/debug/app-debug.apk
```

### Run unit tests

```bash
./gradlew :app:testDebugUnitTest :core:test
```

### Run lint

```bash
./gradlew :app:lintDebug
```

## Install & set as default launcher

1. Install the APK:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```
2. Open the system **Settings → Apps → Default apps → Home app** (or the
   "Choose default app" dialog) and select **NEW-LAUNCHER**.
3. NEW-LAUNCHER is selectable as the default home application because its launcher
   activity declares `ACTION_MAIN` + `CATEGORY_HOME` + `CATEGORY_DEFAULT`.

## The Physical Glass Rendering Engine

The glass engine is the core of NEW-LAUNCHER. It is a **simulated physical-optics**
renderer — it is not ray-traced. The design goal is convincing visual results with
efficient GPU usage and broad Android compatibility.

The engine resolves a **render plan** from the device capabilities, the active
`GraphicsProfile` and the chosen glass material, then renders the surface using the
best available strategy:

- **AGSL RuntimeShader** (Android 12+, API 31+) — a fragment shader implementing
  Schlick fresnel edge glow, a moving specular sheen, lens-like internal refraction,
  chromatic dispersion and liquid deformation.
- **RenderEffect blur** — hardware backdrop-style blur for frosted/liquid materials.
- **Compose brush fallback** — translucent tint + gradient highlights for devices
  without shader support, so the visual identity is preserved everywhere.

The five materials are presets of `GlassMaterialParameters` (transparency, blur,
refraction, reflection, fresnel, specular, dispersion, tint, ambient, liquid
deformation). Every parameter is clamped to a physically sensible range.

See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for the full design.

## Performance

Three graphics profiles are exposed in **Settings → Performance**:

- **ULTRA** — maximum glass effects, reflections, dispersion and shaders.
- **BALANCED** — realistic glass effects with optimised rendering.
- **PERFORMANCE** — reduced workload that preserves the visual identity.

The `QualityManager` adapts the *effective* profile from frame time (Choreographer),
available memory, thermal status and battery. The user's chosen profile is treated as
an upper bound — the system only ever steps *down* under pressure, and never
aggressively polls sensors.

## Permissions

The launcher requests the minimum necessary. Every sensitive permission is documented
in [`docs/SECURITY.md`](docs/SECURITY.md). In summary: `CAMERA` (flashlight),
Wi-Fi/Bluetooth state (Control Center toggles), `MODIFY_AUDIO_SETTINGS` (volume),
and `WRITE_SETTINGS` (brightness/rotation, special permission granted via system
Settings). Notification access for the Dynamic Island is entirely opt-in.

## Documentation

- [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) — module architecture and glass engine design.
- [`docs/ROADMAP.md`](docs/ROADMAP.md) — feature roadmap and stage status.
- [`docs/SECURITY.md`](docs/SECURITY.md) — privacy and permission model.
- [`docs/TESTING.md`](docs/TESTING.md) — testing strategy and how to run tests.
- [`docs/KNOWN-LIMITATIONS.md`](docs/KNOWN-LIMITATIONS.md) — known limitations and honest status.
- [`docs/CONTRIBUTING.md`](docs/CONTRIBUTING.md) — development workflow.

## License

Released under the [MIT License](LICENSE).

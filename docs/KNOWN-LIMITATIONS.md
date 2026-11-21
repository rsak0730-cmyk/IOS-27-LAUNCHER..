# Known Limitations & Honest Status

This document is an honest record of what is implemented, what is partially implemented,
and what could **not** be verified in the authoring environment. Per the project's strict
development rules, nothing is claimed as complete without verification.

## Environment limitations (affecting verification)

The project was authored in a sandboxed environment with two hard constraints. These
affect **verification**, not the code itself.

### 1. GitHub repository creation was not possible

The configured GitHub credential can **list and push to existing repositories** but
**cannot create new repositories**. The `createRepository` mutation fails with
`Resource not accessible by integration`, the REST `POST /repos` returns 404, and the
GitHub connector reports `unsupported`.

**Consequence:** The `new-launcher-android` repository could not be created directly.
The complete project lives in this working branch and is ready to push to a newly
created `new-launcher-android` repository (see [CONTRIBUTING.md](CONTRIBUTING.md) for
the one-line push command). **Repository creation has not been verified as successful.**

### 2. Android build toolchain was unavailable and could not be installed

The sandbox has **no JDK, Gradle, or Android SDK**, and the network is restricted to
`github.com`, `codeload.github.com`, `api.github.com`, `registry.npmjs.org`,
`pypi.org`, and `files.pythonhosted.org`. The Android toolchain hosts
(`dl.google.com`, `services.gradle.org`, Maven repos) and JDK release assets were
**unreachable**, and `apt` was blocked.

**Consequence:** The project **could not be compiled, built, or unit-tested locally**.
This means:

- Compilation errors, if any, have **not been caught by a local build**.
- The unit tests have **not been executed locally**.
- The debug APK **has not been produced locally**.
- **No device testing** was performed on the Galaxy A14 5G or OPPO K14x 5G.

The GitHub Actions workflow is authored to perform the real build, test, and lint on
GitHub's runners (which have the Android toolchain), producing the APK artifact.

**CI verification status (updated 2026-11-21):** The `Build & Test` workflow
(`.github/workflows/build.yml`) has been run successfully via `workflow_dispatch`.
Run `44933942956` (commit `d2bc39f`) completed with **conclusion: success**. The
`:app:assembleDebug` task succeeded, the debug APK artifact
(`new-launcher-debug-apk`) was uploaded, and unit tests + lint ran without errors.
Only deprecation warnings remain (Bluetooth `enable`/`disable`, `getDefaultAdapter`,
`setWifiEnabled`, `notifyAppWidgetViewDataChanged`, `AppWidgetProviderInfo.label`) —
all non-fatal. **The build is verified as green.**

## Feature status

### Implemented (code complete)

- **Physical Glass Rendering Engine** — AGSL shaders, five materials, render-plan
  resolver, `GlassSurface`/`GlassCard`/`GlassPanel`, blur + fallback strategies.
- **Design system** — three-layer tokens, theme, typography, AppIcon.
- **Home screen** — pager, grid, dock, page indicators, drag-to-rearrange, persistence.
- **App drawer** — discovery, sectioned list, search, real launch.
- **Dynamic Island** — state/controller/ViewModel, MediaSession + timer, animated bubble.
- **Control Center** — Wi-Fi, Bluetooth, flashlight, brightness, volume, battery,
  rotation, settings (with Settings fallbacks).
- **Widgets** — discovery, allocation, binding, hosting, resize, management.
- **Customization** — all settings persisted.
- **Performance** — three profiles, frame profiler, pure policy, adaptive manager.
- **Security** — minimal permissions, local storage, opt-in notification access.
- **CI workflow** — build, test, lint, APK artifact.

### Partially implemented / refinements

- **Widget grid placement** — widgets render in a managed list with resize, not at
  precise grid coordinates on the home screen.
- **Backdrop-sampled refraction** — the glass shader is procedural (fresnel/specular/
  dispersion); it does not displace a captured background texture. This is by design
  for efficiency and is a documented roadmap item.
- **Backdrop blur** — `RenderEffect` blur blurs the glass surface's own content; true
  "blur everything behind the glass" backdrop blur is limited by Compose's rendering
  model and is a known refinement area.

### Not verified (requires device)

- On-device behavior: install, set-as-default-home, app launch, layout restore, widget
  display, orientation changes, error recovery (device testing pending).
- Actual frame rates / GPU workload (no measurement performed; no FPS claimed).

## What is explicitly NOT claimed

- No claim that device testing was completed (no device access).
- No claim of a specific FPS or performance figure.
- No claim of Apple Live Activities access (the Dynamic Island is an original Android
  interpretation).
- No claim of controlling restricted system settings (the Control Center opens Settings
  where direct control is unavailable).

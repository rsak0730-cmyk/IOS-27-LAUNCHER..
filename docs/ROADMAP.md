# Roadmap

NEW-LAUNCHER is developed in controlled stages. Each stage is committed after the work
is in place. The status below reflects the **code** state; see
[`KNOWN-LIMITATIONS.md`](KNOWN-LIMITATIONS.md) for the honest verification status
(build/device testing could not be executed in the authoring environment).

## Stages

### Stage 1 — Repository & architecture ✅
- Gradle wrapper, version catalog, multi-module settings.
- Version stack: AGP 8.7.2, Kotlin 2.0.21, Compose BOM 2024.10.01, compileSdk 35,
  minSdk 26.
- Six core modules established.

### Stage 2 — Physical Glass Engine ✅
- AGSL shaders (refraction, fresnel, specular, chromatic dispersion, liquid deformation).
- `GlassRenderPlan` resolver, `GlassShaderBrush`, `GlassEngineController`.
- `GlassSurface` / `GlassCard` / `GlassPanel` components with blur + fallback strategies.
- Five glass materials with configurable parameters.

### Stage 3 — Home Screen ✅
- `HomeViewModel` (layout persistence, rearrangement operations).
- `HomeScreen`: `HorizontalPager`, drag-to-swap grid, glass dock, page indicators,
  edit mode.

### Stage 4 — App Drawer & app management ✅
- `AppDrawerViewModel` + sectioned alphabetical drawer with glass search.
- Real app discovery (`AppProvider`) and launch.

### Stage 5 — Dynamic Island-inspired interactions ✅
- `IslandState`, shared `DynamicIslandController`, `DynamicIslandViewModel`
  (MediaSession listener + timer).
- Animated expand/collapse glass bubble.

### Stage 6 — Control Center & customization ✅
- `ControlCenterViewModel` with real controls + Settings fallbacks.
- `CustomizationViewModel` + full settings screen (all tokens persisted).

### Stage 7 — Android widgets ✅
- `WidgetManager` (discovery), `WidgetHostController` (allocate/bind/host/resize),
  `WidgetHostView` (hosts real `AppWidgetHostView`), `WidgetPicker`, `WidgetsScreen`.

### Stage 8 — Performance optimization & device testing ⚠️ partially
- `GraphicsProfile` (ULTRA/BALANCED/PERFORMANCE), `PerformanceProfiler`,
  `PerformancePolicy` (unit-tested), `QualityManager`.
- **Device testing on Galaxy A14 5G / OPPO K14x 5G: not yet performed** (see limitations).

### Stage 9 — Security & stability review ✅ (code)
- Minimal permissions, local-only storage, no analytics, opt-in notification access.
- See [`SECURITY.md`](SECURITY.md).

### Stage 10 — Automated APK build ⚠️ pending CI run
- GitHub Actions workflow defined (build, test, lint, APK artifact).
- **The workflow has not yet been run on GitHub** (see limitations).

## Future work

- **True backdrop-sampled refraction** — capture the region behind a glass surface and
  displace it in the shader (currently the shader is procedural).
- **Wallpaper engine** — animated/live wallpapers and deeper wallpaper integration.
- **Icon themes / custom icon packs** — system-level icon override.
- **Gesture navigation** — swipe-up app switcher, multi-finger shortcuts.
- **Widget grid placement** — position widgets precisely on the home-screen grid
  (currently widgets render in a managed list with resize).
- **More Dynamic Island activities** — calendar, weather, fitness (where data sources
  are available on-device).
- **Per-app glass overrides** — per-app material presets.

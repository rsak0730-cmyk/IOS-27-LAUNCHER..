# AGENTS.md

Instructions for AI agents (and humans) working in this repository.

## Project summary

**NEW-LAUNCHER 2.0** is a **native Android home-screen replacement (launcher)** — an
original, premium, iPhone-inspired experience built on Android's customisation
flexibility. It is **not** a copy of Apple's proprietary Dynamic Island / Live Activities
infrastructure; the Dynamic Island here is an original Android interpretation built on
`MediaSession`, notifications and foreground-service APIs.

- **Stack:** Kotlin 2.0.21 · Jetpack Compose (BOM 2024.10.01) · Material 3 · AGP 8.7.2 ·
  Gradle 8.9+ · JDK 17 · minSdk 26 / targetSdk 35
- **Architecture:** modular (`:app` · `:feature:*` · `:core:*`) with MVVM + ViewModels
- **Rendering:** native AGSL `RuntimeShader` + `RenderEffect` (Physical Glass Rendering
  Engine) — no 3D engine
- **Persistence:** local DataStore (Preferences) — offline, no backend, no telemetry
- **Networking:** none

Read [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) before making architectural changes.
See [`TECHNOLOGY_RESOURCE_CATALOG.md`](TECHNOLOGY_RESOURCE_CATALOG.md) for the resource
selection decisions that govern which libraries are used or rejected.

## Guiding principles

1. **Understand before acting.** Read the relevant module(s) and docs first. Do not
   blindly install libraries, copy snippets, or run scripts.
2. **Minimal dependencies.** Prefer standard, official, native Android solutions. Add a
   library only when it solves a real problem the platform does not, and only after
   checking the project's selection rules in `TECHNOLOGY_RESOURCE_CATALOG.md`.
3. **Never claim unverified functionality.** If you did not build and verify it, say so.
   Distinguish "written and compiled" from "compiled and tested" from "tested on device".
   Device/on-device verification is a distinct claim — see `docs/KNOWN-LIMITATIONS.md`.
4. **Security by default.** No API keys, no hardcoded credentials, no telemetry, no
   unverified external dependencies. Treat external/third-party code as untrusted.
5. **Defensive by design.** Wrap platform/external operations in `runCatching`/`try-catch`
   and degrade gracefully. Rendering and composition must never crash the app.

## Module layout & dependency rules

```
:app
 ├── :feature:home          (UI + ViewModel)
 ├── :feature:appdrawer     (UI + ViewModel)
 ├── :feature:dynamicisland (UI + ViewModel + shared controller)
 ├── :feature:controlcenter (UI + ViewModel)
 ├── :feature:widgets       (AppWidget host + UI)
 ├── :feature:customization (UI + ViewModel)
 └── core
      ├── :core:common        (constants, AppProvider, AppSorter, logging)
      ├── :core:designsystem  (tokens, theme, AppIcon)
      ├── :core:glassengine   (Physical Glass Rendering Engine)
      ├── :core:graphics      (color math, AGSL shader bridge)
      ├── :core:performance   (GraphicsProfile, profiler, QualityManager)
      └── :core:storage       (DataStore, settings, layout, repository)
```

- **Feature modules depend only on core modules** — never cross-feature dependencies.
- `:app` orchestrates composition and overlays.
- `:core:glassengine` depends on `:core:graphics`, `:core:designsystem`, `:core:performance`.
- `:core:designsystem` is the single home of design tokens; `:core:glassengine` owns
  rendering. Design and rendering are decoupled.
- Package namespaces: `com.newlauncher.core.*` and `com.newlauncher.feature.*`;
  applicationId `com.newlauncher.android`.

## Architecture rules

- **Three-layer design tokens** in `:core:designsystem`: Primitive → Semantic →
  Component. Glass material parameters live in the token layer so the GPU shader and the
  CPU fallback share one source of truth.
- **Render plan** (`GlassRenderPlan`, unit-tested): `AGSL_SHADER` (API 31+, full
  procedural glass) → `RENDER_EFFECT` blur (API 31+, hardware blur) → `BRUSH_FALLBACK`
  (API < 31, translucent tint). `QualityManager` steps the profile **down** only.
- **App discovery** (`:core:common` `AppProvider`): `PackageManager.queryIntentActivities`
  with `ACTION_MAIN` + `CATEGORY_LAUNCHER`; the manifest's `<queries>` grants visibility
  **without** a blanket `QUERY_ALL_PACKAGES` declaration. Icons/labels always come from
  the system — never placeholders.

## Coding conventions

- **Icon rendering** — the verified, CI-green pattern for rasterising `Drawable` icons in
  Compose (use this; do **not** invent new image-painter APIs):

  ```kotlin
  val pxSize = with(density) { sizeDp.dp.toPx() }.toInt()
  val imageBitmap = remember(drawable, pxSize) {
      try { drawable.toBitmap(width = pxSize, height = pxSize)?.asImageBitmap() }
      catch (_: Exception) { null }
  }
  // non-null → Image(bitmap = imageBitmap, contentScale = ContentScale.Fit)
  // null → Box(...) {}  // Box always needs a content lambda
  ```

  **No** `asPainter`, `asImageBitmap()`-on-null, `ColorPainter`, `Drawable.toPainter()`,
  or passing `dp` as pixel dimensions (causes blur/glitch and NPEs).

- **Correct Compose/Android APIs** (confirmed to compile with the pinned BOM):
  `RuntimeShader(source)` · `ShaderBrush(shader)` ·
  `RenderEffect.createBlurEffect(rx, ry, TileMode.CLAMP)` · `FontWeight.Normal` ·
  `TextFieldDefaults.colors(...)` · `Icons.Filled.*` · `ClosedFloatingPointRange<Float>` ·
  `lifecycle-viewmodel-compose` · `CompositionLocalProvider(LocalGlassEngine provides x)`
  (`provides` is a member infix — no top-level import) · `drawContent`/`drawRect` are
  `DrawScope` members (no import).
- **MediaSession**: `getActiveSessions(ComponentName)` returns `List<MediaController>`
  directly — the element **is** the controller; use `play()`/`pause()` directly (no
  `sessionToken` chain).
- **Widgets**: `AppWidgetProviderInfo.loadIcon(Context, int)`;
  `AppWidgetHost.createView(Context, int, AppWidgetProviderInfo)`;
  `AppWidgetManager.getInstalledProviders()` (there is no `getProviderInfo`);
  `bindAppWidgetIdIfAllowed(int, ComponentName)`; `Intent().apply { component = ... }`.
- **Gestures**: `detectDragGesturesAfterLongPress.onDrag` takes
  `(change: PointerInputChange, dragAmount: Offset)`; apply `.position` on the change.

## Verification & CI

- **Do not attempt local builds** in the authoring sandbox — no JDK/Gradle/Android SDK is
  available. All build/test verification goes through **CI** (`.github/workflows/build.yml`).
- CI runs: `:app:assembleDebug`, unit tests, lint; uploads the debug APK + reports.
  **Do not set `continue-on-error: true`** on verification steps — it masks real failures.
- **Kotlin errors** are emitted as `e: ` lines — grep `^e: ` in the CI log. Surface the
  head (compiler) and tail (task failures) via `::error::` annotations.
- Dispatch the workflow via:
  `POST repos/<owner>/<repo>/actions/workflows/425984931/dispatches` with
  `ref=<branch>` (`gh run dispatch --workflow` is **not** supported).
- Read CI errors from the run/job **annotations** (`::error::`) via the GitHub run page;
  raw-log retrieval via `gh` is unreliable.
- Only report a change as **verified** when a CI run returns `conclusion: success`.

## Security

- **Privacy-first, offline-first.** Local preferences only; no analytics/telemetry; no
  API keys; `allowBackup="false"`.
- **No blind script execution.** Review any script before running; treat external repos
  as untrusted. Respect each resource's licence (see `TECHNOLOGY_RESOURCE_CATALOG.md`).
- **Never request or store credentials** in chat or code.
- All file/network-sensitive operations are wrapped in `runCatching` and degrade
  gracefully; preference writes are clamped to valid ranges.

## Documentation

- `docs/ARCHITECTURE.md` — module graph, glass engine, performance, persistence, wiring.
- `docs/SECURITY.md` — permissions, notification access, package visibility, data.
- `docs/TESTING.md` — testing strategy and what has/has not been run on device.
- `docs/KNOWN-LIMITATIONS.md` — known limitations and verification status.
- `TECHNOLOGY_RESOURCE_CATALOG.md` — resource catalogue + selection verdicts.
- `README.md` — product overview and highlights.

Update the relevant doc when behaviour, architecture or verification status changes.

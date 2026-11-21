# Architecture

NEW-LAUNCHER uses a modern, modular Android architecture with a strict separation
between **UI**, **application logic** and **platform integration**.

## Module graph

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

**Dependency rules**

- Feature modules depend only on core modules (no cross-feature dependencies), so the
  `:app` module orchestrates composition and overlays.
- `:core:glassengine` depends on `:core:graphics`, `:core:designsystem` and
  `:core:performance`.
- `:core:designsystem` is the single home of design tokens; `:core:glassengine` owns
  rendering. Design and rendering are decoupled.

## Design system: three-layer tokens

Following the design-intelligence reference, NEW-LAUNCHER uses a three-layer token
architecture (see `core/designsystem/tokens/`):

1. **Primitive** — raw values (`PrimitiveColors`, `SpacingTokens`, `TypographyTokens`,
   `RadiusTokens`, `ShadowTokens`, `AnimationTokens`, `GlassTokens`).
2. **Semantic** — purpose aliases (`SemanticColors`).
3. **Component** — per-component overrides consumed by UI.

The glass material parameters (`GlassMaterialParameters` + `GlassMaterialPresets`)
live in the design-token layer so the GPU shader and the CPU fallback share one source
of truth.

## Physical Glass Rendering Engine

Located in `:core:glassengine`. Responsibilities:

- **`GlassShaderSources`** — the AGSL fragment shader (fresnel, specular, refraction,
  dispersion, liquid deformation) plus uniform names.
- **`GlassShaderUniforms`** — maps `GlassMaterialParameters` + surface size + time to
  shader uniforms.
- **`GlassShaderBrush`** — compiles the AGSL source via `android.graphics.RuntimeShader`
  and exposes a Compose `ShaderBrush` plus uniform application.
- **`GlassRenderPlan`** — a pure, testable resolver that picks the rendering strategy
  from device capability + profile + material.
- **`GlassEngineController`** — the engine facade; holds the active material and
  profile, provides the plan. Exposed via `LocalGlassEngine`.
- **`GlassSurface` / `GlassCard` / `GlassPanel`** — Compose components that consume the
  plan and render the glass.

### Render strategies

| Strategy | When | What |
|----------|------|------|
| `AGSL_SHADER` | Android 12+ (API 31+) and profile allows | Full procedural glass shader |
| `RENDER_EFFECT` blur | API 31+, material has blur | Hardware blur layer |
| `BRUSH_FALLBACK` | API < 31 or shader unavailable | Translucent tint + gradient highlights |

### Simulated optics vs. ray tracing

The shader is **procedural** — it computes fresnel/specular/refraction math directly
rather than sampling a captured backdrop texture. This produces convincing glass
appearance efficiently. **True backdrop-sampled refraction** (displacing the actual
background behind the glass) is a documented roadmap item that requires a backdrop
capture pipeline; it is intentionally not claimed as implemented.

## Performance

- **`GraphicsProfile`** — `ULTRA` / `BALANCED` / `PERFORMANCE`, each a budget of which
  effects are enabled.
- **`PerformanceProfiler`** — Choreographer-based frame-time sampler (rolling average +
  p95). No sensor polling.
- **`PerformancePolicy`** — pure, unit-tested decision logic mapping signals
  (frame time, memory, thermal, battery) to an allowed profile.
- **`QualityManager`** — combines the profiler with slow (5 s) periodic reads of
  memory/thermal/battery and resolves the effective profile. The user's profile is an
  upper bound; the system only steps down.

## Persistence

- **`LauncherSettings`** — all customization, serialized with kotlinx.serialization.
- **`HomeLayout`** — pages, cells, dock and widget placements, serialized.
- **`LauncherDataStore`** — a single DataStore (Preferences) scoped to the launcher.
- **`LauncherRepository`** — reactive `StateFlow` accessors + suspend writers. All
  writes are clamped to valid ranges.

## App wiring

- **`NewLauncherApp`** creates an `AppContainer` (singletons: repository, glass engine,
  quality manager, widget host).
- **`LauncherActivity`** (exported, `CATEGORY_HOME`) starts the quality manager and
  widget host, binds the glass engine to settings, and composes the home screen with
  Dynamic Island, Control Center, app drawer, widgets and customization overlays.

## App discovery

`AppProvider` (`:core:common`) uses `PackageManager.queryIntentActivities` with an
`ACTION_MAIN` + `CATEGORY_LAUNCHER` intent. The manifest's `<queries>` entry grants
visibility of all launchable apps without a blanket `QUERY_ALL_PACKAGES` declaration.
Icons and labels come from the system — never placeholders.

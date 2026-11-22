# Technology Resource Catalogue

An organised, maintained catalogue of the development resources available to the
**NEW-LAUNCHER** project — an original **native Android launcher** built with Kotlin,
Jetpack Compose, AGSL glass shaders and local DataStore persistence (no backend).

This catalogue is the single reference for **technology selection**. It records what
each resource is, whether it is appropriate for this project, and — critically — the
**reason for selection or rejection**. The governing principle (from the project's
master instructions) is: **understand the project first, then select only what is
genuinely useful. Do not install or integrate everything.**

## How to use this catalogue

- **Status** values:
  - **IN USE** — already a dependency or built-in capability of the project.
  - **REFERENCE** — a reference implementation/guide consulted during design; not a
    runtime dependency.
  - **CONSIDERED** — evaluated for this project and explicitly rejected, with a reason.
  - **NOT-APPLICABLE** — wrong platform/category for a native Android launcher; rejected.
- **Licence** values are recorded from public metadata. Resources marked NOT-APPLICABLE
  or CONSIDERED are **not integrated**, so their licences have **not** been vetted for
  use — re-verify at the repo before any future integration.
- URLs marked ✅ were **verified on 2026-11-22** to resolve to the intended project.

## Project profile (drives selection)

| Attribute | Value |
|---|---|
| Type | Android home-screen replacement (launcher) |
| Platform | Android (minSdk 26, targetSdk/compileSdk 35) |
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose (BOM 2024.10.01), Material 3 |
| Rendering | Native AGSL `RuntimeShader` + `RenderEffect` (no 3D engine) |
| Persistence | Local DataStore (Preferences) — offline, no backend |
| Build | AGP 8.7.2, Gradle 8.9+, JDK 17; CI on GitHub Actions |
| Networking | None (offline-first; no API keys, no telemetry) |

**Selection consequences:** web/React/3D-web/backend/SaaS resources are generally
**NOT-APPLICABLE**. Native Android, Compose, graphics and mobile-testing resources are
**selected**. Agent "skills" (design/senior-dev guidance) are used as **REFERENCE** for
how the engineering agent works, not as app dependencies.

---

## PART 1 — The original 10 video resources (all URLs ✅ verified)

These are agent-guidance "skills" and research tools, **not** app dependencies. They
shape how the engineering agent designs, critiques and documents — they are not linked
into the Android app.

### 1. REA — Reverse Engineering
- **URL:** https://github.com/morluto/rea ✅
- **Category:** Research / reverse engineering (agent skill)
- **Purpose:** Reverse-engineer apps and native binaries; interface/behaviour analysis;
  understanding existing implementations.
- **Licence:** See repo (verify before any use).
- **Compatibility:** Agent-side tooling; not an Android dependency.
- **Maintenance:** Active (commits within hours of verification).
- **Security review:** Reverse-engineering tool — treat as untrusted; run only for
  legitimate analysis of code the user owns/authorises.
- **Installation status:** Not installed.
- **Integration location:** None (reference for researching system/launcher behaviour).
- **Reason:** **REFERENCE.** Useful when studying how Android launcher/system APIs or an
  existing implementation behave. Not needed for the current codebase.

### 2. Ponytail — Senior Developer
- **URL:** https://github.com/DietrichGebert/ponytail ✅
- **Category:** Engineering discipline (agent skill)
- **Purpose:** Senior-level coding practices; "the best code is the code you never
  wrote"; avoid unnecessary code; better implementation decisions.
- **Licence:** See repo (verify before use).
- **Compatibility:** Agent-side guidance.
- **Maintenance:** Active (v5.3.x, recent commits).
- **Security review:** Guidance only; no runtime surface.
- **Installation status:** Not installed.
- **Integration location:** Agent working-practices (governs this project's "avoid
  unnecessary dependencies" rule).
- **Reason:** **REFERENCE.** Directly aligns with the project's minimal-dependency,
  maintainable-architecture mandate.

### 3. Hallmark — Design
- **URL:** https://github.com/Nutlope/hallmark ✅
- **Category:** UI design (agent skill)
- **Purpose:** Anti-AI-slop design skill; visual direction; design quality; avoiding
  generic interfaces.
- **Licence:** See repo (verify before use).
- **Compatibility:** Agent-side design guidance.
- **Maintenance:** Active (v2.0.0, recent).
- **Security review:** Guidance only.
- **Installation status:** Not installed.
- **Integration location:** Design-direction reference for the glass/home UI.
- **Reason:** **REFERENCE.** Informs the premium, non-generic visual direction of the
  launcher (glass-first, iPhone-inspired but original).

### 4. Impeccable — UI Refinement
- **URL:** https://github.com/pbakaus/impeccable ✅
- **Category:** UI critique / refinement (agent skill)
- **Purpose:** Design vocabulary for the coding agent; polish, critique, audit; catch
  AI slop before it ships; typography and spacing.
- **Licence:** See repo (verify before use).
- **Compatibility:** Agent-side design guidance.
- **Maintenance:** Active (Rust engine, recent commits).
- **Security review:** Guidance + a CLI tool; review before running.
- **Installation status:** Not installed.
- **Integration location:** Design-refinement reference (critique/audit pass on UI).
- **Reason:** **REFERENCE.** Complements Hallmark for the refinement/critique phase of
  UI work.

### 5. UI/UX Pro Max
- **URL:** https://github.com/nextlevelbuilder/ui-ux-pro-max-skill ✅
- **Category:** Design systems / UI/UX intelligence (agent skill)
- **Purpose:** Design systems; UI/UX intelligence; colour palettes; typography and
  layouts; cross-platform product patterns.
- **Licence:** See repo (verify before use).
- **Compatibility:** Agent-side design guidance.
- **Maintenance:** Active (large dataset, recent commits).
- **Security review:** Guidance + data; review before use.
- **Installation status:** Not installed.
- **Integration location:** Design-token and layout reference (the project already uses
  a three-layer token architecture).
- **Reason:** **REFERENCE.** Supports the design-system/token discipline already in
  `:core:designsystem`.

### 6. Diagram Design
- **URL:** https://github.com/cathrynlavery/diagram-design ✅
- **Category:** Diagrams / visual documentation (agent skill)
- **Purpose:** Architecture diagrams; visual documentation; application flows; 45
  diagram types, self-contained HTML+SVG.
- **Licence:** See repo (verify before use).
- **Compatibility:** Documentation generation.
- **Maintenance:** Active (recent releases).
- **Security review:** Generates local HTML/SVG; review output before publishing.
- **Installation status:** Not installed.
- **Integration location:** Documentation (architecture/flow diagrams in `docs/`).
- **Reason:** **REFERENCE.** For producing the architecture and flow diagrams referenced
  in `docs/ARCHITECTURE.md`.

### 7. Humanizer
- **URL:** https://github.com/blader/humanizer ✅
- **Category:** Writing / documentation quality (agent skill)
- **Purpose:** Removes signs of AI-generated writing; natural writing; documentation
  quality; user-friendly explanations.
- **Licence:** See repo (verify before use).
- **Compatibility:** Agent-side writing guidance.
- **Maintenance:** Active (v4.0.0, recent).
- **Security review:** Guidance only.
- **Installation status:** Not installed.
- **Integration location:** Documentation writing (README, docs, commit messages).
- **Reason:** **REFERENCE.** Keeps documentation and user-facing text natural and
  professional.

### 8. Onyx
- **URL:** https://github.com/onyx-robotics/onyx-agent ✅
- **Category:** AI agent research (agent orchestration)
- **Purpose:** Large-scale AI agent teams for code-improvement discovery and
  "autoresearch".
- **Licence:** See repo (verify before use).
- **Compatibility:** Agent orchestration; not an app dependency.
- **Maintenance:** Active (v0.4.0, recent).
- **Security review:** Agent orchestration — review before use; handles credentials.
- **Installation status:** Not installed.
- **Integration location:** None.
- **Reason:** **NOT-APPLICABLE** to the launcher app. An agent-orchestration research
  project; not relevant to building/running the Android launcher.

### 9. Last30Days
- **URL:** https://github.com/mvanhorn/last30days-skill ✅
- **Category:** Recent research (agent skill)
- **Purpose:** Researches any topic across Reddit, X, YouTube, HN, Polymarket and the
  web; synthesises a grounded summary.
- **Licence:** See repo (verify before use).
- **Compatibility:** Agent-side research; requires network access.
- **Maintenance:** Active (v4.5.0, recent).
- **Security review:** Research skill; scraping/aggregation — respect platform terms.
- **Installation status:** Not installed.
- **Integration location:** None.
- **Reason:** **NOT-APPLICABLE** to the app. A general research skill; not needed to
  build the launcher (and the sandbox restricts outbound hosts).

### 10. Scrapling
- **URL:** https://github.com/D4Vinci/Scrapling ✅
- **Category:** Web scraping (library)
- **Purpose:** Web scraping framework; public data extraction; full-scale crawling.
- **Licence:** See repo (verify before use).
- **Compatibility:** Python web-scraping; not Android.
- **Maintenance:** Active (recent commits).
- **Security review:** Scraping tool — respect robots/terms; not used.
- **Installation status:** Not installed.
- **Integration location:** None.
- **Reason:** **NOT-APPLICABLE.** The launcher is offline-first with no scraping
  requirement.

---

## PART 10 — Android application development (most relevant)

### 1. Now in Android
- **URL:** https://github.com/android/nowinandroid ✅
- **Category:** Android architecture reference app
- **Purpose:** A fully functional, modular Android app built entirely with Kotlin and
  Jetpack Compose — the canonical reference for clean Android architecture (modules,
  MVVM, dependency injection, testing, build logic).
- **Licence:** Apache-2.0
- **Compatibility:** Android / Kotlin / Compose — directly aligned with this project.
- **Maintenance:** Actively maintained by Google/Android team.
- **Security review:** Reference code; no runtime integration.
- **Installation status:** Not a dependency (reference only).
- **Integration location:** Architecture reference for module layout, MVVM and build
  conventions.
- **Reason:** **REFERENCE.** The project's modular `:app`/`:feature:*`/`:core:*` layout
  and MVVM + ViewModel pattern follow this canonical structure.

### 2. Jetpack Compose Samples
- **URL:** https://github.com/android/compose-samples ✅
- **Category:** Compose reference samples
- **Purpose:** Official Jetpack Compose samples (JetNews, Jetchat, Jetsnack, Jetcaster,
  JetLagged, Reply) covering Compose patterns, animation, layout and testing.
- **Licence:** Apache-2.0
- **Compatibility:** Android / Compose.
- **Maintenance:** Actively maintained by the Android team.
- **Security review:** Reference code.
- **Installation status:** Not a dependency.
- **Integration location:** Compose pattern reference (animations, lazy lists, gestures).
- **Reason:** **REFERENCE.** Used to confirm correct Compose APIs (e.g. pager, gestures,
  `Image`, animation) when implementing UI features.

### 3. AndroidX
- **URL:** https://github.com/androidx/androidx
- **Category:** Android support libraries (IN USE)
- **Purpose:** The foundation AndroidX libraries (core, lifecycle, activity, datastore,
  etc.) backported and maintained by Google.
- **Licence:** Apache-2.0
- **Compatibility:** Android — the project's foundation.
- **Maintenance:** Actively maintained.
- **Security review:** Official Google libraries; trusted.
- **Installation status:** **IN USE** (core-ktx, lifecycle, activity-compose,
  datastore-preferences, etc. via the version catalog).
- **Integration location:** `gradle/libs.versions.toml` and module `build.gradle.kts`.
- **Reason:** **IN USE.** Core platform foundation; no alternative needed.

### 4. Coil
- **URL:** https://github.com/coil-kt/coil ✅
- **Category:** Image loading (library)
- **Purpose:** Image loading for Android and Compose Multiplatform (network/coroutine
  image loading with caching and Compose integration).
- **Licence:** Apache-2.0
- **Compatibility:** Android / Compose.
- **Maintenance:** Actively maintained.
- **Security review:** Well-established library; trusted.
- **Installation status:** Not installed.
- **Integration location:** None.
- **Reason:** **CONSIDERED — REJECTED.** App icons come from `PackageManager` as
  `Drawable`s (not network URLs) and are rasterised once and cached via `remember` in
  `AppIcon`. Coil's network-loading/coroutine-caching adds no value for system drawables
  and would be unnecessary weight. Reconsider only if remote image/wallpaper loading is
  added later.

### 5. Ktor
- **URL:** https://github.com/ktorio/ktor
- **Category:** Kotlin networking framework
- **Purpose:** Kotlin multiplatform HTTP client/server framework.
- **Licence:** Apache-2.0
- **Compatibility:** Kotlin multiplatform.
- **Maintenance:** Actively maintained.
- **Security review:** Trusted framework.
- **Installation status:** Not installed.
- **Integration location:** None.
- **Reason:** **CONSIDERED — REJECTED.** The launcher is offline-first with no network
  requirement (see SECURITY.md — no networking, no API keys). Adding a client would
  violate the minimal-permission, privacy-first design.

### 6. Google Filament
- **URL:** https://github.com/google/filament ✅
- **Category:** Real-time physically-based rendering engine
- **Purpose:** Cross-platform PBR rendering engine (Android/iOS/desktop/WebGPU/WebGL2)
  with reflections, refraction, environment lighting, etc.
- **Licence:** Apache-2.0
- **Compatibility:** Android (via native C++/JNI); heavy.
- **Maintenance:** Actively maintained by Google.
- **Security review:** Official Google engine; trusted.
- **Installation status:** Not installed.
- **Integration location:** None (the glass engine uses native AGSL instead).
- **Reason:** **CONSIDERED — REJECTED.** The project's Physical Glass Rendering Engine
  already produces the required glass look (fresnel, specular, refraction, dispersion,
  liquid deformation) using **native `android.graphics.RuntimeShader` (AGSL)** +
  `RenderEffect`, which is lightweight and API-26+. Per the project's explicit rule —
  *"Do not use expensive 3D rendering when a lightweight shader or native implementation
  can produce the required result"* — Filament's C++ PBR engine is unnecessary weight
  for a procedural glass shader. Filament would only be reconsidered if true
  ray-traced/backdrop-sampled refraction (a documented roadmap item) is pursued.

---

## PART 11 — Testing, security and quality

### 1. Maestro
- **URL:** https://github.com/mobile-dev-inc/Maestro ✅
- **Category:** Mobile E2E automation
- **Purpose:** Painless E2E automation for mobile (and web); declarative flows for
  Android/iOS.
- **Licence:** Apache-2.0
- **Compatibility:** Android/iOS; runs on device/emulator.
- **Maintenance:** Actively maintained.
- **Security review:** Well-established; trusted. Drives real UI interactions — scope
  flows to the app under test.
- **Installation status:** Not yet integrated (no device/emulator CI job yet).
- **Integration location:** Future `androidTest` / CI E2E flows (alongside the
  instrumented tests described in `docs/TESTING.md`).
- **Reason:** **REFERENCE / planned.** The natural tool for the on-device E2E flows the
  project already lists in `TESTING.md` (install, set-as-home, launch apps, restore
  layout). To be integrated once device/emulator testing is available.

### 2. Sentry
- **URL:** https://github.com/getsentry/sentry
- **Category:** Crash reporting / error tracking
- **Purpose:** Crash and error reporting platform with Android SDK.
- **Licence:** SDK MIT (platform has a commercial EBQ licence); verify current terms.
- **Compatibility:** Android.
- **Maintenance:** Actively maintained.
- **Security review:** Established; but it transmits crash data to a third party —
  would require user-consent/privacy review given the project's no-telemetry policy.
- **Installation status:** Not installed.
- **Integration location:** None yet (candidate for production builds only).
- **Reason:** **CONSIDERED.** Directly relevant to the launch-crash class of bug just
  fixed. Rejected for now because (a) the launcher is offline-first with a strict
  no-telemetry/no-upload policy (see `docs/SECURITY.md`), and (b) no production release
  exists. Reconsider only for a privacy-reviewed, opt-in production crash-reporting
  path.

### 3. OWASP Cheat Sheet Series
- **URL:** https://github.com/OWASP/CheatSheetSeries ✅
- **Category:** Security reference
- **Purpose:** Concise, high-value information on specific application-security topics.
- **Licence:** CC-BY-SA-4.0
- **Compatibility:** Platform-agnostic guidance.
- **Maintenance:** Actively maintained by OWASP.
- **Security review:** Authoritative security reference.
- **Installation status:** Not installed (reference documentation).
- **Integration location:** Security review process (`docs/SECURITY.md`).
- **Reason:** **REFERENCE.** The authoritative checklist for ongoing security review of
  the launcher's permissions, storage and data handling.

### 4. Semgrep
- **URL:** https://github.com/semgrep-rules (rules) / https://github.com/semgrep/semgrep (engine)
- **Category:** Static analysis / SAST
- **Purpose:** Static analysis with a large rule registry; finds bugs and security
  issues in code.
- **Licence:** Semgrep engine AGPL-3.0; `semgrep-rules` MIT — verify before CI use.
- **Compatibility:** Multi-language (incl. Kotlin/Java).
- **Maintenance:** Actively maintained.
- **Security review:** Established SAST tool.
- **Installation status:** Not installed.
- **Integration location:** Candidate CI static-analysis step.
- **Reason:** **CONSIDERED.** A reasonable addition to CI for catching security/quality
  regressions. Not integrated yet to avoid adding a heavy CI tool with no current
  failing need; reconsider if the rule set proves valuable.

### 5–10. Playwright, Vitest, Jest, Testing Library, Storybook, Lighthouse
- **URLs:** https://github.com/microsoft/playwright · https://github.com/vitest-dev/vitest ·
  https://github.com/jestjs/jest · https://github.com/testing-library/react-testing-library ·
  https://github.com/storybookjs/storybook · https://github.com/GoogleChrome/lighthouse
- **Category:** Web testing / quality tools
- **Purpose:** Web E2E (Playwright), JS unit testing (Vitest/Jest), component testing
  (Testing Library/Storybook), web performance auditing (Lighthouse).
- **Licence:** MIT (Playwright, Vitest, Jest, Testing Library, Storybook); Apache-2.0
  (Lighthouse) — verify each before use.
- **Compatibility:** Web/JS toolchain.
- **Maintenance:** All actively maintained.
- **Security review:** Established tools; not integrated.
- **Installation status:** Not installed.
- **Integration location:** None.
- **Reason:** **NOT-APPLICABLE.** Web-only tooling. The Android launcher uses JUnit +
  Robolectric + Espresso/Compose UI test (already configured in the app module), which
  are the correct native equivalents.

---

## PART 6 — 3D graphics, realism, glass and rendering

Google Filament is covered above (CONSIDERED — REJECTED in favour of native AGSL). The
remaining resources are web/3D-web engines:

| Resource | URL | Purpose | Licence | Status | Reason |
|---|---|---|---|---|---|
| Three.js | https://github.com/mrdoob/three.js | WebGL 3D library | MIT | NOT-APPLICABLE | Web-only; native AGSL used instead. |
| React Three Fiber | https://github.com/pmndrs/react-three-fiber | React 3D renderer | MIT | NOT-APPLICABLE | React/web 3D; not native Android. |
| Drei | https://github.com/pmndrs/drei | R3F helpers | MIT | NOT-APPLICABLE | R3F helper; web 3D. |
| Babylon.js | https://github.com/BabylonJS/Babylon.js | Web 3D engine | Apache-2.0 | NOT-APPLICABLE | Web 3D engine; native AGSL used. |
| React Three Postprocessing | https://github.com/pmndrs/react-postprocessing | R3F post-processing | MIT | NOT-APPLICABLE | Web 3D post-processing. |
| OGL | https://github.com/oframe/ogl | WebGL renderer | MIT | NOT-APPLICABLE | Web WebGL. |
| PixiJS | https://github.com/pixijs/pixijs | Web 2D/WebGL renderer | MIT | NOT-APPLICABLE | Web renderer. |
| Luma.gl | https://github.com/visgl/luma.gl | WebGL/WebGPU framework | MIT | NOT-APPLICABLE | Web GPU framework. |
| Creative Web Toolbox | https://github.com/silencoo/creative-web-toolbox | Web creative tooling | verify | NOT-APPLICABLE | Web creative tooling. |

**Selection note:** the project's glass/rendering needs (refraction, reflection, fresnel,
specular, dispersion, transparency, distortion) are met by **native AGSL
`RuntimeShader`** + `RenderEffect` in `:core:glassengine` / `:core:graphics`, which is
the lightweight, native, API-26+ solution the project mandates. No web 3D engine is
introduced.

---

## PART 5 — Animation, motion and interactions

The launcher uses **native Jetpack Compose animation** (`SpringSpec`,
`animateFloatAsState`, `InfiniteTransition`, `InfiniteTransition` sheen). Web animation
libraries are NOT-APPLICABLE:

| Resource | URL | Purpose | Licence | Status | Reason |
|---|---|---|---|---|---|
| Motion | https://github.com/motiondivision/motion | Web/React animation | MIT | NOT-APPLICABLE | Web animation; native Compose used. |
| GSAP | https://github.com/greensock/GSAP | Web animation | custom (verify) | NOT-APPLICABLE | Web animation. |
| React Spring | https://github.com/pmndrs/react-spring | React spring physics | MIT | NOT-APPLICABLE | React; Compose `SpringSpec` used. |
| Lenis | https://github.com/darkroomengineering/lenis | Web smooth scroll | MIT | NOT-APPLICABLE | Web scroll. |
| Rive | https://github.com/rive-app/rive | Animation tool/runtime | Apache-2.0 | NOT-APPLICABLE | Could be used for complex motion but not needed; native Compose animation suffices. |
| Lottie Web | https://github.com/airbnb/lottie-web | Web animation runtime | MIT | NOT-APPLICABLE | Web Lottie; native Compose used. |
| Use Gesture | https://github.com/pmndrs/use-gesture | React gestures | MIT | NOT-APPLICABLE | React gestures; Compose pointer input used. |
| Theatre.js | https://github.com/theatre-js/theatre | Web animation | MIT | NOT-APPLICABLE | Web animation. |

---

## PART 7 — Physics and natural movement

The launcher uses **Compose spring physics** (`SpringSpec`) and `detectDragGestures`.
Heavy physics engines are NOT-APPLICABLE:

| Resource | URL | Purpose | Licence | Status | Reason |
|---|---|---|---|---|---|
| Rapier | https://github.com/dimforge/rapier | 2D/3D physics engine | Apache-2.0 | NOT-APPLICABLE | No rigid-body physics needed; Compose springs used. |
| React Three Rapier | https://github.com/pmndrs/react-three-rapier | R3F physics | MIT | NOT-APPLICABLE | Web 3D physics. |
| Matter.js | https://github.com/liabru/matter-js | 2D physics | MIT | NOT-APPLICABLE | Web 2D physics. |
| Cannon-es | https://github.com/pmndrs/cannon-es | 3D physics | MIT | NOT-APPLICABLE | Web 3D physics. |

(React Spring / Use Gesture already listed under PART 5.)

---

## PART 8 — Backend, databases and API integration

The launcher is **offline-first** (local DataStore, no network, no API keys, no
telemetry — see `docs/SECURITY.md`). All backend/API resources are NOT-APPLICABLE:

| Resource | URL | Licence | Status | Reason |
|---|---|---|---|---|
| Supabase | https://github.com/supabase/supabase | MIT | NOT-APPLICABLE | No backend; offline-first. |
| Appwrite | https://github.com/appwrite/appwrite | MIT | NOT-APPLICABLE | No backend. |
| FastAPI | https://github.com/fastapi/fastapi | MIT | NOT-APPLICABLE | Python backend; no backend. |
| Hono | https://github.com/honojs/hono | MIT | NOT-APPLICABLE | Web server; no backend. |
| OpenAPI Specification | https://github.com/OAI/OpenAPI-Specification | Apache-2.0 | NOT-APPLICABLE | No API. |
| tRPC | https://github.com/trpc/trpc | MIT | NOT-APPLICABLE | TypeScript RPC; no backend. |
| Redis | https://github.com/redis/redis | BSD-3-Clause | NOT-APPLICABLE | No server/cache. |
| TanStack Query | https://github.com/TanStack/query | MIT | NOT-APPLICABLE | React data fetching; no network. |
| TanStack Table | https://github.com/TanStack/table | MIT | NOT-APPLICABLE | React tables; not used. |
| React Hook Form | https://github.com/react-hook-form/react-hook-form | MIT | NOT-APPLICABLE | React forms; Compose used. |
| Zod | https://github.com/colinhacks/zod | MIT | NOT-APPLICABLE | TS schema validation; kotlinx.serialization used. |
| Better Auth | https://github.com/better-auth/better-auth | MIT | NOT-APPLICABLE | No auth/backend. |
| UploadThing | https://github.com/pingdotgg/uploadthing | MIT | NOT-APPLICABLE | File upload; no uploads. |
| Resend | https://github.com/resend/resend-node | MIT | NOT-APPLICABLE | Email; no email. |
| Inngest | https://github.com/inngest/inngest | MIT | NOT-APPLICABLE | Background jobs; none needed. |

**Rule honoured:** no fake authentication, simulated payments or non-functional API
connections are introduced — there is no API surface at all.

---

## PART 9 — Data visualisation and dashboards

No dashboards/charts in a launcher. ALL NOT-APPLICABLE:

| Resource | URL | Licence | Status |
|---|---|---|---|
| Apache ECharts | https://github.com/apache/echarts | Apache-2.0 | NOT-APPLICABLE |
| Recharts | https://github.com/recharts/recharts | MIT | NOT-APPLICABLE |
| Nivo | https://github.com/plouc/nivo | MIT | NOT-APPLICABLE |
| Visx | https://github.com/airbnb/visx | MIT | NOT-APPLICABLE |
| React Flow | https://github.com/xyflow/xyflow | MIT | NOT-APPLICABLE |
| Tremor | https://github.com/tremorlabs/tremor | MIT | NOT-APPLICABLE |

---

## PART 3 — Complete application architecture (web/SaaS starters)

All web/SaaS starter stacks. NOT-APPLICABLE to a native Android launcher:

| Resource | URL | Licence | Status | Reason |
|---|---|---|---|---|
| Open SaaS | https://github.com/wasp-lang/open-saas | MIT | NOT-APPLICABLE | Web SaaS starter. |
| Bulletproof React | https://github.com/alan2207/bulletproof-react | MIT | NOT-APPLICABLE | React starter. |
| T3 Stack | https://github.com/t3-oss/create-t3-app | MIT | NOT-APPLICABLE | React/TS starter. |
| FastAPI Full Stack Template | https://github.com/fastapi/full-stack-fastapi-template | MIT | NOT-APPLICABLE | Python web stack. |
| RealWorld | https://github.com/gothinkster/realworld | MIT | NOT-APPLICABLE | Web app spec/starter. |
| Next.js Boilerplate | https://github.com/ixartz/Next-js-Boilerplate | MIT | NOT-APPLICABLE | Next.js starter. |

---

## PART 4 — Professional UI/UX and component libraries (web/React)

All web/React component libraries. The launcher uses **native Material 3 + custom glass
components** in Compose. NOT-APPLICABLE as libraries (design principles transfer via the
REFERENCE skills in PART 1):

| Resource | URL | Licence | Status |
|---|---|---|---|
| shadcn/ui | https://github.com/shadcn-ui/ui | MIT | NOT-APPLICABLE (React) |
| Magic UI | https://github.com/magicuidesign/magicui | MIT | NOT-APPLICABLE (React) |
| Aceternity UI | https://github.com/aceternity/aceternity-ui | MIT | NOT-APPLICABLE (React) |
| Cult UI | https://github.com/nolly-studio/cult-ui | MIT | NOT-APPLICABLE (React) |
| Origin UI | https://github.com/origin-space/originui | MIT | NOT-APPLICABLE (React) |
| Radix UI | https://github.com/radix-ui/primitives | MIT | NOT-APPLICABLE (React) |
| React Bits | https://github.com/DavidHDev/react-bits | MIT | NOT-APPLICABLE (React) |
| Animate UI | https://github.com/imskyleen/animate-ui | MIT | NOT-APPLICABLE (React) |
| Taste Skill | https://github.com/Leonxlnx/taste-skill | MIT | NOT-APPLICABLE (agent skill, web-design) |
| Emil Kowalski Skills | https://github.com/emilkowalski/skills | MIT | NOT-APPLICABLE (agent skill, web-design) |

---

## PART 2 — AI agents and autonomous development

Agent-orchestration frameworks/research. NOT-APPLICABLE to the launcher app (they are
meta-tools for running agents, not app dependencies):

| Resource | URL | Licence | Status | Reason |
|---|---|---|---|---|
| OpenHands | https://github.com/All-Hands-AI/OpenHands | MIT | NOT-APPLICABLE | Agent runtime; not an app dep. |
| LangGraph | https://github.com/langchain-ai/langgraph | MIT | NOT-APPLICABLE | Agent orchestration. |
| Anthropic Skills | https://github.com/anthropics/skills | MIT (verify) | NOT-APPLICABLE | Agent skills; not app dep. |
| Agent Skills Library | https://github.com/gptnius/skills-library | MIT (verify) | NOT-APPLICABLE | Agent skills catalogue. |
| Vercel AI SDK | https://github.com/vercel/ai | Apache-2.0 | NOT-APPLICABLE | LLM SDK; no AI features in launcher. |
| MCP Servers | https://github.com/modelcontextprotocol/servers | MIT (varies) | NOT-APPLICABLE | MCP servers; no AI integration. |
| Browser Use | https://github.com/browser-use/browser-use | MIT | NOT-APPLICABLE | Web browser automation. |
| Agentic Tools | https://github.com/thinkjones/agentic-tools | MIT (verify) | NOT-APPLICABLE | Agent tooling. |
| Awesome AI Agents | https://github.com/lllray/awesome-ai-agents | MIT (verify) | NOT-APPLICABLE | List/curated resources. |
| Awesome Agent Skills | https://github.com/uhub/awesome-agent-skills | MIT (verify) | NOT-APPLICABLE | List/curated resources. |

(Onyx is covered under PART 1 — NOT-APPLICABLE.)

---

## Summary — what this project actually uses

| Resource | Status | Where |
|---|---|---|
| AndroidX | **IN USE** | Foundation libraries (version catalog) |
| Jetpack Compose + Material 3 | **IN USE** | All UI |
| Native AGSL `RuntimeShader` + `RenderEffect` | **IN USE** | `:core:glassengine`, `:core:graphics` |
| Kotlin + kotlinx.serialization + DataStore | **IN USE** | Persistence |
| JUnit + Robolectric + Espresso/Compose UI test | **IN USE** | Testing (configured) |
| Now in Android | REFERENCE | Architecture/module conventions |
| Jetpack Compose Samples | REFERENCE | Compose API patterns |
| Ponytail / Hallmark / Impeccable / UI-UX Pro Max | REFERENCE | Design & engineering discipline |
| REA / Diagram Design / Humanizer | REFERENCE | Research, diagrams, documentation |
| OWASP Cheat Sheet Series | REFERENCE | Security review |
| Maestro | REFERENCE (planned) | On-device E2E testing (when device testing is available) |
| Coil / Ktor / Filament / Sentry / Semgrep | CONSIDERED — REJECTED | See per-resource reasons |
| All web/React/3D-web/backend/SaaS resources (PARTs 2–9) | NOT-APPLICABLE | Wrong platform/category |

**Maintenance:** This catalogue is updated whenever a resource is added, integrated,
re-evaluated or rejected. Record the date and reason for every change.

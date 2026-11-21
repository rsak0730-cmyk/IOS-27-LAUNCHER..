# Contributing

Thank you for contributing to NEW-LAUNCHER! This guide covers the development workflow,
the stage-based approach, and how to get the project onto GitHub.

## Prerequisites

- JDK 17
- Android SDK (platform 35, build-tools 35.0.0)
- Android Studio Ladybug or newer (or any IDE with Gradle support)

## Development workflow

The project is developed in **controlled stages**. After each stage:

1. Compile (`./gradlew :app:assembleDebug`).
2. Run tests (`./gradlew :app:testDebugUnitTest :core:test`).
3. Inspect errors and fix regressions.
4. Review code changes.
5. Update documentation.
6. Commit working changes with a clear message.

### Stage checklist

1. Repository & architecture
2. Physical Glass Engine
3. Home screen
4. App drawer & app management
5. Dynamic Island-inspired interactions
6. Control Center & customization
7. Android widgets
8. Performance optimization & device testing
9. Security & stability review
10. Automated APK build

## Build & test locally

```bash
./gradlew :app:assembleDebug          # build the debug APK
./gradlew :app:testDebugUnitTest      # run unit tests
./gradlew :app:lintDebug              # run lint
```

## Code style

- Kotlin, `official` code style (configured in `gradle.properties`).
- Keep UI, application logic, and platform integration separated across modules.
- Prefer the existing design tokens over hard-coded values.
- Never introduce fake functionality or placeholder buttons as completed features.
- Never claim a feature works without verifying it.

## Project structure

See [`ARCHITECTURE.md`](ARCHITECTURE.md) for the module graph and design-system
architecture.

## Putting the project on GitHub

The authoring environment could not create the `new-launcher-android` repository
directly (see [`KNOWN-LIMITATIONS.md`](KNOWN-LIMITATIONS.md)). To publish the project:

1. **Create the repository** on GitHub (web UI or a credential with repository-create
   permission):
   ```bash
   # Option A: via GitHub CLI with a token that has repo-create permission
   gh repo create new-launcher-android --public \
     --description "An advanced Android launcher featuring photorealistic glass physics, liquid glass interactions, premium iPhone-inspired UI and advanced Android customization."
   ```
   Or create it at https://github.com/new-repository.

2. **Push this branch** to the new repository:
   ```bash
   git remote add origin https://github.com/<your-username>/new-launcher-android.git
   git push -u origin <current-branch>
   ```

3. **Verify** the repository, then push. Once pushed, the GitHub Actions workflow runs
   automatically on push and produces the debug APK artifact.

## Pull requests

- Open PRs from a feature branch into `main`.
- Ensure the CI workflow passes (build, tests, lint).
- Keep PRs focused and describe the change and any verification performed.

## Reporting issues

Open a GitHub issue describing the problem, the device, and the steps to reproduce.
Include logs (`adb logcat`) where relevant.

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew clean                  # Clean build artifacts
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests on device
./gradlew check                  # Run lint and tests
./gradlew installDebug           # Build and install debug APK to connected device
```

## Architecture

Single-module Android TV app (`app/`) using **MVVM + Jetpack Compose for TV**.

**Entry point:** `MainActivity` sets up the `NebulaTheme` and renders `LauncherScreen`.

**Navigation model:** A `ViewScreen` enum (`WIDGETS`, `APP_GRID`, `BROWSER`) drives which panel is shown. `LauncherScreen` owns the current screen state and renders the active panel composable.

**Panel structure:** Each feature area is a top-level composable in its own package:
- `home/Panel.kt` — Widget area (placeholder)
- `apps/Panel.kt` — App grid (implemented via `AppGridViewModel`)
- `browser/Panel.kt` — GeckoView browser (placeholder)
- `backgrouds/Panel.kt` — Dynamic backgrounds (placeholder)

**ViewModel:** `AppGridViewModel` loads installed packages via `PackageManager`, exposing `StateFlow<List<AppInfo>>` consumed by `AppGrid.kt`.

**UI components:** Reusable components live in `ui/components/core/` (e.g., `PanelView` for glass-morphism panel containers) and `ui/components/navigation/` (TV-optimized D-pad-navigable nav bar).

## Key Tech Details

- **Kotlin** 2.2.0, **AGP** 8.11.1, **Gradle** 8.13, **Java** 18
- **Min SDK** 26, **Target/Compile SDK** 36
- **Browser:** GeckoView 141.x (Firefox engine) — not yet wired up
- **Navigation:** AndroidX Navigation3 (**stable** Nov 2025) — `navigation3-runtime` + `navigation3-ui`
- **Dependency injection:** Hilt (dependency present, not yet implemented)
- Touchscreen is optional (`required=false`); all navigation must support D-pad/remote

## Permissions

- `QUERY_ALL_PACKAGES` — required for launcher to enumerate installed apps
- `INTERNET` — required for browser and dynamic content

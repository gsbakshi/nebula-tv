---
name: nebula-architecture
description: Nebula launcher MVVM architecture decisions, module layout, data flow, and the roadmap for Hilt, Navigation3, and GeckoView integration. Context for all code generation.
user-invocable: false
---

# Nebula Architecture Reference

## App Structure (Single Module)
```
MainActivity
└── NebulaTheme (TV MaterialTheme)
    └── LauncherScreen (owns nav state)
        ├── NavigationBar (D-pad horizontal tabs)
        │   └── NavigationItem × N (focusable tabs)
        └── Panel Switcher (when expression on ViewScreen)
            ├── WidgetAreaPanel   → home/Panel.kt
            ├── AppGridPanel      → apps/Panel.kt + AppGridViewModel
            ├── BrowserPanel      → browser/Panel.kt (GeckoView — TODO)
            └── BackgroundsPanel  → backgrouds/Panel.kt (typo, don't rename)
```

## Current Navigation Model
```kotlin
// In LauncherScreen — simple state switch, no back stack
var currentView by remember { mutableStateOf(ViewScreen.WIDGETS) }

when (currentView) {
    ViewScreen.WIDGETS  -> WidgetAreaPanel(Modifier.fillMaxSize())
    ViewScreen.APP_GRID -> AppGridPanel(Modifier.fillMaxSize())
    ViewScreen.BROWSER  -> BrowserPanel(Modifier.fillMaxSize())
}
```

**Migration path**: Navigation3 (stable Nov 2025) when back stack needed.
Pattern: `SnapshotStateList<ViewScreen>` instead of `MutableState<ViewScreen>`.

## Data Flow (Current)
```
PackageManager ──► AppGridViewModel ──► StateFlow<List<AppInfo>> ──► AppGrid ──► LazyVerticalGrid
   (IO thread)      (viewModelScope)        (Main thread)           (Compose)       (TvLazyGrid)
```

## PanelView Component
`ui/components/core/PanelView.kt` — the frosted glass container.
All panel root composables MUST use `PanelView {}` as their outermost container.
This provides visual consistency (glass-morphism background) across panels.

## Hilt — NOT YET ENABLED
Hilt 2.57 is in `libs.versions.toml`. Plugin is commented out in `app/build.gradle.kts`.

To enable when ready:
1. `app/build.gradle.kts`: Uncomment `id("com.google.dagger.hilt.android")`
2. Create `NebulaApplication : Application()` with `@HiltAndroidApp`
3. Register in `AndroidManifest.xml`: `android:name=".NebulaApplication"`
4. Add `@AndroidEntryPoint` to `MainActivity`
5. Replace `viewModel()` with `hiltViewModel()` in composables
6. Use `@Inject` constructor in ViewModels + `@HiltViewModel`

## GeckoView — NOT YET WIRED
GeckoView 141.x is in deps and Mozilla Maven is configured.
Integration requires:
1. `NebulaApplication` class (needed for singleton GeckoRuntime anyway)
2. `AndroidView {}` wrapper in `BrowserPanel`
3. `GeckoSession` per tab, managed in `BrowserViewModel`
4. `GeckoRuntime` singleton in Application

See `gecko-browser-specialist` agent for implementation patterns.

## Background Service — NOT YET IMPLEMENTED
Widget refresh and background image updates need:
- `repeatOnLifecycle(STARTED)` in ViewModels (pauses when backgrounded)
- Eventually: `WorkManager` for refresh when launcher is not foreground
- NASA/RSS data should survive launcher process if possible

## Folder Note — IMPORTANT
`backgrouds/` (note the typo — missing 'd') exists in git history.
DO NOT rename this folder. Use the typo as-is to avoid git complications.
Reference it exactly: `com.example.nebula.backgrouds`

## Build Variants
- **Debug**: Current. `minifyEnabled = false`. Use for development.
- **Release**: `minifyEnabled = false` (also currently). Add ProGuard rules before enabling.

## Tech Stack Versions (as of build)
- Kotlin 2.2.0 / Compose BOM 2025.07.00 / AGP 8.13 / Java 18
- Min SDK 26 / Target SDK 36 / Compile SDK 36
- GeckoView 141.x / Navigation3 1.0.0-alpha05 (update to stable in libs.versions.toml)
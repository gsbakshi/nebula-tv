---
name: project-conventions
description: Nebula project coding conventions, naming standards, package structure, commit format, and architectural patterns. Loaded automatically for context when writing code.
user-invocable: false
---

# Nebula Project Conventions

## Package Structure
```
com.example.nebula/
├── MainActivity.kt          — Entry point only, no business logic
├── ViewModel.kt             — AppGridViewModel (rename eventually)
├── AppGrid.kt               — App grid composable
├── AppItem.kt               — App card composable
├── apps/Panel.kt            — App grid panel
├── home/Panel.kt            — Widget area panel
├── browser/Panel.kt         — GeckoView browser panel
├── backgrouds/Panel.kt      — ⚠️ Typo in folder name — DO NOT RENAME (preserves git history)
├── widgets/                 — All widget data sources + composables (TODO: create)
├── constants/Screen.kt      — ViewScreen enum
└── ui/
    ├── theme/               — Color, Type, Theme composables
    └── components/
        ├── core/            — Generic reusable components (PanelView, LauncherScreen)
        └── navigation/      — Nav bar (Bar.kt, Item.kt)
```

## Naming Conventions

| Element | Pattern | Example |
|---------|---------|---------|
| Panel composable | `{Feature}Panel` | `BrowserPanel` |
| ViewModel | `{Feature}ViewModel` | `AppGridViewModel` |
| UI state sealed class | `{Feature}State` | `BrowserState` |
| Data model | `{Feature}Info` or `{Feature}Data` | `AppInfo`, `ApodData` |
| Composable functions | PascalCase | `AppGrid()`, `PanelView()` |
| StateFlow (public) | `val {name}: StateFlow<T>` | `val uiState: StateFlow<AppState>` |
| MutableStateFlow (private) | `val _{name} = MutableStateFlow` | `val _uiState = MutableStateFlow(...)` |
| Template placeholders | `UPPER_SNAKE_CASE` | `PANEL_NAME`, `WIDGET_DATA` |

## ViewModel Pattern (Standard)
```kotlin
class FeatureViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<FeatureState>(FeatureState.Loading)
    val uiState: StateFlow<FeatureState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            try {
                // fetch data
            } catch (e: Exception) {
                _uiState.value = FeatureState.Error(e.message ?: "Error")
            }
        }
    }
}
```

## Compose Rules
- Always use `collectAsStateWithLifecycle()` — NOT `collectAsState()`
- State lives in ViewModel, not in composable `remember {}`
- All interactive composables need D-pad focus (touchscreen is `required=false`)
- `androidx.tv.material3` components preferred over `material3` where TV variants exist
- `PanelView {}` wraps all top-level panel content
- `@OptIn(ExperimentalTvMaterial3Api::class)` required for most TV components

## Compose Stability — @Immutable and @Stable

Annotate data classes to prevent unnecessary recompositions:

```kotlin
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

// @Immutable: all public properties are val AND their types are deeply immutable.
// Compose will skip recomposing any composable that only reads this object
// when other state changes — safe to use on most data/model classes.
@Immutable
data class ApodData(
    val title: String,
    val imageUrl: String,
    val explanation: String,
    val date: LocalDate,
)

// @Stable: object CAN change, but Compose will be notified via State<T> when it does.
// Use for classes with mutable state that's tracked by Compose snapshot system.
@Stable
class WidgetConfig(
    var refreshIntervalMinutes: Int = 30,  // mutable, but Compose is notified
)
```

**When to use each:**
| Annotation | When | Why |
|------------|------|-----|
| `@Immutable` | Pure data classes (ApodData, AppInfo, WeatherData) | Strongest skip guarantee |
| `@Stable` | Classes with observable mutable state | Weaker but correct for mutable |
| Neither | Primitive types, String, List<@Immutable> | Compose infers stability |
| Neither | MutableList, MutableMap, non-data classes | Unstable — triggers recomposition |

**Nebula convention:** All data model classes in `widgets/`, `apps/`, and `browser/` should be `@Immutable` data classes.

## Commit Message Format
Conventional commits: `type(scope): message`

Types: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`
Scopes: `nebula`, `browser`, `widgets`, `apps`, `backgrounds`, `nav`, `theme`

Examples:
- `feat(browser): integrate GeckoView with AndroidView wrapper`
- `fix(apps): correct D-pad focus traversal in app grid`
- `feat(widgets): add NASA APOD widget with daily refresh`

## Key Library Status
| Library | State | Notes |
|---------|-------|-------|
| Compose for TV | Active | Primary UI |
| Navigation3 | Installed, **stable** (Nov 2025) | Migrate from when-expr when back stack needed |
| GeckoView 141.x | Installed, unused | Use `AndroidView {}` wrapper in Compose |
| Hilt 2.57 | Installed, **disabled** | Uncomment plugin when DI needed |
| Coroutines 1.10.2 | Active | viewModelScope, Flows |

## API Key Storage
```
# local.properties (git-ignored — never commit)
NASA_API_KEY=your_key_here

# app/build.gradle.kts — inject into BuildConfig
buildConfigField("String", "NASA_API_KEY", "\"${properties["NASA_API_KEY"] ?: "DEMO_KEY"}\"")

# Usage in code
val key = BuildConfig.NASA_API_KEY
```

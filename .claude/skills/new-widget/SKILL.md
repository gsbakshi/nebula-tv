---
name: new-widget
description: Scaffold a new home screen widget for Nebula — NASA imagery, RSS feed, weather, calendar, or custom data source. Includes data source, ViewModel with lifecycle-aware refresh, and Composable UI.
---

Create a new widget. Ask for:
1. **Widget name** (e.g., "NasaApod", "RssFeed", "Weather")
2. **Data source** (REST API / Android ContentProvider / local)
3. **Refresh interval** (Daily / 30min / 15min / Real-time observer)

## Files to Create

### 1. `app/src/main/java/com/example/nebula/widgets/{Name}Widget.kt`
The Composable UI for the widget. Uses `WidgetState<T>` pattern with Loading/Success/Stale/Error states. Stale state shows cached data + a "last updated X min ago" indicator.

### 2. `app/src/main/java/com/example/nebula/widgets/{Name}DataSource.kt`
Data fetching layer:
- REST APIs: OkHttp (already in GeckoView transitive deps) or Retrofit if added
- ContentProvider: Calendar, Contacts via context.contentResolver
- NASA APOD: `https://api.nasa.gov/planetary/apod?api_key=BuildConfig.NASA_API_KEY`
- Weather: `https://api.open-meteo.com/v1/forecast` (free, no key)
- RSS: Parse with `XmlPullParser` or ROME library

### 3. `app/src/main/java/com/example/nebula/widgets/{Name}ViewModel.kt`
- `MutableStateFlow<WidgetState<T>>` initialized with `Loading`
- `repeatOnLifecycle(Lifecycle.State.STARTED)` for refresh loop
- `SupervisorJob` scope (failure isolated)
- Stale-on-error: preserve last `Success` data when refresh fails
- `SavedStateHandle` for persistence across config changes

## Template Reference
See `WidgetTemplate.kt` in this directory.

## Architecture Reference
Consult the `widget-architect` agent for lifecycle patterns, API rate limiting, and caching strategy.

## After Scaffolding
1. Add `BuildConfig` fields for any API keys in `app/build.gradle.kts`
2. Add keys to `local.properties` (git-ignored)
3. Run `./gradlew assembleDebug` to verify
4. Run the `compose-performance-reviewer` agent on the new widget
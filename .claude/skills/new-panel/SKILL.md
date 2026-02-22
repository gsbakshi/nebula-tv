---
name: new-panel
description: Scaffold a new feature panel for the Nebula launcher — creates Panel.kt, ViewModel, updates ViewScreen enum and LauncherScreen routing, and adds a nav bar item.
---

Create a new feature panel. If the panel name isn't provided, ask: "What is the panel name? (e.g., Search, Settings, Favorites)"

## Files to Create / Modify

Given panel name `{Name}` (e.g., "Search"):

### 1. Create `app/src/main/java/com/example/nebula/{name_lowercase}/Panel.kt`
Use the template in `.claude/skills/new-panel/PanelTemplate.kt`. Replace all `PANEL_NAME` placeholders with the actual name. The Panel composable must:
- Accept `Modifier` parameter
- Wrap content in `PanelView` (glass-morphism container)
- Use `collectAsStateWithLifecycle()` for ViewModel state

### 2. Create `app/src/main/java/com/example/nebula/{name_lowercase}/{Name}ViewModel.kt`
Standard ViewModel with `StateFlow<{Name}State>` sealed class:
- `Loading` — initial state
- `Success(data: ...)` — populated state
- `Error(message: String)` — failure state

### 3. Update `constants/Screen.kt`
Add the new panel to `ViewScreen` enum. Follow the existing pattern.

### 4. Update `ui/components/core/Launcher.kt`
Add branch to the `when(currentView)` block.

### 5. Update `ui/components/navigation/Bar.kt`
Add a `NavigationItem` for the new panel. The nav bar uses icons from `androidx.compose.material.icons`.

## Template Reference
See `PanelTemplate.kt` in this directory for the base structure.

## After Scaffolding
1. Run `./gradlew assembleDebug` to verify compilation
2. Run the tv-navigation-auditor agent to verify D-pad focus is in place
3. The panel starts as a placeholder — feature implementation is separate

---
name: tv-patterns
description: Android TV Compose patterns — D-pad focus, FocusRequester, Navigation3 back stack, TV Material3 components, key event handling, and TV-specific UX patterns for Nebula.
user-invocable: false
---

# Android TV Compose Patterns — Nebula Reference

## 1. Focus — The Golden Rule
Every interactive element must be visually focused AND keyboard-activatable.
TV users cannot tap. D-pad Center (KEYCODE_DPAD_CENTER) = click.

### Option A: Use TV Material3 (preferred)
```kotlin
import androidx.tv.material3.Card
import androidx.tv.material3.Button

// TV Card handles focus, scale animation, and OK key automatically
Card(onClick = { /* fires on D-pad center too */ }) { content() }
Button(onClick = { }) { Text("Action") }
```

### Option B: Custom Focus with Scale Animation ("Focus as State" pattern)
```kotlin
var isFocused by remember { mutableStateOf(false) }
val scale by animateFloatAsState(if (isFocused) 1.05f else 1f, label = "scale")

Box(
    modifier = Modifier
        .scale(scale)
        .border(
            width = if (isFocused) 2.dp else 0.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(8.dp)
        )
        .onFocusChanged { isFocused = it.isFocused }
        .focusable()
        .clickable { performAction() }
)
```

## 2. ⚠️ Critical Modifier Order Rule
`focusRequester()` MUST appear BEFORE any modifier that adds focusability:

```kotlin
// ✅ CORRECT — focusRequester before focusable
Modifier
    .focusRequester(focusRequester)
    .focusable()

// ❌ WRONG — focusRequester will silently fail to attach
Modifier
    .focusable()
    .focusRequester(focusRequester)
```

## 3. Auto-Focus on Panel Entry
Always request focus on the first item when a panel becomes visible:
```kotlin
val firstItemFocus = remember { FocusRequester() }

LaunchedEffect(Unit) {
    runCatching { firstItemFocus.requestFocus() }  // runCatching handles "no focused item" gracefully
}

// CORRECT ORDER: focusRequester before focusable
Box(modifier = Modifier
    .focusRequester(firstItemFocus)  // ← first
    .focusable()                      // ← second
)
```

## 4. focusGroup — Fix Erratic Focus in Grids
Without `focusGroup`, D-pad in lazy lists "jumps erratically" when items recompose during scroll.
Wrap each row in `focusGroup()` to contain traversal:

```kotlin
TvLazyVerticalGrid(columns = TvGridCells.Fixed(5)) {
    // Group items into rows using focusGroup on the row wrapper
    items(apps, key = { it.packageName }) { app ->
        AppItem(app, modifier = Modifier.focusable())
    }
}

// For explicit row-level grouping:
Row(modifier = Modifier.focusGroup()) {
    rowItems.forEach { item ->
        ItemCard(item, modifier = Modifier.focusable())
    }
}
```

## 5. Directional Focus Control
Block focus from escaping a zone in a specific direction:
```kotlin
// Nav bar: prevent focus escaping upward past the nav bar
Modifier.focusProperties {
    up = FocusRequester.Cancel    // blocks upward exit
    down = contentAreaFocusRequester  // routes focus into content
}

// Content area: prevent focus going back into nav bar accidentally
Modifier.focusProperties {
    up = FocusRequester.Cancel  // or link explicitly to nav bar item
}
```

## 6. Navigation3 Back Stack (Stable Nov 2025)

Two patterns — choose based on whether you need process-death survival:

### Simple (no serialization needed — good for Nebula's ViewScreen enum)
```kotlin
// Plain SnapshotStateList — no @Serializable required
val backStack = remember { mutableStateListOf<Any>(ViewScreen.WIDGETS) }

NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },  // ← required parameter
    entryProvider = { key ->
        when (key) {
            is ViewScreen.WIDGETS  -> NavEntry(key) { WidgetAreaPanel() }
            is ViewScreen.APP_GRID -> NavEntry(key) { AppGridPanel() }
            is ViewScreen.BROWSER  -> NavEntry(key) { BrowserPanel() }
            else -> NavEntry(Unit) { /* unknown */ }
        }
    }
)
```

### Saveable (survives config change + process death)
```kotlin
// Keys MUST implement NavKey AND have @Serializable
@Serializable data object Home : NavKey
@Serializable data class Product(val id: String) : NavKey

val backStack = rememberNavBackStack(Home)  // persists across rotation + process death

NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider = entryProvider {           // DSL style
        entry<Home> { HomeScreen() }
        entry<Product> { key -> ProductScreen(key.id) }
    }
)
```

// Navigate
backStack.add(ViewScreen.BROWSER)
backStack.removeLastOrNull()  // go back

Note: Current Nebula uses `when(currentView)` with `MutableState<ViewScreen>`.
Migrate to Navigation3 when back stack behavior is needed (browser history, settings drill-down).

## 4. Key Event Handling
```kotlin
// Intercept before child components see it
Modifier.onPreviewKeyEvent { event ->
    when {
        event.key == Key.DirectionCenter && event.type == KeyEventType.KeyDown -> {
            performPrimaryAction(); true  // consumed
        }
        event.key == Key.Back -> {
            navigateBack(); true
        }
        else -> false  // pass through to children
    }
}

// After child components (for supplementary handling)
Modifier.onKeyEvent { event ->
    if (event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
        // custom behavior
        true
    } else false
}
```

## 5. App Grid — LazyVerticalGrid (Standard Compose)
⚠️ `TvLazyVerticalGrid` was **removed** in `tv-foundation 1.0.0-alpha10+`. Use standard `LazyVerticalGrid` — TV D-pad focus is handled by TV Material3 Card items, not the grid itself.

```kotlin
// tv-foundation 1.0.0-alpha12 only contains: ExperimentalTvFoundationApi, TvImeOptions, TvKeyboardAlignment
// TvLazyVerticalGrid / TvGridCells no longer exist in this version

LazyVerticalGrid(
    columns = GridCells.Fixed(5),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {
    itemsIndexed(apps, key = { _, app -> app.packageName }) { index, app ->
        AppItem(
            appInfo = app,
            modifier = if (index == 0) Modifier.focusRequester(firstItemFocus) else Modifier,
            onAppClick = { startActivity(it.launchIntent) }
        )
    }
}
```

## 6. collectAsStateWithLifecycle — Always Use This
```kotlin
// ✅ Stops collecting when launcher is backgrounded — saves battery/CPU
val state by viewModel.uiState.collectAsStateWithLifecycle()

// ❌ Never use this in Compose — always collects regardless of lifecycle
val state by viewModel.uiState.collectAsState()
```

## 7. TV Remote Key Reference
| Key | KeyCode | Usage |
|-----|---------|-------|
| OK / D-pad Center | KEYCODE_DPAD_CENTER | Primary action |
| D-pad Up/Down/Left/Right | KEYCODE_DPAD_* | Navigation |
| Back | KEYCODE_BACK | Go back / dismiss |
| Home | KEYCODE_HOME | Return to launcher |
| Play/Pause | KEYCODE_MEDIA_PLAY_PAUSE | Media control |
| Fast Forward | KEYCODE_MEDIA_FAST_FORWARD | Skip |
| Rewind | KEYCODE_MEDIA_REWIND | Skip back |

## 8. GeckoView in Compose (AndroidView Bridge)
```kotlin
@Composable
fun BrowserView(session: GeckoSession, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { ctx -> GeckoView(ctx).also { it.setSession(session) } },
        update = { view ->
            if (view.session !== session) {
                view.releaseSession()
                view.setSession(session)
            }
        },
        modifier = modifier.fillMaxSize()
    )
}
```
GeckoView is a SurfaceView — it cannot be a Composable directly.
---
name: new-composable
description: Scaffold a TV-optimized reusable Composable with D-pad focus handling, scale animation on focus, TV Material3 patterns, and a @Preview configured for 1080p TV dark background.
---

Create a new reusable Composable. Ask for:
1. **Name** (e.g., `ChannelCard`, `AppTile`, `NewsHeadline`, `FocusableChip`)
2. **Interactive?** (has onClick/select behavior) or decorative
3. **Location**: `ui/components/core/` (generic reusable) or a feature package

## What to Generate

### For an interactive Composable:
```kotlin
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ComponentName(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    // ... your params
) {
    // Option A: Use TV Card (focus handled automatically)
    androidx.tv.material3.Card(
        onClick = onClick,
        modifier = modifier
    ) {
        // content
    }

    // Option B: Custom focus with scale animation
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1.0f,
        label = "focus_scale"
    )
    Box(
        modifier = modifier
            .scale(scale)
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = if (isFocused) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable(onClick = onClick)
    )
}
```

### Preview Setup (always include):
```kotlin
@Preview(
    showBackground = true,
    backgroundColor = 0xFF0D0D1A,  // Nebula dark space background
    widthDp = 320,
    heightDp = 180,
    name = "ComponentName — TV 1080p"
)
@Composable
private fun ComponentNamePreview() {
    NebulaTheme {
        ComponentName(onClick = {})
    }
}
```

## TV Material3 Component Cheat Sheet
Prefer these over standard Material3 when available:
- `tv.material3.Card` — auto focus, ripple, scale
- `tv.material3.Button` — D-pad safe, handles OK key
- `tv.material3.ListItem` — standard TV list row with focus
- `tv.material3.Surface` — base focusable container
- `tv.material3.Text` — no difference, but use for consistency

After generating, run the `tv-navigation-auditor` agent to verify focus compliance.

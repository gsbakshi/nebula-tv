#!/usr/bin/env python3
"""
NEBULA FACTORY — Zone 1: Post-Edit TV Focus Guard
Runs after every Edit/Write tool call on Kotlin files.
Detects interactive Composables that may be missing D-pad focus handling.
This is critical: touchscreen is required=false for Nebula.
Input: JSON from stdin with tool call data.
Exit 0 always (warning only, non-blocking).
"""
import sys
import json
import re

try:
    data = json.load(sys.stdin)
except (json.JSONDecodeError, Exception):
    sys.exit(0)

fp = data.get("file_path", "")
if not fp.endswith(".kt"):
    sys.exit(0)

# Combine new content from Edit (new_string) or Write (content)
new_content = data.get("new_string", "") + data.get("content", "")

if "@Composable" not in new_content:
    sys.exit(0)

# Patterns indicating interactive behavior (needs D-pad support)
click_patterns = [
    "clickable",
    "onClick =",
    "onClick = {",
    "selectable",
    "toggleable",
]

# Patterns indicating TV-aware focus handling is already present
tv_focus_patterns = [
    "focusable",
    "FocusRequester",
    "onFocusChanged",
    "hasFocus",
    "androidx.tv",
    ".tv.material",
    "tvClickable",
    "FocusDirection",
    "BringIntoViewRequester",
    "onKeyEvent",
    "onPreviewKeyEvent",
]

has_click = any(p in new_content for p in click_patterns)
has_tv_focus = any(p in new_content for p in tv_focus_patterns)

# Count how many new @Composable functions were added
new_composables = re.findall(r"@Composable\s+(?:fun|private fun)", new_content)

if has_click and not has_tv_focus and new_composables:
    print("📺 TV-FOCUS WARNING ─────────────────────────────────", file=sys.stderr)
    print("   New @Composable with interactive behavior detected.", file=sys.stderr)
    print("   Touchscreen is required=false — D-pad must work.", file=sys.stderr)
    print("", file=sys.stderr)
    print("   Options:", file=sys.stderr)
    print("   1. Use androidx.tv.material3.Card / Button (focus built-in)", file=sys.stderr)
    print("   2. Add Modifier.focusable() + onFocusChanged { }", file=sys.stderr)
    print("   3. Add FocusRequester for programmatic focus control", file=sys.stderr)
    print("─────────────────────────────────────────────────────", file=sys.stderr)

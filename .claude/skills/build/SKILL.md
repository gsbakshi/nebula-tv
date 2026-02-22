---
name: build
description: Build the Nebula debug APK with Gradle and parse the output for errors, warnings, and diagnostics.
---

Run the debug build:

```bash
./gradlew assembleDebug
```

Parse the output and report in this format:

## Build Result
**Status**: SUCCESS ✓ or FAILED ✗
**Duration**: (from Gradle output)
**Tasks**: X executed, Y up-to-date, Z from cache

## Errors (if FAILED)
For each error, provide:
- **File**: `path/to/File.kt:line`
- **Error**: exact message
- **Likely cause**: brief diagnosis
- **Fix**: concrete suggestion

## Warnings to Address
List any Kotlin/Compose compiler warnings that indicate real issues (ignore deprecation noise unless it's a TV API).

## Performance Notes
If more than 5 tasks re-ran (not UP-TO-DATE), note what changed and whether incremental compilation is working.

## Common Nebula Build Failures to Diagnose
- `Unresolved reference: ExperimentalTvMaterial3Api` → missing `@OptIn` annotation
- GeckoView resolve failure → check Mozilla Maven repository in `settings.gradle.kts`
- `Compose compiler version mismatch` → check Kotlin ↔ Compose BOM compatibility
- `Hilt` processor errors → Hilt plugin is in `libs.versions.toml` but not applied — check `build.gradle.kts`
- `Cannot access class 'androidx.navigation3...'` → Navigation3 alpha dependency conflict

Do not suggest `./gradlew clean` unless you see a specific reason (stale resources, generated code conflict).
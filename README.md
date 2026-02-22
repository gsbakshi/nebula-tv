# Nebula

A next-generation Android TV launcher — app grid, Firefox-powered browser, live widgets, and space-inspired dynamic backgrounds, all built for remote navigation.

> **Status:** Active development — pre-1.0, not yet production ready.

---

## Features

- **App Grid** — browse and launch all installed apps with D-pad navigation
- **GeckoView Browser** — full Firefox engine with URL bar, back/forward, progress indicator
- **Widget Home** — RSS feeds, weather, calendar, and real-time content *(in progress)*
- **Dynamic Backgrounds** — live NASA/astronomy imagery and screensaver mode *(in progress)*

## Requirements

- Android TV or Google TV device (Android 8.0+, API 26+)
- All navigation is D-pad/remote first — touchscreen optional

## Building

**Prerequisites:** JDK 18, Android Studio Ladybug or newer.

```bash
# Clone
git clone https://github.com/gsbakshi/nebula-tv.git
cd nebula-tv

# Build debug APK (outputs to app/build/outputs/apk/debug/)
./gradlew assembleDebug

# Build + install directly to a connected device
./gradlew installDebug

# Lint + tests
./gradlew check
```

> If building from the terminal, make sure `JAVA_HOME` points to JDK 18.

### Device note

The debug APK filters to `armeabi-v7a` by default to keep the GeckoView bundle under 200 MB. For other ABIs, adjust `abiFilters` in `app/build.gradle.kts`.

## Architecture

Single-module MVVM app using **Jetpack Compose for TV**.

```
app/src/main/java/com/example/nebula/
├── MainActivity.kt         Entry point; renders LauncherScreen
├── ViewModel.kt            AppGridViewModel (installed app list)
├── NebulaApplication.kt    Singleton GeckoRuntime
├── browser/                GeckoView browser panel + ViewModel
├── apps/                   App grid panel
├── home/                   Widget home panel (placeholder)
├── backgrouds/             Dynamic backgrounds (placeholder)
└── ui/
    ├── components/         Shared composables (PanelView, NavBar)
    └── theme/              Nebula design tokens
```

Navigation between panels is driven by a `ViewScreen` enum. See [`CLAUDE.md`](CLAUDE.md) for full architecture notes.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for setup instructions, branch conventions, and code guidelines.

## Security

To report a vulnerability, see [SECURITY.md](SECURITY.md). Please do not open a public issue.

## License

[Mozilla Public License 2.0](LICENSE.md)

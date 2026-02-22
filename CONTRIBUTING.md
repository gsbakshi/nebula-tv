# Contributing to Nebula

Thanks for your interest in contributing! Here's everything you need to get started.

## Prerequisites

| Tool | Version |
|------|---------|
| Android Studio | Ladybug or newer |
| JDK | 18 (Temurin recommended) |
| Android TV device or emulator | Android 8.0+ (API 26+) |

> **Tip:** Set `JAVA_HOME` to your JDK 18 installation before running Gradle commands from the terminal.

## Getting started

```bash
git clone https://github.com/gsbakshi/nebula-tv.git
cd nebula-tv
./gradlew assembleDebug        # build
./gradlew installDebug         # build + install to connected device
```

## Development workflow

1. **Fork** the repo and create a branch from `main`
2. **Name your branch** using one of these prefixes:
   - `feat/short-description` — new feature
   - `fix/short-description` — bug fix
   - `chore/short-description` — tooling, deps, docs
   - `refactor/short-description` — internal restructuring
3. **Make your changes** — see code conventions below
4. **Verify locally** before pushing:
   ```bash
   ./gradlew lint     # must pass with no new errors
   ./gradlew test     # unit tests
   ```
5. **Open a PR** against `main` — the PR template will guide you through the checklist

CI runs automatically on every PR. Both **Build & Lint** and **Unit Tests** must pass before a PR can be merged.

## Code conventions

- **Kotlin** — follow standard Kotlin idioms; avoid Java-style patterns
- **Compose data classes** — annotate with `@Immutable` for Compose skip optimisation
- **ViewModels** — always extend `AndroidViewModel` (never pass `Context` directly)
- **GeckoView layout** — toolbar must be in a `Column` row **above** `GeckoViewComposable`, never z-stacked on top. GeckoView is a `SurfaceView` and punches through the compositor.
- **Coroutines** — use `stateIn(SharingStarted.WhileSubscribed(5_000))` for UI state; always rethrow `CancellationException`; wrap external calls in `withTimeout`
- **D-pad navigation** — every interactive element must be reachable and clearly focused via remote. Test on a real TV or the Android TV emulator.

For a full reference, run `/project-conventions` in Claude Code.

## Adding a new panel

Run `/new-panel` in Claude Code for a guided scaffold with the correct template.

## Project structure

```
app/src/main/java/com/example/nebula/
├── browser/        GeckoView browser panel
├── apps/           App grid launcher panel
├── home/           Widget home panel (in progress)
├── backgrouds/     Dynamic backgrounds (in progress)
└── ui/
    ├── components/ Shared composables (PanelView, NavBar, …)
    └── theme/      Nebula design tokens
```

## Reporting bugs and requesting features

Please use the [issue templates](https://github.com/gsbakshi/nebula-tv/issues/new/choose) rather than opening a blank issue. For questions and discussions, use [GitHub Discussions](https://github.com/gsbakshi/nebula-tv/discussions).

## License

By contributing, you agree that your contributions will be licensed under the [Mozilla Public License 2.0](LICENSE.md).

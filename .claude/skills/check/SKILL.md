---
name: check
description: Run full Gradle check suite — lint, unit tests, and code quality — then summarize what needs attention.
---

Run the full check suite:

```bash
./gradlew check
```

Parse and summarize:

## Lint Results
**Total**: X errors, Y warnings

List **Error** severity issues (these block release builds):
- `LintCheck` at `file:line` — description — suggested fix

Highlight TV-specific lint issues:
- `FocusTraversalOrder` — focus traversal not declared for complex layouts
- `ClickableViewAccessibility` — clickable without focus handling
- `HardcodedText` — strings not in `strings.xml`

Report output location: `app/build/reports/lint-results-debug.html`

## Test Results
**Status**: X passed, Y failed, Z skipped

If tests exist and fail, show:
- Test name and class
- Failure message / stack trace (abbreviated)
- Suggested fix

If **no tests exist yet** (current state of project):
> No unit tests written yet. The project has `hilt-android-testing` and `ui-test-junit4` ready.
> First test to write: `AppGridViewModelTest` — verify that `loadApps()` populates the StateFlow.
> See `.claude/skills/test-patterns/` for guidance (once created).

## Priority Action List
Top 3 issues to fix, ordered by severity and effort.
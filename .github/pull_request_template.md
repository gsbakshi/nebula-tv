## Summary

<!-- What does this PR do? Why? 2-3 bullet points. -->

-
-

## Type of change

<!-- Check all that apply -->

- [ ] Bug fix
- [ ] New feature
- [ ] Refactor / cleanup
- [ ] Docs / comments
- [ ] CI / tooling

## Related issues

<!-- Closes #123 -->

## Test plan

<!-- How did you verify this works? Check what applies. -->

- [ ] Built and installed on a physical Android TV device
- [ ] D-pad navigation tested (all interactive elements reachable)
- [ ] `./gradlew lint` passes with no new errors
- [ ] `./gradlew test` passes

## Screenshots / recordings

<!-- For UI changes, attach a photo or screen recording from the TV. -->

## Checklist

- [ ] No direct commits to `main` — changes are scoped to this branch
- [ ] New Kotlin files use `@Immutable` on data classes passed to Compose
- [ ] No `Context` passed into `ViewModel` constructors (use `AndroidViewModel`)
- [ ] GeckoView: toolbar stays in a `Column` row above `GeckoViewComposable` (never z-stacked)

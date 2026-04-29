# Jetpack Compose Audit Report

Target: `D:\workspace\project\GSYGithubAppCompose`
Date: 2026-04-29
Scope: `app`, `core/ui`, `data`, `feature/*` production Compose sources
Excluded from scoring: `**/src/test/**`, `**/src/androidTest/**`, generated build outputs except Compose compiler reports
Confidence: High
Overall Score: 58/100

## Scorecard

| Category | Score | Weight | Status | Notes |
|----------|-------|--------|--------|-------|
| Performance | 4/10 | 35% | needs work | Many safe list/composition fixes landed, but compiler reports still show 50 unstable classes, so the rubric caps performance at 4. |
| State management | 7/10 | 25% | solid | Lifecycle-aware collection and saveable dialog state are improved; remaining risk is broader UI-state/model stability and CompositionLocal-heavy detail screens. |
| Side effects | 7/10 | 20% | solid | Composition-time loading and stale paging callbacks were fixed; request cancellation/old-result guards were added for repo detail readme/file flows. |
| Composable API quality | 6/10 | 20% | needs work | Shared components now expose more modifiers, but raw domain models, direct navigator access, missing previews, and some strings remain. |

## What Changed Since The Previous Audit

- Previous audit score: `46/100`.
- Current audit score: `58/100`.
- Main improvement: the high-risk correctness issues around lifecycle collection, composition-time loading, branch/file reloads, stale request results, paging side effects, and saveable local dialog state were addressed.
- The performance score did not rise because the measured unstable-class count is still above the audit cap. This is expected until the project moves more screen/component APIs to stable UI models or immutable collection types.

## Notes And Limits

- Compiler diagnostics used: yes.
- Reports were regenerated with the skill init script: `./gradlew.bat :app:assembleDebug --init-script C:/Users/Asher.Guo/.codex/skills/compose_skill/scripts/compose-reports.init.gradle --no-daemon --quiet`.
- Module-wide aggregate from generated `*-module.json`: `253/386 = 65.5%` skippable restartable composables.
- Named-only aggregate from generated `*-composables.csv`: `67/67 = 100.0%` skippable named restartable composables.
- Unstable classes found in generated `*-classes.txt`: `50`.
- Strong Skipping appears active: all named restartable composables in the generated CSV reports are skippable, and no named non-skippable restartable composable remains in the measured surface.
- Material 3, visual design tokens, accessibility scoring, and UI-test coverage are out of scope for this v1 Compose audit.

## Critical Findings

1. **Performance is still capped by unstable model boundaries**
   - Why it matters: Strong Skipping helps, but broad unstable model and collection parameters still make recomposition behavior harder to reason about and keep the compiler diagnostics noisy.
   - Evidence: generated `*-classes.txt` reports currently contain `50` unstable classes; shared/screen APIs still pass raw model or list state through paths such as `core/ui/src/main/java/com/shuyu/gsygithubappcompose/core/ui/components/UserItem.kt`, `core/ui/src/main/java/com/shuyu/gsygithubappcompose/core/ui/components/RepositoryItem.kt`, `feature/detail/src/main/java/com/shuyu/gsygithubappcompose/feature/detail/info/RepositoryDetailInfoHeader.kt`, and multiple feature `UiState` containers.
   - Fix direction: introduce stable presentation models for hot/shared UI boundaries and prefer immutable collection types where lists cross composable APIs.
   - References: https://developer.android.com/develop/ui/compose/performance/stability/fix

2. **Release performance hygiene is still incomplete**
   - Why it matters: compiler-level skippability is only one part of perceived performance. Startup and scroll performance should also be protected with release-oriented measurement and baseline profiles.
   - Evidence: no dedicated baseline profile module/config was found in this audit pass; release performance hardening remains a follow-up item rather than part of this safe Compose correctness batch.
   - Fix direction: add baseline profile coverage for launch and core navigation paths, then validate release minify/R8 behavior separately from UI correctness changes.
   - References: https://developer.android.com/develop/ui/compose/performance/baseline-profiles

3. **Composable API quality improved but remains mixed**
   - Why it matters: reusable UI is easier to compose, preview, and test when APIs expose `modifier`, avoid hidden navigation dependencies, and can render without app-wide ambient state.
   - Evidence: this pass added modifiers to several shared components, but components such as `RepositoryItem` and `EventItem` still keep default direct `LocalNavigator` behavior for compatibility, and extracted component previews are still sparse.
   - Fix direction: continue migrating shared components toward callback-only APIs and add focused `@Preview` coverage for `core/ui` components.
   - References: https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md, https://developer.android.com/develop/ui/compose/tooling/previews

## Fixed In This Pass

- Replaced plain Android UI `collectAsState()` usage with lifecycle-aware collection across the visible Compose screen surface. References: https://developer.android.com/develop/ui/compose/state
- Moved list screen data loading out of the composable body and into a keyed effect. References: https://developer.android.com/develop/ui/compose/side-effects
- Added stable lazy-list keys and `contentType` where it was safe to do without changing behavior. References: https://developer.android.com/develop/ui/compose/lists
- Reworked pull-to-load-more from derived state keyed effects into `snapshotFlow` with current callback/state capture, avoiding stale callback capture and repeated bottom-trigger loads. References: https://developer.android.com/develop/ui/compose/side-effects
- Added saveable dialog state for markdown and profile editing flows, while preserving cursor selection across prop-driven text updates. References: https://developer.android.com/develop/ui/compose/state
- Added request cancellation and stale-result guards for repo detail readme/file loading when repository, branch, default branch, or path changes. References: https://developer.android.com/develop/ui/compose/side-effects
- Cached date parsing/formatting in composables where inputs are stable, reducing repeated composition work. References: https://developer.android.com/develop/ui/compose/performance/bestpractices
- Added `modifier` seams to several reusable components and localized the top app bar back content description. References: https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md, https://developer.android.com/develop/ui/compose/resources

## Performance Ceiling Check

```text
named-only skippable% = 67/67 = 100.0% -> no cap from named skippability
unstable classes = 50 -> ">=8 unstable classes used as params" band -> cap at 4
qualitative score after safe fixes: 6
applied performance score: 4
```

The named-only metric is healthy, but the unstable-class count is still far above the threshold. That is why performance remains `4/10` even after fixing several concrete list/composition issues.

## Prioritized Next Fixes

1. **Stabilize UI-state and shared component inputs**
   - Target paths: `feature/*/*UiState`, `core/ui/src/main/java/com/shuyu/gsygithubappcompose/core/ui/components/*Item.kt`, `feature/detail/src/main/java/com/shuyu/gsygithubappcompose/feature/detail/info/RepositoryDetailInfoHeader.kt`.
   - Suggested approach: map raw network/domain models to small stable UI models at ViewModel boundaries, and use immutable collection types for lists that cross composable APIs.
   - References: https://developer.android.com/develop/ui/compose/performance/stability/fix

2. **Finish decoupling reusable components from navigation**
   - Target paths: `core/ui/src/main/java/com/shuyu/gsygithubappcompose/core/ui/components/RepositoryItem.kt`, `core/ui/src/main/java/com/shuyu/gsygithubappcompose/core/ui/components/EventItem.kt`, avatar/user/repository shared component call sites.
   - Suggested approach: keep app navigation in screen/event handlers and make shared components callback-driven by default.
   - References: https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md, https://developer.android.com/develop/ui/compose/navigation

3. **Add preview and release performance coverage**
   - Target paths: `core/ui` previews, app launch/navigation baseline profile coverage, release build configuration.
   - Suggested approach: add focused previews for shared components first, then add a baseline profile for launch, home, search, and repo-detail entry paths.
   - References: https://developer.android.com/develop/ui/compose/tooling/previews, https://developer.android.com/develop/ui/compose/performance/baseline-profiles

4. **Plan a separate Material 3/design-system audit**
   - This audit intentionally did not score design tokens, colors, typography, or accessibility. Those should be handled separately so they do not get mixed into correctness/performance changes.
   - References: https://developer.android.com/develop/ui/compose/designsystems/material3

# Jetpack Compose Audit Report

Target: `D:\workspace\project\GSYGithubAppCompose`
Date: 2026-04-29
Scope: `app`, `core/ui`, `data`, `feature/*` production Compose sources
Excluded from scoring: `**/src/test/**`, `**/src/androidTest/**`, generated build outputs except Compose compiler reports
Confidence: High
Overall Score: 80/100

## Scorecard

| Category | Score | Weight | Status | Notes |
|----------|-------|--------|--------|-------|
| Performance | 8/10 | 35% | solid | Compiler diagnostics now show 100% named skippability and zero unstable composable parameters after stability configuration and API cleanup. |
| State management | 8/10 | 25% | solid | Lifecycle-aware collection, saveable dialog state, request cancellation, and stale-result guards cover the highest-risk state paths. |
| Side effects | 8/10 | 20% | solid | Composition-time loading was removed, pull-to-load-more is state-aware, and navigation side effects are more screen-owned. |
| Composable API quality | 8/10 | 20% | solid | Shared components gained modifiers, previews, resource-backed strings, stable compiler boundaries, and `RepositoryItem` is callback-driven. |

## What Changed Since The Previous Audit

- Previous audit score: `46/100`.
- Current audit score: `80/100`.
- Main improvement: the high-risk correctness issues around lifecycle collection, composition-time loading, branch/file reloads, stale request results, paging side effects, and saveable local dialog state were addressed.
- Follow-up improvement: Compose compiler stability configuration plus reusable component API cleanup removed unstable composable parameters from the measured surface.

## Notes And Limits

- Compiler diagnostics used: yes.
- Reports were regenerated with the skill init script: `./gradlew.bat :app:assembleDebug --init-script C:/Users/Asher.Guo/.codex/skills/compose_skill/scripts/compose-reports.init.gradle --no-daemon --quiet --rerun-tasks`.
- Module-wide aggregate from generated `*-module.json`: `268/401 = 66.8%` skippable restartable composables.
- Named-only aggregate from generated `*-composables.csv`: `73/73 = 100.0%` skippable named restartable composables.
- Unstable classes found in generated `*-classes.txt`: `14`, but no unstable composable parameters remain in generated `*-composables.txt`.
- Strong Skipping appears active: all named restartable composables in the generated CSV reports are skippable, and no named non-skippable restartable composable remains in the measured surface.
- `compose_compiler_config.conf` is now wired through the root Gradle build for Compose modules, documenting the stability contract for immutable model and collection boundaries.
- Material 3, visual design tokens, accessibility scoring, and UI-test coverage are out of scope for this v1 Compose audit.

## Critical Findings

1. **Performance cap has been removed, but release profiling is still the next ceiling**
   - Why it matters: compiler-level stability is now healthy, so the next performance gains should come from real-device startup/scroll measurement rather than more source-only cleanup.
   - Evidence: generated reports show `73/73 = 100.0%` named skippability and no unstable composable parameters; no baseline-profile module/config is present yet.
   - Fix direction: add baseline profile coverage for launch, home, search, and repo-detail entry paths, then validate release minify/R8 behavior separately.
   - References: https://developer.android.com/develop/ui/compose/performance/stability/fix

2. **Release performance hygiene is still incomplete**
   - Why it matters: compiler reports cannot prove real startup, scroll, or detail-page latency. Those paths need release-oriented measurement.
   - Evidence: no dedicated baseline profile module/config was found in this audit pass.
   - Fix direction: add baseline profiles and macrobenchmark coverage for the most common navigation flows.
   - References: https://developer.android.com/develop/ui/compose/performance/baseline-profiles

3. **Composable API quality is solid, with remaining migration work**
   - Why it matters: reusable UI is easier to compose, preview, and test when APIs expose `modifier`, avoid hidden navigation dependencies, and can render without app-wide ambient state.
   - Evidence: `RepositoryItem` is now callback-driven and core component previews were added; `EventItem` still owns complex navigation/open-url behavior internally for compatibility.
   - Fix direction: continue migrating `EventItem` toward explicit callbacks and route its action handling from screens.
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
- Added Compose compiler stability configuration for immutable model/collection boundaries. References: https://developer.android.com/develop/ui/compose/performance/stability/fix
- Moved `RepositoryItem` navigation out to screen call sites and added focused core component previews. References: https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md, https://developer.android.com/develop/ui/compose/tooling/previews

## Performance Ceiling Check

```text
named-only skippable% = 73/73 = 100.0% -> no cap from named skippability
unstable composable parameters = 0 -> no unstable-parameter cap
module-wide skippability = 268/401 = 66.8% -> lambda-heavy aggregate, not the ceiling input
qualitative score after stability/API fixes: 8
applied performance score: 8
```

The named-only metric is healthy and the measured composable parameter surface no longer contains unstable parameters. Performance is still `8/10` rather than `9-10` because release profiling and baseline profiles are not yet present.

## Prioritized Next Fixes

1. **Finish decoupling reusable components from navigation**
   - Target paths: `core/ui/src/main/java/com/shuyu/gsygithubappcompose/core/ui/components/EventItem.kt`, avatar/user/repository shared component call sites.
   - Suggested approach: keep app navigation in screen/event handlers and make shared components callback-driven by default.
   - References: https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md, https://developer.android.com/develop/ui/compose/navigation

2. **Add release performance coverage**
   - Target paths: app launch/navigation baseline profile coverage, release build configuration.
   - Suggested approach: add a baseline profile for launch, home, search, and repo-detail entry paths.
   - References: https://developer.android.com/develop/ui/compose/tooling/previews, https://developer.android.com/develop/ui/compose/performance/baseline-profiles

3. **Plan a separate Material 3/design-system audit**
   - This audit intentionally did not score design tokens, colors, typography, or accessibility. Those should be handled separately so they do not get mixed into correctness/performance changes.
   - References: https://developer.android.com/develop/ui/compose/designsystems/material3

# Jetpack Compose Audit Report

Target: `D:\workspace\project\GSYGithubAppCompose`
Date: 2026-04-14
Scope: `app`, `core/ui`, `data`, `feature/*` production Compose sources
Excluded from scoring: `**/src/test/**`, `**/src/androidTest/**`, preview-only code (none found)
Confidence: High
Overall Score: 46/100

## Scorecard

| Category | Score | Weight | Status | Notes |
|----------|-------|--------|--------|-------|
| Performance | 4/10 | 35% | needs work | Lazy list identity is weak, and compiler reports show too many unstable shared params in `feature:detail`. |
| State management | 5/10 | 25% | needs work | State ownership is mostly clear, but lifecycle-aware collection is missing across Android UI and some local state can go stale. |
| Side effects | 6/10 | 20% | needs work | Most loading work is inside `LaunchedEffect`, but there is still composition-time work and event collection is inconsistent. |
| Composable API quality | 5/10 | 20% | needs work | Shared components skip `modifier`, accept unstable models directly, and navigation is still stringly typed on Navigation 2.8.5. |

## Critical Findings

1. **Performance: many lazy lists still render without stable item keys**
   - Why it matters: item identity is left to position, so moved/inserted items can trigger unnecessary recomposition, misplaced remembered state, and worse scroll behavior.
   - Evidence: `feature/search/src/main/java/com/shuyu/gsygithubappcompose/feature/search/SearchScreen.kt:89`, `feature/search/src/main/java/com/shuyu/gsygithubappcompose/feature/search/SearchScreen.kt:164`, `feature/profile/src/main/java/com/shuyu/gsygithubappcompose/feature/profile/ProfileShared.kt:72`, `feature/detail/src/main/java/com/shuyu/gsygithubappcompose/feature/detail/file/RepoDetailFileScreen.kt:71`
   - Fix direction: add `key = { ... }` everywhere identity matters, and stop indexing into lists when `items(list, key = ...)` or `itemsIndexed(...)` is available.
   - References: <https://developer.android.com/develop/ui/compose/lists>

2. **State management: Android screens collect flows without lifecycle awareness**
   - Why it matters: `collectAsState()` keeps collecting even when the screen is stopped, which wastes work and can keep screen state hot longer than intended.
   - Evidence: `app/src/main/java/com/shuyu/gsygithubappcompose/MainActivity.kt:49`, `feature/search/src/main/java/com/shuyu/gsygithubappcompose/feature/search/SearchScreen.kt:34`, `feature/detail/src/main/java/com/shuyu/gsygithubappcompose/feature/detail/RepoDetailScreen.kt:106`, `feature/profile/src/main/java/com/shuyu/gsygithubappcompose/feature/profile/ProfileScreen.kt:28`
   - Fix direction: add `lifecycle-runtime-compose` and switch Android UI collectors to `collectAsStateWithLifecycle()`.
   - References: <https://developer.android.com/develop/ui/compose/state>

3. **Side effects: one screen still launches data loading directly from composition**
   - Why it matters: composition should stay side-effect free. Calling `loadData(...)` in the composable body means recomposition can retrigger work unpredictably.
   - Evidence: `feature/list/src/main/java/com/shuyu/gsygithubappcompose/feature/list/ListScreen.kt:39`
   - Fix direction: move initial loading into `LaunchedEffect(userName, repoName, listType)` or into the ViewModel init path.
   - References: <https://developer.android.com/develop/ui/compose/side-effects>

4. **API quality and performance: shared UI still accepts unstable network/domain models directly**
   - Why it matters: reusable composables like `UserItem`, `CommitItem`, `ProfileHeader`, and `RepositoryDetailInfoHeader` depend on unstable models, so the compiler cannot prove stable skipping at the component boundary.
   - Evidence: `core/ui/build/compose_audit/ui-composables.txt:16`, `core/ui/build/compose_audit/ui-composables.txt:155`, `feature/detail/build/compose_audit/detail-composables.txt:26`, `feature/profile/build/compose_audit/profile-composables.txt:15`
   - Fix direction: map network/domain models to UI-specific stable models before they cross shared composable APIs, and keep ViewModels out of shared component seams.
   - References: <https://developer.android.com/develop/ui/compose/architecture>, <https://developer.android.com/develop/ui/compose/performance/stability>

## Category Details

### Performance — 4/10

**What is working**

- Compiler diagnostics are enabled and Strong Skipping is on across generated module reports, for example `feature/search/build/compose_audit/release/search-module.json:25` and `core/ui/build/compose_audit/release/ui-module.json:25`.
- Some list-heavy code already uses keys correctly, for example `feature/history/src/main/java/com/shuyu/gsygithubappcompose/feature/history/HistoryScreen.kt:46`.
- `GSYPullRefresh` uses `derivedStateOf` plus `LaunchedEffect` for load-more triggering instead of polling imperatively: `core/ui/src/main/java/com/shuyu/gsygithubappcompose/core/ui/components/GSYPullRefresh.kt:130`.

**What is hurting the score**

- Stable keys are missing in many `LazyColumn`/`LazyRow` call sites.
- Several reusable composables still take unstable models as direct parameters.
- The compiler reports show one important module with too many inferred unstable classes for a high score.

**Performance ceiling check**

```
named-only skippable% = 67/67 = 100.0% -> no cap from skippability band
feature:detail inferred unstable classes = 8 -> falls in ">=8 unstable classes used as params" band -> cap at 4
qualitative score: 6
applied score: 4 (ceiling lowered from 6)
```

Module-wide aggregate from all generated `*-module.json` reports is `255/386 = 66.1%`, while the named-only aggregate from all generated `*-composables.csv` reports is `67/67 = 100.0%`. This repo is heavily anchored by lambdas, so the named-only metric is the fairer ceiling input, but `feature:detail` still hits the unstable-class cap on its own.

**Evidence**

- `feature/search/src/main/java/com/shuyu/gsygithubappcompose/feature/search/SearchScreen.kt:89` — search history list has no `key`, so item identity falls back to position. · References: <https://developer.android.com/develop/ui/compose/lists>
- `feature/search/src/main/java/com/shuyu/gsygithubappcompose/feature/search/SearchScreen.kt:164` — repository/user result lists also omit `key`, repeating the same identity problem on bigger result sets. · References: <https://developer.android.com/develop/ui/compose/lists>
- `feature/list/src/main/java/com/shuyu/gsygithubappcompose/feature/list/ListScreen.kt:72` — `items(uiState.list.size)` indexes into the backing list instead of using keyed item APIs. · References: <https://developer.android.com/develop/ui/compose/lists>
- `feature/detail/src/main/java/com/shuyu/gsygithubappcompose/feature/detail/file/RepoDetailFileScreen.kt:71` — file browser rows are also index-based and unkeyed. · References: <https://developer.android.com/develop/ui/compose/lists>
- `core/ui/build/compose_audit/ui-composables.txt:16` — `CommitItem` takes unstable `RepoCommit`. · References: <https://developer.android.com/develop/ui/compose/performance/stability>
- `core/ui/build/compose_audit/ui-composables.txt:155` — `UserItem` takes unstable `User`. · References: <https://developer.android.com/develop/ui/compose/performance/stability>
- `feature/detail/build/compose_audit/detail-composables.txt:26` — `RepositoryDetailInfoHeader` takes unstable `RepositoryDetailModel`. · References: <https://developer.android.com/develop/ui/compose/performance/stability>
- `feature/detail/build/compose_audit/release/detail-module.json:15` — compiler report shows `feature:detail` has `8` inferred unstable classes, which triggers the rubric cap. · References: <https://developer.android.com/develop/ui/compose/performance/stability/diagnose>, <https://developer.android.com/develop/ui/compose/performance/tooling>

### State Management — 5/10

**What is working**

- Most screen-level loading still comes from ViewModels rather than ad-hoc local state, for example `feature/trending/src/main/java/com/shuyu/gsygithubappcompose/feature/trending/TrendingScreen.kt:20` and `feature/issue/src/main/java/com/shuyu/gsygithubappcompose/feature/issue/IssueScreen.kt:67`.
- Local ephemeral UI state is generally kept local, such as `openAboutDialog` and `openFeedbackDialog` in `feature/home/src/main/java/com/shuyu/gsygithubappcompose/feature/home/HomeScreen.kt:68`.

**What is hurting the score**

- Android UI code consistently uses `collectAsState()` instead of `collectAsStateWithLifecycle()`.
- `GSYMarkdownInputDialog` remembers prop-derived text state without keys, so changing `initialTitle`/`initialText` while the dialog stays composed can leave stale text on screen.
- Repo detail sub-screens read screen-wide ViewModels through `CompositionLocal`, which hides dependencies that should stay explicit at screen boundaries.

**Evidence**

- `app/src/main/java/com/shuyu/gsygithubappcompose/MainActivity.kt:49` — activity-level language flow is collected with plain `collectAsState()`. · References: <https://developer.android.com/develop/ui/compose/state>
- `feature/search/src/main/java/com/shuyu/gsygithubappcompose/feature/search/SearchScreen.kt:34` — the search screen collects eleven flows with plain `collectAsState()`, multiplying off-screen collection cost. · References: <https://developer.android.com/develop/ui/compose/state>
- `feature/detail/src/main/java/com/shuyu/gsygithubappcompose/feature/detail/RepoDetailScreen.kt:76` — multiple ViewModels are injected through `CompositionLocal`, which weakens explicit state ownership and dependency clarity. · References: <https://developer.android.com/develop/ui/compose/architecture>, <https://developer.android.com/develop/ui/compose/compositionlocal>
- `feature/detail/src/main/java/com/shuyu/gsygithubappcompose/feature/detail/file/RepoDetailFileScreen.kt:33` — child screen state depends on `LocalRepoDetailFileViewModel` rather than an explicit parameter. · References: <https://developer.android.com/develop/ui/compose/architecture>, <https://developer.android.com/develop/ui/compose/compositionlocal>
- `core/ui/src/main/java/com/shuyu/gsygithubappcompose/core/ui/components/GSYMarkdownInputDialog.kt:69` — `remember { mutableStateOf(TextFieldValue(initialTitle ?: \"\")) }` has no key, so it will not reset when `initialTitle` changes. · References: <https://developer.android.com/develop/ui/compose/state>
- `core/ui/src/main/java/com/shuyu/gsygithubappcompose/core/ui/components/GSYMarkdownInputDialog.kt:70` — `initialText` has the same stale-cache problem. · References: <https://developer.android.com/develop/ui/compose/state>

### Side Effects — 6/10

**What is working**

- Most initial loads are correctly wrapped in `LaunchedEffect`, for example `feature/profile/src/main/java/com/shuyu/gsygithubappcompose/feature/profile/ProfileScreen.kt:31`, `feature/trending/src/main/java/com/shuyu/gsygithubappcompose/feature/trending/TrendingScreen.kt:24`, and `feature/detail/src/main/java/com/shuyu/gsygithubappcompose/feature/detail/file/RepoDetailFileScreen.kt:42`.
- `GSYPullRefresh` correctly uses `LaunchedEffect(shouldLoadMore)` for reactive paging instead of mutating state during composition: `core/ui/src/main/java/com/shuyu/gsygithubappcompose/core/ui/components/GSYPullRefresh.kt:150`.

**What is hurting the score**

- `ListScreen` still performs initial loading directly in composition.
- Toast/event collection is duplicated between `BaseScreen`, `SearchScreen`, `HomeScreen`, and `RepoDetailScreen` instead of following one consistent event pattern.

**Evidence**

- `feature/list/src/main/java/com/shuyu/gsygithubappcompose/feature/list/ListScreen.kt:39` — `listViewModel.loadData(...)` is called during composition. · References: <https://developer.android.com/develop/ui/compose/side-effects>
- `data/src/main/java/com/shuyu/gsygithubappcompose/data/repository/vm/BaseScreen.kt:24` — generic toast collection is hard-coded into a wrapper via `LaunchedEffect(Unit)`, which makes event handling implicit and spreads side-effect policy across the tree. · References: <https://developer.android.com/develop/ui/compose/side-effects>
- `feature/search/src/main/java/com/shuyu/gsygithubappcompose/feature/search/SearchScreen.kt:52` — screen-specific toast collection duplicates the same responsibility already present in `BaseScreen`. · References: <https://developer.android.com/develop/ui/compose/side-effects>
- `feature/detail/src/main/java/com/shuyu/gsygithubappcompose/feature/detail/RepoDetailScreen.kt:130` — repo-detail event collection is yet another custom event pipeline instead of one screen-level convention. · References: <https://developer.android.com/develop/ui/compose/side-effects>

### Composable API Quality — 5/10

**What is working**

- There are real reusable building blocks such as `GSYCardItem`, `GSYTopAppBar`, `GSYPullRefresh`, and `GSYGeneralLoadState`, which gives the codebase actual API seams to improve instead of copy-paste UI.
- `GSYCardItem` and `GSYTopAppBar` already expose `modifier` in the conventional position: `core/ui/src/main/java/com/shuyu/gsygithubappcompose/core/ui/components/GSYCardItem.kt:18`, `core/ui/src/main/java/com/shuyu/gsygithubappcompose/core/ui/components/GSYTopAppBar.kt:18`.

**What is hurting the score**

- Many shared/reusable composables still omit `modifier`.
- Shared APIs accept raw network/domain models directly.
- Navigation still uses string routes and string interpolation even though the project is already on Navigation Compose `2.8.5`.
- No `@Preview` coverage was found for extracted UI components.

**Evidence**

- `core/ui/src/main/java/com/shuyu/gsygithubappcompose/core/ui/components/UserItem.kt:18` — `UserItem` is reusable UI but does not expose `modifier`. · References: <https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md>
- `feature/profile/src/main/java/com/shuyu/gsygithubappcompose/feature/profile/ProfileShared.kt:90` — `ProfileHeader` is shared UI without `modifier`, which limits composition and styling from callers. · References: <https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md>
- `feature/detail/src/main/java/com/shuyu/gsygithubappcompose/feature/detail/RepoDetailScreen.kt:312` — `RepoActionButton` also omits `modifier`. · References: <https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md>
- `app/src/main/java/com/shuyu/gsygithubappcompose/MainActivity.kt:60` — routes are declared as string literals like `composable(\"welcome\")` and `composable(\"repo_detail/{userName}/{repoName}\")`. · References: <https://developer.android.com/develop/ui/compose/navigation>
- `feature/search/src/main/java/com/shuyu/gsygithubappcompose/feature/search/SearchScreen.kt:172` — runtime navigation is built with string interpolation: `navigator.navigate(\"person/${user.login}\")`. · References: <https://developer.android.com/develop/ui/compose/navigation>
- `core/ui/build/compose_audit/ui-composables.txt:155` — `UserItem` takes unstable `User` directly instead of a UI model seam. · References: <https://developer.android.com/develop/ui/compose/architecture>, <https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md>
- `feature/detail/build/compose_audit/detail-composables.txt:26` — `RepositoryDetailInfoHeader` takes unstable `RepositoryDetailModel` directly. · References: <https://developer.android.com/develop/ui/compose/architecture>, <https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md>
- `core/ui/build/compose_audit/ui-composables.txt:16` — `CommitItem` takes unstable `RepoCommit` directly. · References: <https://developer.android.com/develop/ui/compose/architecture>, <https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md>

## Prioritized Fixes

1. Move `listViewModel.loadData(userName, repoName, listType)` out of composition and into `LaunchedEffect(userName, repoName, listType)` in `feature/list/src/main/java/com/shuyu/gsygithubappcompose/feature/list/ListScreen.kt:39`. References: <https://developer.android.com/develop/ui/compose/side-effects>
2. Add stable `key = { ... }` and stop index-based list rendering in `feature/search/src/main/java/com/shuyu/gsygithubappcompose/feature/search/SearchScreen.kt:89`, `feature/search/src/main/java/com/shuyu/gsygithubappcompose/feature/search/SearchScreen.kt:164`, `feature/list/src/main/java/com/shuyu/gsygithubappcompose/feature/list/ListScreen.kt:72`, and `feature/detail/src/main/java/com/shuyu/gsygithubappcompose/feature/detail/file/RepoDetailFileScreen.kt:71`. References: <https://developer.android.com/develop/ui/compose/lists>
3. Add `lifecycle-runtime-compose` and replace Android UI `collectAsState()` with `collectAsStateWithLifecycle()` starting at `app/src/main/java/com/shuyu/gsygithubappcompose/MainActivity.kt:49`, `feature/search/src/main/java/com/shuyu/gsygithubappcompose/feature/search/SearchScreen.kt:34`, `feature/detail/src/main/java/com/shuyu/gsygithubappcompose/feature/detail/RepoDetailScreen.kt:106`, and `feature/profile/src/main/java/com/shuyu/gsygithubappcompose/feature/profile/ProfileScreen.kt:28`. References: <https://developer.android.com/develop/ui/compose/state>
4. Introduce stable UI models plus `modifier` parameters for reusable components such as `core/ui/src/main/java/com/shuyu/gsygithubappcompose/core/ui/components/UserItem.kt:18`, `feature/profile/src/main/java/com/shuyu/gsygithubappcompose/feature/profile/ProfileShared.kt:90`, and `feature/detail/src/main/java/com/shuyu/gsygithubappcompose/feature/detail/RepoDetailScreen.kt:312`. References: <https://developer.android.com/develop/ui/compose/architecture>, <https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md>

## Notes And Limits

- Full-scope compiler diagnostics were generated with the skill init script under module-local `build/compose_audit/` directories.
- Weight choice: default `35/25/20/20`.
- Renormalization: none.
- Compiler diagnostics used: yes — examples include `feature/detail/build/compose_audit/release/detail-module.json`, `core/ui/build/compose_audit/ui-composables.txt`, and `feature/profile/build/compose_audit/profile-composables.txt`.
- The repo is already on Kotlin `2.2.10`, Compose Runtime `1.7.6`, and Navigation Compose `2.8.5`, so newer guidance like Strong Skipping defaults and typed navigation routes applies here.
- No `@Preview`-annotated extracted components were found during the audit.

## Suggested Follow-Up

- Run a `material-3` audit next. This repo has many direct `Color.White`, `Color.Gray`, `Color.Red`, and explicit UI styling choices, but design-system quality was intentionally out of scope for this v1 audit.

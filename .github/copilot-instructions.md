# Architecture Rules

## Modules

开发需求要，要先了解整个项目的结构和可用的模块，记住下方模块的要求和特点

### core/common
- 存放 `DataStore<Preferences>` 和用户 token 等，另外多语言文本（/res/values/ 和 /res/values-zh-rCN）和图片资源等公共资源也放在这个模块

### core/ui
- 存放所有自定义控件，主题，颜色等相关内容，特别注意，下拉刷新和加载更多控件需要统一使用 GSYPullRefresh

### core/network
- 是网络请求模块 ，网络数据的实体都在这个模块的 model/ 目录下，接口地址是 api/ 下的 GitHubApiService，同时也有 config 配置，比如  PAGE_SIZE

### core/database
- 是所有数据库模块，包括所有数据库能力，有 xxDao、xxEntiny，而每次修改数据库如果设计增删字段，需要修改增加 AppDatabase 的数据库版本

### data
- 模块是数据操作处理，包括所有 xxxRepository ，另外所有数据库的  toEntity 和 toXXX 网络数据的实体，都写在 mapper/DataMappers.kt 内统一处理
- data 模块不存放实体 Model , 实体 Model 在 core/network 的 model/ 目录下

### feature
- 模块是页面功能模块，内部每个模块每个模块的页面 xxxScreen 和 xxxViewModel

## 一个常规页面模块结构

最重要的一点， import 某个 class 的时候，要确定这个路径是否存在和正确

### 1. 模块职责划分

*   **`feature/xxx` 模块**:
  *   `xxxScreen.kt`: 负责 UI 层的展示，接收 `xxxViewModel` 提供的 UI 状态并渲染。
  *   `xxxViewModel.kt`: 负责 UI 逻辑和数据编排，管理 UI 状态，与 `xxxRepository` 交互。
*   **`data` 模块**:
  *   `xxxRepository.kt`: 负责数据访问逻辑，协调网络和数据库，实现数据缓存策略。
*   **`core/network` 模块**:
  *   `model/`: 存放网络数据实体。
  *   `api/GitHubApiService`: 定义网络接口。
*   **`core/database` 模块**:
  *   `dao/`: 存放数据访问对象 (DAO)。
  *   `entity/`: 存放数据库实体。
*   **`core/ui` 模块**:
  *   存放所有自定义控件，主题，颜色等相关内容。
  *   提供 `GSYGeneralLoadState`, `GSYPullRefresh`, `GSYTopAppBar`, `GSYNavigator`, `GSYNavHost` 等通用 UI 组件。
*   **`core/common` 模块**:
  *   存放 `DataStore<Preferences>` 和用户 token 等。
  *   多语言文本（`/res/values/` 和 `/res/values-zh-rCN`）和图片资源等公共资源。

### 2. `xxxScreen.kt` (Composable UI) 实现细节

*   **ViewModel 注入**:
  *   在 Composable 函数签名中使用 `viewModel: XxxViewModel = hiltViewModel()`。
  *   确保导入 `androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel`。
*   **UI 状态观察**:
  *   使用 `val uiState by viewModel.uiState.collectAsState()` 从 ViewModel 收集 UI 状态。
  *   `uiState` 通常是一个 `data class`，包含页面所需的所有状态信息（数据列表、加载状态、错误信息等）。
*   **初始数据加载**:
  *   使用 `LaunchedEffect(Unit) { viewModel.doInitialLoad() }` 在 Composable 首次组合时触发初始数据加载。
  *   对于需要参数的页面（如 `RepoDetailInfoScreen`），`LaunchedEffect` 的 key 应包含这些参数，例如 `LaunchedEffect(owner, name) { viewModel.loadRepoDetailInfo(owner, name) }`，以确保参数变化时重新加载数据。
*   **通用加载状态处理**:
  *   使用 `GSYGeneralLoadState` 包裹主要内容，处理首次加载、错误和重试机制。
  *   `isLoading` 参数应根据 `uiState.isPageLoading` 和数据是否为空来判断。
    ```kotlin
    GSYGeneralLoadState(
        isLoading = uiState.isPageLoading && uiState.repositories.isEmpty(), // 根据实际 UI 状态调整
        error = uiState.error,
        retry = { viewModel.refresh() }
    ) {
        // 主要内容在此处，例如 GSYPullRefresh
    }
    ```
*   **下拉刷新和加载更多**:
  *   使用 `GSYPullRefresh` 实现刷新和加载更多功能。
  *   参数如 `isRefreshing`, `onRefresh`, `isLoadMore`, `onLoadMore`, `hasMore`, `itemCount`, `loadMoreError` 都应绑定到 `uiState` 中的对应状态。
    ```kotlin
    GSYPullRefresh(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { viewModel.refresh() },
        isLoadMore = uiState.isLoadingMore, // 根据 ViewModel 状态判断是否正在加载更多
        onLoadMore = { viewModel.loadMore() },
        hasMore = uiState.hasMore,
        itemCount = uiState.repositories.size,
        loadMoreError = uiState.loadMoreError,
        contentPadding = PaddingValues(5.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 条件性 UI 渲染，例如根据 uiState.repoDetail 是否为空显示头部信息
        uiState.repoDetail?.let { headerData ->
            item {
                RepositoryDetailInfoHeader(repositoryDetailModel = headerData)
            }
        }
        // 动态内容渲染，如 SegmentedButton
        item {
            SegmentedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                items = listOf(stringResource(R.string.events), stringResource(R.string.commits)),
                selectedIndex = when (uiState.selectedItemType) {
                    RepoDetailItemType.EVENT -> 0
                    RepoDetailItemType.COMMIT -> 1
                },
                onItemSelected = { index ->
                    when (index) {
                        0 -> viewModel.setSelectedItemType(RepoDetailItemType.EVENT)
                        1 -> viewModel.setSelectedItemType(RepoDetailItemType.COMMIT)
                    }
                }
            )
        }
        items(uiState.repoDetailList) { item ->
            // 显示单个项目，通常会有一个对应的 Composable Item
            when (item) {
                is RepoDetailListItem.EventItem -> {
                    EventItem(event = item.event)
                }
                is RepoDetailListItem.CommitItem -> {
                    CommitItem(commit = item.commit)
                }
            }
        }
    }
    ```
*   **标题栏**:
  *   使用 `GSYTopAppBar` 作为页面标题栏。
*   **导航**:
  *   导入 `import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator`。
  *   通过 `val navigator = LocalNavigator.current` 获取导航器实例。
  *   使用 `navigator.navigate()` 或 `navigator.replace()` 进行页面跳转。

### 3. `xxxViewModel.kt` 实现细节

*   **继承**:
  *   必须继承 `BaseViewModel`。
  *   对于特定场景（如 `ProfileViewModel` 继承 `BaseProfileViewModel`），可以进一步抽象公共逻辑。
*   **Hilt 注解**:
  *   使用 `@HiltViewModel` 注解，确保 Hilt 可以创建其实例。
*   **依赖注入**:
  *   在构造函数中注入 `xxxRepository` (和其他必要的依赖，如 `UserPreferencesDataStore`, `StringResourceProvider`)。
*   **UI 状态管理**:
  *   定义一个 `data class` 作为 `XxxUiState`，继承 `BaseUiState`，包含页面所需的所有状态。
  *   `BaseViewModel` 会通过 `MutableStateFlow` 或类似机制暴露 `uiState` 给 UI。
  *   `XxxUiState` 示例：
      ```kotlin
      data class XxxUiState(
          val items: List<ItemModel> = emptyList(),
          // RepoDetailInfoUiState 示例，包含特定页面状态
          val repoDetail: RepositoryDetailModel? = null,
          val repoDetailList: List<RepoDetailListItem> = emptyList(),
          val owner: String? = null,
          val repoName: String? = null,
          val selectedItemType: RepoDetailItemType = RepoDetailItemType.EVENT,
          val isSwitchingItemType: Boolean = false,
          // ProfileUiState 示例
          val user: User? = null,
          val orgMembers: List<User>? = null,
          val userEvents: List<Event>? = null,
          override val isPageLoading: Boolean = false,
          override val isRefreshing: Boolean = false,
          override val isLoadingMore: Boolean = false,
          override val error: String? = null,
          override val currentPage: Int = 1,
          override val hasMore: Boolean = false,
          override val loadMoreError: Boolean = false
      ) : BaseUiState
      ```
*   **数据加载逻辑**:
  *   **`loadData` 方法**: 覆盖 `BaseViewModel` 中的 `loadData` 方法，实现具体的数据获取逻辑。
    *   参数 `initialLoad`, `isRefresh`, `isLoadMore` 用于区分加载类型。
    *   使用 `launchDataLoadWithUser` (或 `launchDataLoad`) 包装数据加载协程，它会处理加载状态的更新。
    *   调用 `xxxRepository` 的方法获取数据。
    *   通过 `repoResult.data.fold` 处理成功和失败情况。
    *   成功时，调用 `handleResult` 更新 `uiState` 中的数据列表、分页信息等。
    *   失败时，调用 `updateErrorState` 更新错误信息。
    *   **示例 (`RepoDetailInfoViewModel`)**: 在 `loadData` 中先获取详情 (`repositoryRepository.getRepositoryDetail`)，成功后再根据 `selectedItemType` (`uiState.selectedItemType`) 触发事件或提交列表的获取 (`fetchItems` 函数，内部调用 `eventRepository.getRepositoryEvents` 或 `repositoryRepository.getRepoCommits`)。
    *   **示例 (`BaseProfileViewModel`)**: 抽象 `getUserLogin` 方法，由子类实现，然后根据用户类型（`fetchedUser.type == "Organization"` 或普通用户）加载不同的数据（组织成员列表 `userRepository.getOrgMembers` 或用户事件 `userRepository.getUserEvents`）。
  *   **`doInitialLoad()`**: 触发首次数据获取。
  *   **`refresh()`**: 处理下拉刷新，通常会重置分页并重新获取第一页数据。
  *   **`loadMore()`**: 处理分页，获取下一页数据。
  *   **`commonStateUpdater`**: 在 `BaseViewModel` 的构造函数中提供，用于更新 `XxxUiState` 中的通用状态字段。
  *   **辅助函数**: 可以定义如 `collectAndHandleListResult` 这样的辅助函数来统一处理列表数据的收集和状态更新，减少重复代码。

### 4. `xxxRepository.kt` 实现细节

*   **Hilt 注解**:
  *   使用 `@Singleton` 和 `@Inject constructor` 确保 Hilt 提供单例依赖。
*   **依赖注入**:
  *   在构造函数中注入 `GitHubApiService` (来自 `core/network`) 和 `xxxDao` (来自 `core/database`)。
  *   对于需要 GraphQL 的仓库，注入 `ApolloClient` (如 `RepositoryRepository`)。
  *   对于 `UserRepository`，可能还需要注入 `UserPreferencesDataStore` 和 `AppDatabase` 用于登录/登出和数据清理。
*   **数据获取方法**:
  *   通常返回 `Flow<RepositoryResult<List<ItemModel>>>` 或 `Flow<RepositoryResult<SingleItemModel>>`。
  *   `RepositoryResult` 包含：
    *   `data`: `Result<T>` 包装的实际数据（成功或失败）。
    *   `dataSource`: `DataSource.CACHE` 或 `DataSource.NETWORK`，指示数据来源。
    *   `isDbEmpty`: `Boolean`，指示数据库是否为空。
    *   数据返回方式类似 `RepositoryResult(Result.success(networkEvents), DataSource.NETWORK, isDbEmpty)` 
  *   使用 `flow { ... }` 构建数据流。
*   **缓存优先策略**:
  *   **1. 尝试从数据库加载**:
    *   首先调用 `xxxDao` 的方法获取本地缓存数据。
    *   如果数据库不为空，则 `emit` `DataSource.CACHE` 的结果，并使用 `toXxxModel()` 映射到业务模型。
    *   对于分页数据，通常只在 `page == 1` 时检查数据库（例如 `EventRepository` 和 `RepositoryRepository` 的 `getTrendingRepositories`）。
  *   **2. 从网络获取**:
    *   发起网络请求 (通过 `GitHubApiService` 或 `ApolloClient`)。
  *   **3. 更新数据库**:
    *   成功获取网络数据后，先 `clearXxxRepos()` 清除旧数据（如果需要，特别是 `page == 1` 时），然后 `insertAll()` 插入新数据。
    *   使用 `mapper/DataMappers.kt` 中的 `toXxxEntity()` 将网络模型映射到数据库实体。
  *   **4. 发射网络数据**:
    *   `emit` `DataSource.NETWORK` 的结果，并使用 `toXxxModel()` 映射到业务模型。
  *   **5. 错误处理**:
    *   使用 `try-catch` 捕获网络请求异常，并 `emit` 失败结果。
  *   **通用辅助函数**:
    *   `UserRepository` 中提供了 `getFromCacheAndNetwork` 和 `getPaginatedFromCacheAndNetwork` 等通用函数，用于封装缓存优先和分页逻辑，鼓励复用。

### 5. 数据映射 (`mapper/DataMappers.kt`)

*   所有数据库的 `toEntity` 和网络数据的 `toXXX` 实体转换都写在 `mapper/DataMappers.kt` 内统一处理。
*   例如：`toTrendingEntity()` (网络模型 -> 数据库实体), `toTrendingRepoModel()` (数据库实体 -> 业务模型)。
*   在映射的时候，需要确保 Model 和 Entity 的字段是对应的，不要自己想象出来多余的字段

### 6. 多语言支持

*   所有面向用户的文本都应来自 `core/common/res/values/` 和 `core/common/res/values-zh-rCN/` 中定义的字符串资源。

### 7. 数据库变更注意事项

*   如果引入新的数据库表或模式更改：
  *   增加 `AppDatabase` 的版本号。
  *   创建新的 `Entity` 类。
  *   创建新的 `Dao` 接口。
  *   将新的 `Dao` 添加到 `DatabaseModule` 中提供。


## Hilt 使用注意避免：
* hilt 现在需要导入的是 androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel，不要导入错误
* Hilt 创建一个 @HiltViewModel 实例时，它首先调用该类的构造函数，并将所有依赖项（如 userRepository）作为参数传入。
* Kotlin 构造顺序: 在 Kotlin 中，当一个子类（ProfileViewModel）被实例化时，其构造过程遵循以下顺序：
  *  子类的构造函数参数被求值。
  *  父类（BaseProfileViewModel）的 init 代码块和构造函数被执行。
  *  子类（ProfileViewModel）的 init 代码块和属性初始化器被执行。
* 所以需要避免在子类的构造函数参数中传递给父类的任何方法调用，这些方法调用依赖于子类的属性，因为这些属性在父类构造期间尚未初始化。

## 导航打开新页面
- 导入的是 import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator
- 使用 core/ui 下的 GSYNavigator 和 GSYNavHost ，例如  val navigator = LocalNavigator.current
- 使用  navigator.navigate 或者  navigator.replace

## 新增数据库表需要注意
1、升级 AppDatabase 的版本号
2、添加新的 Entity 类
3、添加对应的 Dao 类
4、添加 DatabaseModule 中的 Dao 提供方法


## 注意：需要特别注意的问题
- 工作时注意当前是 windows 环境还是 macOS 环境
- D:\workspace\project\GSYGithubAppCompose\data\src\main\java\com\shuyu\gsygithubappcompose\data\repository\EventRepository.kt 是正确的路径方式 ，D:\workspace\project\GSYGithubAppCompose\data\src\main\java\com\shuyu.gsygithubappcompose\data\repository\EventRepository.kt 是错误的路径方式，不应该有 shuyu.gsygithubappcompose 这样的路径
- 不允许随意删除我的注释和无用代码
- 所有显示类型的文本内容都要多语言,不能写死,多语言在 core/common模块的 /res/values/ 和 /res/values-zh-rCN 下，需要注意中文和英文两个
- 所有模块内代码都是在  src/main/java/packageName/ 下
- 依赖添加和版本修改需要走 gradle/ 下的 libs.versions.toml 进行统一管理
- 创建代码时，要以 libs.versions.toml 里的版本为主，尽量使用正确的 API
- Icons 使用 import androidx.compose.material.icons.Icons / import androidx.compose.material.icons.filled
- 每次修改后，需要注意检查是否有这个修改的关联使用需要同步处理
- 任何修改数据库表的变动，都需要修改增加 AppDatabase 的数据库版本
- 使用控件优先判断 core/ui 有没有合适，没有合适的，考虑添加自定义的控件进去（如果符合通用情况）

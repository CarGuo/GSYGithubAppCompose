![](./logo.png)

[中文](README.md)

## An open-source native Android Github client App, providing richer features and a better experience. It aims to better manage and maintain personal Github projects daily, offering a more convenient "driving" experience～～Σ(￣。￣ﾉ)ﾉ. The project is developed with Jetpack Compose and offers a variety of counterparts for comparison:

* ### Flutter Version ( https://github.com/CarGuo/GSYGithubAppFlutter )
* ### Kotlin View Version ( https://github.com/CarGuo/GSYGithubAppKotlin )
* ### ReactNative Version ( https://github.com/CarGuo/GSYGithubApp )
* ### Weex Version ( https://github.com/CarGuo/GSYGithubAppWeex )
* ### [If cloning is too slow or images are not displaying, try downloading from Gitee](https://gitee.com/CarGuo/GSYGithubAppCompose)

| Official Account | Juejin                                                        | Zhihu                                     | CSDN                                    | Jianshu                                        |
|------------------|---------------------------------------------------------------|-------------------------------------------|-----------------------------------------|------------------------------------------------|
| GSYTech          | [Click Me](https://juejin.cn/user/582aca2ba22b9d006b59ae68/posts) | [Click Me](https://www.zhihu.com/people/carguo) | [Click Me](https://blog.csdn.net/ZuoYueLiang) | [Click Me](https://www.jianshu.com/u/6e613846e1ea) |

![Official Account QR Code](http://img.cdn.guoshuyu.cn/WeChat-Code)

```
A native Android App developed based on Jetpack Compose. Currently in its initial version and continuously being improved.

The project's goal is to facilitate daily maintenance and browsing of Github, and it's also suitable for learning and practicing Compose, covering the use of various frameworks.

Based on usage and feedback, the user experience and features will be updated and improved from time to time. Feel free to raise issues.
```

[![Github Actions](https://github.com/CarGuo/GSYGithubAppCompose/workflows/CI/badge.svg)](https://github.com/CarGuo/GSYGithubAppCompose/actions)
[![GitHub stars](https://img.shields.io/github/stars/CarGuo/GSYGithubAppCompose.svg)](https://github.com/CarGuo/GSYGithubAppCompose/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/CarGuo/GSYGithubAppCompose.svg)](https://github.com/CarGuo/GSYGithubAppCompose/network)
[![GitHub issues](https://img.shields.io/github/issues/CarGuo/GSYGithubAppCompose.svg)](https://github.com/CarGuo/GSYGithubAppCompose/issues)
[![GitHub license](https://img.shields.io/github/license/CarGuo/GSYGithubAppCompose.svg)](https://github.com/CarGuo/GSYGithubAppCompose/blob/master/LICENSE)

|            |            |            |            |
|------------|------------|------------|------------|
| ![](3.gif) | ![](4.gif) | ![](1.gif) | ![](2.gif) | 

## How to Compile and Run

> ### Important: You need to configure the `local.properties` file in the project's root directory and enter your registered Github client_id and client_secret.

    ndk.dir="xxxxxxxx"
    CLIENT_ID = "xxxxxx"
    CLIENT_SECRET = "xxxxxx"

[Portal to register a Github APP](https://github.com/settings/applications/new)
, of course, the prerequisite is that you have a Github account (～￣▽￣)～.

### Now, the Github API requires secure login (authorized login), so you must fill in the Authorization callback URL field with the following when registering your Github App:

`gsygithubapp://authed`

<div>
<img src="http://img.cdn.guoshuyu.cn/register0.png" width="426px"/>
<img src="http://img.cdn.guoshuyu.cn/register1.jpg" width="426px"/>
</div>

## Project Structure Diagrams

### KeyFeatures

![](./doc/KeyFeatures.png)

### Core Technologies

![](./doc/CoreTechnologies.png)

### Layer Structure

![](./doc/LayerStructure.png)

### Technology Stack

![](./doc/TechnologyStack.png)

### Navigation Flow

![](./doc/NavigationFlow.png)

### Data Flow Architecture

![](./doc/DataFlowArchitecture.png)

### Overall Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          GSYGithubAppCompose                            │
│                       (Jetpack Compose + MVVM)                         │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                ┌───────────────────┼───────────────────┐
                │                   │                   │
        ┌───────▼────────┐  ┌──────▼──────┐   ┌───────▼────────┐
        │  Presentation  │  │    Data     │   │      Core      │
        │     Layer      │  │    Layer    │   │     Layer      │
        └────────────────┘  └─────────────┘   └────────────────┘
                │                   │                   │
        ┌───────▼────────┐  ┌──────▼──────┐   ┌───────▼────────┐
        │   feature/*    │  │    data     │   │  core/network  │
        │                │  │             │   │  core/database │
        │ - welcome      │  │ Repository  │   │  core/common   │
        │ - login        │  │   Pattern   │   │  core/ui       │
        │ - home         │  │             │   └────────────────┘
        │ - dynamic      │  │ - User      │
        │ - trending     │  │ - Event     │
        │ - profile      │  │ - Repo      │
        │ - search       │  └─────────────┘
        │ - detail       │
        │ - code         │
        │ - issue        │
        │ - push         │
        │ - list         │
        │ - notification │
        │ - info         │
        │ - history      │
        └────────────────┘
```

### Module Dependency Diagram

```
                                    ┌─────────┐
                                    │   app   │
                                    └────┬────┘
                                         │
                 ┌───────────────────────┼────────────────────────┐
                 │                       │                        │
          ┌──────▼──────┐         ┌─────▼─────┐          ┌──────▼──────┐
          │  feature/*  │         │    data   │          │   core/ui   │
          │             │         │           │          │             │
          │ All Feature │◄────────┤ Repository │          │ Common UI   │
          │   Modules   │         │           │          │ Components  │
          └──────┬──────┘         └─────┬─────┘          └──────┬──────┘
                 │                      │                       │
                 │              ┌───────┼────────┐              │
                 │              │       │        │              │
                 └──────────────┼───────┼────────┼──────────────┘
                                │       │        │
                    ┌───────────▼─┐  ┌──▼────────▼──┐  ┌─────────────┐
                    │core/network │  │core/database │  │core/common  │
                    │             │  │              │  │             │
                    │ Retrofit    │  │    Room      │  │ DataStore   │
                    │ Apollo      │  │    Entity    │  │   Token     │
                    │ Model       │  │    DAO       │  │  Resources  │
                    └─────────────┘  └──────────────┘  └─────────────┘

Dependency Rules:
  app          → feature/*, core/ui, data
  feature/*    → data, core/ui, core/common
  data         → core/network, core/database, core/common
  core/ui      → core/common
  core/network → (Independent Module)
  core/database→ (Independent Module)
  core/common  → (Independent Module)
```

### Detailed Module Structure

```
GSYGithubAppCompose/
│
├── app/                                    # Main application module
│   ├── MainActivity.kt                     # Main entry point, navigation config
│   ├── MainViewModel.kt                    # Application-level ViewModel
│   └── GSYApplication.kt                   # Application, Hilt entry point
│
├── core/                                   # Core base modules
│   │
│   ├── network/                            # Network layer
│   │   ├── api/
│   │   │   └── GitHubApiService.kt        # GitHub REST API interface
│   │   ├── model/                          # Network data models
│   │   │   ├── User.kt
│   │   │   ├── Repository.kt
│   │   │   ├── Event.kt
│   │   │   └── ...
│   │   ├── config/
│   │   │   └── NetworkConfig.kt           # Network config (PAGE_SIZE, etc.)
│   │   └── di/
│   │       └── NetworkModule.kt           # Retrofit, OkHttp, Apollo DI
│   │
│   ├── database/                           # Database layer
│   │   ├── entity/                         # Room Entity
│   │   │   ├── UserEntity.kt
│   │   │   ├── RepositoryEntity.kt
│   │   │   ├── HistoryEntity.kt
│   │   │   └── ...
│   │   ├── dao/                            # Room DAO
│   │   │   ├── UserDao.kt
│   │   │   ├── RepositoryDao.kt
│   │   │   ├── HistoryDao.kt
│   │   │   └── ...
│   │   ├── AppDatabase.kt                  # Room Database configuration
│   │   └── di/
│   │       └── DatabaseModule.kt          # Room DI
│   │
│   ├── common/                             # Common resources module
│   │   ├── datastore/
│   │   │   └── UserPreferencesDataStore.kt # User preference storage
│   │   ├── utils/
│   │   │   └── StringResourceProvider.kt  # String resource provider
│   │   ├── di/
│   │   │   └── CommonModule.kt            # DataStore DI
│   │   └── res/                            # Common resources
│   │       ├── values/                     # English resources
│   │       │   └── strings.xml
│   │       └── values-zh-rCN/              # Chinese resources
│   │           └── strings.xml
│   │
│   └── ui/                                 # UI components module
│       ├── components/
│       │   ├── GSYPullRefresh.kt          # Pull-to-refresh control
│       │   ├── GSYGeneralLoadState.kt     # General loading state
│       │   ├── GSYTopAppBar.kt            # General top app bar
│       │   ├── GSYLoadingDialog.kt        # Loading dialog
│       │   └── ...
│       ├── navigation/
│       │   ├── GSYNavigator.kt            # Navigator
│       │   └── GSYNavHost.kt              # Navigation Host
│       ├── theme/
│       │   ├── Theme.kt                    # Material 3 Theme
│       │   ├── Color.kt                    # Color definitions
│       │   └── Type.kt                     # Typography definitions
│       └── base/
│           ├── BaseScreen.kt              # Base Screen (Toast support)
│           └── ...
│
├── data/                                   # Data layer
│   ├── repository/                         # Repository implementations
│   │   ├── UserRepository.kt               # User data repository
│   │   ├── EventRepository.kt              # Event data repository
│   │   ├── RepositoryRepository.kt         # Repository data repository
│   │   ├── HistoryRepository.kt            # Browsing history repository
│   │   └── vm/
│   │       ├── BaseViewModel.kt           # Base ViewModel
│   │       └── BaseUiState.kt             # Base UI state
│   └── mapper/
│       └── DataMappers.kt                  # Data mapping (Entity ↔ Model)
│
└── feature/                                # Feature modules
    │
    ├── welcome/                            # Welcome screen
    │   ├── WelcomeScreen.kt
    │   └── WelcomeViewModel.kt
    │
    ├── login/                              # Login screen
    │   ├── LoginScreen.kt
    │   └── LoginViewModel.kt
    │
    ├── home/                               # Home screen (bottom navigation)
    │   ├── HomeScreen.kt
    │   └── HomeViewModel.kt
    │
    ├── dynamic/                            # Dynamic (event stream)
    │   ├── DynamicScreen.kt
    │   ├── DynamicViewModel.kt
    │   └── components/
    │       └── EventItem.kt
    │
    ├── trending/                           # Trending (popular repositories)
    │   ├── TrendingScreen.kt
    │   ├── TrendingViewModel.kt
    │   └── components/
    │       └── TrendingRepoItem.kt
    │
    ├── profile/                            # Personal center
    │   ├── ProfileScreen.kt
    │   ├── ProfileViewModel.kt
    │   └── components/
    │       └── ProfileHeader.kt
    │
    ├── search/                             # Search
    │   ├── SearchScreen.kt
    │   └── SearchViewModel.kt
    │
    ├── detail/                             # Repository detail
    │   ├── RepoDetailScreen.kt
    │   └── RepoDetailViewModel.kt
    │
    ├── code/                               # Code browsing
    │   ├── CodeScreen.kt
    │   └── CodeViewModel.kt
    │
    ├── issue/                              # Issue management
    │   ├── IssueScreen.kt
    │   └── IssueViewModel.kt
    │
    ├── push/                               # Push management
    │   ├── PushScreen.kt
    │   └── PushViewModel.kt
    │
    ├── list/                               # List screen
    │   ├── ListScreen.kt
    │   └── ListViewModel.kt
    │
    ├── notification/                       # Notifications
    │   ├── NotificationScreen.kt
    │   └── NotificationViewModel.kt
    │
    ├── info/                               # Info screen
    │   ├── InfoScreen.kt
    │   └── InfoViewModel.kt
    │
    └── history/                            # Browsing History
        ├── HistoryScreen.kt
        └── HistoryViewModel.kt
```

### Technology Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Tech Stack                                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  UI Layer              Jetpack Compose + Material 3                 │
│                        Navigation Compose                           │
│                        Coil (Image Loading)                         │
│                        Lottie (Complex Animations)                  │
│  ├─────────────────────────────────────────────────────────────┤   │
│                                                                     │
│  State Management      StateFlow + ViewModel                         │
│                        Kotlin Coroutines + Flow                     │
│  ├─────────────────────────────────────────────────────────────┤   │
│                                                                     │
│  Dependency Injection  Hilt (Dagger 2)                               │
│  ├─────────────────────────────────────────────────────────────┤   │
│                                                                     │
│  Network Layer         Retrofit 2 + OkHttp                           │
│                        Apollo GraphQL                               │
│                        Gson / Kotlinx Serialization                │
│  ├─────────────────────────────────────────────────────────────┤   │
│                                                                     │
│  Database Layer        Room Database                                 │
│                        DataStore (Replaces SharedPreferences)       │
│  ├─────────────────────────────────────────────────────────────┤   │
│                                                                     │
│  Architecture Pattern  MVVM + Repository Pattern                     │
│                        Clean Architecture                           │
│                        Unidirectional Data Flow                     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### Data Flow Diagram

```
┌──────────────┐        ┌──────────────┐        ┌──────────────┐
│              │        │              │        │              │
│   Screen     │◄───────┤  ViewModel   │◄───────┤  Repository  │
│  (Compose)   │ State  │   (MVVM)     │  Flow  │   (Data)     │
│              │        │              │        │              │
└──────┬───────┘        └──────┬───────┘        └──────┬───────┘
       │                       │                       │
       │ User Action           │ Business Logic        │ Data Source
       │                       │                       │
       ▼                       ▼                       ▼
┌──────────────┐        ┌──────────────┐        ┌──────────────┐
│              │        │              │        │              │
│   onClick    │───────►│  loadData()  │───────►│  Network /   │
│   onRefresh  │ Event  │  refresh()   │ API    │  Database    │
│              │        │              │ Call   │              │
└──────────────┘        └──────────────┘        └──────────────┘

Data Flow:
1. User actions trigger UI events.
2. ViewModel handles business logic.
3. Repository coordinates data sources (Network/Database).
4. Data is returned to the ViewModel via Flow.
5. ViewModel updates the UiState.
6. The UI automatically recomposes to display the new state.
```

### Layer Responsibilities

| Layer             | Module              | Responsibility                  | Key Technologies                 |
|-------------------|---------------------|---------------------------------|----------------------------------|
| **Presentation**  | feature/*           | UI rendering, user interaction  | Jetpack Compose, Navigation    |
| **Business**      | data (ViewModel)    | Business logic, state management| StateFlow, Coroutines          |
| **Data**          | data (Repository)   | Data access, caching strategy   | Repository Pattern             |
| **Network**       | core/network        | API calls, network requests     | Retrofit, Apollo, OkHttp         |
| **Storage**       | core/database       | Local caching, data persistence | Room, DataStore                |
| **Foundation**    | core/common, core/ui| Common utilities, UI components | i18n, Themes, Utils              |

## Download

#### APK Download Link: [APK Download Link](https://github.com/CarGuo/GSYGithubAppCompose/releases)

### Sample Images (Screenshots may not be fully up-to-date)

<img src="http://img.cdn.guoshuyu.cn/showapp1.jpg" width="426px"/>

<img src="http://img.cdn.guoshuyu.cn/showapp2.jpg" width="426px"/>

<img src="http://img.cdn.guoshuyu.cn/showapp3.jpg" width="426px"/>

### Thanks

<img src="http://img.cdn.guoshuyu.cn/thanks.jpg" width="426px"/>


https://deepwiki.com/CarGuo/GSYGithubAppCompose

### LICENSE

```
CarGuo/GSYGithubAppFlutter is licensed under the
Apache License 2.0

A permissive license whose main conditions require preservation of copyright and license notices.
Contributors provide an express grant of patent rights.
Licensed works, modifications, and larger works may be distributed under different terms and without source code.
```


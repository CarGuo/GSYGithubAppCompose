# Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         App Module                              │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  MainActivity (Navigation Host)                           │  │
│  │    - NavHost with NavController                           │  │
│  │    - Routes: welcome, login, home                         │  │
│  │                                                            │  │
│  │  MainViewModel                                            │  │
│  │    - isLoggedIn: Flow<Boolean>                            │  │
│  │                                                            │  │
│  │  GSYApplication (@HiltAndroidApp)                         │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              ↓ depends on
┌─────────────────────────────────────────────────────────────────┐
│                      Feature Modules                            │
│  ┌─────────────┬─────────────┬─────────────┬─────────────────┐ │
│  │  welcome/   │   login/    │    home/    │   dynamic/      │ │
│  │             │             │             │   trending/     │ │
│  │  Splash     │  Token      │  Bottom     │   profile/      │ │
│  │  Screen     │  Input      │  Nav        │                 │ │
│  │             │  Screen     │  (3 tabs)   │  Screen +       │ │
│  │             │             │             │  ViewModel      │ │
│  └─────────────┴─────────────┴─────────────┴─────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              ↓ depends on
┌─────────────────────────────────────────────────────────────────┐
│                       Data Module                               │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  Repositories (Repository Pattern)                        │  │
│  │    - UserRepository                                       │  │
│  │    - RepositoryRepository                                 │  │
│  │    - EventRepository                                      │  │
│  │                                                            │  │
│  │  Business Logic:                                          │  │
│  │    - Data aggregation                                     │  │
│  │    - Cache strategy                                       │  │
│  │    - Error handling                                       │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              ↓ depends on
┌─────────────────────────────────────────────────────────────────┐
│                       Core Modules                              │
│  ┌────────────┬────────────┬────────────┬────────────────────┐ │
│  │ network/   │ database/  │  common/   │      ui/           │ │
│  │            │            │            │                    │ │
│  │ • Retrofit │ • Room DB  │ • DataStore│ • Theme            │ │
│  │ • OkHttp   │ • DAOs     │ • Prefs    │ • Components       │ │
│  │ • API      │ • Entities │ • Utils    │ • AvatarImage      │ │
│  │   Service  │            │            │ • Material3        │ │
│  │ • Models   │            │            │                    │ │
│  │            │            │            │                    │ │
│  │ @Provides  │ @Provides  │ @Provides  │ @Composable        │ │
│  │ via Hilt   │ via Hilt   │ via Hilt   │                    │ │
│  └────────────┴────────────┴────────────┴────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              ↓ depends on
┌─────────────────────────────────────────────────────────────────┐
│                    External Dependencies                        │
│                                                                 │
│  • Hilt (DI)           • Coil (Images)      • Material 3        │
│  • Retrofit (Network)  • Room (Database)    • Navigation        │
│  • Coroutines (Async)  • DataStore (Prefs)  • Compose          │
└─────────────────────────────────────────────────────────────────┘
```

## Data Flow

```
User Interaction
     ↓
Composable UI (Screen)
     ↓
User Action Event
     ↓
ViewModel
     ↓
Repository
     ↓
┌─────────────┬──────────────┐
│   Network   │   Database   │
│   (Remote)  │   (Local)    │
└─────────────┴──────────────┘
     ↓              ↓
API Response    Cached Data
     ↓              ↓
Repository (Combines/Transforms)
     ↓
UiState (StateFlow)
     ↓
Composable UI (Recomposes)
     ↓
User sees updated UI
```

## Screen Navigation Flow

```
App Launch
    ↓
WelcomeScreen (Splash)
    ↓
    ├─ isLoggedIn = true  ──→ HomeScreen
    │                              ↓
    │                         BottomNavigation
    │                              ↓
    │                    ┌─────────┼─────────┐
    │                    ↓         ↓         ↓
    │                Dynamic   Trending  Profile
    │                                        ↓
    │                                    [Logout]
    │                                        ↓
    └─ isLoggedIn = false ──→ LoginScreen ──┘
                                  ↓
                           [Login Success]
                                  ↓
                              HomeScreen
```

## Module Dependencies

```
app
 ├── feature/welcome
 ├── feature/login ──────┐
 ├── feature/home        │
 ├── feature/dynamic ────┤
 ├── feature/trending ───┼──→ data
 ├── feature/profile ────┘       ├── core/network
 │                                ├── core/database
 └── core/ui                      └── core/common
     └── core/ui (theme)
```

## State Management Pattern (MVVM)

```
┌──────────────────────────────────────────────────────────┐
│                      View Layer                          │
│  @Composable                                             │
│  fun DynamicScreen(viewModel: DynamicViewModel) {        │
│      val uiState by viewModel.uiState.collectAsState()   │
│      // UI renders based on uiState                      │
│  }                                                        │
└──────────────────────────────────────────────────────────┘
                         ↕ (StateFlow)
┌──────────────────────────────────────────────────────────┐
│                    ViewModel Layer                       │
│  @HiltViewModel                                          │
│  class DynamicViewModel @Inject constructor(             │
│      private val eventRepository: EventRepository        │
│  ) : ViewModel() {                                       │
│      private val _uiState = MutableStateFlow(...)        │
│      val uiState: StateFlow<...> = _uiState.asStateFlow()│
│                                                           │
│      fun loadEvents() {                                  │
│          viewModelScope.launch {                         │
│              // Call repository                          │
│              // Update _uiState                          │
│          }                                               │
│      }                                                   │
│  }                                                       │
└──────────────────────────────────────────────────────────┘
                         ↕ (suspend functions)
┌──────────────────────────────────────────────────────────┐
│                   Repository Layer                       │
│  @Singleton                                              │
│  class EventRepository @Inject constructor(              │
│      private val apiService: GitHubApiService            │
│  ) {                                                     │
│      suspend fun getReceivedEvents(...): Result<...> {   │
│          return try {                                    │
│              val events = apiService.getReceivedEvents() │
│              Result.success(events)                      │
│          } catch (e: Exception) {                        │
│              Result.failure(e)                           │
│          }                                               │
│      }                                                   │
│  }                                                       │
└──────────────────────────────────────────────────────────┘
                         ↕ (Retrofit)
┌──────────────────────────────────────────────────────────┐
│                     Data Source                          │
│  interface GitHubApiService {                            │
│      @GET("users/{username}/received_events")            │
│      suspend fun getReceivedEvents(...)                  │
│  }                                                       │
└──────────────────────────────────────────────────────────┘
```

## Dependency Injection (Hilt)

```
@HiltAndroidApp
Application
    ↓ provides
@InstallIn(SingletonComponent::class)
Modules
    ├── NetworkModule
    │   ├── @Provides OkHttpClient
    │   ├── @Provides Retrofit
    │   └── @Provides GitHubApiService
    │
    ├── DatabaseModule
    │   ├── @Provides AppDatabase
    │   ├── @Provides UserDao
    │   └── @Provides RepositoryDao
    │
    └── CommonModule
        └── @Provides UserPreferencesDataStore
            ↓ injected into
        Repositories (@Singleton)
            ↓ injected into
        ViewModels (@HiltViewModel)
            ↓ used by
        @AndroidEntryPoint Activity
            ↓ provides to
        @Composable Screens
```

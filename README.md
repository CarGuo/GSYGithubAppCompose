# GSY GitHub App - Compose Edition

A modern GitHub client built with Jetpack Compose, following clean architecture principles.

## Architecture

This project follows the modular architecture pattern inspired by [androidify](https://github.com/android/androidify), with clear separation of concerns:

### Module Structure

```
app/                    # Main application module
├── MainActivity       # Navigation host
├── MainViewModel      # App-level state
└── GSYApplication     # Application class with Hilt

core/                  # Core foundation modules
├── network/          # Network layer (Retrofit, GitHub API)
├── database/         # Local persistence (Room)
├── common/           # Shared utilities (DataStore, preferences)
└── ui/              # UI components and theme

data/                  # Data layer
└── repository/       # Repository implementations

feature/              # Feature modules (UI + ViewModels)
├── welcome/         # Splash screen
├── login/           # GitHub OAuth login
├── home/            # Main screen with bottom navigation
├── dynamic/         # Activity feed (动态)
├── trending/        # Trending repositories (趋势)
└── profile/         # User profile (我的)
```

## Tech Stack

### State Management
- **ViewModel + StateFlow**: Unidirectional data flow
- **Hilt**: Dependency injection
- **Kotlin Coroutines**: Asynchronous operations

### Networking
- **Retrofit**: REST API client
- **OkHttp**: HTTP client with logging
- **Gson**: JSON serialization

### Local Storage
- **Room**: SQLite database for caching
- **DataStore**: Preferences storage for user settings

### Image Loading
- **Coil**: Async image loading with Compose support

### UI
- **Jetpack Compose**: Modern declarative UI
- **Material 3**: Design system
- **Navigation Compose**: Screen navigation

## Features

### ✅ Implemented

1. **Welcome Screen** (`feature/welcome`)
   - Splash screen with app branding
   - Auto-navigation based on login status

2. **Login Screen** (`feature/login`)
   - GitHub Personal Access Token authentication
   - Loading states and error handling
   - Persists authentication via DataStore

3. **Home Screen** (`feature/home`)
   - Bottom navigation with 3 tabs
   - Tab persistence during configuration changes

4. **Dynamic Feed** (`feature/dynamic`)
   - Displays user's received events
   - Event list with user avatars
   - Repository information

5. **Trending Repositories** (`feature/trending`)
   - Shows trending repositories
   - Star and fork counts
   - Repository descriptions
   - Language indicators

6. **User Profile** (`feature/profile`)
   - User avatar and bio
   - Followers/following/repos counts
   - Logout functionality

### Core Functionality

- **Network Layer**: Complete GitHub API integration
  - User authentication
  - Fetch user profile
  - Fetch events
  - Fetch trending repositories
  
- **Database Layer**: Room database for caching
  - User entity
  - Repository entity
  - DAO interfaces

- **State Management**: 
  - ViewModels with StateFlow
  - Repository pattern
  - Hilt dependency injection

- **Image Loading**: Coil integration with circular avatars

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 11 or later
- Android SDK 34

### GitHub Token

1. Go to [GitHub Settings > Developer settings > Personal access tokens](https://github.com/settings/tokens)
2. Generate a new token with appropriate scopes:
   - `repo` (Full control of private repositories)
   - `user` (Read all user profile data)
3. Use this token to login in the app

### Build & Run

```bash
./gradlew build
./gradlew installDebug
```

## Project Structure Details

### Core Modules

#### core/network
- **GitHubApiService**: Retrofit interface for GitHub API
- **NetworkModule**: Hilt module providing Retrofit/OkHttp
- **Models**: Data models (User, Repository, Event)

#### core/database
- **AppDatabase**: Room database instance
- **DAOs**: UserDao, RepositoryDao
- **Entities**: Database entities

#### core/common
- **UserPreferencesDataStore**: DataStore for user preferences
- Utility classes and extensions

#### core/ui
- **Theme**: Material 3 theme configuration
- **Components**: Reusable UI components (AvatarImage)

### Data Module
- **UserRepository**: User authentication and profile
- **RepositoryRepository**: GitHub repositories
- **EventRepository**: User events/activity

### Feature Modules

Each feature module follows the same pattern:
- **Screen**: Composable UI
- **ViewModel**: State management
- **UiState**: Data class for UI state

## Code Style

Following the androidify project conventions:
- Clean Architecture with clear module boundaries
- MVVM pattern with ViewModels
- Unidirectional data flow (UDF)
- Dependency injection with Hilt
- Compose best practices
- Material 3 design guidelines

## License

This project is for educational purposes.

## References

- [gsy_github_app_flutter](https://github.com/CarGuo/gsy_github_app_flutter) - Feature reference
- [androidify](https://github.com/android/androidify) - Architecture reference
- [GitHub REST API](https://docs.github.com/en/rest) - API documentation

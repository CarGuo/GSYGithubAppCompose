# Quick Start Guide

## For Developers

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 11+
- Android SDK 34
- GitHub Personal Access Token

### Getting a GitHub Token

1. Visit: https://github.com/settings/tokens
2. Click "Generate new token" → "Generate new token (classic)"
3. Name: "GSY GitHub App"
4. Select scopes:
   - ✅ repo (Full control of private repositories)
   - ✅ user (Read all user profile data)
5. Generate and copy the token

### Build & Run

```bash
# Clone repository
git clone https://github.com/CarGuo/GSYGithubAppCompose.git
cd GSYGithubAppCompose

# Build
./gradlew build

# Install on device/emulator
./gradlew installDebug

# Or open in Android Studio and click Run ▶
```

### First Time Use

1. App launches to Welcome screen (2-second splash)
2. Navigate to Login screen
3. Paste your GitHub Personal Access Token
4. Click "Login"
5. Explore the app!

## Project Tour

### File Structure

```
app/src/main/java/com/shuyu/gsygithubappcompose/
├── MainActivity.kt          # Navigation setup
├── MainViewModel.kt         # App-level state
└── GSYApplication.kt        # Hilt application

core/
├── network/
│   ├── api/GitHubApiService.kt      # API interface
│   ├── model/Models.kt              # Data models
│   └── di/NetworkModule.kt          # DI setup
├── database/
│   ├── AppDatabase.kt               # Room database
│   ├── dao/Daos.kt                  # Database access
│   ├── entity/Entities.kt           # DB entities
│   └── di/DatabaseModule.kt         # DI setup
├── common/
│   ├── datastore/UserPreferencesDataStore.kt  # Preferences
│   └── di/CommonModule.kt           # DI setup
└── ui/
    ├── theme/                       # Material 3 theme
    └── components/AvatarImage.kt    # Reusable components

data/src/main/java/com/shuyu/gsygithubappcompose/data/repository/
├── UserRepository.kt        # User data
├── EventRepository.kt       # Activity events
└── RepositoryRepository.kt  # GitHub repos

feature/
├── welcome/WelcomeScreen.kt         # Splash
├── login/
│   ├── LoginScreen.kt               # UI
│   └── LoginViewModel.kt            # State
├── home/HomeScreen.kt               # Bottom nav
├── dynamic/
│   ├── DynamicScreen.kt             # UI
│   └── DynamicViewModel.kt          # State
├── trending/
│   ├── TrendingScreen.kt            # UI
│   └── TrendingViewModel.kt         # State
└── profile/
    ├── ProfileScreen.kt             # UI
    └── ProfileViewModel.kt          # State
```

## Key Concepts

### Navigation Routes

- `welcome` → Splash screen
- `login` → Token input
- `home` → Main app (3 tabs)

### Bottom Navigation Tabs

1. **动态 (Dynamic)**: User activity feed
2. **趋势 (Trending)**: Popular repositories
3. **我的 (Profile)**: User profile

### State Management

Each feature follows MVVM:

```kotlin
Screen (Composable)
   ↓ observes
UiState (StateFlow)
   ↓ updated by
ViewModel
   ↓ calls
Repository
   ↓ fetches from
API/Database
```

### Dependency Injection

Everything is provided by Hilt:

```kotlin
@HiltAndroidApp
class GSYApplication : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity()

@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel()

@Singleton
class MyRepository @Inject constructor(
    private val apiService: GitHubApiService
)
```

## Common Tasks

### Add a New Feature

1. Create module: `feature/myfeature`
2. Add to `settings.gradle.kts`
3. Create `build.gradle.kts`
4. Implement `MyFeatureScreen.kt`
5. Implement `MyFeatureViewModel.kt`
6. Add route to `MainActivity`

### Add a New API Endpoint

1. Add method to `GitHubApiService.kt`
2. Update models in `Models.kt` if needed
3. Add repository method
4. Call from ViewModel

### Add a Database Entity

1. Create entity in `core/database/entity/`
2. Create DAO in `core/database/dao/`
3. Add to `AppDatabase.kt`
4. Update repository

## Troubleshooting

### Build Fails

```bash
# Clean and rebuild
./gradlew clean build
```

### Login Fails

- Check token has correct permissions
- Check internet connection
- Check GitHub API status

### Images Not Loading

- Check internet connection
- Verify URLs are valid

## API Rate Limits

GitHub API has rate limits:
- Authenticated: 5,000 requests/hour
- Unauthenticated: 60 requests/hour

Use authentication token to get higher limit.

## Development Tips

1. **Hot Reload**: Use Compose Preview for quick UI iteration
2. **Logging**: Check Logcat for OkHttp network logs
3. **Debug DB**: Use Database Inspector in Android Studio
4. **State**: Use `rememberSaveable` for screen rotations
5. **Navigation**: Use `popUpTo` to manage back stack

## Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

## Performance

- **Offline-first**: Database caches data
- **Lazy Loading**: LazyColumn for lists
- **Image Caching**: Coil handles caching
- **Coroutines**: Non-blocking async operations

## Resources

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [GitHub API](https://docs.github.com/en/rest)
- [Material 3](https://m3.material.io/)

## License

See LICENSE file for details.

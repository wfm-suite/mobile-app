# Android Clean Architecture Guide

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [Architecture Layers](#architecture-layers)
3. [Project Structure](#project-structure)
4. [Dependency Injection](#dependency-injection)
5. [Data Flow](#data-flow)
6. [Navigation](#navigation)
7. [UI Patterns](#ui-patterns)
8. [Implementation Guide](#implementation-guide)

---

## 🎯 Project Overview

This guide describes a **Clean Architecture** implementation with **MVVM** pattern for Android applications.

### Key Characteristics
- **Clean Architecture** with clear separation of concerns
- **MVVM** (Model-View-ViewModel) presentation pattern
- **Declarative UI** approach
- **Dependency Injection** for loose coupling
- **RESTful API** integration
- **Local data persistence**
- **Type-safe Navigation**

---

## 🏗️ Architecture Layers

The application follows a layered architecture with clear separation of concerns. Each layer has specific responsibilities following Clean Architecture principles.

### Layer Organization

```
app/
├── presentation/          # UI and user interaction layer
├── domain/               # Business logic layer
├── data/                 # Data management layer
└── core/                 # Shared utilities and cross-cutting concerns
```

### 1. Presentation Layer (`presentation/`)

```
presentation/
├── app/                    # App-level components
│   ├── App.kt             # Root component
│   ├── AppViewModel.kt    # App-level state management
│   └── AppUiState.kt      # App-level UI state
├── screen/                # Feature screens
│   ├── home/
│   │   ├── HomeScreen.kt       # UI component
│   │   ├── HomeViewModel.kt    # State and logic
│   │   └── HomeUiState.kt      # UI state model
│   ├── login/
│   ├── profile/
│   └── ...
├── component/             # Reusable UI components
│   ├── CustomButton.kt
│   ├── CustomTextField.kt
│   └── ...
├── navigation/            # Navigation configuration
│   ├── ScreenRoute.kt     # Route definitions
│   └── AppNavigation.kt   # Navigation setup
└── theme/                 # UI theming
    ├── Theme.kt
    ├── Color.kt
    ├── Type.kt
    └── AppDimensions.kt
```

**Structure Guidelines**:
- Each screen has: `Screen` (UI), `ViewModel` (logic), `UiState` (state)
- ViewModels use reactive streams for state management
- Screens observe state changes and recompose UI
- Keep UI components stateless when possible
- Separate business logic from UI logic

### 2. Domain Layer (`domain/`)

```
domain/
├── model/                 # Business entities
│   ├── UserInfo.kt
│   ├── Rota.kt
│   ├── EmployeeInfo.kt
│   └── ...
├── repository/            # Repository contracts
│   ├── UserRepository.kt
│   ├── RotaRepository.kt
│   └── PreferenceRepository.kt
└── usecase/              # Business operations
    ├── user/
    │   ├── AuthenticationUseCase.kt
    │   ├── UserProfileUseCase.kt
    │   └── GetRotaUseCase.kt
    ├── rota/
    └── leave/
```

**Structure Guidelines**:
- Define pure business entities independent of frameworks
- Create repository interfaces (contracts)
- Encapsulate business operations in use cases
- Keep domain models framework-agnostic
- Follow single responsibility principle

### 3. Data Layer (`data/`)

```
data/
├── model/                 # Data transfer objects
│   ├── UserResponse.kt
│   ├── UserRequest.kt
│   └── BaseResponse.kt
├── mapper/               # Data transformation
│   ├── UserMapper.kt
│   ├── RotaMapper.kt
│   └── LeaveMapper.kt
├── repository/           # Repository implementations
│   ├── UserRepositoryImpl.kt
│   ├── RotaRepositoryImpl.kt
│   └── PreferenceRepositoryImpl.kt
├── source/
│   ├── remote/          # Remote data operations
│   │   ├── ApiService.kt
│   │   ├── RemoteDataSource.kt
│   │   └── HttpClientFactory.kt
│   └── local/           # Local data operations
│       ├── PreferenceDataSource.kt
│       └── DataStoreFactory.kt
├── provider/            # Data providers
│   └── AuthTokenProvider.kt
└── util/
    └── NetworkUtil.kt
```

**Structure Guidelines**:
- Create DTOs for external data sources
- Implement mappers to convert DTOs to domain models
- Implement repository interfaces from domain layer
- Separate remote and local data sources
- Handle data operations and transformations

### 4. Core Layer (`core/`)

```
core/
├── di/                   # Dependency injection
│   ├── InitKoin.kt
│   ├── AppModule.kt
│   ├── NetworkModule.kt
│   ├── RepositoryModule.kt
│   ├── UseCaseModule.kt
│   ├── ViewModelModule.kt
│   ├── LocalModule.kt
│   └── PlatformModule.kt
├── util/                 # Utilities
│   ├── ResultWrapper.kt  # Result handling
│   ├── AppActions.kt     # Platform actions
│   └── ImageExtension.kt
└── platform/             # Platform abstractions
    └── ImagePicker.kt
```

**Structure Guidelines**:
- Organize dependencies into logical modules
- Create utility classes for common operations
- Define platform-specific interfaces
- Keep cross-cutting concerns centralized
- Maintain separation from business logic

---

## 📁 Project Structure

### Application Structure

```
app/src/main/
├── AndroidManifest.xml
├── java/com/yourapp/
│   ├── MainActivity.kt                    # Entry point
│   ├── Application.kt                     # Application class
│   ├── core/
│   │   ├── di/                           # Dependency injection modules
│   │   ├── platform/                     # Platform-specific implementations
│   │   └── util/                         # Utility classes
│   ├── presentation/                     # UI layer
│   ├── domain/                           # Business logic
│   └── data/                             # Data layer
└── res/                                  # Android resources
    ├── drawable/
    ├── layout/
    ├── values/
    └── ...
```

---

## 💉 Dependency Injection

### DI Container Setup

#### 1. Application Class
```kotlin
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize DI container
        initializeDependencies()
    }
    
    private fun initializeDependencies() {
        // Register all modules
        registerNetworkModule()
        registerDataModule()
        registerDomainModule()
        registerPresentationModule()
    }
}
```

#### 2. Module Structure
```kotlin
// Organize dependencies into logical modules
- NetworkModule: HTTP client, API services
- DataModule: Repositories, data sources, local storage
- DomainModule: Use cases, business logic
- PresentationModule: ViewModels, UI components
```

#### 3. Network Module
```kotlin
// Register network-related dependencies
- HTTP Client (with interceptors, timeout configuration)
- API Service (handles HTTP requests)
- Remote Data Source (abstracts API calls)
```

#### 4. Repository Module
```kotlin
// Register repository implementations
- PreferenceRepository: Local preferences storage
- UserRepository: User data management
- DataRepository: Business data management
- AuthTokenProvider: Authentication token handling
```

#### 5. ViewModel Module
```kotlin
// Register ViewModels with their dependencies
- AppViewModel: App-level state
- LoginViewModel: Authentication logic
- HomeViewModel: Home screen logic
- ProfileViewModel: Profile management
```

#### 6. Platform Module
```kotlin
// Register platform-specific implementations
- Local Storage Factory
- Platform Actions (intents, permissions)
- Image Picker
- Location Services
```

---

## 🔄 Data Flow

### Complete Flow Example: User Login

```
┌─────────────┐
│   Screen    │  1. User clicks login
│  (UI Layer) │
└──────┬──────┘
       │ viewModel.login(email, password)
       ▼
┌─────────────┐
│  ViewModel  │  2. Calls use case
│(Presentation)│
└──────┬──────┘
       │ authUseCase.login()
       ▼
┌─────────────┐
│  Use Case   │  3. Orchestrates business logic
│  (Domain)   │
└──────┬──────┘
       │ userRepository.login()
       ▼
┌─────────────┐
│ Repository  │  4. Fetches from data source
│   (Data)    │
└──────┬──────┘
       │ remoteDataSource.login()
       ▼
┌─────────────┐
│ Data Source │  5. Makes API call
│   (Data)    │
└──────┬──────┘
       │ apiService.post()
       ▼
┌─────────────┐
│  API/Ktor   │  6. HTTP request
│  (Network)  │
└──────┬──────┘
       │ Response
       ▼
┌─────────────┐
│   Mapper    │  7. DTO → Domain Model
│   (Data)    │
└──────┬──────┘
       │ ResultWrapper<UserInfo>
       ▼
┌─────────────┐
│  ViewModel  │  8. Updates UI state
│   _uiState  │
└──────┬──────┘
       │ StateFlow emission
       ▼
┌─────────────┐
│   Screen    │  9. UI recomposes
│  (Compose)  │
└─────────────┘
```

### ResultWrapper Pattern

```kotlin
// Sealed class for handling API results
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

**Usage in Repository**:
```kotlin
override suspend fun login(username: String, password: String): Result<UserInfo> {
    return try {
        val response = remoteDataSource.login(username, password)
        
        // Save auth token if login successful
        if (response.token != null) {
            preferenceDataSource.saveAuthToken(response.token)
        }
        
        Result.Success(response.user.toDomainModel())
    } catch (e: Exception) {
        Result.Error(e.message ?: "Login failed")
    }
}
```

**Usage in ViewModel**:
```kotlin
fun login(email: String, password: String) {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        
        when (val result = authUseCase.login(email, password)) {
            is Result.Success -> {
                _uiState.update { 
                    it.copy(isLoading = false, isLoggedIn = true) 
                }
            }
            is Result.Error -> {
                _uiState.update { 
                    it.copy(isLoading = false, error = result.message) 
                }
            }
            Result.Loading -> {
                // Handle loading state
            }
        }
    }
}
```

---

## 🧭 Navigation

### Type-Safe Navigation Pattern

#### 1. Define Routes
```kotlin
// Sealed class for type-safe navigation
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    
    // Route with parameters
    object ProfileDetail : Screen("profile/{userId}") {
        fun createRoute(userId: String) = "profile/$userId"
    }
    
    // Route with complex object (requires serialization)
    object Shift : Screen("shift/{rotaJson}") {
        fun createRoute(rotaJson: String) = "shift/$rotaJson"
    }
}
```

#### 2. Navigation Setup
```kotlin
fun setupNavigation(
    navController: NavController,
    initialScreen: Screen
) {
    navController.graph = createNavigationGraph {
        startDestination = initialScreen.route
        
        destination(Screen.Login.route) {
            LoginScreen()
        }
        
        destination(Screen.Home.route) {
            HomeScreen()
        }
        
        destination(Screen.ProfileDetail.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            ProfileDetailScreen(userId = userId ?: "")
        }
    }
}
```

#### 3. Navigation Usage
```kotlin
// Simple navigation
navController.navigate(Screen.Home.route)

// With arguments
navController.navigate(Screen.ProfileDetail.createRoute("123"))

// With complex object (serialize to JSON)
val rotaJson = serializeToJson(rota)
navController.navigate(Screen.Shift.createRoute(rotaJson))

// Navigate back
navController.navigateUp()

// Navigate with clearing back stack
navController.navigate(Screen.Home.route) {
    popUpTo(Screen.Login.route) { inclusive = true }
}
```

---

## 🎨 UI Patterns

### 1. Screen Structure

```kotlin
fun HomeScreen(viewModel: HomeViewModel) {
    // Collect state from ViewModel
    val uiState = viewModel.uiState.observeAsState()
    val navController = getNavController()
    val snackbarHost = getSnackbarHost()
    
    // Side effects - Execute once on screen creation
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    
    // Side effects - React to state changes
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHost.showSnackbar(it)
            viewModel.clearMessage()
        }
    }
    
    // UI Content - Stateless composable
    HomeScreenContent(
        isLoading = uiState.isLoading,
        data = uiState.data,
        onAction = viewModel::handleAction
    )
}

// Stateless UI component
fun HomeScreenContent(
    isLoading: Boolean,
    data: List<Item>,
    onAction: () -> Unit
) {
    // Pure UI rendering based on parameters
}
```

### 2. ViewModel Pattern

```kotlin
class HomeViewModel(
    private val useCase: GetDataUseCase
) : ViewModel() {
    
    // Private mutable state
    private val _uiState = MutableStateFlow(HomeUiState())
    // Public immutable state
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = useCase()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            data = result.data
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            message = result.message
                        )
                    }
                }
                Result.Loading -> {
                    // Handle loading state
                }
            }
        }
    }
    
    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
```

### 3. UiState Pattern

```kotlin
data class HomeUiState(
    val isLoading: Boolean = false,
    val data: List<Item> = emptyList(),
    val message: String? = null,
    val error: String? = null
)
```

### 4. Refresh on Navigation

```kotlin
fun HomeScreen(viewModel: HomeViewModel) {
    val navController = getNavController()
    val currentRoute = navController.currentRoute.observeAsState()
    
    // Refresh data when navigating to this screen
    LaunchedEffect(currentRoute.value) {
        if (currentRoute.value == Screen.Home.route) {
            viewModel.refreshData()
        }
    }
    
    // ... rest of the screen
}
```

### 5. Reusable Components

```kotlin
fun PrimaryButton(
    modifier: Modifier = Modifier,
    label: String,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White
            )
        } else {
            Text(label)
        }
    }
}
```

---

## 📝 Implementation Guide

### Step 1: Setup Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/yourapp/
│   │   │   ├── presentation/
│   │   │   ├── domain/
│   │   │   ├── data/
│   │   │   ├── core/
│   │   │   ├── MainActivity.kt
│   │   │   └── App.kt
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── test/
└── build.gradle.kts
```

### Step 2: Configure Build System

```kotlin
android {
    namespace = "com.yourapp"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "com.yourapp"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Core Android
    // UI Framework
    // Lifecycle Components
    // Dependency Injection
    // Networking
    // Local Storage
    // Navigation
    // Serialization
    // Image Loading
    // Date/Time Utilities
}
```

### Step 3: Create Application Class

```kotlin
class YourApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize dependency injection
        initializeDI()
        
        // Initialize other app-level components
        initializeLogging()
        initializeAnalytics()
    }
    
    private fun initializeDI() {
        // Setup DI container with all modules
    }
}
```

### Step 4: Setup Dependency Injection

```kotlin
// AppModule - Main module that includes all sub-modules
fun setupAppModule() {
    registerNetworkModule()
    registerDataModule()
    registerDomainModule()
    registerPresentationModule()
}

// NetworkModule - Network-related dependencies
fun registerNetworkModule() {
    // Register HTTP client
    // Register API service
    // Register remote data source
}

// ViewModelModule - Presentation layer dependencies
fun registerPresentationModule() {
    // Register ViewModels with their dependencies
    // HomeViewModel(useCase1, useCase2)
    // ProfileViewModel(useCase3)
}
```

### Step 5: Create Domain Layer

```kotlin
// Domain Model - Pure business entities
data class User(
    val id: String,
    val name: String,
    val email: String
)

// Repository Interface - Contract for data operations
interface UserRepository {
    suspend fun getUser(id: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
}

// Use Case - Single business operation
class GetUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<User> {
        return repository.getUser(userId)
    }
}
```

### Step 6: Implement Data Layer

```kotlin
// DTO - Data Transfer Object for API
data class UserResponse(
    val id: String,
    val name: String,
    val email: String
)

// Mapper - Convert DTO to Domain Model
fun UserResponse.toDomain() = User(
    id = id,
    name = name,
    email = email
)

// Repository Implementation - Implements domain interface
class UserRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
) : UserRepository {
    override suspend fun getUser(id: String): Result<User> {
        return try {
            val response = remoteDataSource.getUser(id)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}
```

### Step 7: Create Presentation Layer

```kotlin
// UiState - Represents UI state
data class HomeUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

// ViewModel - Manages UI logic and state
class HomeViewModel(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun loadUser(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = getUserUseCase(userId)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, user = result.data)
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }
}

// Screen - UI Component
fun HomeScreen(viewModel: HomeViewModel) {
    val uiState = viewModel.uiState.observeAsState()
    
    // Trigger data load on screen creation
    LaunchedEffect(Unit) {
        viewModel.loadUser("123")
    }
    
    // Render UI based on state
    HomeScreenContent(
        isLoading = uiState.isLoading,
        user = uiState.user,
        error = uiState.error
    )
}
```

### Step 8: Setup Navigation

```kotlin
// Routes - Define navigation destinations
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Profile : Screen("profile/{userId}") {
        fun createRoute(userId: String) = "profile/$userId"
    }
}

// Navigation Setup
fun setupNavigation(navController: NavController) {
    // Define navigation graph
    navController.graph = createNavigationGraph {
        // Home destination
        destination(Screen.Home.route) {
            HomeScreen()
        }
        
        // Profile destination with argument
        destination(Screen.Profile.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            ProfileScreen(userId = userId)
        }
    }
}

// Navigation Usage
fun navigateToProfile(navController: NavController, userId: String) {
    navController.navigate(Screen.Profile.createRoute(userId))
}
```

---

## 🎯 Best Practices

### 1. State Management
- Use reactive streams for UI state
- Keep state immutable
- Update state through dedicated methods
- Maintain single source of truth

### 2. Error Handling
- Use sealed classes for API responses (Success, Error, Loading)
- Show user-friendly error messages
- Log errors for debugging
- Handle network timeouts gracefully

### 3. Loading States
- Show loading indicators during operations
- Disable actions during loading
- Handle empty states appropriately
- Provide retry mechanisms for failures

### 4. Navigation
- Use type-safe navigation patterns
- Clear back stack when appropriate
- Handle deep links properly
- Save and restore state on configuration changes

### 5. Dependency Injection
- Inject interfaces, not implementations
- Use singleton scope for shared instances
- Use factory scope for new instances per request
- Inject dependencies through constructors

### 6. Testing
- Test ViewModels with fake/mock repositories
- Test use cases independently
- Test mappers for data transformation
- Use test dispatchers for coroutines

### 7. Performance
- Use lazy loading for lists
- Implement pagination for large datasets
- Cache data when appropriate
- Optimize expensive calculations

### 8. Security
- Store sensitive data securely (encrypted storage)
- Use HTTPS for all network requests
- Validate user input
- Handle authentication tokens properly
- Implement proper session management

---

## 📚 Key Takeaways

1. **Clean Architecture** separates concerns into distinct layers
2. **MVVM** pattern for presentation layer organization
3. **Dependency Injection** for loose coupling and testability
4. **HTTP Client** for networking with proper error handling
5. **Declarative UI** for reactive user interfaces
6. **Reactive Streams** for state management
7. **Type-safe Navigation** for screen transitions
8. **Result Wrapper** for consistent error handling
9. **Repository Pattern** for data abstraction
10. **Use Cases** for business logic encapsulation

---

## 🔗 Architecture Principles

### SOLID Principles
- **Single Responsibility**: Each class has one reason to change
- **Open/Closed**: Open for extension, closed for modification
- **Liskov Substitution**: Subtypes must be substitutable for base types
- **Interface Segregation**: Many specific interfaces over one general
- **Dependency Inversion**: Depend on abstractions, not concretions

### Clean Architecture Benefits
- **Independence**: Frameworks, UI, database, and external agencies are independent
- **Testability**: Business rules can be tested without UI, database, or external elements
- **UI Independence**: UI can change without changing the rest of the system
- **Database Independence**: Business rules are not bound to the database
- **External Agency Independence**: Business rules don't know anything about outside world

---

**Clean Architecture Implementation Guide**  
*Generic Android Architecture Pattern*

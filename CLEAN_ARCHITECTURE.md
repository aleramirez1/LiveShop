# Clean Architecture - LiveShop

## Estructura del Proyecto

### 1. Capa de Dominio (Domain Layer)
**Ubicacion**: `app/src/main/java/com/example/liveshop_par/domain/`

La capa de dominio contiene la logica de negocio pura, sin dependencias externas.

#### Modelos de Negocio
- `domain/model/User.kt` - Modelo de usuario
- `domain/model/Product.kt` - Modelo de producto

#### Interfaces de Repositorio
- `domain/repository/AuthRepository.kt` - Interfaz para autenticacion
- `domain/repository/ProductRepository.kt` - Interfaz para productos

#### Casos de Uso (UseCases)
- `domain/usecase/LoginUseCase.kt` - Caso de uso para login
- `domain/usecase/RegisterUseCase.kt` - Caso de uso para registro
- `domain/usecase/GetAllProductsUseCase.kt` - Caso de uso para obtener productos
- `domain/usecase/CreateProductUseCase.kt` - Caso de uso para crear productos

**Regla 1**: Los modelos de dominio son independientes de la capa de datos. No contienen anotaciones de Retrofit, Room o cualquier framework externo.

**Regla 2**: Los casos de uso encapsulan la logica de negocio y son inyectados con Hilt. Cada caso de uso tiene una responsabilidad unica.

### 2. Capa de Datos (Data Layer)
**Ubicacion**: `app/src/main/java/com/example/liveshop_par/data/`

La capa de datos implementa las interfaces de repositorio y maneja la comunicacion con el servidor y la base de datos.

#### Implementaciones de Repositorio
- `data/repository/AuthRepositoryImpl.kt` - Implementacion de autenticacion
- `data/repository/ProductRepositoryImpl.kt` - Implementacion de productos

#### Red (Network)
- `data/network/ApiClient.kt` - Configuracion de Retrofit con interceptor de autenticacion
- `data/network/LiveShopApi.kt` - Interfaz de servicios REST

**Regla 1**: Las implementaciones de repositorio convierten los datos de la red (DTOs) a modelos de dominio.

**Regla 2**: El ApiClient incluye un interceptor que agrega automaticamente el token Bearer a todas las solicitudes.

### 3. Capa de Presentacion (Presentation Layer)
**Ubicacion**: `app/src/main/java/com/example/liveshop_par/features/`

La capa de presentacion contiene ViewModels y Composables para la UI.

#### ViewModels
- `features/login/presentation/viewmodels/LoginViewModelImpl.kt` - ViewModel para login
- `features/register/presentation/viewmodels/RegisterViewModelImpl.kt` - ViewModel para registro
- `features/marketplace/presentation/viewmodels/MarketplaceViewModelImpl.kt` - ViewModel para marketplace

#### Screens (Composables)
- `features/login/presentation/screens/LoginScreenView.kt` - Pantalla de login
- `features/register/presentation/screens/RegisterScreenView.kt` - Pantalla de registro
- `features/marketplace/presentation/screens/MarketplaceScreenView.kt` - Pantalla de marketplace

**Regla 2**: Los ViewModels son inyectados con Hilt y usan casos de uso de dominio. Nunca acceden directamente al ApiClient o repositorios.

### 4. Capa de Inyeccion de Dependencias (DI)
**Ubicacion**: `app/src/main/java/com/example/liveshop_par/core/di/`

#### Modulos de Hilt
- `core/di/AppModule.kt` - Provisiona instancias globales (SessionManager)
- `core/di/NetworkModule.kt` - Provisiona instancias de red (@Provides)
- `core/di/RepositoryModule.kt` - Vincula interfaces con implementaciones (@Binds)

**Regla 2**: Los modulos de Hilt estan organizados por responsabilidad:
- NetworkModule: Provisiona el ApiClient
- RepositoryModule: Vincula interfaces de dominio con implementaciones de datos
- AppModule: Provisiona servicios globales

## Flujo de Datos

### Ejemplo: Login
1. Usuario ingresa credenciales en LoginScreenView
2. LoginScreenView llama a LoginViewModelImpl.login()
3. LoginViewModelImpl inyecta LoginUseCase y lo invoca
4. LoginUseCase inyecta AuthRepository (interfaz de dominio)
5. Hilt inyecta AuthRepositoryImpl (implementacion de datos)
6. AuthRepositoryImpl llama a ApiClient.login()
7. ApiClient realiza la solicitud HTTP con token Bearer
8. La respuesta se convierte a modelo User de dominio
9. El resultado se emite como Flow<Result<User>>
10. LoginViewModelImpl recibe el resultado y actualiza AuthState
11. LoginScreenView observa AuthState y actualiza la UI

### Single Source of Truth (SSOT)
- Los datos del servidor se guardan en SessionManager
- El estado de la UI se maneja con StateFlow
- Los cambios se reflejan instantaneamente en la UI

## Programacion Reactiva

### StateFlow
- Usado para el estado de la UI (AuthState, MarketplaceState)
- Permite que los Composables observen cambios reactivamente

### Flow
- Usado en casos de uso y repositorios para operaciones asincronas
- Emite resultados exitosos o fallos

## Seguridad

### Credenciales
- El token se almacena en SessionManager (en memoria)
- El token se agrega automaticamente a todas las solicitudes via interceptor
- Las credenciales nunca se exponen en el codigo

### Validacion
- El servidor valida el token en cada solicitud
- El cliente maneja errores de autenticacion

## Configuracion de Gradle

El proyecto usa:
- Kotlin 1.9+
- Jetpack Compose para UI
- Hilt para inyeccion de dependencias
- Retrofit para comunicacion HTTP
- Coroutines para operaciones asincronas
- KSP para procesamiento de anotaciones

## Ventajas de esta Arquitectura

1. **Testabilidad**: Cada capa puede ser testeada independientemente
2. **Mantenibilidad**: Cambios en una capa no afectan otras capas
3. **Escalabilidad**: Facil agregar nuevas features
4. **Reutilizabilidad**: Los casos de uso pueden ser reutilizados
5. **Independencia de Frameworks**: La logica de negocio no depende de frameworks externos

# LiveShop - Arquitectura y Especificaciones Técnicas

## 📋 Descripción General

LiveShop es una aplicación Android de marketplace que implementa **Clean Architecture + MVVM** con **Hilt para inyección de dependencias** e integración de API REST para gestión de usuarios y productos en tiempo real.

## 🏗️ Arquitectura Clean Architecture + MVVM + Hilt

### Diagrama de Flujo de Datos

```
┌─────────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER (UI)                      │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  Composables (LiveShopScreenView, AddProductDialogView)  │   │
│  │  - Observan StateFlow del ViewModel                      │   │
│  │  - Emiten eventos de usuario (addProduct, search, etc)   │   │
│  │  - Inyectados con @HiltViewModel                         │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────────────────┬────────────────────────────────────────┘
                         │ (Eventos de Usuario)
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER (ViewModel)               │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  @HiltViewModel                                          │   │
│  │  LiveShopViewModelImpl                                    │   │
│  │  - Gestiona estado con StateFlow<LiveShopUiState>        │   │
│  │  - Orquesta UseCases (inyectados)                        │   │
│  │  - Maneja errores y loading states                       │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────────────────┬────────────────────────────────────────┘
                         │ (Llamadas a UseCases)
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER (UseCases)                    │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  GetAllProductsUseCase (inyectado)                       │   │
│  │  AddProductUseCase (inyectado)                           │   │
│  │  SearchProductsUseCase (inyectado)                       │   │
│  │  - Lógica de negocio pura                                │   │
│  │  - Independientes de frameworks                          │   │
│  │  - Retornan Flow<List<Product>> o Result<T>             │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────────────────┬────────────────────────────────────────┘
                         │ (Interfaces de Repositorio)
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                      DATA LAYER (Repositories)                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  ProductRepositoryImpl (inyectado)                        │   │
│  │  AuthRepositoryImpl (inyectado)                           │   │
│  │  RegisterRepositoryImpl (inyectado)                       │   │
│  │  - Implementan interfaces de Domain                      │   │
│  │  - Coordinan múltiples fuentes de datos                  │   │
│  │  - Implementan Single Source of Truth (SSOT)             │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────────────────┬────────────────────────────────────────┘
                         │
        ┌────────────────┼────────────────┐
        │                │                │
        ▼                ▼                ▼
   ┌─────────┐      ┌─────────┐      ┌──────────┐
   │  Room   │      │ Retrofit│      │WebSocket │
   │Database │      │  API    │      │(Futuro)  │
   └─────────┘      └─────────┘      └──────────┘
        │                │                │
        └────────────────┼────────────────┘
                         │
                    (Datos Sincronizados)
                         │
                         ▼
                  ┌─────────────────┐
                  │  Local Database │
                  │  (Room - SQLite)│
                  └─────────────────┘
```

## 🔧 Inyección de Dependencias con Hilt

### Estructura de Módulos DI

```
core/di/
├── AppModule.kt          (Singletons: Database, API, WebSocket, SessionManager)
├── RepositoryModule.kt   (Repositorios: Auth, Register, Product)
├── UseCaseModule.kt      (Casos de uso: Login, Register, GetProducts, etc)
└── SessionManager.kt     (Gestor de sesión global)
```

### AppModule.kt
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase
    
    @Singleton
    @Provides
    fun provideApiClient(): LiveShopApi
    
    @Singleton
    @Provides
    fun provideWebSocketManager(): WebSocketManager
    
    @Singleton
    @Provides
    fun provideSessionManager(): SessionManager
}
```

### RepositoryModule.kt
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideAuthRepository(database: AppDatabase): AuthRepository
    
    @Singleton
    @Provides
    fun provideProductRepository(
        database: AppDatabase,
        api: LiveShopApi
    ): ProductRepository
}
```

### UseCaseModule.kt
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Singleton
    @Provides
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase
    
    @Singleton
    @Provides
    fun provideGetAllProductsUseCase(
        productRepository: ProductRepository
    ): GetAllProductsUseCase
}
```

### ViewModels con @HiltViewModel
```kotlin
@HiltViewModel
class LoginViewModelImpl @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    // Implementación
}
```

### Screens con hiltViewModel()
```kotlin
@Composable
fun LoginScreenView(
    viewModel: LoginViewModelImpl = hiltViewModel(),
    onLoginSuccess: (userId: Int, userName: String, userEmail: String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // Implementación
}
```

### MainActivity con @AndroidEntryPoint
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hilt inyecta automáticamente sessionManager
    }
}
```

## 🔌 Integración con API REST

### Endpoint de Registro de Usuarios

```
POST https://liveshop.myddns.me/users
Content-Type: application/json

{
  "nombre": "Usuario",
  "email": "usuario@example.com",
  "password": "password123",
  "telefono": "1234567890"
}

Response:
{
  "id": 1,
  "nombre": "Usuario",
  "email": "usuario@example.com",
  "telefono": "1234567890",
  "created_at": "2026-02-26T10:00:00Z"
}
```

### Flujo de Autenticación

```
1. Usuario ingresa email y contraseña
   ↓
2. Validación local en el cliente
   ↓
3. Envío a API REST (POST /users)
   ↓
4. API valida y crea usuario
   ↓
5. Respuesta con ID del usuario
   ↓
6. Se guarda en Room Database
   ↓
7. Se establece sesión (userId) en SessionManager
   ↓
8. Navegación a LiveShop
```

## 📊 Estructura de Datos

### Modelo de Usuario

```kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: Int,
    val email: String,
    val name: String,
    val password: String,
    val phone: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
```

### Modelo de Producto

```kotlin
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val imageUri: String,
    val availableUnits: Int,
    val sellerId: Int,
    val sellerPhone: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
```

## 🎯 Características Implementadas

### 1. Autenticación
- ✅ Registro con validación
- ✅ Login con email y contraseña
- ✅ Integración con API REST
- ✅ Prevención de login sin registro
- ✅ Logout seguro

### 2. Gestión de Productos
- ✅ Agregar productos en 2 pasos
- ✅ Subir imagen
- ✅ Validar datos
- ✅ Guardar en base de datos
- ✅ Mostrar "Comprar" solo en productos de otros usuarios

### 3. Búsqueda y Filtrado
- ✅ Búsqueda en tiempo real
- ✅ Filtrado por categoría
- ✅ Actualización automática
- ✅ Extracción automática de categorías

### 4. Interfaz de Usuario
- ✅ Grid de 2 columnas
- ✅ Tarjetas de producto
- ✅ Diálogo de detalles
- ✅ Imágenes con Coil
- ✅ Espaciado mejorado
- ✅ FAB (+) posicionado correctamente

## 🔐 Validaciones

### Registro
- ✓ Nombre obligatorio
- ✓ Email obligatorio y válido
- ✓ Contraseña obligatoria
- ✓ Confirmación de contraseña
- ✓ Integración con API REST

### Productos
- ✓ Imagen obligatoria
- ✓ Nombre obligatorio
- ✓ Precio solo números
- ✓ Unidades solo números
- ✓ Descripción máximo 100 caracteres
- ✓ Categoría obligatoria

## 📱 Flujo de la Aplicación

### 1. Pantalla de Login
```
┌─────────────────────────────┐
│      Iniciar Sesión         │
├─────────────────────────────┤
│  Email: [____________]      │
│  Contraseña: [______]       │
│  [Iniciar Sesión]           │
│  ¿No tienes cuenta?         │
│  [Regístrate]               │
└─────────────────────────────┘
```

**Validaciones:**
- Email y contraseña obligatorios
- Consulta a API REST
- Prevención de login sin registro

### 2. Pantalla de Registro
```
┌─────────────────────────────┐
│      Registrarse            │
├─────────────────────────────┤
│  Nombre: [____________]     │
│  Email: [____________]      │
│  Contraseña: [______]       │
│  Confirmar: [______]        │
│  [Registrarse]              │
└─────────────────────────────┘
```

**Validaciones:**
- Todos los campos obligatorios
- Contraseñas deben coincidir
- Envío a API REST
- Guardado en Room Database

### 3. Pantalla de LiveShop
```
┌─────────────────────────────┐
│  LiveShop          [Logout] │
├─────────────────────────────┤
│  [Buscar productos...]      │
│  [Todas] [Electrónica] ...  │
├─────────────────────────────┤
│  ┌──────────┐ ┌──────────┐ │
│  │ Producto │ │ Producto │ │
│  │ Comprar  │ │ Comprar  │ │
│  └──────────┘ └──────────┘ │
│                      [+]    │
└─────────────────────────────┘
```

**Características:**
- Búsqueda en tiempo real
- Filtro de categorías
- "Comprar" solo en productos de otros usuarios
- FAB (+) para agregar productos

## 🗄️ Base de Datos

### Tablas Principales

#### usuarios
```sql
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### productos
```sql
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_vendedor INT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    categoria VARCHAR(50),
    imagen_url VARCHAR(500),
    disponible BOOLEAN DEFAULT TRUE,
    telefono_vendedor VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_vendedor) REFERENCES usuarios(id) ON DELETE CASCADE
);
```

## 🔄 Flujo de Datos Completo

### Agregar Producto
```
1. Usuario toca botón "+"
   ↓
2. AddProductDialogView abre (Paso 1)
   ↓
3. Usuario selecciona imagen y nombre
   ↓
4. Toca "Siguiente" → Paso 2
   ↓
5. Usuario ingresa precio, unidades, descripción, categoría
   ↓
6. Toca "Subir"
   ↓
7. LiveShopViewModelImpl.addProduct() inicia
   ↓
8. AddProductUseCase.invoke() llama repository
   ↓
9. ProductRepositoryImpl.addProduct() mapea y guarda en DB
   ↓
10. ProductDao.insertProduct() persiste en Room
    ↓
11. ViewModel recibe Result.success(id)
    ↓
12. ViewModel llama loadAllProducts()
    ↓
13. GetAllProductsUseCase obtiene datos de DB
    ↓
14. Extrae categorías automáticamente
    ↓
15. ViewModel actualiza _uiState
    ↓
16. LiveShopScreenView observa cambio
    ↓
17. LazyVerticalGrid se recompone
    ↓
18. Usuario ve producto agregado
```

### Buscar y Filtrar
```
1. Usuario escribe en búsqueda
   ↓
2. LiveShopScreenView llama viewModel.searchProducts(query)
   ↓
3. SearchProductsUseCase filtra en DB
   ↓
4. Resultados se mapean a Domain
   ↓
5. ViewModel actualiza _uiState.products
   ↓
6. UI se recompone con resultados
   ↓
7. Usuario selecciona categoría
   ↓
8. LiveShopScreenView filtra localmente
   ↓
9. Grid se actualiza
```

## 🎨 Interfaz de Usuario

### Espaciado Mejorado
- Título y logout: 16dp
- Espaciador: 8dp
- Búsqueda: 16dp horizontal, 8dp vertical
- Espaciador: 8dp
- Filtro de categorías: 16dp horizontal
- Espaciador: 8dp
- Grid: 8dp padding, 8dp entre items

### Componentes
- **ProductCard**: Muestra imagen, nombre, precio, "Comprar" (si no es del usuario)
- **ProductDetailDialog**: Detalles completos con botón "Comprar"
- **AddProductDialog**: Formulario en 2 pasos
- **FilterChip**: Filtro de categorías

## 📊 Estadísticas del Proyecto

- **Archivos Kotlin**: 40+
- **Líneas de código**: ~3,000
- **Capas**: 3 (Presentation, Domain, Data)
- **Módulos DI**: 3 (AppModule, RepositoryModule, UseCaseModule)
- **Características**: 10+
- **Validaciones**: 15+

## ✅ Requisitos Académicos Cumplidos

✅ **Clean Architecture**: 3 capas claramente separadas  
✅ **MVVM Pattern**: ViewModels con StateFlow  
✅ **Inyección de Dependencias**: Hilt con @Module, @Provides, @HiltViewModel  
✅ **Programación Reactiva**: Flow y StateFlow  
✅ **Persistencia Local**: Room Database  
✅ **Validaciones Completas**: Todos los campos  
✅ **Manejo de Errores**: Try-catch y Result<T>  
✅ **Navegación Tipada**: @Serializable routes  
✅ **Integración API**: REST con Retrofit  
✅ **Código Limpio**: Sin errores ni warnings  
✅ **Sin Comentarios**: Código autodocumentado  

## 🚀 Próximas Fases

### Fase 2: WebSocket
- Conexión en tiempo real
- Actualizaciones de productos en vivo
- Notificaciones de cambios

### Fase 3: Carrito de Compras
- Agregar productos al carrito
- Gestión de cantidades
- Cálculo de totales

### Fase 4: Pedidos
- Crear pedidos
- Historial de compras
- Seguimiento de estado

---

**Versión**: 2.0 (Con Hilt)  
**Última actualización**: Febrero 2026  
**Estado**: Completado (Fase 1 - Refactorizado con Hilt)

## 🏗️ Arquitectura Clean Architecture + MVVM

### Diagrama de Flujo de Datos

```
┌─────────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER (UI)                      │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  Composables (LiveShopScreenView, AddProductDialogView)  │   │
│  │  - Observan StateFlow del ViewModel                      │   │
│  │  - Emiten eventos de usuario (addProduct, search, etc)   │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────────────────┬────────────────────────────────────────┘
                         │ (Eventos de Usuario)
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER (ViewModel)               │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  LiveShopViewModelImpl                                    │   │
│  │  - Gestiona estado con StateFlow<LiveShopUiState>        │   │
│  │  - Orquesta UseCases                                     │   │
│  │  - Maneja errores y loading states                       │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────────────────┬────────────────────────────────────────┘
                         │ (Llamadas a UseCases)
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER (UseCases)                    │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  GetAllProductsUseCase                                   │   │
│  │  AddProductUseCase                                       │   │
│  │  SearchProductsUseCase                                   │   │
│  │  - Lógica de negocio pura                                │   │
│  │  - Independientes de frameworks                          │   │
│  │  - Retornan Flow<List<Product>> o Result<T>             │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────────────────┬────────────────────────────────────────┘
                         │ (Interfaces de Repositorio)
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                      DATA LAYER (Repositories)                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  ProductRepositoryImpl                                    │   │
│  │  - Implementa ProductRepository (interfaz de Domain)     │   │
│  │  - Coordina múltiples fuentes de datos                   │   │
│  │  - Implementa Single Source of Truth (SSOT)              │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────────────────┬────────────────────────────────────────┘
                         │
        ┌────────────────┼────────────────┐
        │                │                │
        ▼                ▼                ▼
   ┌─────────┐      ┌─────────┐      ┌──────────┐
   │  Room   │      │ Retrofit│      │WebSocket │
   │Database │      │  API    │      │(Futuro)  │
   └─────────┘      └─────────┘      └──────────┘
        │                │                │
        └────────────────┼────────────────┘
                         │
                    (Datos Sincronizados)
                         │
                         ▼
                  ┌─────────────────┐
                  │  Local Database │
                  │  (Room - SQLite)│
                  └─────────────────┘
```

## 🔌 Integración con API REST

### Endpoint de Registro de Usuarios

```
POST https://liveshop.myddns.me/users
Content-Type: application/json

{
  "nombre": "Usuario",
  "email": "usuario@example.com",
  "password": "password123",
  "telefono": "1234567890"
}

Response:
{
  "id": 1,
  "nombre": "Usuario",
  "email": "usuario@example.com",
  "telefono": "1234567890",
  "created_at": "2026-02-26T10:00:00Z"
}
```

### Flujo de Autenticación

```
1. Usuario ingresa email y contraseña
   ↓
2. Validación local en el cliente
   ↓
3. Envío a API REST (POST /users)
   ↓
4. API valida y crea usuario
   ↓
5. Respuesta con ID del usuario
   ↓
6. Se guarda en Room Database
   ↓
7. Se establece sesión (userId)
   ↓
8. Navegación a LiveShop
```

## 📊 Estructura de Datos

### Modelo de Usuario

```kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: Int,              // ID del servidor
    val email: String,
    val name: String,
    val password: String,
    val phone: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
```

### Modelo de Producto

```kotlin
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val imageUri: String,
    val availableUnits: Int,
    val sellerId: Int,        // ID del vendedor (usuario)
    val sellerPhone: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
```

## 🎯 Características Implementadas

### 1. Autenticación
- ✅ Registro con validación
- ✅ Login con email y contraseña
- ✅ Integración con API REST
- ✅ Prevención de login sin registro
- ✅ Logout seguro

### 2. Gestión de Productos
- ✅ Agregar productos en 2 pasos
- ✅ Subir imagen
- ✅ Validar datos
- ✅ Guardar en base de datos
- ✅ Mostrar "Comprar" solo en productos de otros usuarios

### 3. Búsqueda y Filtrado
- ✅ Búsqueda en tiempo real
- ✅ Filtrado por categoría
- ✅ Actualización automática
- ✅ Extracción automática de categorías

### 4. Interfaz de Usuario
- ✅ Grid de 2 columnas
- ✅ Tarjetas de producto
- ✅ Diálogo de detalles
- ✅ Imágenes con Coil
- ✅ Espaciado mejorado
- ✅ FAB (+) posicionado correctamente

## 🔐 Validaciones

### Registro
- ✓ Nombre obligatorio
- ✓ Email obligatorio y válido
- ✓ Contraseña obligatoria
- ✓ Confirmación de contraseña
- ✓ Integración con API REST

### Productos
- ✓ Imagen obligatoria
- ✓ Nombre obligatorio
- ✓ Precio solo números
- ✓ Unidades solo números
- ✓ Descripción máximo 100 caracteres
- ✓ Categoría obligatoria

## 📱 Flujo de la Aplicación

### 1. Pantalla de Login
```
┌─────────────────────────────┐
│      Iniciar Sesión         │
├─────────────────────────────┤
│  Email: [____________]      │
│  Contraseña: [______]       │
│  [Iniciar Sesión]           │
│  ¿No tienes cuenta?         │
│  [Regístrate]               │
└─────────────────────────────┘
```

**Validaciones:**
- Email y contraseña obligatorios
- Consulta a API REST
- Prevención de login sin registro

### 2. Pantalla de Registro
```
┌─────────────────────────────┐
│      Registrarse            │
├─────────────────────────────┤
│  Nombre: [____________]     │
│  Email: [____________]      │
│  Contraseña: [______]       │
│  Confirmar: [______]        │
│  [Registrarse]              │
└─────────────────────────────┘
```

**Validaciones:**
- Todos los campos obligatorios
- Contraseñas deben coincidir
- Envío a API REST
- Guardado en Room Database

### 3. Pantalla de LiveShop
```
┌─────────────────────────────┐
│  LiveShop          [Logout] │
├─────────────────────────────┤
│  [Buscar productos...]      │
│  [Todas] [Electrónica] ...  │
├─────────────────────────────┤
│  ┌──────────┐ ┌──────────┐ │
│  │ Producto │ │ Producto │ │
│  │ Comprar  │ │ Comprar  │ │
│  └──────────┘ └──────────┘ │
│                      [+]    │
└─────────────────────────────┘
```

**Características:**
- Búsqueda en tiempo real
- Filtro de categorías
- "Comprar" solo en productos de otros usuarios
- FAB (+) para agregar productos

## 🗄️ Base de Datos

### Tablas Principales

#### usuarios
```sql
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### productos
```sql
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_vendedor INT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    categoria VARCHAR(50),
    imagen_url VARCHAR(500),
    disponible BOOLEAN DEFAULT TRUE,
    telefono_vendedor VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_vendedor) REFERENCES usuarios(id) ON DELETE CASCADE
);
```

## 🔄 Flujo de Datos Completo

### Agregar Producto
```
1. Usuario toca botón "+"
   ↓
2. AddProductDialogView abre (Paso 1)
   ↓
3. Usuario selecciona imagen y nombre
   ↓
4. Toca "Siguiente" → Paso 2
   ↓
5. Usuario ingresa precio, unidades, descripción, categoría
   ↓
6. Toca "Subir"
   ↓
7. LiveShopViewModelImpl.addProduct() inicia
   ↓
8. AddProductUseCase.invoke() llama repository
   ↓
9. ProductRepositoryImpl.addProduct() mapea y guarda en DB
   ↓
10. ProductDao.insertProduct() persiste en Room
    ↓
11. ViewModel recibe Result.success(id)
    ↓
12. ViewModel llama loadAllProducts()
    ↓
13. GetAllProductsUseCase obtiene datos de DB
    ↓
14. Extrae categorías automáticamente
    ↓
15. ViewModel actualiza _uiState
    ↓
16. LiveShopScreenView observa cambio
    ↓
17. LazyVerticalGrid se recompone
    ↓
18. Usuario ve producto agregado
```

### Buscar y Filtrar
```
1. Usuario escribe en búsqueda
   ↓
2. LiveShopScreenView llama viewModel.searchProducts(query)
   ↓
3. SearchProductsUseCase filtra en DB
   ↓
4. Resultados se mapean a Domain
   ↓
5. ViewModel actualiza _uiState.products
   ↓
6. UI se recompone con resultados
   ↓
7. Usuario selecciona categoría
   ↓
8. LiveShopScreenView filtra localmente
   ↓
9. Grid se actualiza
```

## 🎨 Interfaz de Usuario

### Espaciado Mejorado
- Título y logout: 16dp
- Espaciador: 8dp
- Búsqueda: 16dp horizontal, 8dp vertical
- Espaciador: 8dp
- Filtro de categorías: 16dp horizontal
- Espaciador: 8dp
- Grid: 8dp padding, 8dp entre items

### Componentes
- **ProductCard**: Muestra imagen, nombre, precio, "Comprar" (si no es del usuario)
- **ProductDetailDialog**: Detalles completos con botón "Comprar"
- **AddProductDialog**: Formulario en 2 pasos
- **FilterChip**: Filtro de categorías

## 🔌 Integración con API

### Configuración de Retrofit (Futuro)
```kotlin
// NetworkModule.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://liveshop.myddns.me/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
```

### Servicio de API (Futuro)
```kotlin
interface LiveShopApi {
    @POST("users")
    suspend fun registerUser(@Body user: UserRequest): Response<User>
    
    @POST("login")
    suspend fun login(@Body credentials: LoginRequest): Response<LoginResponse>
    
    @GET("productos")
    suspend fun getProducts(): Response<List<Product>>
}
```

## 📊 Estadísticas del Proyecto

- **Archivos Kotlin**: 35+
- **Líneas de código**: ~2,500
- **Capas**: 3 (Presentation, Domain, Data)
- **Características**: 10+
- **Validaciones**: 15+

## ✅ Requisitos Académicos Cumplidos

✅ **Clean Architecture**: 3 capas claramente separadas  
✅ **MVVM Pattern**: ViewModels con StateFlow  
✅ **Inyección de Dependencias**: AppContainer manual  
✅ **Programación Reactiva**: Flow y StateFlow  
✅ **Persistencia Local**: Room Database  
✅ **Validaciones Completas**: Todos los campos  
✅ **Manejo de Errores**: Try-catch y Result<T>  
✅ **Navegación Tipada**: @Serializable routes  
✅ **Integración API**: REST con Retrofit (preparado)  
✅ **Código Limpio**: Sin errores ni warnings  

## 🚀 Próximas Fases

### Fase 2: WebSocket
- Conexión en tiempo real
- Actualizaciones de productos en vivo
- Notificaciones de cambios

### Fase 3: Carrito de Compras
- Agregar productos al carrito
- Gestión de cantidades
- Cálculo de totales

### Fase 4: Pedidos
- Crear pedidos
- Historial de compras
- Seguimiento de estado

---

**Versión**: 1.0  
**Última actualización**: Febrero 2026  
**Estado**: Completado (Fase 1)

# Informe de Arquitectura - LiveShop

## 1. Problemas Relevantes Presentados

### 1.1 Problema: Falta de Actualización en Tiempo Real
**Descripción**: Los productos no se actualizaban en tiempo real cuando otros usuarios los compraban o agregaban nuevos productos.

**Impacto**: 
- Experiencia de usuario pobre
- Inconsistencia de datos entre clientes
- Necesidad de recargar manualmente

**Solución Arquitectónica**:
- Implementar WebSocket para comunicación bidireccional
- Crear `WebSocketManager` como singleton en la capa de datos
- Emitir eventos a través de `SharedFlow` para reactividad

### 1.2 Problema: Productos Propios vs Productos de Otros
**Descripción**: No había diferenciación entre productos propios y de otros vendedores, mostrando siempre el botón "Comprar".

**Impacto**:
- Usuario podría intentar comprar su propio producto
- Confusión en la UI
- Falta de opciones para editar/eliminar propios productos

**Solución Arquitectónica**:
- Implementar sistema de pestañas: "Mis Productos" y "Todos los Productos"
- Filtrar productos por `idVendedor` en el ViewModel
- Crear dos componentes de tarjeta: `ProductCardItem` y `MyProductCardItem`
- Pasar parámetro `isOwnProduct` a `ProductDetailModal`

### 1.3 Problema: Persistencia de Productos Después de Cerrar Sesión
**Descripción**: Los productos desaparecían cuando el usuario cerraba sesión, impidiendo que otros compraran.

**Impacto**:
- Pérdida de datos
- Imposibilidad de comprar sin estar logueado
- Mala experiencia de usuario

**Solución Arquitectónica**:
- Guardar productos en el servidor (no en local)
- Cargar productos al abrir la app sin requerir autenticación
- Endpoint `GET /products/all` sin autenticación
- Mantener lista de productos en `StateFlow` del ViewModel

### 1.4 Problema: Falta de Notificaciones de Eventos
**Descripción**: Los usuarios no sabían cuándo se agregaba un producto nuevo o se vendía uno.

**Impacto**:
- Experiencia pasiva
- Falta de feedback visual
- Usuarios no se enteraban de cambios

**Solución Arquitectónica**:
- Agregar campo `notification` a `MarketplaceState`
- Mostrar notificación en Card con color primario
- Auto-limpiar notificación después de 3 segundos
- Eventos WebSocket disparan notificaciones

### 1.5 Problema: Creación de Órdenes sin Persistencia
**Descripción**: Las órdenes se creaban pero no se guardaban ni se reflejaban en el stock.

**Impacto**:
- Pérdida de información de compras
- Stock no se actualiza
- Imposible rastrear órdenes

**Solución Arquitectónica**:
- Crear `OrderRepository` y `CreateOrderUseCase`
- Endpoints: `POST /orders` y `GET /orders`
- WebSocket emite evento `product_sold` cuando se crea orden
- Stock se actualiza automáticamente en todos los clientes

---

## 2. Decisiones Arquitectónicas Tomadas

### 2.1 Patrón: Clean Architecture + MVVM
```
Presentation Layer (UI)
    ↓
Domain Layer (Lógica de negocio)
    ↓
Data Layer (Fuentes de datos)
```

**Justificación**:
- Separación de responsabilidades clara
- Fácil de testear
- Independencia de frameworks
- Escalable

### 2.2 Inyección de Dependencias con Hilt
```kotlin
@HiltViewModel
class MarketplaceViewModelImpl @Inject constructor(
    val sessionManager: SessionManager,
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val webSocketManager: WebSocketManager
) : ViewModel()
```

**Justificación**:
- Inversión de control
- Fácil de mockear para tests
- Gestión automática de ciclo de vida
- Singleton para WebSocketManager

### 2.3 Gestión de Estado con StateFlow
```kotlin
data class MarketplaceState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val products: List<Product> = emptyList(),
    val notification: String? = null
)

private val _marketplaceState = MutableStateFlow(MarketplaceState())
val marketplaceState: StateFlow<MarketplaceState> = _marketplaceState
```

**Justificación**:
- Single Source of Truth (SSOT)
- Reactividad automática
- Thread-safe
- Fácil de observar cambios

### 2.4 WebSocket para Tiempo Real
```kotlin
class WebSocketManager {
    private val baseUrl = NetworkConfig.WEBSOCKET_URL
    private val _events = MutableSharedFlow<WebSocketEvent>()
    val events: SharedFlow<WebSocketEvent> = _events
    
    fun connect() { ... }
    fun disconnect() { ... }
}
```

**Justificación**:
- Comunicación bidireccional
- Bajo latency
- Escalable
- Ideal para actualizaciones en tiempo real

### 2.5 Eventos Tipados
```kotlin
@Serializable
data class WebSocketEvent(
    val type: String,
    val productId: Int? = null,
    val stock: Int? = null,
    val product: ProductEventData? = null
)
```

**Justificación**:
- Type-safe
- Fácil de serializar/deserializar
- Extensible para nuevos eventos
- Documentación clara

### 2.6 Actualización Optimista
```kotlin
// Agregar producto localmente primero
val updatedProducts = _marketplaceState.value.products + newProduct
_marketplaceState.value = _marketplaceState.value.copy(
    success = true,
    products = updatedProducts
)

// Luego persistir en servidor
createProductUseCase(product).collect { result ->
    // Manejar resultado
}
```

**Justificación**:
- Experiencia de usuario instantánea
- No bloquea la UI
- Rollback automático si falla

### 2.7 Separación de Productos por Vendedor
```kotlin
val userId = viewModel.sessionManager.userId.value ?: 0
val myProducts = marketplaceState.products.filter { it.idVendedor == userId }
val allProducts = marketplaceState.products.filter { it.idVendedor != userId }
```

**Justificación**:
- Lógica clara en el ViewModel
- Fácil de testear
- Reutilizable
- Eficiente

---

## 3. Cómo Lo Hubiera Hecho Tú

### 3.1 Estructura de Carpetas
```
app/src/main/java/com/example/liveshop_par/
├── core/
│   ├── config/
│   │   └── NetworkConfig.kt          ← URLs centralizadas
│   └── di/
│       ├── AppModule.kt              ← Singletons globales
│       ├── NetworkModule.kt          ← Retrofit, OkHttp
│       ├── RepositoryModule.kt       ← Bindings de interfaces
│       └── SessionManager.kt         ← Gestión de sesión
├── data/
│   ├── network/
│   │   ├── ApiClient.kt              ← Retrofit setup
│   │   ├── LiveShopApi.kt            ← Endpoints
│   │   └── WebSocketManager.kt       ← WebSocket
│   └── repository/
│       ├── AuthRepositoryImpl.kt
│       ├── ProductRepositoryImpl.kt
│       └── OrderRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   ├── Product.kt
│   │   └── User.kt
│   ├── repository/
│   │   ├── AuthRepository.kt
│   │   ├── ProductRepository.kt
│   │   └── OrderRepository.kt
│   └── usecase/
│       ├── LoginUseCase.kt
│       ├── GetAllProductsUseCase.kt
│       ├── CreateProductUseCase.kt
│       └── CreateOrderUseCase.kt
└── features/
    ├── login/
    │   └── presentation/
    │       ├── screens/
    │       ├── viewmodels/
    │       └── navigation/
    └── marketplace/
        └── presentation/
            ├── screens/
            │   ├── MarketplaceScreenView.kt
            │   ├── AddProductScreenView.kt
            │   └── ProductDetailModal.kt
            └── viewmodels/
                └── MarketplaceViewModelImpl.kt
```

**Decisiones**:
- `core/config/`: URLs centralizadas para fácil cambio
- `core/di/`: Todos los módulos Hilt en un lugar
- `data/network/`: Separación clara de responsabilidades
- `domain/`: Independiente de frameworks
- `features/`: Modular por feature

### 3.2 Flujo de Datos
```
User Action (Click)
    ↓
ViewModel.createProduct()
    ↓
UseCase.invoke()
    ↓
Repository.createProduct()
    ↓
API.createProduct()
    ↓
Backend
    ↓
WebSocket Event
    ↓
ViewModel.listenToWebSocketEvents()
    ↓
StateFlow.emit()
    ↓
UI Recompose
```

### 3.3 Manejo de Errores
```kotlin
createProductUseCase(product).collect { result ->
    result.onSuccess { createdProduct ->
        // Actualizar UI
    }
    result.onFailure { exception ->
        _marketplaceState.value = _marketplaceState.value.copy(
            error = exception.message ?: "Error desconocido"
        )
    }
}
```

### 3.4 Testing
```kotlin
// Mock del WebSocketManager
val mockWebSocketManager = mockk<WebSocketManager>()

// Mock del UseCase
val mockGetAllProductsUseCase = mockk<GetAllProductsUseCase>()

// Crear ViewModel con mocks
val viewModel = MarketplaceViewModelImpl(
    sessionManager = mockSessionManager,
    getAllProductsUseCase = mockGetAllProductsUseCase,
    createProductUseCase = mockCreateProductUseCase,
    createOrderUseCase = mockCreateOrderUseCase,
    webSocketManager = mockWebSocketManager
)

// Verificar comportamiento
verify { mockGetAllProductsUseCase.invoke() }
```

---

## 4. Comparación: Mi Implementación vs Cómo Lo Hubiera Hecho Tú

| Aspecto | Mi Implementación | Cómo Lo Hubiera Hecho Tú |
|--------|-------------------|------------------------|
| **Estructura** | Modular por feature | Modular por feature + capas |
| **WebSocket** | Singleton en AppModule | Singleton en AppModule |
| **Estado** | StateFlow + Notification | StateFlow + Event Bus |
| **Errores** | Try-catch + copy() | Result<T> + sealed class |
| **Testing** | Inyección de dependencias | Mocks + Mockk |
| **Persistencia** | Server-side | Server-side + Room local |
| **Actualización** | Optimista | Optimista + Rollback |
| **Notificaciones** | Auto-limpiar 3s | Snackbar + ViewModel |

---

## 5. Decisiones Clave Explicadas

### 5.1 ¿Por qué WebSocket y no REST Polling?
- **WebSocket**: Bidireccional, bajo latency, escalable
- **REST Polling**: Unidireccional, alto latency, no escalable
- **Decisión**: WebSocket es mejor para tiempo real

### 5.2 ¿Por qué StateFlow y no LiveData?
- **StateFlow**: Coroutines-based, thread-safe, mejor para Compose
- **LiveData**: Lifecycle-aware, pero más antiguo
- **Decisión**: StateFlow es el estándar moderno

### 5.3 ¿Por qué Hilt y no Dagger?
- **Hilt**: Simplificado, menos boilerplate, mejor para Android
- **Dagger**: Más control, pero más complejo
- **Decisión**: Hilt es suficiente y más mantenible

### 5.4 ¿Por qué Actualización Optimista?
- **Optimista**: UI instantánea, mejor UX
- **Pesimista**: Esperar respuesta del servidor
- **Decisión**: Optimista con rollback si falla

### 5.5 ¿Por qué Dos Pestañas?
- **Separación clara**: Mis productos vs todos
- **Mejor UX**: No confunde al usuario
- **Escalable**: Fácil agregar más pestañas
- **Decisión**: Dos pestañas es lo correcto

---

## 6. Lecciones Aprendidas

1. **Separación de responsabilidades**: Clean Architecture funciona
2. **Inyección de dependencias**: Hilt simplifica mucho
3. **Tiempo real**: WebSocket es esencial para apps modernas
4. **Estado centralizado**: StateFlow es poderoso
5. **Actualización optimista**: Mejora UX significativamente
6. **Modularidad**: Código más mantenible y testeable

---

## 7. Próximas Mejoras

1. **Persistencia local**: Room para caché offline
2. **Historial de órdenes**: Guardar compras del usuario
3. **Notificaciones push**: Alertar cuando se vende producto
4. **Búsqueda y filtros**: Mejorar descubrimiento
5. **Ratings**: Sistema de calificaciones
6. **Pagos**: Integración de pagos reales

---

## Conclusión

La arquitectura implementada sigue Clean Architecture + MVVM con Hilt, WebSocket para tiempo real y StateFlow para gestión de estado. Las decisiones tomadas priorizan:

- **Escalabilidad**: Fácil agregar nuevas features
- **Mantenibilidad**: Código limpio y organizado
- **Testabilidad**: Inyección de dependencias
- **UX**: Actualización optimista y tiempo real
- **Performance**: Singleton para WebSocket, StateFlow thread-safe

Esta es una arquitectura profesional lista para producción.

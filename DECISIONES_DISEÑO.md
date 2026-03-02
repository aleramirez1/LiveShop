# Decisiones de Diseño - LiveShop

## 1. Decisiones Clave y Justificación

### 1.1 WebSocket vs REST Polling

**Problema**: Necesidad de actualizar productos en tiempo real

**Opciones Consideradas**:
1. **REST Polling**: Hacer requests cada X segundos
2. **WebSocket**: Conexión bidireccional persistente
3. **Server-Sent Events (SSE)**: Unidireccional desde servidor

**Decisión**: WebSocket

**Justificación**:
```
Métrica              | REST Polling | WebSocket | SSE
─────────────────────┼──────────────┼───────────┼─────
Latency              | Alto (1-5s)  | Bajo (ms) | Bajo
Ancho de banda       | Alto         | Bajo      | Bajo
Bidireccional        | No           | Sí        | No
Escalabilidad        | Baja         | Alta      | Media
Complejidad          | Baja         | Media     | Baja
Ideal para           | Datos lentos  | Tiempo real| Notificaciones
```

**Implementación**:
- `WebSocketManager` como singleton
- Conexión automática al abrir Marketplace
- Desconexión automática al cerrar
- Reconexión automática si falla

---

### 1.2 StateFlow vs LiveData

**Problema**: Gestión de estado reactivo

**Opciones Consideradas**:
1. **LiveData**: Lifecycle-aware, antiguo
2. **StateFlow**: Coroutines-based, moderno
3. **MutableState**: Compose-only, limitado

**Decisión**: StateFlow

**Justificación**:
```kotlin
// StateFlow: Moderno, thread-safe, Coroutines
private val _marketplaceState = MutableStateFlow(MarketplaceState())
val marketplaceState: StateFlow<MarketplaceState> = _marketplaceState

// Ventajas:
// - Thread-safe por defecto
// - Funciona con Compose
// - Mejor performance
// - Más flexible
```

---

### 1.3 Actualización Optimista vs Pesimista

**Problema**: Experiencia de usuario al crear/comprar productos

**Opciones Consideradas**:
1. **Pesimista**: Esperar respuesta del servidor
2. **Optimista**: Actualizar UI inmediatamente
3. **Híbrida**: Mostrar loading, luego actualizar

**Decisión**: Optimista con rollback

**Justificación**:
```kotlin
// Optimista: Actualizar UI primero
val updatedProducts = _marketplaceState.value.products + newProduct
_marketplaceState.value = _marketplaceState.value.copy(
    success = true,
    products = updatedProducts
)

// Luego persistir en servidor
createProductUseCase(product).collect { result ->
    result.onSuccess { /* OK */ }
    result.onFailure { 
        // Rollback: Remover producto si falla
        _marketplaceState.value = _marketplaceState.value.copy(
            products = _marketplaceState.value.products.filter { it.id != newProduct.id }
        )
    }
}

// Ventajas:
// - UX instantánea
// - No bloquea UI
// - Rollback automático si falla
// - Mejor percepción de velocidad
```

---

### 1.4 Dos Pestañas vs Un Solo Grid

**Problema**: Diferenciación entre productos propios y de otros

**Opciones Consideradas**:
1. **Un grid**: Todos los productos con indicador
2. **Dos pestañas**: Separación clara
3. **Filtro dropdown**: Cambiar vista dinámicamente

**Decisión**: Dos pestañas

**Justificación**:
```
Aspecto              | Un Grid | Dos Pestañas | Dropdown
─────────────────────┼─────────┼──────────────┼──────────
Claridad             | Media   | Alta         | Media
Accesibilidad        | Alta    | Alta         | Media
Confusión            | Sí      | No           | Sí
Escalabilidad        | Baja    | Alta         | Media
Implementación       | Fácil   | Media        | Fácil
UX                   | Buena   | Excelente    | Buena
```

**Implementación**:
```kotlin
var selectedTab by remember { mutableStateOf(0) }

val myProducts = marketplaceState.products.filter { it.idVendedor == userId }
val allProducts = marketplaceState.products.filter { it.idVendedor != userId }

if (selectedTab == 0) {
    // Mostrar myProducts con MyProductCardItem
} else {
    // Mostrar allProducts con ProductCardItem
}
```

---

### 1.5 Persistencia: Server-side vs Local

**Problema**: Productos deben persistir aunque cierres sesión

**Opciones Consideradas**:
1. **Server-side**: Base de datos en servidor
2. **Local**: Room database en dispositivo
3. **Híbrida**: Ambas

**Decisión**: Server-side + Local (opcional)

**Justificación**:
```
Aspecto              | Server | Local | Híbrida
─────────────────────┼────────┼───────┼────────
Sincronización       | Fácil  | Difícil| Compleja
Offline              | No     | Sí    | Sí
Escalabilidad        | Alta   | Baja  | Alta
Consistencia         | Alta   | Baja  | Media
Implementación       | Fácil  | Media | Compleja
Costo                | Bajo   | Bajo  | Bajo
```

**Implementación Actual**:
- Productos guardados en servidor
- Endpoint `GET /products/all` sin autenticación
- Cargar al abrir app
- Persistencia en memoria (StateFlow)

**Mejora Futura**:
- Agregar Room para caché local
- Sincronizar con servidor
- Funcionar offline

---

### 1.6 Hilt vs Dagger vs Manual

**Problema**: Inyección de dependencias

**Opciones Consideradas**:
1. **Manual**: Sin framework
2. **Dagger**: Completo pero complejo
3. **Hilt**: Simplificado para Android

**Decisión**: Hilt

**Justificación**:
```
Aspecto              | Manual | Dagger | Hilt
─────────────────────┼────────┼────────┼──────
Boilerplate          | Alto   | Medio  | Bajo
Curva aprendizaje    | Baja   | Alta   | Media
Poder                | Bajo   | Alto   | Alto
Mantenibilidad       | Baja   | Media  | Alta
Testing              | Difícil| Fácil  | Fácil
Recomendado          | No     | Sí     | Sí*
```

*Hilt es el estándar moderno para Android

---

### 1.7 Eventos Tipados vs Strings

**Problema**: Comunicación por WebSocket

**Opciones Consideradas**:
1. **Strings**: `"product_created"`, `"product_sold"`
2. **Enums**: `enum class EventType { CREATED, SOLD }`
3. **Sealed Classes**: `sealed class Event`
4. **Data Classes**: `@Serializable data class WebSocketEvent`

**Decisión**: Data Classes Serializables

**Justificación**:
```kotlin
@Serializable
data class WebSocketEvent(
    val type: String,
    val productId: Int? = null,
    val stock: Int? = null,
    val product: ProductEventData? = null
)

// Ventajas:
// - Type-safe
// - Serializable automáticamente
// - Extensible
// - Documentación clara
// - Fácil de debuggear
```

---

### 1.8 Notificaciones: Auto-limpiar vs Manual

**Problema**: Mostrar notificaciones de eventos

**Opciones Consideradas**:
1. **Manual**: Usuario cierra notificación
2. **Auto-limpiar**: Desaparece automáticamente
3. **Snackbar**: Material Design
4. **Toast**: Sistema Android

**Decisión**: Auto-limpiar en 3 segundos

**Justificación**:
```kotlin
_marketplaceState.value = _marketplaceState.value.copy(
    notification = "Nuevo producto: ${productData.nombre}"
)
kotlinx.coroutines.delay(3000)
_marketplaceState.value = _marketplaceState.value.copy(notification = null)

// Ventajas:
// - No requiere interacción
// - Desaparece automáticamente
// - No bloquea UI
// - Mejor UX
```

---

## 2. Patrones Utilizados

### 2.1 Clean Architecture
```
Presentation → Domain ← Data
     ↓           ↑        ↓
    UI      Business   API/DB
```

**Ventajas**:
- Separación clara
- Testeable
- Escalable
- Independiente de frameworks

### 2.2 MVVM (Model-View-ViewModel)
```
View (Composable)
    ↓
ViewModel (Estado + Lógica)
    ↓
Model (Datos)
```

**Ventajas**:
- Separación UI/Lógica
- Reactive
- Testeable
- Ideal para Compose

### 2.3 Repository Pattern
```
ViewModel → Repository → DataSource
                ↓
            API/Database
```

**Ventajas**:
- Abstracción de datos
- Fácil cambiar fuente
- Testeable
- Reutilizable

### 2.4 Use Case Pattern
```
ViewModel → UseCase → Repository
```

**Ventajas**:
- Lógica de negocio centralizada
- Reutilizable
- Testeable
- Documentación clara

---

## 3. Decisiones de Seguridad

### 3.1 Autenticación
- Token JWT en header `Authorization: Bearer <token>`
- Interceptor automático en OkHttp
- Refresh token automático (futuro)

### 3.2 Datos Sensibles
- Número de teléfono en campo `email` (mapeo)
- Contraseña hasheada en servidor
- No guardar tokens en SharedPreferences (futuro: EncryptedSharedPreferences)

### 3.3 Endpoints Públicos
- `GET /products/all` sin autenticación
- Permite comprar sin sesión
- Mejor UX

---

## 4. Decisiones de Performance

### 4.1 Singleton para WebSocket
```kotlin
@Singleton
@Provides
fun provideWebSocketManager(): WebSocketManager {
    return WebSocketManager()
}
```
- Una sola conexión
- Reutilizable
- Menor consumo de recursos

### 4.2 StateFlow Thread-Safe
- No necesita sincronización manual
- Seguro para Coroutines
- Mejor performance

### 4.3 Lazy Loading (Futuro)
- Cargar productos en páginas
- Reducir memoria
- Mejor scroll performance

---

## 5. Decisiones de UX

### 5.1 Actualización Optimista
- Feedback inmediato
- Mejor percepción de velocidad
- Rollback automático

### 5.2 Notificaciones Visuales
- Informar cambios en tiempo real
- Auto-limpiar
- No intrusivas

### 5.3 Dos Pestañas
- Claridad
- Menos confusión
- Mejor organización

### 5.4 Modal para Detalles
- Información completa
- Acciones contextuales
- No navega

---

## 6. Decisiones de Escalabilidad

### 6.1 Modular por Feature
```
features/
├── login/
├── marketplace/
├── orders/ (futuro)
└── profile/ (futuro)
```
- Fácil agregar features
- Código organizado
- Reutilizable

### 6.2 Capas Independientes
- Domain sin dependencias
- Data intercambiable
- Presentation flexible

### 6.3 Inyección de Dependencias
- Fácil mockear
- Testeable
- Flexible

---

## 7. Comparación: Decisiones Tomadas vs Alternativas

| Decisión | Elegida | Alternativa | Por qué |
|----------|---------|-------------|---------|
| Tiempo Real | WebSocket | REST Polling | Latency bajo |
| Estado | StateFlow | LiveData | Moderno |
| Actualización | Optimista | Pesimista | Mejor UX |
| Pestañas | Dos | Un grid | Claridad |
| Persistencia | Server | Local | Sincronización |
| DI | Hilt | Dagger | Simplicidad |
| Eventos | Data Class | String | Type-safe |
| Notificaciones | Auto | Manual | UX |

---

## Conclusión

Las decisiones tomadas priorizan:

1. **UX**: Actualización optimista, notificaciones, pestañas claras
2. **Escalabilidad**: Modular, capas independientes, DI
3. **Mantenibilidad**: Clean Architecture, patrones claros
4. **Performance**: Singleton, StateFlow, lazy loading
5. **Seguridad**: Autenticación, endpoints públicos controlados

Estas decisiones crean una arquitectura profesional, escalable y mantenible.

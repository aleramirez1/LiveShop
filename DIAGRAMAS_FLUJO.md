# Diagramas de Flujo - LiveShop

## 1. Flujo de Creación de Producto

```
┌─────────────────────────────────────────────────────────────┐
│ Usuario presiona "+" en Marketplace                         │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ AddProductModalView abre (Paso 1: Imagen + Nombre)         │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ Usuario presiona "Siguiente"                                │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ Paso 2: Precio, Stock, Descripción, Categoría              │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ Usuario presiona "Agregar"                                  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ ViewModel.createProduct()                                   │
│ - Crear objeto Product local                               │
│ - Agregar a lista de productos (OPTIMISTA)                 │
│ - Actualizar UI inmediatamente                             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ CreateProductUseCase.invoke()                               │
│ - Llamar a ProductRepository                               │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ ProductRepositoryImpl.createProduct()                        │
│ - Llamar a API.createProduct()                             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ Backend: POST /products                                     │
│ - Guardar en base de datos                                 │
│ - Enviar evento WebSocket: product_created                 │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ WebSocket: Todos reciben evento product_created            │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ ViewModel.listenToWebSocketEvents()                         │
│ - Agregar producto a lista                                 │
│ - Mostrar notificación: "Nuevo producto: [nombre]"         │
│ - Auto-limpiar notificación en 3s                          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ UI Recompose                                                │
│ - Producto aparece en grid de todos                        │
│ - Notificación visible en la parte superior                │
└─────────────────────────────────────────────────────────────┘
```

## 2. Flujo de Compra de Producto

```
┌─────────────────────────────────────────────────────────────┐
│ Usuario ve producto en "Todos los Productos"               │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ Usuario presiona tarjeta del producto                       │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ ProductDetailModal abre                                     │
│ - isOwnProduct = false                                      │
│ - Muestra botón "Comprar por SMS"                          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ Usuario presiona "Comprar por SMS"                          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ Se abre app de SMS con mensaje preformateado:              │
│ "Hola, me interesa tu producto: [nombre]"                 │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ ViewModel.createOrder()                                     │
│ - Llamar a CreateOrderUseCase                              │
│ - Parámetros: productId, vendorId, buyerId, quantity      │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ Backend: POST /orders                                       │
│ - Crear orden en base de datos                             │
│ - Disminuir stock del producto                             │
│ - Enviar evento WebSocket: product_sold                    │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ WebSocket: Todos reciben evento product_sold               │
│ - productId, stock actualizado                             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ ViewModel.listenToWebSocketEvents()                         │
│ - Actualizar stock del producto                            │
│ - Mostrar notificación: "Producto vendido: [nombre]"       │
│ - Si stock = 0, marcar como agotado                        │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ UI Recompose                                                │
│ - Stock actualizado en todas las pantallas                 │
│ - Notificación visible                                     │
└─────────────────────────────────────────────────────────────┘
```

## 3. Flujo de Gestión de Estado

```
┌──────────────────────────────────────────────────────────────┐
│ MarketplaceState (Single Source of Truth)                   │
├──────────────────────────────────────────────────────────────┤
│ - isLoading: Boolean                                         │
│ - error: String?                                            │
│ - success: Boolean                                          │
│ - products: List<Product>                                   │
│ - notification: String?                                     │
└──────────────────────────────────────────────────────────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
   ┌────────┐  ┌────────┐  ┌────────────┐
   │ UI     │  │ WebSocket│ │ UseCase   │
   │Compose │  │ Events  │ │ Results   │
   └────────┘  └────────┘  └────────────┘
        │            │            │
        └────────────┼────────────┘
                     │
                     ▼
        ┌────────────────────────┐
        │ ViewModel.updateState()│
        │ _marketplaceState.value│
        │ = copy(...)            │
        └────────────────────────┘
                     │
                     ▼
        ┌────────────────────────┐
        │ StateFlow emite nuevo  │
        │ estado                 │
        └────────────────────────┘
                     │
                     ▼
        ┌────────────────────────┐
        │ UI recompose           │
        │ automáticamente        │
        └────────────────────────┘
```

## 4. Flujo de Pestañas

```
┌─────────────────────────────────────────────────────────────┐
│ MarketplaceScreenView                                       │
├─────────────────────────────────────────────────────────────┤
│ val userId = sessionManager.userId.value                   │
│ val myProducts = products.filter { it.idVendedor == userId }
│ val allProducts = products.filter { it.idVendedor != userId}
└─────────────────────────────────────────────────────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
        ▼                         ▼
┌──────────────────┐    ┌──────────────────┐
│ Pestaña 0        │    │ Pestaña 1        │
│ Mis Productos    │    │ Todos            │
├──────────────────┤    ├──────────────────┤
│ - myProducts     │    │ - allProducts    │
│ - Editar         │    │ - Comprar        │
│ - Eliminar       │    │ - Sin sesión OK  │
└──────────────────┘    └──────────────────┘
        │                         │
        ▼                         ▼
┌──────────────────┐    ┌──────────────────┐
│ MyProductCardItem│    │ ProductCardItem  │
│ - Botones        │    │ - Botón Comprar  │
│ - Editar/Eliminar│    │ - SMS            │
└──────────────────┘    └──────────────────┘
        │                         │
        └────────────┬────────────┘
                     │
                     ▼
        ┌────────────────────────┐
        │ ProductDetailModal     │
        │ isOwnProduct = true/false
        │ Muestra acciones       │
        │ correspondientes       │
        └────────────────────────┘
```

## 5. Flujo de WebSocket

```
┌─────────────────────────────────────────────────────────────┐
│ WebSocketManager.connect()                                  │
│ URL: wss://liveshop.myddns.me/ws                           │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ OkHttpClient.newWebSocket()                                 │
│ - Establece conexión                                        │
│ - Escucha mensajes                                          │
└────────────────────┬────────────────────────────────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
        ▼                         ▼
┌──────────────────┐    ┌──────────────────┐
│ onMessage()      │    │ onFailure()      │
│ Recibe evento    │    │ Maneja error     │
│ JSON             │    │ Reconecta        │
└──────────────────┘    └──────────────────┘
        │
        ▼
┌──────────────────────────────────────────┐
│ json.decodeFromString<WebSocketEvent>()  │
│ Parsea JSON a objeto                     │
└──────────────────┬───────────────────────┘
                   │
                   ▼
        ┌──────────────────────┐
        │ _events.emit(event)  │
        │ SharedFlow emite     │
        └──────────────────────┘
                   │
                   ▼
        ┌──────────────────────┐
        │ ViewModel escucha    │
        │ listenToWebSocket    │
        │ Events()             │
        └──────────────────────┘
                   │
                   ▼
        ┌──────────────────────┐
        │ Actualiza estado     │
        │ Muestra notificación │
        │ UI recompose         │
        └──────────────────────┘
```

## 6. Arquitectura de Capas

```
┌─────────────────────────────────────────────────────────────┐
│ PRESENTATION LAYER (UI)                                     │
├─────────────────────────────────────────────────────────────┤
│ - MarketplaceScreenView (Composable)                        │
│ - AddProductScreenView (Composable)                         │
│ - ProductDetailModal (Composable)                           │
│ - MarketplaceViewModelImpl (ViewModel)                       │
└────────────────────┬────────────────────────────────────────┘
                     │
┌─────────────────────────────────────────────────────────────┐
│ DOMAIN LAYER (Lógica de Negocio)                            │
├─────────────────────────────────────────────────────────────┤
│ - Product (Model)                                           │
│ - ProductRepository (Interface)                             │
│ - OrderRepository (Interface)                               │
│ - GetAllProductsUseCase                                     │
│ - CreateProductUseCase                                      │
│ - CreateOrderUseCase                                        │
└────────────────────┬────────────────────────────────────────┘
                     │
┌─────────────────────────────────────────────────────────────┐
│ DATA LAYER (Fuentes de Datos)                               │
├─────────────────────────────────────────────────────────────┤
│ - ProductRepositoryImpl                                      │
│ - OrderRepositoryImpl                                        │
│ - ApiClient (Retrofit)                                      │
│ - LiveShopApi (Endpoints)                                   │
│ - WebSocketManager (WebSocket)                              │
│ - NetworkConfig (URLs)                                      │
└────────────────────┬────────────────────────────────────────┘
                     │
┌─────────────────────────────────────────────────────────────┐
│ EXTERNAL SERVICES                                           │
├─────────────────────────────────────────────────────────────┤
│ - Backend API (REST)                                        │
│ - WebSocket Server                                          │
│ - Database (MySQL)                                          │
└─────────────────────────────────────────────────────────────┘
```

## 7. Inyección de Dependencias

```
┌─────────────────────────────────────────────────────────────┐
│ Hilt Modules                                                │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│ AppModule                                                   │
│ ├── SessionManager (Singleton)                              │
│ └── WebSocketManager (Singleton)                            │
│                                                              │
│ NetworkModule                                               │
│ ├── Retrofit (Singleton)                                    │
│ ├── OkHttpClient (Singleton)                                │
│ └── LiveShopApi (Singleton)                                 │
│                                                              │
│ RepositoryModule                                            │
│ ├── AuthRepository → AuthRepositoryImpl                      │
│ ├── ProductRepository → ProductRepositoryImpl                │
│ └── OrderRepository → OrderRepositoryImpl                    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
                     │
                     ▼
        ┌────────────────────────┐
        │ @HiltViewModel         │
        │ MarketplaceViewModelImpl│
        │ @Inject constructor(   │
        │   sessionManager,      │
        │   getAllProductsUseCase│
        │   createProductUseCase │
        │   createOrderUseCase   │
        │   webSocketManager     │
        │ )                      │
        └────────────────────────┘
```

Estos diagramas muestran cómo funciona la arquitectura en tiempo real y cómo se comunican los diferentes componentes.

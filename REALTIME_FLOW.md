# Flujo en Tiempo Real - LiveShop

## 🔄 Arquitectura en Tiempo Real

### 1. Conexión WebSocket
- Se establece automáticamente cuando se abre la pantalla de Marketplace
- URL: `wss://liveshop.myddns.me/ws`
- Se desconecta automáticamente cuando se cierra la pantalla

### 2. Eventos Soportados

#### `product_created`
Se dispara cuando alguien sube un nuevo producto:
```json
{
  "type": "product_created",
  "product": {
    "id": 123,
    "nombre": "Laptop",
    "precio": 1500.00,
    "stock": 5,
    "imagen": "https://...",
    "nombre_vendedor": "Juan",
    "numero_vendedor": "+52...",
    "id_vendedor": 1,
    "descripcion": "Laptop nueva",
    "categoria": "Electrónica"
  }
}
```

#### `product_sold`
Se dispara cuando se vende un producto:
```json
{
  "type": "product_sold",
  "productId": 123,
  "stock": 4,
  "vendorNumber": "+52...",
  "buyerNumber": "+52..."
}
```

### 3. Flujo de Compra en Tiempo Real

1. **Usuario A** presiona "Comprar por SMS" en un producto
2. Se abre la app de SMS
3. Se crea una orden en el backend (`POST /orders`)
4. El backend envía evento `product_sold` por WebSocket
5. **Todos los usuarios** (incluyendo Usuario A) reciben el evento
6. El stock se actualiza automáticamente en la UI
7. Se muestra notificación: "Producto vendido: [nombre]"

### 4. Flujo de Nuevo Producto en Tiempo Real

1. **Usuario B** presiona "+" y agrega un nuevo producto
2. Se crea el producto en el backend (`POST /products`)
3. El backend envía evento `product_created` por WebSocket
4. **Todos los usuarios** reciben el evento
5. El nuevo producto aparece automáticamente en el grid
6. Se muestra notificación: "Nuevo producto: [nombre]"

### 5. Notificaciones

Las notificaciones aparecen en la parte superior del Marketplace:
- Duración: 3 segundos
- Color: Primario (azul)
- Texto: Blanco

### 6. Actualización de UI

- **Productos nuevos**: Se agregan al final del grid
- **Productos vendidos**: El stock se actualiza en tiempo real
- **Stock = 0**: El producto se marca como agotado

## 📝 Implementación en Android

### WebSocketManager.kt
- Maneja la conexión WebSocket
- Emite eventos a través de SharedFlow
- Se conecta/desconecta automáticamente

### MarketplaceViewModelImpl.kt
- Escucha eventos del WebSocket
- Actualiza el estado de productos
- Muestra notificaciones

### MarketplaceScreenView.kt
- Muestra notificaciones en tiempo real
- Actualiza el grid automáticamente
- Refleja cambios de stock

## 🚀 Próximos Pasos

1. Implementar persistencia local con Room
2. Agregar historial de órdenes
3. Agregar filtros y búsqueda en tiempo real

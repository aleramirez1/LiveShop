# Configuración WebSocket

## URL del WebSocket

La URL del WebSocket está configurada en:
- **Archivo**: `app/src/main/java/com/example/liveshop_par/data/network/WebSocketManager.kt`
- **Línea**: `private val baseUrl = "wss://liveshop.myddns.me/ws"`

## Cambiar la URL

Para cambiar la URL del WebSocket, edita la línea en `WebSocketManager.kt`:

```kotlin
private val baseUrl = "wss://tu-url-aqui/ws"
```

## Flujo de Eventos

1. **Conexión**: El WebSocket se conecta automáticamente cuando se crea el ViewModel
2. **Escucha**: El ViewModel escucha eventos del WebSocket en tiempo real
3. **Actualización**: Cuando llega un evento de compra, actualiza el stock del producto
4. **Desconexión**: Se desconecta automáticamente cuando se destruye el ViewModel

## Estructura del Evento

El WebSocket envía eventos con la siguiente estructura:

```json
{
  "type": "sold",
  "productId": 123,
  "stock": 5,
  "vendorNumber": "+52...",
  "buyerNumber": "+52..."
}
```

## Implementación

- **WebSocketManager.kt**: Maneja la conexión y recepción de eventos
- **MarketplaceViewModelImpl.kt**: Escucha los eventos y actualiza el estado
- **LiveShopApi.kt**: Define los endpoints de órdenes (POST /orders, GET /orders)

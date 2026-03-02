# Configuración de URL del WebSocket

## 📍 Ubicación

La URL del WebSocket está centralizada en:

**Archivo**: `app/src/main/java/com/example/liveshop_par/core/config/NetworkConfig.kt`

```kotlin
object NetworkConfig {
    const val BASE_URL = "https://liveshop.myddns.me/"
    const val WEBSOCKET_URL = "wss://liveshop.myddns.me/ws"
}
```

## 🔧 Cómo Cambiar la URL

1. Abre el archivo `NetworkConfig.kt`
2. Edita la línea:
   ```kotlin
   const val WEBSOCKET_URL = "wss://tu-nueva-url/ws"
   ```
3. Guarda el archivo
4. Reconstruye el proyecto

## 📝 Notas

- La URL debe ser `wss://` (WebSocket Secure) para producción
- Para desarrollo local, puedes usar `ws://` (sin seguridad)
- El WebSocket se conecta automáticamente cuando se abre la pantalla de Marketplace
- Se desconecta automáticamente cuando cierras la pantalla

## 🔗 Dónde se Usa

- `WebSocketManager.kt`: Lee la URL de `NetworkConfig.WEBSOCKET_URL`
- `MarketplaceViewModelImpl.kt`: Usa `WebSocketManager` para escuchar eventos

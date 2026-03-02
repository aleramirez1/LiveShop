package com.example.liveshop_par.data.network

import android.util.Log
import com.example.liveshop_par.core.config.NetworkConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class WebSocketEvent(
    val type: String,
    val productId: Int? = null,
    val stock: Int? = null,
    val vendorNumber: String? = null,
    val buyerNumber: String? = null,
    val product: ProductEventData? = null
)

@Serializable
data class ProductEventData(
    val id: Int? = null,
    val nombre: String? = null,
    val precio: Double? = null,
    val stock: Int? = null,
    val imagen: String? = null,
    val nombre_vendedor: String? = null,
    val numero_vendedor: String? = null,
    val id_vendedor: Int? = null,
    val descripcion: String? = null,
    val categoria: String? = null
)

// regla1: websocket manager en capa de datos, maneja conexion en tiempo real
@Singleton
class WebSocketManager @Inject constructor() {
    private val baseUrl = NetworkConfig.WEBSOCKET_URL
    private var webSocket: WebSocket? = null
    private val json = Json { ignoreUnknownKeys = true }
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private val _events = MutableSharedFlow<WebSocketEvent>()
    val events: SharedFlow<WebSocketEvent> = _events.asSharedFlow()
    
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    fun connect() {
        if (webSocket != null) return
        
        try {
            val request = Request.Builder()
                .url(baseUrl)
                .build()
            
            webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                    Log.d("WebSocketManager", "Conexión abierta")
                }
                
                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.d("WebSocketManager", "Mensaje recibido: $text")
                    try {
                        val event = json.decodeFromString<WebSocketEvent>(text)
                        scope.launch {
                            _events.emit(event)
                        }
                    } catch (e: Exception) {
                        Log.e("WebSocketManager", "Error al decodificar mensaje: ${e.message}")
                    }
                }
                
                override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                    Log.e("WebSocketManager", "Error en WebSocket: ${t.message}")
                    webSocket.close(1001, "Error en conexión")
                    this@WebSocketManager.webSocket = null
                }
                
                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d("WebSocketManager", "Conexión cerrada: $reason")
                    this@WebSocketManager.webSocket = null
                }
            })
        } catch (e: Exception) {
            Log.e("WebSocketManager", "Error al conectar: ${e.message}")
        }
    }
    
    fun disconnect() {
        webSocket?.close(1000, "Desconectando")
        webSocket = null
    }
}

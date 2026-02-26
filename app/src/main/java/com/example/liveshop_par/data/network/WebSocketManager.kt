package com.example.liveshop_par.data.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketManager(
    private val wsUrl: String = "ws://localhost:8080"
) : WebSocketListener() {

    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()
    private val json = Json

    private val _productUpdates = MutableSharedFlow<ProductUpdate>()
    val productUpdates: SharedFlow<ProductUpdate> = _productUpdates.asSharedFlow()

    private val _connectionStatus = MutableSharedFlow<ConnectionStatus>()
    val connectionStatus: SharedFlow<ConnectionStatus> = _connectionStatus.asSharedFlow()

    private val _errors = MutableSharedFlow<String>()
    val errors: SharedFlow<String> = _errors.asSharedFlow()

    fun connect() {
        try {
            val request = Request.Builder()
                .url(wsUrl)
                .build()

            webSocket = client.newWebSocket(request, this)
            println("✓ Conectando a WebSocket: $wsUrl")
        } catch (e: Exception) {
            println("✗ Error al conectar: ${e.message}")
            emitError("Error de conexión: ${e.message}")
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Desconexión normal")
        webSocket = null
    }

    fun subscribeToProducts() {
        sendMessage("""{"type": "subscribe_products"}""")
    }

    fun subscribeToUser(userId: Int) {
        sendMessage("""{"type": "subscribe_user", "userId": $userId}""")
    }

    fun notifyProductCreated(product: ProductResponse) {
        val json = Json.encodeToString(ProductResponse.serializer(), product)
        sendMessage("""{"type": "product_created", "product": $json}""")
    }

    fun notifyProductUpdated(product: ProductResponse) {
        val json = Json.encodeToString(ProductResponse.serializer(), product)
        sendMessage("""{"type": "product_updated", "product": $json}""")
    }

    fun notifyProductDeleted(product: ProductResponse) {
        val json = Json.encodeToString(ProductResponse.serializer(), product)
        sendMessage("""{"type": "product_deleted", "product": $json}""")
    }

    private fun sendMessage(message: String) {
        try {
            webSocket?.send(message)
            println("📤 Mensaje enviado: $message")
        } catch (e: Exception) {
            println("✗ Error al enviar mensaje: ${e.message}")
            emitError("Error al enviar: ${e.message}")
        }
    }

    override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
        println("✓ WebSocket conectado")
        emitConnectionStatus(ConnectionStatus.CONNECTED)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        try {
            println("📨 Mensaje recibido: $text")
            val jsonElement = json.parseToJsonElement(text)
            val jsonObject = jsonElement.jsonObject

            when (val type = jsonObject["type"]?.toString()?.trim('"')) {
                "connection" -> {
                    println("✓ Conexión establecida")
                    emitConnectionStatus(ConnectionStatus.CONNECTED)
                }
                "products_list" -> {
                    val products = jsonObject["products"]?.jsonArray
                    products?.forEach { productJson ->
                        val product = json.decodeFromJsonElement(
                            ProductResponse.serializer(),
                            productJson
                        )
                        emitProductUpdate(ProductUpdate.ProductsList(listOf(product)))
                    }
                }
                "product_created" -> {
                    val product = jsonObject["product"]?.let {
                        json.decodeFromJsonElement(ProductResponse.serializer(), it)
                    }
                    product?.let {
                        emitProductUpdate(ProductUpdate.ProductCreated(it))
                    }
                }
                "product_updated" -> {
                    val product = jsonObject["product"]?.let {
                        json.decodeFromJsonElement(ProductResponse.serializer(), it)
                    }
                    product?.let {
                        emitProductUpdate(ProductUpdate.ProductUpdated(it))
                    }
                }
                "product_deleted" -> {
                    val product = jsonObject["product"]?.let {
                        json.decodeFromJsonElement(ProductResponse.serializer(), it)
                    }
                    product?.let {
                        emitProductUpdate(ProductUpdate.ProductDeleted(it))
                    }
                }
                "pong" -> {
                    println("✓ Pong recibido")
                }
                "error" -> {
                    val message = jsonObject["message"]?.toString()?.trim('"') ?: "Error desconocido"
                    emitError(message)
                }
            }
        } catch (e: Exception) {
            println("✗ Error procesando mensaje: ${e.message}")
            emitError("Error procesando mensaje: ${e.message}")
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        onMessage(webSocket, bytes.utf8())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("✗ WebSocket cerrando: $reason")
        emitConnectionStatus(ConnectionStatus.DISCONNECTING)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        println("✗ WebSocket cerrado: $reason")
        emitConnectionStatus(ConnectionStatus.DISCONNECTED)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
        println("✗ Error WebSocket: ${t.message}")
        emitError("Error WebSocket: ${t.message}")
        emitConnectionStatus(ConnectionStatus.ERROR)
    }

    private suspend fun emitProductUpdate(update: ProductUpdate) {
        try {
            _productUpdates.emit(update)
        } catch (e: Exception) {
            println("Error emitiendo actualización: ${e.message}")
        }
    }

    private suspend fun emitConnectionStatus(status: ConnectionStatus) {
        try {
            _connectionStatus.emit(status)
        } catch (e: Exception) {
            println("Error emitiendo estado: ${e.message}")
        }
    }

    private suspend fun emitError(message: String) {
        try {
            _errors.emit(message)
        } catch (e: Exception) {
            println("Error emitiendo error: ${e.message}")
        }
    }

    sealed class ProductUpdate {
        data class ProductsList(val products: List<ProductResponse>) : ProductUpdate()
        data class ProductCreated(val product: ProductResponse) : ProductUpdate()
        data class ProductUpdated(val product: ProductResponse) : ProductUpdate()
        data class ProductDeleted(val product: ProductResponse) : ProductUpdate()
    }

    enum class ConnectionStatus {
        CONNECTED,
        DISCONNECTED,
        DISCONNECTING,
        ERROR
    }
}

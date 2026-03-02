package com.example.liveshop_par.data.repository

import com.example.liveshop_par.domain.repository.NotificationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val okHttpClient: OkHttpClient
) : NotificationRepository {

    override fun observeSellerNotifications(token: String): Flow<String> = callbackFlow {
        val wsUrl = "wss://liveshop.myddns.me/ws"

        val request = Request.Builder()
            .url(wsUrl)
            .addHeader("Authorization", "Bearer $token")
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                println("WS Conectado exitosamente")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                trySend(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                println("WS Error: ${t.message}")
                close(t)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                println("WS Cerrado")
                close()
            }
        }

        val webSocket = okHttpClient.newWebSocket(request, listener)

        awaitClose {
            webSocket.cancel()
        }
    }
}
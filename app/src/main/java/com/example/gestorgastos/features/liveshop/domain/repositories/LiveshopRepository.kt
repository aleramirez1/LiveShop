package com.example.liveshop.features.liveshop.domain.repositories

import com.example.liveshop.features.liveshop.domain.entities.LiveEvent
import com.example.liveshop.features.liveshop.domain.entities.Order
import com.example.liveshop.features.liveshop.domain.entities.Product
import com.example.liveshop.features.liveshop.domain.entities.Reservation
import kotlinx.coroutines.flow.Flow

interface LiveshopRepository {
    fun getProductsFlow(): Flow<List<Product>>
    fun getLiveEventsFlow(): Flow<LiveEvent>
    fun connectWebSocket()
    fun disconnectWebSocket()

    suspend fun updateProductOptimistic(product: Product): Result<Unit>
    suspend fun deleteProductOptimistic(productId: String): Result<Unit>
    suspend fun createProductOptimistic(product: Product): Result<Unit>
    suspend fun buyProductOptimistic(productId: String): Result<Unit>

    suspend fun getOrders(): Result<List<Order>>
    suspend fun createOrder(order: Order): Result<Order>
    suspend fun updateOrder(order: Order): Result<Order>
    suspend fun deleteOrder(orderId: String): Result<Unit>

    suspend fun getReservations(): Result<List<Reservation>>
    suspend fun createReservation(reservation: Reservation): Result<Reservation>
    suspend fun deleteReservation(reservationId: String): Result<Unit>
}

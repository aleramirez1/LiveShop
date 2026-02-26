package com.example.liveshop.features.liveshop.data.repositories

import com.example.liveshop.core.network.LiveshopApi
import com.example.liveshop.features.liveshop.data.datasources.local.OrderDao
import com.example.liveshop.features.liveshop.data.datasources.local.ProductDao
import com.example.liveshop.features.liveshop.data.datasources.local.ReservationDao
import com.example.liveshop.features.liveshop.data.datasources.remote.mapper.toDomain
import com.example.liveshop.features.liveshop.data.datasources.remote.mapper.toDto
import com.example.liveshop.features.liveshop.data.datasources.remote.mapper.toEntity
import com.example.liveshop.features.liveshop.data.datasources.remote.model.LiveEventDto
import com.example.liveshop.features.liveshop.data.websocket.WebSocketManager
import com.example.liveshop.features.liveshop.domain.entities.LiveEvent
import com.example.liveshop.features.liveshop.domain.entities.Order
import com.example.liveshop.features.liveshop.domain.entities.Product
import com.example.liveshop.features.liveshop.domain.entities.Reservation
import com.example.liveshop.features.liveshop.domain.repositories.LiveshopRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class LiveshopRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val orderDao: OrderDao,
    private val reservationDao: ReservationDao,
    private val webSocketManager: WebSocketManager,
    private val liveshopApi: LiveshopApi
) : LiveshopRepository {

    override fun getProductsFlow(): Flow<List<Product>> =
        productDao.getAllProducts().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getLiveEventsFlow(): Flow<LiveEvent> {
        val eventChannel = webSocketManager.getEventChannel()
        return eventChannel.receiveAsFlow().map { dto ->
            when (dto) {
                is LiveEventDto.ProductUpdatedDto -> {
                    productDao.insertProduct(dto.product.toEntity())
                    LiveEvent.ProductUpdated(dto.product.toDomain())
                }

                is LiveEventDto.ProductDeletedDto -> {
                    productDao.deleteProductById(dto.productId)
                    LiveEvent.ProductDeleted(dto.productId)
                }

                is LiveEventDto.PriceChangedDto -> {
                    val entity = productDao.getProductById(dto.productId)
                    if (entity != null) {
                        productDao.updateProduct(entity.copy(price = dto.newPrice))
                    }
                    LiveEvent.PriceChanged(dto.productId, dto.newPrice)
                }

                is LiveEventDto.QuantityChangedDto -> {
                    val entity = productDao.getProductById(dto.productId)
                    if (entity != null) {
                        productDao.updateProduct(entity.copy(quantity = dto.newQuantity))
                    }
                    LiveEvent.QuantityChanged(dto.productId, dto.newQuantity)
                }

                is LiveEventDto.OrderCreatedDto -> {
                    orderDao.insertOrder(dto.order.toEntity())
                    LiveEvent.OrderCreated(dto.order.toDomain())
                }

                is LiveEventDto.ReservationCreatedDto -> {
                    reservationDao.insertReservation(dto.reservation.toEntity())
                    LiveEvent.ReservationCreated(dto.reservation.toDomain())
                }
            }
        }
    }

    override fun connectWebSocket() {
        if (!webSocketManager.isConnected()) {
            webSocketManager.connect("ws://10.0.2.2:8000/liveshop")
        }
    }

    override fun disconnectWebSocket() {
        webSocketManager.disconnect()
    }

    override suspend fun updateProductOptimistic(product: Product): Result<Unit> {
        return try {
            val previous = productDao.getProductById(product.id)
            productDao.updateProduct(product.toEntity())

            val updatedFromServer = liveshopApi.updateProduct(product.id, product.toDto())
            productDao.updateProduct(updatedFromServer.toEntity())

            Result.success(Unit)
        } catch (e: Exception) {
            val previous = productDao.getProductById(product.id)
            if (previous != null) {
                productDao.updateProduct(previous)
            }
            Result.failure(e)
        }
    }

    override suspend fun deleteProductOptimistic(productId: String): Result<Unit> {
        return try {
            val previous = productDao.getProductById(productId)
            if (previous != null) {
                productDao.deleteProductById(productId)
            }

            liveshopApi.deleteProduct(productId)
            Result.success(Unit)
        } catch (e: Exception) {
            val previous = productDao.getProductById(productId)
            if (previous != null) {
                productDao.insertProduct(previous)
            }
            Result.failure(e)
        }
    }

    override suspend fun createProductOptimistic(product: Product): Result<Unit> {
        return try {
            productDao.insertProduct(product.toEntity())

            val created = liveshopApi.createProduct(product.toDto())
            productDao.insertProduct(created.toEntity())

            Result.success(Unit)
        } catch (e: Exception) {
            productDao.deleteProductById(product.id)
            Result.failure(e)
        }
    }

    override suspend fun buyProductOptimistic(productId: String): Result<Unit> {
        return try {
            val previous = productDao.getProductById(productId)
            if (previous == null) {
                return Result.failure(IllegalStateException("Producto no encontrado"))
            }

            val updatedLocal = previous.copy(quantity = previous.quantity - 1)
            productDao.updateProduct(updatedLocal)

            val updatedFromServer = liveshopApi.buyProduct(productId)
            productDao.updateProduct(updatedFromServer.toEntity())

            Result.success(Unit)
        } catch (e: Exception) {
            val previous = productDao.getProductById(productId)
            if (previous != null) {
                productDao.updateProduct(previous)
            }
            Result.failure(e)
        }
    }

    override suspend fun getOrders(): Result<List<Order>> {
        return try {
            val orders = liveshopApi.getOrders()
            orders.forEach { orderDao.insertOrder(it.toEntity()) }
            Result.success(orders.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createOrder(order: Order): Result<Order> {
        return try {
            orderDao.insertOrder(order.toEntity())
            val created = liveshopApi.createOrder(order.toDto())
            orderDao.insertOrder(created.toEntity())
            Result.success(created.toDomain())
        } catch (e: Exception) {
            orderDao.deleteOrderById(order.id)
            Result.failure(e)
        }
    }

    override suspend fun updateOrder(order: Order): Result<Order> {
        return try {
            orderDao.updateOrder(order.toEntity())
            val updated = liveshopApi.updateOrder(order.id, order.toDto())
            orderDao.updateOrder(updated.toEntity())
            Result.success(updated.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteOrder(orderId: String): Result<Unit> {
        return try {
            orderDao.deleteOrderById(orderId)
            liveshopApi.deleteOrder(orderId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReservations(): Result<List<Reservation>> {
        return try {
            val reservations = liveshopApi.getReservations()
            reservations.forEach { reservationDao.insertReservation(it.toEntity()) }
            Result.success(reservations.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createReservation(reservation: Reservation): Result<Reservation> {
        return try {
            reservationDao.insertReservation(reservation.toEntity())
            val created = liveshopApi.createReservation(reservation.toDto())
            reservationDao.insertReservation(created.toEntity())
            Result.success(created.toDomain())
        } catch (e: Exception) {
            reservationDao.deleteReservationById(reservation.id)
            Result.failure(e)
        }
    }

    override suspend fun deleteReservation(reservationId: String): Result<Unit> {
        return try {
            reservationDao.deleteReservationById(reservationId)
            liveshopApi.deleteReservation(reservationId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}



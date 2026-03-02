package com.example.liveshop_par.domain.usecase

import com.example.liveshop_par.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// regla1: caso de uso en capa de dominio, independiente de implementacion
class CreateOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(
        productId: Int,
        vendorId: Int,
        buyerId: Int,
        quantity: Int,
        buyerNumber: String
    ): Flow<Result<Unit>> = flow {
        try {
            orderRepository.createOrder(productId, vendorId, buyerId, quantity, buyerNumber)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

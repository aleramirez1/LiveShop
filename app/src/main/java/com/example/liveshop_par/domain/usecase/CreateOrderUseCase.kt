package com.example.liveshop_par.domain.usecase

import com.example.liveshop_par.domain.model.Order
import com.example.liveshop_par.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
        operator fun invoke(order: Order): Flow<Result<Order>> {
            return orderRepository.createOrder(order)
        }
}

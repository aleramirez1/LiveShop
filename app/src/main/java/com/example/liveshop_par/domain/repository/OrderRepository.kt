package com.example.liveshop_par.domain.repository

import com.example.liveshop_par.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun createOrder(order: Order): Flow<Result<Order>>
}
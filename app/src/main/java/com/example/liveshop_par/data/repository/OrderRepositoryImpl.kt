package com.example.liveshop_par.data.repository

import android.content.Context
import com.example.liveshop_par.data.network.CreateOrderRequest
import com.example.liveshop_par.data.network.LiveShopApi
import com.example.liveshop_par.domain.model.Order
import com.example.liveshop_par.domain.repository.OrderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val apiService: LiveShopApi,
    @ApplicationContext private val context: Context
) : OrderRepository{

     override fun createOrder(order: Order): Flow<Result<Order>> = flow {
        try {
            val request = CreateOrderRequest(
                productoid = order.productoid,
                cantidad = order.cantidad,
            )

            val response = apiService.createOrder(request)
            if (response.isSuccessful) {
                emit(Result.success(order))
            } else {
                emit(Result.failure(Exception("Error al crear orden")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }


}
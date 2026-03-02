package com.example.liveshop_par.data.repository

import com.example.liveshop_par.data.network.ApiClient
import com.example.liveshop_par.data.network.CreateOrderRequest
import com.example.liveshop_par.domain.repository.OrderRepository
import javax.inject.Inject

// regla2: implementacion de repositorio en capa de datos, usa api client
class OrderRepositoryImpl @Inject constructor() : OrderRepository {
    override suspend fun createOrder(
        productId: Int,
        vendorId: Int,
        buyerId: Int,
        quantity: Int,
        buyerNumber: String
    ) {
        val request = CreateOrderRequest(
            id_producto = productId,
            id_vendedor = vendorId,
            id_comprador = buyerId,
            cantidad = quantity,
            numero_comprador = buyerNumber
        )
        
        val response = ApiClient.apiService.createOrder(request)
        if (!response.isSuccessful) {
            throw Exception("Error al crear orden: ${response.code()}")
        }
    }
}

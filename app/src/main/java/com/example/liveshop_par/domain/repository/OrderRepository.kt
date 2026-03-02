package com.example.liveshop_par.domain.repository

// regla1: interfaz de repositorio en capa de dominio
interface OrderRepository {
    suspend fun createOrder(
        productId: Int,
        vendorId: Int,
        buyerId: Int,
        quantity: Int,
        buyerNumber: String
    )
}

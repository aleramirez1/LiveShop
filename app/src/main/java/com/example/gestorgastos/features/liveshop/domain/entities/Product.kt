package com.example.liveshop.features.liveshop.domain.entities

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
    val description: String,
    val isAvailable: Boolean,
    val sellerId: String,
    val sellerName: String,
    val sellerPhone: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
    val reservedBy: String? = null,
    val reservedAt: Long? = null
)

data class Order(
    val id: String,
    val productId: String,
    val buyerId: String,
    val buyerName: String,
    val buyerPhone: String,
    val quantity: Int,
    val totalPrice: Double,
    val status: OrderStatus = OrderStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)

enum class OrderStatus {
    PENDING, RESERVED, PURCHASED, CANCELLED
}

data class Reservation(
    val id: String,
    val productId: String,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val quantity: Int,
    val expiresAt: Long,
    val timestamp: Long = System.currentTimeMillis()
)

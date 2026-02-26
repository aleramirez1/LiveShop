package com.example.liveshop.features.liveshop.data.datasources.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
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

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val productId: String,
    val buyerId: String,
    val buyerName: String,
    val buyerPhone: String,
    val quantity: Int,
    val totalPrice: Double,
    val status: String = "PENDING",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reservations")
data class ReservationEntity(
    @PrimaryKey
    val id: String,
    val productId: String,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val quantity: Int,
    val expiresAt: Long,
    val timestamp: Long = System.currentTimeMillis()
)

package com.example.liveshop.features.liveshop.data.datasources.remote.model

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("isAvailable")
    val isAvailable: Boolean,
    @SerializedName("sellerId")
    val sellerId: String,
    @SerializedName("sellerName")
    val sellerName: String,
    @SerializedName("sellerPhone")
    val sellerPhone: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    @SerializedName("reservedBy")
    val reservedBy: String? = null,
    @SerializedName("reservedAt")
    val reservedAt: Long? = null
)

data class OrderDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("productId")
    val productId: String,
    @SerializedName("buyerId")
    val buyerId: String,
    @SerializedName("buyerName")
    val buyerName: String,
    @SerializedName("buyerPhone")
    val buyerPhone: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("totalPrice")
    val totalPrice: Double,
    @SerializedName("status")
    val status: String = "PENDING",
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

data class ReservationDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("productId")
    val productId: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("userPhone")
    val userPhone: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("expiresAt")
    val expiresAt: Long,
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

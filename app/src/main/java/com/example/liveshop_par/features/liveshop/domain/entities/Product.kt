package com.example.liveshop_par.features.liveshop.domain.entities

data class Product(
    val id: Int = 0,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val imageUri: String,
    val availableUnits: Int,
    val sellerId: Int,
    val sellerPhone: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

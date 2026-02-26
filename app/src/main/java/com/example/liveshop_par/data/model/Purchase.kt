package com.example.liveshop_par.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class Purchase(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val buyerId: Int,
    val productId: Int,
    val quantity: Int,
    val totalPrice: Double,
    val purchaseDate: String
)

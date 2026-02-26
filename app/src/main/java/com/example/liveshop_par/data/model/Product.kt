package com.example.liveshop_par.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val imageUri: String,
    val availableUnits: Int,
    val sellerId: Int,
    val sellerName: String = "",
    val sellerPhone: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

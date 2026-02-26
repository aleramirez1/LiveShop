package com.example.liveshop_par.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: Int = 0,
    val email: String,
    val name: String,
    val password: String,
    val phone: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

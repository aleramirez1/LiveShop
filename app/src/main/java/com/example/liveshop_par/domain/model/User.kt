package com.example.liveshop_par.domain.model

// regla1: modelos de negocio en capa de dominio, independientes de la capa de datos
data class User(
    val id: Int,
    val name: String,
    val number: String,
    val token: String
)

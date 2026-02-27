package com.example.liveshop_par.domain.model

import android.net.Uri

// regla1: modelos de negocio en capa de dominio, independientes de la capa de datos
data class Product(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val imagen: Uri?,
    val nombreVendedor: String,
    val numeroVendedor: String,
    val idVendedor: Int,
    val descripcion: String = "",
    val categoria: String = ""
)

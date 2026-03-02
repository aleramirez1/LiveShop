package com.example.liveshop_par.domain.model

class Order (
    val idorder: Int,
    val vendedorid: Int,
    val vendedornombre: String,
    val vendedornumero: String,
    val productoid: Int,
    val cantidad: Int,
    val entregado: Boolean,
    val creacion: String
)
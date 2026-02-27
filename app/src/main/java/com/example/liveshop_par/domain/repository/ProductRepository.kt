package com.example.liveshop_par.domain.repository

import com.example.liveshop_par.domain.model.Product
import kotlinx.coroutines.flow.Flow

// regla1: interfaz de repositorio en capa de dominio, sin dependencias externas
interface ProductRepository {
    fun getAllProducts(): Flow<Result<List<Product>>>
    fun createProduct(product: Product): Flow<Result<Product>>
}

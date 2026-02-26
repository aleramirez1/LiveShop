package com.example.liveshop_par.features.liveshop.domain.repositories

import com.example.liveshop_par.features.liveshop.domain.entities.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun addProduct(product: Product): Result<Long>
    fun getAllProducts(): Flow<List<Product>>
    suspend fun getProductById(id: Int): Result<Product>
    fun searchProducts(query: String): Flow<List<Product>>
    fun getProductsByCategory(category: String): Flow<List<Product>>
    suspend fun updateProduct(product: Product): Result<Unit>
    suspend fun purchaseProduct(buyerId: Int, productId: Int, quantity: Int): Result<Unit>
}

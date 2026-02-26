package com.example.liveshop_par.data.repository

import com.example.liveshop_par.data.database.ProductDao
import com.example.liveshop_par.data.model.Product
import com.example.liveshop_par.data.network.LiveShopApi
import com.example.liveshop_par.features.liveshop.domain.repositories.ProductRepository

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    private val api: LiveShopApi
) : ProductRepository {

    override suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val response = api.getAllProducts()
            val products = response.map { it.toProduct() }
            productDao.insertAll(products)
            Result.success(products)
        } catch (e: Exception) {
            val cachedProducts = productDao.getAllProducts()
            if (cachedProducts.isNotEmpty()) {
                Result.success(cachedProducts)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun addProduct(
        name: String,
        description: String,
        price: Double,
        stock: Int,
        category: String,
        sellerId: Int,
        sellerName: String,
        sellerPhone: String
    ): Result<Product> {
        return try {
            val response = api.addProduct(
                name = name,
                description = description,
                price = price,
                stock = stock,
                category = category,
                sellerId = sellerId,
                sellerName = sellerName,
                sellerPhone = sellerPhone
            )
            val product = response.toProduct()
            productDao.insert(product)
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchProducts(query: String): Result<List<Product>> {
        return try {
            val response = api.searchProducts(query)
            val products = response.map { it.toProduct() }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductById(id: Int): Result<Product> {
        return try {
            val response = api.getProductById(id)
            val product = response.toProduct()
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

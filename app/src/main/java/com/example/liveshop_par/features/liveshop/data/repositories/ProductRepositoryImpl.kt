package com.example.liveshop_par.features.liveshop.data.repositories

import com.example.liveshop_par.data.database.ProductDao
import com.example.liveshop_par.data.model.Product as ProductEntity
import com.example.liveshop_par.data.network.ApiClient
import com.example.liveshop_par.data.network.ProductRequest
import com.example.liveshop_par.features.liveshop.data.mapper.ProductMapper
import com.example.liveshop_par.features.liveshop.domain.entities.Product
import com.example.liveshop_par.features.liveshop.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductRepositoryImpl(
    private val productDao: ProductDao
) : ProductRepository {

    private val api = ApiClient.apiService

    override fun getAllProducts(): Flow<List<Product>> = flow {
        try {
            val response = api.getAllProducts()
            val dataProducts = response.map { 
                ProductEntity(
                    id = it.id ?: 0,
                    name = it.name ?: "",
                    price = it.price ?: 0.0,
                    description = "",
                    category = "",
                    imageUri = it.img_url ?: "",
                    availableUnits = it.stock ?: 0,
                    sellerId = it.seller_id ?: 0,
                    sellerName = "",
                    sellerPhone = ""
                )
            }
            dataProducts.forEach { productDao.insertProduct(it) }
            emit(dataProducts.map { ProductMapper.toDomain(it) })
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun addProduct(product: Product): Result<Long> {
        return try {
            val request = ProductRequest(
                name = product.name,
                price = product.price,
                stock = product.availableUnits,
                img_url = product.imageUri
            )
            val response = api.createProduct(request)
            if (response.product != null) {
                val dataProduct = ProductEntity(
                    id = response.product.id ?: 0,
                    name = response.product.name ?: "",
                    price = response.product.price ?: 0.0,
                    description = "",
                    category = "",
                    imageUri = response.product.img_url ?: "",
                    availableUnits = response.product.stock ?: 0,
                    sellerId = response.product.seller_id ?: 0,
                    sellerName = "",
                    sellerPhone = ""
                )
                val id = productDao.insertProduct(dataProduct)
                Result.success(id)
            } else {
                Result.failure(Exception(response.error ?: response.message ?: "Error al crear producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun searchProducts(query: String): Flow<List<Product>> = flow {
        try {
            val allProducts = api.getAllProducts()
            val filtered = allProducts.filter { 
                it.name?.contains(query, ignoreCase = true) == true
            }
            emit(filtered.map { 
                ProductMapper.toDomain(
                    ProductEntity(
                        id = it.id ?: 0,
                        name = it.name ?: "",
                        price = it.price ?: 0.0,
                        description = "",
                        category = "",
                        imageUri = it.img_url ?: "",
                        availableUnits = it.stock ?: 0,
                        sellerId = it.seller_id ?: 0,
                        sellerName = "",
                        sellerPhone = ""
                    )
                )
            })
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun getProductById(id: Int): Result<Product> {
        return try {
            val response = api.getProductById(id)
            val dataProduct = ProductEntity(
                id = response.id ?: 0,
                name = response.name ?: "",
                price = response.price ?: 0.0,
                description = "",
                category = "",
                imageUri = response.img_url ?: "",
                availableUnits = response.stock ?: 0,
                sellerId = response.seller_id ?: 0,
                sellerName = "",
                sellerPhone = ""
            )
            Result.success(ProductMapper.toDomain(dataProduct))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getProductsByCategory(category: String): Flow<List<Product>> = flow {
        try {
            val allProducts = api.getAllProducts()
            emit(allProducts.map { 
                ProductMapper.toDomain(
                    ProductEntity(
                        id = it.id ?: 0,
                        name = it.name ?: "",
                        price = it.price ?: 0.0,
                        description = "",
                        category = "",
                        imageUri = it.img_url ?: "",
                        availableUnits = it.stock ?: 0,
                        sellerId = it.seller_id ?: 0,
                        sellerName = "",
                        sellerPhone = ""
                    )
                )
            })
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun purchaseProduct(buyerId: Int, productId: Int, quantity: Int): Result<Unit> {
        return try {
            val request = com.example.liveshop_par.data.network.OrderRequest(
                product_id = productId,
                quantity = quantity
            )
            api.createOrder(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

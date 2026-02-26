package com.example.liveshop_par.data.repository

import com.example.liveshop_par.data.database.ProductDao
import com.example.liveshop_par.data.model.Product
import com.example.liveshop_par.data.network.ApiClient
import com.example.liveshop_par.data.network.ProductRequest
import com.example.liveshop_par.data.network.ProductResponse
import com.example.liveshop_par.features.liveshop.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.liveshop_par.features.liveshop.domain.entities.Product as DomainProduct

class ProductRepositoryImpl(
    private val productDao: ProductDao
) : ProductRepository {

    private val api = ApiClient.apiService

    override fun getAllProducts(): Flow<List<DomainProduct>> = flow {
        try {
            val response = api.getAllProducts()
            val dataProducts = response.map { mapToDataProduct(it) }
            dataProducts.forEach { productDao.insertProduct(it) }
            emit(dataProducts.map { mapToDomainProduct(it) })
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun addProduct(product: DomainProduct): Result<Long> {
        return try {
            val request = ProductRequest(
                name = product.name,
                price = product.price,
                stock = product.availableUnits,
                img_url = product.imageUri
            )
            val response = api.createProduct(request)
            if (response.product != null) {
                val dataProduct = mapToDataProduct(response.product)
                val id = productDao.insertProduct(dataProduct)
                Result.success(id)
            } else {
                Result.failure(Exception(response.error ?: response.message ?: "Error al crear producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun searchProducts(query: String): Flow<List<DomainProduct>> = flow {
        try {
            val allProducts = api.getAllProducts()
            val filtered = allProducts.filter { 
                it.name?.contains(query, ignoreCase = true) == true
            }
            emit(filtered.map { mapToDomainProduct(mapToDataProduct(it)) })
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun getProductById(id: Int): Result<DomainProduct> {
        return try {
            val response = api.getProductById(id)
            Result.success(mapToDomainProduct(mapToDataProduct(response)))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getProductsByCategory(category: String): Flow<List<DomainProduct>> = flow {
        try {
            val allProducts = api.getAllProducts()
            emit(allProducts.map { mapToDomainProduct(mapToDataProduct(it)) })
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun updateProduct(product: DomainProduct): Result<Unit> {
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
            val response = api.createOrder(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapToDataProduct(productResponse: ProductResponse): Product {
        return Product(
            id = productResponse.id ?: 0,
            name = productResponse.name ?: "",
            price = productResponse.price ?: 0.0,
            description = "",
            category = "",
            imageUri = productResponse.img_url ?: "",
            availableUnits = productResponse.stock ?: 0,
            sellerId = productResponse.seller_id ?: 0,
            sellerName = "",
            sellerPhone = ""
        )
    }

    private fun mapToDomainProduct(dataProduct: Product): DomainProduct {
        return DomainProduct(
            id = dataProduct.id,
            name = dataProduct.name,
            price = dataProduct.price,
            description = dataProduct.description,
            category = dataProduct.category,
            imageUri = dataProduct.imageUri,
            availableUnits = dataProduct.availableUnits,
            sellerId = dataProduct.sellerId,
            sellerName = dataProduct.sellerName,
            sellerPhone = dataProduct.sellerPhone,
            createdAt = dataProduct.createdAt
        )
    }
}

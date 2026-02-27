package com.example.liveshop_par.data.repository

import android.content.Context
import android.net.Uri
import com.example.liveshop_par.data.network.LiveShopApi
import com.example.liveshop_par.data.network.CreateProductRequest
import com.example.liveshop_par.domain.model.Product
import com.example.liveshop_par.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Base64
import javax.inject.Inject

// regla1: implementacion de repositorio en capa de datos, inyectada con hilt
class ProductRepositoryImpl @Inject constructor(
    private val apiService: LiveShopApi,
    private val context: Context
) : ProductRepository {
    
    override fun getAllProducts(): Flow<Result<List<Product>>> = flow {
        try {
            val response = apiService.getAllProducts()
            if (response.isSuccessful) {
                val products = response.body()?.map { productData ->
                    Product(
                        id = productData.id ?: 0,
                        nombre = productData.nombre ?: "",
                        precio = productData.precio ?: 0.0,
                        stock = productData.stock ?: 0,
                        imagen = if (productData.imagen?.isNotEmpty() == true) {
                            Uri.parse("data:image/jpeg;base64,${productData.imagen}")
                        } else null,
                        nombreVendedor = productData.nombre_vendedor ?: "",
                        numeroVendedor = productData.numero_vendedor ?: "",
                        idVendedor = productData.id_vendedor ?: 0,
                        descripcion = "",
                        categoria = ""
                    )
                } ?: emptyList()
                emit(Result.success(products))
            } else {
                emit(Result.failure(Exception("Error al cargar productos")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun createProduct(product: Product): Flow<Result<Product>> = flow {
        try {
            var imagenBase64 = ""
            if (product.imagen != null) {
                try {
                    val inputStream = context.contentResolver.openInputStream(product.imagen)
                    val bytes = inputStream?.readBytes() ?: byteArrayOf()
                    imagenBase64 = Base64.getEncoder().encodeToString(bytes)
                } catch (e: Exception) {
                    emit(Result.failure(e))
                    return@flow
                }
            }
            
            val request = CreateProductRequest(
                nombre = product.nombre,
                precio = product.precio,
                stock = product.stock,
                imagen = imagenBase64,
                nombre_vendedor = product.nombreVendedor,
                numero_vendedor = product.numeroVendedor
            )
            
            val response = apiService.createProduct(request)
            if (response.isSuccessful) {
                emit(Result.success(product))
            } else {
                emit(Result.failure(Exception("Error al crear producto")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

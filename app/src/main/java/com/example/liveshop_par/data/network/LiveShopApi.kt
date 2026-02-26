package com.example.liveshop_par.data.network

import kotlinx.serialization.Serializable
import retrofit2.http.*

@Serializable
data class RegisterRequest(
    val email: String,
    val nombre: String,
    val password: String,
    val telefono: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class UserResponse(
    val id: Int,
    val nombre: String,
    val email: String,
    val telefono: String,
    val created_at: String? = null
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val user: T? = null,
    val product: T? = null,
    val products: List<T>? = null
)

@Serializable
data class ProductRequest(
    val id_vendedor: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val categoria: String,
    val imagen_url: String,
    val telefono_vendedor: String
)

@Serializable
data class ProductResponse(
    val id: Int,
    val id_vendedor: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val categoria: String,
    val imagen_url: String,
    val disponible: Boolean,
    val nombre_vendedor: String,
    val telefono_vendedor: String,
    val created_at: String? = null
)

interface LiveShopApi {
    @POST("users")
    suspend fun registerUser(@Body request: RegisterRequest): ApiResponse<UserResponse>
    
    @POST("users/login")
    suspend fun loginUser(@Body request: LoginRequest): ApiResponse<UserResponse>
    
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): ApiResponse<UserResponse>
    
    @POST("products")
    suspend fun createProduct(@Body request: ProductRequest): ApiResponse<ProductResponse>
    
    @GET("products")
    suspend fun getAllProducts(): ApiResponse<List<ProductResponse>>
    
    @GET("products/{id}")
    suspend fun getProduct(@Path("id") productId: Int): ApiResponse<ProductResponse>
    
    @GET("products/seller/{id}")
    suspend fun getSellerProducts(@Path("id") sellerId: Int): ApiResponse<List<ProductResponse>>
    
    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): ApiResponse<List<ProductResponse>>
    
    @GET("products/category/{categoria}")
    suspend fun getProductsByCategory(@Path("categoria") categoria: String): ApiResponse<List<ProductResponse>>
    
    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") productId: Int,
        @Body request: ProductRequest
    ): ApiResponse<ProductResponse>
    
    @DELETE("products/{id}")
    suspend fun deleteProduct(
        @Path("id") productId: Int,
        @Body request: Map<String, Int>
    ): ApiResponse<Unit>
}

package com.example.liveshop_par.data.network

import kotlinx.serialization.Serializable
import retrofit2.http.*

@Serializable
data class LoginRequest(
    val name: String? = null,
    val number: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val expires_at: String? = null,
    val error: String? = null
)

@Serializable
data class CreateUserRequest(
    val name: String,
    val number: String,
    val password: String
)

@Serializable
data class UpdateUserRequest(
    val name: String,
    val number: String
)

@Serializable
data class UserResponse(
    val id: Int? = null,
    val name: String? = null,
    val number: String? = null
)

@Serializable
data class CreateUserResponse(
    val message: String? = null,
    val error: String? = null,
    val user: UserResponse? = null
)

@Serializable
data class CreateProductResponse(
    val message: String? = null,
    val error: String? = null,
    val product: ProductResponse? = null
)

@Serializable
data class UpdatePasswordRequest(
    val password: String
)

@Serializable
data class ProductRequest(
    val name: String,
    val price: Double,
    val stock: Int,
    val img_url: String? = null
)

@Serializable
data class ProductResponse(
    val id: Int? = null,
    val name: String? = null,
    val price: Double? = null,
    val stock: Int? = null,
    val seller_id: Int? = null,
    val img_url: String? = null
)

@Serializable
data class OrderRequest(
    val product_id: Int,
    val quantity: Int
)

@Serializable
data class OrderResponse(
    val id: Int? = null,
    val buyer_id: Int? = null,
    val product_id: Int? = null,
    val quantity: Int? = null,
    val total_price: Double? = null
)

@Serializable
data class ApiResponse<T>(
    val message: String? = null,
    val error: String? = null,
    val data: T? = null,
    val user: T? = null,
    val product: T? = null,
    val products: List<T>? = null
)

interface LiveShopApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): CreateUserResponse

    @GET("users")
    suspend fun getAllUsers(): List<UserResponse>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") userId: Int): UserResponse

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body request: UpdateUserRequest
    ): UserResponse

    @PUT("users/password/{id}")
    suspend fun updatePassword(
        @Path("id") userId: Int,
        @Body request: UpdatePasswordRequest
    ): UserResponse

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") userId: Int): ApiResponse<Unit>

    @POST("products")
    suspend fun createProduct(@Body request: ProductRequest): CreateProductResponse

    @GET("products")
    suspend fun getAllProducts(): List<ProductResponse>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId: Int): ProductResponse

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") productId: Int,
        @Body request: ProductRequest
    ): ProductResponse

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") productId: Int): ApiResponse<Unit>

    @POST("orders")
    suspend fun createOrder(@Body request: OrderRequest): OrderResponse

    @GET("orders")
    suspend fun getAllOrders(): List<OrderResponse>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") orderId: Int): OrderResponse

    @PUT("orders/{id}")
    suspend fun updateOrder(
        @Path("id") orderId: Int,
        @Body request: OrderRequest
    ): OrderResponse

    @DELETE("orders/{id}")
    suspend fun deleteOrder(@Path("id") orderId: Int): ApiResponse<Unit>
}

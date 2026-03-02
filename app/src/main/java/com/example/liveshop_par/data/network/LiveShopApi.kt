package com.example.liveshop_par.data.network

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.*

@Serializable
data class LoginRequest(
    val number: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val id: Int? = null,
    val nombre: String? = null,
    val email: String? = null
)

@Serializable
data class CreateUserRequest(
    val name: String,
    val number: String,
    val password: String
)

@Serializable
data class UserResponse(
    val id: Int? = null,
    val name: String? = null,
    val number: String? = null,
    val password: String? = null
)

@Serializable
data class CreateProductRequest(
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val imagen: String = "",
    val nombre_vendedor: String = "",
    val numero_vendedor: String = ""
)

@Serializable
data class ProductResponse(
    val message: String? = null,
    val product: ProductData? = null
)

@Serializable
data class ProductData(
    val id: Int? = null,
    val nombre: String? = null,
    val precio: Double? = null,
    val stock: Int? = null,
    val imagen: String? = null,
    val nombre_vendedor: String? = null,
    val numero_vendedor: String? = null,
    val id_vendedor: Int? = null
)

@Serializable
data class CreateOrderRequest(
    val id_producto: Int,
    val id_vendedor: Int,
    val id_comprador: Int,
    val cantidad: Int,
    val numero_comprador: String
)

@Serializable
data class OrderResponse(
    val id: Int? = null,
    val id_producto: Int? = null,
    val id_vendedor: Int? = null,
    val id_comprador: Int? = null,
    val cantidad: Int? = null,
    val entregado: Boolean? = null
)

interface LiveShopApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): UserResponse

    @POST("products")
    suspend fun createProduct(@Body request: CreateProductRequest): Response<ProductResponse>
    
    @GET("products/all")
    suspend fun getAllProducts(): Response<List<ProductData>>
    
    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<OrderResponse>
    
    @GET("orders")
    suspend fun getAllOrders(): Response<List<OrderResponse>>
}

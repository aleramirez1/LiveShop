package com.example.liveshop_par.data.network

import kotlinx.serialization.SerialName
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
    @SerialName("name")
    val nombre: String,
    @SerialName("price")
    val precio: Double,
    @SerialName("stock")
    val stock: Int,
    @SerialName("description")
    val descripcion: String,
    @SerialName("img_url")
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
    @SerialName("id_product")
    val id: Int? = null,

    @SerialName("name")
    val nombre: String? = null,

    @SerialName("price")
    val precio: Double? = null,
    @SerialName("stock")
    val stock: Int? = null,
    @SerialName("description")
    val descripcion: String,
    @SerialName("img_url")
    val imagen: String? = null,
    val nombre_vendedor: String? = null,
    val numero_vendedor: String? = null,
    @SerialName("seller_id")
    val id_vendedor: Int? = null
)

interface LiveShopApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): UserResponse

    @POST("products")
    suspend fun createProduct(@Body request: CreateProductRequest): Response<ProductResponse>
    
    @GET("products/public")
    suspend fun getAllProducts(): Response<List<ProductData>>

    @GET("products")
    suspend fun getAllProductsByUser(): Response<List<ProductData>>
}

package com.example.liveshop_par.data.network

import kotlinx.serialization.Serializable
import retrofit2.http.*

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val id: Int? = null,
    val nombre: String? = null,
    val email: String? = null,
    val token: String? = null
)

@Serializable
data class CreateUserRequest(
    val nombre: String,
    val email: String,
    val password: String
)

@Serializable
data class UserResponse(
    val id: Int? = null,
    val nombre: String? = null,
    val email: String? = null
)

interface LiveShopApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): UserResponse
}

package com.example.liveshop.features.auth.domain.repositories

interface AuthRepository {
    fun isRegistered(): Boolean
    fun isLoggedIn(): Boolean
    suspend fun login(username: String, password: String): Result<Unit>
    suspend fun register(username: String, password: String): Result<Unit>
    fun logout()
}


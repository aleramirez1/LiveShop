package com.example.liveshop_par.features.login.domain.repositories

import com.example.liveshop_par.data.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
}

package com.example.liveshop_par.features.register.domain.repositories

import com.example.liveshop_par.data.model.User

interface RegisterRepository {
    suspend fun register(email: String, name: String, password: String, phone: String = ""): Result<User>
}

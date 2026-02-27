package com.example.liveshop_par.domain.repository

import com.example.liveshop_par.domain.model.User
import kotlinx.coroutines.flow.Flow

// regla1: interfaz de repositorio en capa de dominio, sin dependencias externas
interface AuthRepository {
    fun login(number: String, password: String): Flow<Result<User>>
    fun register(name: String, number: String, password: String): Flow<Result<User>>
}

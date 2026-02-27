package com.example.liveshop_par.data.repository

import com.example.liveshop_par.data.network.LiveShopApi
import com.example.liveshop_par.data.network.LoginRequest
import com.example.liveshop_par.data.network.CreateUserRequest
import com.example.liveshop_par.domain.model.User
import com.example.liveshop_par.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// regla1: implementacion de repositorio en capa de datos, inyectada con hilt
class AuthRepositoryImpl @Inject constructor(
    private val apiService: LiveShopApi
) : AuthRepository {
    
    override fun login(number: String, password: String): Flow<Result<User>> = flow {
        try {
            val response = apiService.login(LoginRequest(number, password))
            if (response.isSuccessful) {
                val body = response.body()
                val token = response.headers()["Authorization"] ?: ""
                
                if (token.isNotEmpty() && body != null) {
                    val user = User(
                        id = body.id ?: 0,
                        name = body.nombre ?: "",
                        number = number,
                        token = token
                    )
                    emit(Result.success(user))
                } else {
                    emit(Result.failure(Exception("Token no encontrado")))
                }
            } else {
                emit(Result.failure(Exception("Credenciales incorrectas")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun register(name: String, number: String, password: String): Flow<Result<User>> = flow {
        try {
            val response = apiService.createUser(CreateUserRequest(name, number, password))
            val user = User(
                id = response.id ?: 0,
                name = name,
                number = number,
                token = ""
            )
            emit(Result.success(user))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

package com.example.liveshop_par.data.repository

import com.example.liveshop_par.data.database.UserDao
import com.example.liveshop_par.data.model.User
import com.example.liveshop_par.data.network.ApiClient
import com.example.liveshop_par.data.network.CreateUserRequest
import com.example.liveshop_par.features.register.domain.repositories.RegisterRepository

class RegisterRepositoryImpl(
    private val userDao: UserDao
) : RegisterRepository {

    private val api = ApiClient.apiService

    override suspend fun register(
        email: String,
        name: String,
        password: String,
        phone: String
    ): Result<User> {
        return try {
            val request = CreateUserRequest(
                name = name,
                number = phone,
                password = password
            )
            
            val response = api.createUser(request)
            
            if (response.user != null) {
                val userResponse = response.user
                val user = User(
                    id = userResponse.id ?: 1,
                    email = email,
                    name = userResponse.name ?: name,
                    password = password,
                    phone = userResponse.number ?: phone
                )
                userDao.insertUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception(response.error ?: response.message ?: "Error en registro"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

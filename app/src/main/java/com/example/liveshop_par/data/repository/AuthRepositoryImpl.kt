package com.example.liveshop_par.data.repository

import com.example.liveshop_par.data.database.UserDao
import com.example.liveshop_par.data.model.User
import com.example.liveshop_par.data.network.ApiClient
import com.example.liveshop_par.data.network.LoginRequest
import com.example.liveshop_par.features.login.domain.repositories.AuthRepository

class AuthRepositoryImpl(
    private val userDao: UserDao
) : AuthRepository {

    private val api = ApiClient.apiService

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val request = LoginRequest(
                number = email,
                password = password
            )
            
            val response = api.login(request)
            
            val user = User(
                id = 1,
                email = email,
                name = email,
                password = password,
                phone = email
            )
            userDao.insertUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

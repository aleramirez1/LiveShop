package com.example.liveshop_par.data.repository

import com.example.liveshop_par.data.database.UserDao
import com.example.liveshop_par.data.model.User
import com.example.liveshop_par.features.login.domain.repositories.AuthRepository

class AuthRepositoryImpl(
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByEmail(email)
            if (user != null && user.password == password) {
                Result.success(user)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
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

package com.example.liveshop_par.data.repository

import com.example.liveshop_par.data.database.UserDao
import com.example.liveshop_par.data.model.User
import com.example.liveshop_par.features.register.domain.repositories.RegisterRepository

class RegisterRepositoryImpl(
    private val userDao: UserDao
) : RegisterRepository {

    override suspend fun register(
        email: String,
        name: String,
        password: String,
        phone: String
    ): Result<User> {
        return try {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                return Result.failure(Exception("El email ya está registrado"))
            }

            val user = User(
                id = 0,
                email = email,
                name = name,
                password = password,
                phone = phone
            )

            val userId = userDao.insert(user).toInt()
            val newUser = user.copy(id = userId)
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

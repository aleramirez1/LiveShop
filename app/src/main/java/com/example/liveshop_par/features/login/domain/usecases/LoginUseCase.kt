package com.example.liveshop_par.features.login.domain.usecases

import com.example.liveshop_par.data.model.User
import com.example.liveshop_par.features.login.domain.repositories.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.login(email, password)
    }
}

package com.example.liveshop.features.auth.domain.usecases

import com.example.liveshop.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<Unit> =
        repository.register(username, password)
}


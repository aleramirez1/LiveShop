package com.example.liveshop.features.auth.domain.usecases

import com.example.liveshop.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class IsLoggedInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Boolean = repository.isLoggedIn()
}


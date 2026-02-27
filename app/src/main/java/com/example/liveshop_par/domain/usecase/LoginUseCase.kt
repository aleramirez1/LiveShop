package com.example.liveshop_par.domain.usecase

import com.example.liveshop_par.domain.model.User
import com.example.liveshop_par.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// regla2: casos de uso encapsulan la logica de negocio, inyectados con hilt
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(number: String, password: String): Flow<Result<User>> {
        return authRepository.login(number, password)
    }
}

package com.example.liveshop_par.domain.usecase

import com.example.liveshop_par.domain.model.User
import com.example.liveshop_par.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// regla2: casos de uso encapsulan la logica de negocio, inyectados con hilt
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(name: String, number: String, password: String): Flow<Result<User>> {
        return authRepository.register(name, number, password)
    }
}

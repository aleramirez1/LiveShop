package com.example.liveshop_par.features.register.domain.usecases

import com.example.liveshop_par.data.model.User
import com.example.liveshop_par.features.register.domain.repositories.RegisterRepository

class RegisterUseCase(
    private val registerRepository: RegisterRepository
) {
    suspend operator fun invoke(email: String, name: String, password: String, phone: String = ""): Result<User> {
        return registerRepository.register(email, name, password, phone)
    }
}

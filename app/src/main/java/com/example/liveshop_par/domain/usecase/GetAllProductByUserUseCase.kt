package com.example.liveshop_par.domain.usecase

import com.example.liveshop_par.domain.model.Product
import com.example.liveshop_par.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllProductByUserUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(): Flow<Result<List<Product>>> {
        return productRepository.getAllProductsByUser()
    }
}

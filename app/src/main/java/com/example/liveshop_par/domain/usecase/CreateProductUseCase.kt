package com.example.liveshop_par.domain.usecase

import com.example.liveshop_par.domain.model.Product
import com.example.liveshop_par.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// regla2: casos de uso encapsulan la logica de negocio, inyectados con hilt
class CreateProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(product: Product): Flow<Result<Product>> {
        return productRepository.createProduct(product)
    }
}

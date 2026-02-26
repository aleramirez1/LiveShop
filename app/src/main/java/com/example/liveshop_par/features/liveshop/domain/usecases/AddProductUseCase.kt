package com.example.liveshop_par.features.liveshop.domain.usecases

import com.example.liveshop_par.features.liveshop.domain.entities.Product
import com.example.liveshop_par.features.liveshop.domain.repositories.ProductRepository

class AddProductUseCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(product: Product): Result<Long> {
        return productRepository.addProduct(product)
    }
}

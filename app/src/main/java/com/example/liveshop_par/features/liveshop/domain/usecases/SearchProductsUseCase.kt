package com.example.liveshop_par.features.liveshop.domain.usecases

import com.example.liveshop_par.features.liveshop.domain.entities.Product
import com.example.liveshop_par.features.liveshop.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow

class SearchProductsUseCase(
    private val productRepository: ProductRepository
) {
    operator fun invoke(query: String): Flow<List<Product>> {
        return productRepository.searchProducts(query)
    }
}

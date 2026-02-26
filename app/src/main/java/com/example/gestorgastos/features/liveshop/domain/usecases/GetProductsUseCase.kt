package com.example.liveshop.features.liveshop.domain.usecases

import com.example.liveshop.features.liveshop.domain.entities.Product
import com.example.liveshop.features.liveshop.domain.repositories.LiveshopRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: LiveshopRepository
) {
    operator fun invoke(): Flow<List<Product>> = repository.getProductsFlow()
}

package com.example.liveshop.features.liveshop.domain.usecases

import com.example.liveshop.features.liveshop.domain.entities.Product
import com.example.liveshop.features.liveshop.domain.repositories.LiveshopRepository
import javax.inject.Inject

class UpdateProductUseCase @Inject constructor(
    private val repository: LiveshopRepository
) {
    suspend operator fun invoke(product: Product): Result<Unit> =
        repository.updateProductOptimistic(product)
}

package com.example.liveshop.features.liveshop.domain.usecases

import com.example.liveshop.features.liveshop.domain.repositories.LiveshopRepository
import javax.inject.Inject

class BuyProductUseCase @Inject constructor(
    private val repository: LiveshopRepository
) {
    suspend operator fun invoke(productId: String): Result<Unit> =
        repository.buyProductOptimistic(productId)
}

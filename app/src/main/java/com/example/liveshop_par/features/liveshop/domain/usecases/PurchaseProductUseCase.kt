package com.example.liveshop_par.features.liveshop.domain.usecases

import com.example.liveshop_par.features.liveshop.domain.repositories.ProductRepository

class PurchaseProductUseCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        buyerId: Int,
        productId: Int,
        quantity: Int
    ): Result<Unit> {
        return productRepository.purchaseProduct(buyerId, productId, quantity)
    }
}

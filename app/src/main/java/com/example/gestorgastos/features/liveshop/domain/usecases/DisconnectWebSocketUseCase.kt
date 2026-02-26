package com.example.liveshop.features.liveshop.domain.usecases

import com.example.liveshop.features.liveshop.domain.repositories.LiveshopRepository
import javax.inject.Inject

class DisconnectWebSocketUseCase @Inject constructor(
    private val repository: LiveshopRepository
) {
    operator fun invoke() {
        repository.disconnectWebSocket()
    }
}

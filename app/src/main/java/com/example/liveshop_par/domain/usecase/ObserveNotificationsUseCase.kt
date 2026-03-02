package com.example.liveshop_par.domain.usecase

import com.example.liveshop_par.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(token: String): Flow<String> {
        return repository.observeSellerNotifications(token)
    }
}
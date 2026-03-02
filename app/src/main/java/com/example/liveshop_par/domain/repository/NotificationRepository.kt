package com.example.liveshop_par.domain.repository

import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun observeSellerNotifications(token: String): Flow<String>
}
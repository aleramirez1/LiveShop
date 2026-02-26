package com.example.liveshop.features.liveshop.domain.usecases

import com.example.liveshop.features.liveshop.domain.entities.LiveEvent
import com.example.liveshop.features.liveshop.domain.repositories.LiveshopRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLiveEventsUseCase @Inject constructor(
    private val repository: LiveshopRepository
) {
    operator fun invoke(): Flow<LiveEvent> = repository.getLiveEventsFlow()
}

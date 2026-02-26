package com.example.liveshop.features.liveshop.di

import com.example.liveshop.features.liveshop.data.repositories.LiveshopRepositoryImpl
import com.example.liveshop.features.liveshop.domain.repositories.LiveshopRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLiveshopRepository(
        impl: LiveshopRepositoryImpl
    ): LiveshopRepository
}

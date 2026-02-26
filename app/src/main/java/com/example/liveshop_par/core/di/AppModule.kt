package com.example.liveshop_par.core.di

import com.example.liveshop_par.data.network.ApiClient
import com.example.liveshop_par.data.network.LiveShopApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApiClient(): LiveShopApi {
        return ApiClient.apiService
    }

    @Singleton
    @Provides
    fun provideSessionManager(): SessionManager {
        return SessionManager()
    }
}

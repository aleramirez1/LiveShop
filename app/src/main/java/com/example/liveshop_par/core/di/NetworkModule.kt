package com.example.liveshop_par.core.di

import com.example.liveshop_par.data.network.ApiClient
import com.example.liveshop_par.data.network.LiveShopApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// regla2: modulo de hilt para provisionar instancias de red, @provides para crear instancias
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Singleton
    @Provides
    fun provideApiClient(sessionManager: SessionManager): LiveShopApi {
        ApiClient.setSessionManager(sessionManager)
        return ApiClient.apiService
    }
}

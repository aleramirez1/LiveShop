package com.example.liveshop_par.core.di

import com.example.liveshop_par.data.network.WebSocketManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// regla2: modulo de hilt para provisionar instancias globales de la aplicacion
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideSessionManager(): SessionManager {
        return SessionManager()
    }
    
    @Singleton
    @Provides
    fun provideWebSocketManager(): WebSocketManager {
        return WebSocketManager()
    }
}

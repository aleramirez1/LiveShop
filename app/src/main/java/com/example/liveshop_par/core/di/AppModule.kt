package com.example.liveshop_par.core.di

import android.content.Context
import androidx.room.Room
import com.example.liveshop_par.data.database.AppDatabase
import com.example.liveshop_par.data.network.ApiClient
import com.example.liveshop_par.data.network.LiveShopApi
import com.example.liveshop_par.data.network.WebSocketManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "liveshop_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideApiClient(): LiveShopApi {
        return ApiClient.apiService
    }

    @Singleton
    @Provides
    fun provideWebSocketManager(): WebSocketManager {
        return WebSocketManager()
    }

    @Singleton
    @Provides
    fun provideSessionManager(): SessionManager {
        return SessionManager()
    }
}

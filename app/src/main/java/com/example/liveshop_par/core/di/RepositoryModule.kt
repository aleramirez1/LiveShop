package com.example.liveshop_par.core.di

import com.example.liveshop_par.data.database.AppDatabase
import com.example.liveshop_par.data.network.LiveShopApi
import com.example.liveshop_par.data.repository.AuthRepositoryImpl
import com.example.liveshop_par.data.repository.ProductRepositoryImpl
import com.example.liveshop_par.data.repository.RegisterRepositoryImpl
import com.example.liveshop_par.features.liveshop.domain.repositories.ProductRepository
import com.example.liveshop_par.features.login.domain.repositories.AuthRepository
import com.example.liveshop_par.features.register.domain.repositories.RegisterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideAuthRepository(
        database: AppDatabase
    ): AuthRepository {
        return AuthRepositoryImpl(database.userDao())
    }

    @Singleton
    @Provides
    fun provideRegisterRepository(
        database: AppDatabase
    ): RegisterRepository {
        return RegisterRepositoryImpl(database.userDao())
    }

    @Singleton
    @Provides
    fun provideProductRepository(
        database: AppDatabase,
        api: LiveShopApi
    ): ProductRepository {
        return ProductRepositoryImpl(database.productDao(), api)
    }
}

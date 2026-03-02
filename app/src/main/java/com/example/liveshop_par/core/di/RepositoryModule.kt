package com.example.liveshop_par.core.di

import com.example.liveshop_par.data.repository.AuthRepositoryImpl
import com.example.liveshop_par.data.repository.ProductRepositoryImpl
import com.example.liveshop_par.data.repository.OrderRepositoryImpl
import com.example.liveshop_par.domain.repository.AuthRepository
import com.example.liveshop_par.domain.repository.ProductRepository
import com.example.liveshop_par.domain.repository.OrderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// regla2: modulo de hilt para vinculacion de interfaces con implementaciones, @binds para inyectar
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Singleton
    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
    
    @Singleton
    @Binds
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository
    
    @Singleton
    @Binds
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository
}

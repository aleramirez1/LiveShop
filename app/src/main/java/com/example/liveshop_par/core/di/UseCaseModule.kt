package com.example.liveshop_par.core.di

import com.example.liveshop_par.features.liveshop.domain.repositories.ProductRepository
import com.example.liveshop_par.features.liveshop.domain.usecases.AddProductUseCase
import com.example.liveshop_par.features.liveshop.domain.usecases.GetAllProductsUseCase
import com.example.liveshop_par.features.liveshop.domain.usecases.SearchProductsUseCase
import com.example.liveshop_par.features.liveshop.domain.usecases.PurchaseProductUseCase
import com.example.liveshop_par.features.login.domain.repositories.AuthRepository
import com.example.liveshop_par.features.login.domain.usecases.LoginUseCase
import com.example.liveshop_par.features.register.domain.repositories.RegisterRepository
import com.example.liveshop_par.features.register.domain.usecases.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Singleton
    @Provides
    fun provideLoginUseCase(
        authRepository: AuthRepository
    ): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Singleton
    @Provides
    fun provideRegisterUseCase(
        registerRepository: RegisterRepository
    ): RegisterUseCase {
        return RegisterUseCase(registerRepository)
    }

    @Singleton
    @Provides
    fun provideGetAllProductsUseCase(
        productRepository: ProductRepository
    ): GetAllProductsUseCase {
        return GetAllProductsUseCase(productRepository)
    }

    @Singleton
    @Provides
    fun provideAddProductUseCase(
        productRepository: ProductRepository
    ): AddProductUseCase {
        return AddProductUseCase(productRepository)
    }

    @Singleton
    @Provides
    fun provideSearchProductsUseCase(
        productRepository: ProductRepository
    ): SearchProductsUseCase {
        return SearchProductsUseCase(productRepository)
    }

    @Singleton
    @Provides
    fun providePurchaseProductUseCase(
        productRepository: ProductRepository
    ): PurchaseProductUseCase {
        return PurchaseProductUseCase(productRepository)
    }
}

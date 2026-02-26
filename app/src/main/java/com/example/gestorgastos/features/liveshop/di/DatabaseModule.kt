package com.example.liveshop.features.liveshop.di

import android.content.Context
import androidx.room.Room
import com.example.liveshop.features.liveshop.data.datasources.local.LiveshopDatabase
import com.example.liveshop.features.liveshop.data.datasources.local.OrderDao
import com.example.liveshop.features.liveshop.data.datasources.local.ProductDao
import com.example.liveshop.features.liveshop.data.datasources.local.ReservationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideLiveshopDatabase(
        @ApplicationContext context: Context
    ): LiveshopDatabase = Room.databaseBuilder(
        context,
        LiveshopDatabase::class.java,
        "liveshop_database"
    ).build()

    @Provides
    @Singleton
    fun provideProductDao(database: LiveshopDatabase): ProductDao =
        database.productDao()

    @Provides
    @Singleton
    fun provideOrderDao(database: LiveshopDatabase): OrderDao =
        database.orderDao()

    @Provides
    @Singleton
    fun provideReservationDao(database: LiveshopDatabase): ReservationDao =
        database.reservationDao()
}

package com.example.liveshop.features.liveshop.data.datasources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.liveshop.features.liveshop.data.datasources.local.entity.OrderEntity
import com.example.liveshop.features.liveshop.data.datasources.local.entity.ProductEntity
import com.example.liveshop.features.liveshop.data.datasources.local.entity.ReservationEntity

@Database(
    entities = [ProductEntity::class, OrderEntity::class, ReservationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LiveshopDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun reservationDao(): ReservationDao
}

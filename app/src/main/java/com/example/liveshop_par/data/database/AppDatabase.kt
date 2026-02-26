package com.example.liveshop_par.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.liveshop_par.data.model.Product
import com.example.liveshop_par.data.model.User
import com.example.liveshop_par.data.model.Purchase

@Database(
    entities = [User::class, Product::class, Purchase::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun purchaseDao(): PurchaseDao
}

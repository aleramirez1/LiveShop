package com.example.liveshop_par.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.liveshop_par.data.model.Product
import com.example.liveshop_par.data.model.User

@Database(
    entities = [User::class, Product::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
}

package com.example.liveshop_par.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.liveshop_par.data.model.Purchase
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    @Insert
    suspend fun insertPurchase(purchase: Purchase): Long

    @Query("SELECT * FROM purchases WHERE buyerId = :buyerId ORDER BY purchaseDate DESC")
    fun getPurchasesByBuyer(buyerId: Int): Flow<List<Purchase>>

    @Query("SELECT * FROM purchases WHERE productId = :productId")
    fun getPurchasesByProduct(productId: Int): Flow<List<Purchase>>

    @Query("SELECT * FROM purchases")
    fun getAllPurchases(): Flow<List<Purchase>>
}

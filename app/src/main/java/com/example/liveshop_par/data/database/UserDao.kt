package com.example.liveshop_par.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.liveshop_par.data.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): User?
}

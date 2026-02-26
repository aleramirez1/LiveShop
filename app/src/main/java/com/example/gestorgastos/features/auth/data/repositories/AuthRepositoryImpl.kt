package com.example.liveshop.features.auth.data.repositories

import android.content.Context
import com.example.liveshop.features.auth.data.AuthPrefs
import com.example.liveshop.features.auth.domain.repositories.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AuthRepository {

    override fun isRegistered(): Boolean = AuthPrefs.isRegistered(context)

    override fun isLoggedIn(): Boolean = AuthPrefs.isLoggedIn(context)

    override suspend fun login(username: String, password: String): Result<Unit> =
        AuthPrefs.login(context, username, password)

    override suspend fun register(username: String, password: String): Result<Unit> =
        AuthPrefs.register(context, username, password)

    override fun logout() {
        AuthPrefs.logout(context)
    }
}


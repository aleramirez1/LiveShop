package com.example.liveshop.features.auth.data

import android.content.Context

object AuthPrefs {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"
    private const val KEY_LOGGED_IN = "logged_in"

    fun isRegistered(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return !prefs.getString(KEY_USERNAME, null).isNullOrBlank() && !prefs.getString(KEY_PASSWORD, null).isNullOrBlank()
    }

    fun isLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_LOGGED_IN, false)
    }

    fun register(context: Context, username: String, password: String): Result<Unit> {
        val user = username.trim()
        if (user.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Campos vacíos"))
        }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (isRegistered(context)) {
            return Result.failure(IllegalStateException("Ya existe un usuario registrado"))
        }
        prefs.edit()
            .putString(KEY_USERNAME, user)
            .putString(KEY_PASSWORD, password)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
        return Result.success(Unit)
    }

    fun login(context: Context, username: String, password: String): Result<Unit> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedUser = prefs.getString(KEY_USERNAME, null)
        val storedPass = prefs.getString(KEY_PASSWORD, null)

        if (storedUser.isNullOrBlank() || storedPass.isNullOrBlank()) {
            return Result.failure(IllegalStateException("No hay usuario registrado"))
        }

        if (storedUser != username.trim() || storedPass != password) {
            return Result.failure(IllegalArgumentException("Usuario o contraseña incorrectos"))
        }

        prefs.edit().putBoolean(KEY_LOGGED_IN, true).apply()
        return Result.success(Unit)
    }

    fun logout(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_LOGGED_IN, false).apply()
    }
}

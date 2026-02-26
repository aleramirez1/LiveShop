package com.example.liveshop_par.data.model

data class AuthState(
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val userId: Int = 0,
    val error: String? = null,
    val isLoading: Boolean = false
)

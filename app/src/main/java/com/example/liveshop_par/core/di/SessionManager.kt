package com.example.liveshop_par.core.di

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager {
    private val _userId = MutableStateFlow<Int?>(null)
    val userId: StateFlow<Int?> = _userId.asStateFlow()
    
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()
    
    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()
    
    fun setSession(userId: Int, userName: String, userEmail: String) {
        _userId.value = userId
        _userName.value = userName
        _userEmail.value = userEmail
    }
    
    fun clearSession() {
        _userId.value = null
        _userName.value = null
        _userEmail.value = null
    }
    
    fun isLoggedIn(): Boolean = _userId.value != null
}

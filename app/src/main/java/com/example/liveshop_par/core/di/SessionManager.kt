package com.example.liveshop_par.core.di

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager {
    private val _userId = MutableStateFlow<Int?>(null)
    val userId: StateFlow<Int?> = _userId.asStateFlow()
    
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()
    
    private val _userNumber = MutableStateFlow<String?>(null)
    val userNumber: StateFlow<String?> = _userNumber.asStateFlow()
    
    private val _userPassword = MutableStateFlow<String?>(null)
    val userPassword: StateFlow<String?> = _userPassword.asStateFlow()
    
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()
    
    fun setSession(userId: Int, userName: String, userNumber: String, userPassword: String = "") {
        _userId.value = userId
        _userName.value = userName
        _userNumber.value = userNumber
        _userPassword.value = userPassword
    }
    
    fun saveToken(token: String) {
        _token.value = token
    }
    
    fun getToken(): String? = _token.value
    
    fun saveUserId(userId: Int) {
        _userId.value = userId
    }
    
    fun clearSession() {
        _userId.value = null
        _userName.value = null
        _userNumber.value = null
        _userPassword.value = null
        _token.value = null
    }
    
    fun isLoggedIn(): Boolean = _userId.value != null
}

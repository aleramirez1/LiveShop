package com.example.liveshop_par.features.login.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveshop_par.core.di.SessionManager
import com.example.liveshop_par.data.network.ApiClient
import com.example.liveshop_par.data.network.LoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isAuthenticated: Boolean = false,
    val userId: Int? = null,
    val userName: String? = null,
    val userEmail: String? = null,
    val token: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LoginViewModelImpl @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            try {
                val response = ApiClient.apiService.login(LoginRequest(email, password))
                if (response.token != null && response.id != null) {
                    sessionManager.saveToken(response.token)
                    sessionManager.saveUserId(response.id)
                    _authState.value = AuthState(
                        isAuthenticated = true,
                        userId = response.id,
                        userName = response.nombre ?: "",
                        userEmail = response.email ?: "",
                        token = response.token,
                        isLoading = false
                    )
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = "Credenciales inválidas"
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error de conexión"
                )
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _authState.value = AuthState()
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}

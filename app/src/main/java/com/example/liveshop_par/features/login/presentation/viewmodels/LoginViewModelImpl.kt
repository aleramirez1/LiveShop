package com.example.liveshop_par.features.login.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveshop_par.core.di.SessionManager
import com.example.liveshop_par.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isAuthenticated: Boolean = false,
    val userId: Int? = null,
    val userName: String? = null,
    val userNumber: String? = null,
    val token: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// regla2: viewmodel inyectado con hilt, usa casos de uso de dominio
@HiltViewModel
class LoginViewModelImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    fun login(numero: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            loginUseCase(numero, password).collect { result ->
                result.onSuccess { user ->
                    sessionManager.setSession(
                        userId = user.id,
                        userName = user.name,
                        userNumber = user.number,
                        userPassword = password
                    )
                    sessionManager.saveToken(user.token)
                    
                    _authState.value = AuthState(
                        isAuthenticated = true,
                        userId = user.id,
                        userName = user.name,
                        userNumber = user.number,
                        token = user.token,
                        isLoading = false
                    )
                }
                result.onFailure { exception ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error de conexion"
                    )
                }
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

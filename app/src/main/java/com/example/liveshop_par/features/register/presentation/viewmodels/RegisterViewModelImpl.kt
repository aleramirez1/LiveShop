package com.example.liveshop_par.features.register.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveshop_par.core.di.SessionManager
import com.example.liveshop_par.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterAuthState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

// regla2: viewmodel inyectado con hilt, usa casos de uso de dominio
@HiltViewModel
class RegisterViewModelImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow(RegisterAuthState())
    val authState: StateFlow<RegisterAuthState> = _authState

    fun register(nombre: String, numero: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            registerUseCase(nombre, numero, password).collect { result ->
                result.onSuccess { user ->
                    sessionManager.setSession(
                        userId = user.id,
                        userName = user.name,
                        userNumber = user.number,
                        userPassword = password
                    )
                    sessionManager.saveToken("")
                    
                    _authState.value = RegisterAuthState(
                        isAuthenticated = true,
                        isLoading = false
                    )
                }
                result.onFailure { exception ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error en registro"
                    )
                }
            }
        }
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}


package com.example.liveshop_par.features.register.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveshop_par.data.network.ApiClient
import com.example.liveshop_par.data.network.CreateUserRequest
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

@HiltViewModel
class RegisterViewModelImpl @Inject constructor() : ViewModel() {

    private val _authState = MutableStateFlow(RegisterAuthState())
    val authState: StateFlow<RegisterAuthState> = _authState

    fun register(nombre: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            try {
                val response = ApiClient.apiService.createUser(
                    CreateUserRequest(nombre, email, password)
                )
                if (response.id != null) {
                    _authState.value = RegisterAuthState(
                        isAuthenticated = true,
                        isLoading = false
                    )
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = "Error al crear usuario"
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

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}

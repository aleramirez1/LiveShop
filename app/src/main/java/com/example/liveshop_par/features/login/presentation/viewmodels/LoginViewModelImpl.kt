package com.example.liveshop_par.features.login.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveshop_par.data.model.AuthState
import com.example.liveshop_par.features.login.domain.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModelImpl @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            val result = loginUseCase(email, password)
            result.onSuccess { user ->
                _authState.value = AuthState(
                    isAuthenticated = true,
                    user = user,
                    isLoading = false
                )
            }
            result.onFailure { exception ->
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Error desconocido"
                )
            }
        }
    }

    fun logout() {
        _authState.value = AuthState()
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}

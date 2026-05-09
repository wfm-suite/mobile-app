package org.worklog.app.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.usecase.user.AuthenticationUseCase

class LoginViewModel(
    private val authenticationUseCase: AuthenticationUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun loginUser() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)

        // if validation fails, update UI and return
        if (emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    error = emailError ?: passwordError,
                    isLoading = false
                )
            }
            return
        }

        // proceed with API call
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = authenticationUseCase.login(email, password)) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            userInfo = result.data,
                            isLoggedIn = true,
                            isLoading = false,
                            error = null
                        )
                    }
                }

                is ResultWrapper.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, isLoggedIn = false, error = result.message)
                    }
                }

                is ResultWrapper.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matches(email) -> "Invalid email"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }


    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }
}
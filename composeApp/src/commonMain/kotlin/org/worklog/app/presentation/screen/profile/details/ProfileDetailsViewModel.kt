package org.worklog.app.presentation.screen.profile.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.usecase.user.UpdateUserProfileUseCase
import org.worklog.app.domain.usecase.user.UploadProfileImageUseCase
import org.worklog.app.domain.usecase.user.UserProfileUseCase

class ProfileDetailsViewModel(
    private val getUserProfileUseCase: UserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val uploadProfileImageUseCase: UploadProfileImageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileDetailsUiState())
    val uiState: StateFlow<ProfileDetailsUiState> = _uiState.asStateFlow()

    init {
        getUserProfile()
    }

    private fun getUserProfile() {
        viewModelScope.launch {
            getUserProfileUseCase.getUserProfile.collect { result ->
                if (result is ResultWrapper.Success) {
                    println("\n\n User Profile: ${result.data}\n\n")
                    _uiState.update { it.copy(userInfo = result.data) }
                } else if (result is ResultWrapper.Error) {
                    _uiState.update { it.copy(message = result.message) }
                }
            }
        }
    }

    fun onFirstNameChange(firstName: String) {
        _uiState.update { it.copy(userInfo = it.userInfo?.copy(firstName = firstName)) }
    }

    fun onLastNameChange(lastName: String) {
        _uiState.update { it.copy(userInfo = it.userInfo?.copy(lastName = lastName)) }
    }

    fun onDisplayNameChange(displayName: String) {
        _uiState.update { it.copy(userInfo = it.userInfo?.copy(displayName = displayName)) }
    }

    fun onGenderChange(gender: String) {
        _uiState.update { it.copy(userInfo = it.userInfo?.copy(gender = gender)) }
    }

    fun onDateOfBirthChange(dateOfBirth: String) {
        _uiState.update { it.copy(userInfo = it.userInfo?.copy(dateOfBirth = dateOfBirth)) }
    }

    fun onMaritalStatusChange(maritalStatus: String) {
        _uiState.update { it.copy(userInfo = it.userInfo?.copy(maritalStatus = maritalStatus)) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(userInfo = it.userInfo?.copy(email = email)) }
    }

    fun onPhoneNumberChange(phoneNumber: String) {
        _uiState.update { it.copy(userInfo = it.userInfo?.copy(phoneNumber = phoneNumber)) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun uploadProfileImage(imageBytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = uploadProfileImageUseCase(imageBytes)) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            message = "Profile image uploaded successfully",
                            isLoading = false
                        )
                    }
                }

                is ResultWrapper.Error -> {
                    _uiState.update { it.copy(message = result.message, isLoading = false) }
                }

                else -> {}
            }
        }
    }

    fun onSaveClick() {
        if (!validateInput()) return

        // Handle save logic
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = updateUserProfileUseCase(
                userInfo = _uiState.value.userInfo!!
            )
            when (result) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            message = "Profile Updated Successfully",
                            userInfo = result.data,
                            isLoading = false
                        )
                    }
                }

                is ResultWrapper.Error -> {
                    _uiState.update { it.copy(message = result.message, isLoading = false) }
                }

                else -> {}
            }
        }
    }

    private fun validateInput(): Boolean {
        val userInfo = _uiState.value.userInfo ?: return false

        if (userInfo.firstName.isBlank()) {
            _uiState.update { it.copy(message = "First name is required") }
            return false
        }
        if (userInfo.lastName.isBlank()) {
            _uiState.update { it.copy(message = "Last name is required") }
            return false
        }
        if (userInfo.displayName.isBlank()) {
            _uiState.update { it.copy(message = "Display name is required") }
            return false
        }
        if (userInfo.gender.isBlank()) {
            _uiState.update { it.copy(message = "Gender is required") }
            return false
        }
        if (userInfo.dateOfBirth.isBlank()) {
            _uiState.update { it.copy(message = "Date of birth is required") }
            return false
        }
        if (userInfo.maritalStatus.isBlank()) {
            _uiState.update { it.copy(message = "Marital status is required") }
            return false
        }
        if (userInfo.email.isNullOrBlank()) {
            _uiState.update { it.copy(message = "Email is required") }
            return false
        }
        if (!isValidEmail(userInfo.email)) {
            _uiState.update { it.copy(message = "Invalid email format") }
            return false
        }
        if (userInfo.phoneNumber.isBlank()) {
            _uiState.update { it.copy(message = "Phone number is required") }
            return false
        }
        return true
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        return emailRegex.matches(email)
    }

}
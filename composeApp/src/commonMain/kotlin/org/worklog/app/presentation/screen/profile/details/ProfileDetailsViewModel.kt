package org.worklog.app.presentation.screen.profile.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.UserInfo
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

    // Cached raw UserInfo so we can compose update payloads
    private var cachedUserInfo: UserInfo? = null

    init {
        observeProfile()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Profile loading
    // ─────────────────────────────────────────────────────────────────────────

    private fun observeProfile() {
        viewModelScope.launch {
            getUserProfileUseCase.getUserProfile.collect { result ->
                when (result) {
                    is ResultWrapper.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is ResultWrapper.Success -> {
                        cachedUserInfo = result.data
                        mapToUiState(result.data)
                    }
                    is ResultWrapper.Error -> _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    private fun mapToUiState(user: UserInfo) {
        _uiState.update {
            it.copy(
                isLoading = false,
                // Personal
                firstName = user.firstName,
                lastName = user.lastName,
                displayName = user.displayName,
                email = user.email ?: "",
                phone = user.phoneNumber,
                gender = user.gender,
                dateOfBirth = user.dateOfBirth,
                maritalStatus = user.maritalStatus,
                profilePicture = user.profilePicture.ifEmpty { null },
                // Assignment (read-only)
                companyName = user.companyName,
                companyAddress = user.companyAddress,
                branchName = user.branchName,
                branchAddress = user.branchAddress,
                floor = user.floor,
                department = user.department,
                designation = user.designation,
                // Contact
                nextOfKinName = user.nextOfKinName ?: "",
                nextOfKinRelationship = user.nextOfKinRelationship ?: "",
                nextOfKinPhone = user.nextOfKinPhone ?: "",
                // Bank
                bankAccName = user.bankAccName ?: "",
                bankAccNumber = user.bankAccNumber ?: "",
                bankRoutingNumber = user.bankRoutingNumber ?: "",
                bankAddress = user.bankAddress ?: "",
                // Declaration
                declarationSigned = user.declarationSigned,
                declarationSignedAt = user.declarationSignedAt,
                // Job Status (read-only)
                employeeStatus = user.employeeStatus ?: "",
                employmentType = user.employmentType ?: "",
                contractStartDate = user.contractStartDate ?: "",
                contractEndDate = user.contractEndDate ?: "",
                // RTW
                niNumber = user.niNumber ?: "",
                passportNumber = user.passportNumber ?: "",
                passportExpiry = user.passportExpiry ?: "",
                visaNumber = user.visaNumber ?: "",
                visaExpiry = user.visaExpiry ?: "",
                rtwStatus = user.rtwStatus ?: "",
                rtwExpiry = user.rtwExpiry ?: "",
                // EDI
                ethnicity = user.ethnicity ?: "",
                nationality = user.nationality ?: "",
                disability = user.disability ?: "",
                religion = user.religion ?: "",
                sexualOrientation = user.sexualOrientation ?: "",
                // Vetting
                dbsCheckDate = user.dbsCheckDate ?: "",
                dbsCertificateNumber = user.dbsCertificateNumber ?: "",
                reference1Name = user.reference1Name ?: "",
                reference1Contact = user.reference1Contact ?: "",
                reference2Name = user.reference2Name ?: "",
                reference2Contact = user.reference2Contact ?: "",
                // Collections
                addresses = user.addresses.map { addr ->
                    UserAddressUiState(
                        id = addr.id,
                        addressType = addr.addressType,
                        addressLine1 = addr.addressLine1,
                        addressLine2 = addr.addressLine2 ?: "",
                        city = addr.city,
                        county = addr.county ?: "",
                        postcode = addr.postcode,
                        country = addr.country,
                        isPrimary = addr.isPrimary
                    )
                },
                emergencyContacts = user.emergencyContacts.map { ec ->
                    EmergencyContactUiState(
                        id = ec.id,
                        name = ec.name,
                        relationship = ec.relationship,
                        phone = ec.phone,
                        email = ec.email ?: "",
                        address = ec.address ?: "",
                        isPrimary = ec.isPrimary
                    )
                },
                trainingCourses = user.trainingCourses.map { tc ->
                    TrainingCourseUiState(
                        id = tc.id,
                        courseName = tc.courseName,
                        provider = tc.provider ?: "",
                        completedDate = tc.completedDate ?: "",
                        expiryDate = tc.expiryDate ?: "",
                        certificateNumber = tc.certificateNumber ?: "",
                        status = tc.status
                    )
                },
                existingResignation = user.resignationRequests.firstOrNull()?.let { r ->
                    UserResignationUiState(
                        id = r.id,
                        lastWorkingDay = r.lastWorkingDay,
                        reason = r.reason ?: "",
                        status = r.status,
                        submittedAt = r.submittedAt
                    )
                }
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Field-level setters — Personal Details
    // ─────────────────────────────────────────────────────────────────────────

    fun setFirstName(v: String) = _uiState.update { it.copy(firstName = v) }
    fun setLastName(v: String) = _uiState.update { it.copy(lastName = v) }
    fun setDisplayName(v: String) = _uiState.update { it.copy(displayName = v) }
    fun setEmail(v: String) = _uiState.update { it.copy(email = v) }
    fun setPhone(v: String) = _uiState.update { it.copy(phone = v) }
    fun setGender(v: String) = _uiState.update { it.copy(gender = v) }
    fun setDateOfBirth(v: String) = _uiState.update { it.copy(dateOfBirth = v) }
    fun setMaritalStatus(v: String) = _uiState.update { it.copy(maritalStatus = v) }

    // Contact Details
    fun setNextOfKinName(v: String) = _uiState.update { it.copy(nextOfKinName = v) }
    fun setNextOfKinRelationship(v: String) = _uiState.update { it.copy(nextOfKinRelationship = v) }
    fun setNextOfKinPhone(v: String) = _uiState.update { it.copy(nextOfKinPhone = v) }

    // Bank Details
    fun setBankAccName(v: String) = _uiState.update { it.copy(bankAccName = v) }
    fun setBankAccNumber(v: String) = _uiState.update { it.copy(bankAccNumber = v) }
    fun setBankRoutingNumber(v: String) = _uiState.update { it.copy(bankRoutingNumber = v) }
    fun setBankAddress(v: String) = _uiState.update { it.copy(bankAddress = v) }

    // Declaration
    fun setDeclarationSigned(v: Boolean) = _uiState.update { it.copy(declarationSigned = v) }

    // RTW Details
    fun setNiNumber(v: String) = _uiState.update { it.copy(niNumber = v) }
    fun setPassportNumber(v: String) = _uiState.update { it.copy(passportNumber = v) }
    fun setPassportExpiry(v: String) = _uiState.update { it.copy(passportExpiry = v) }
    fun setVisaNumber(v: String) = _uiState.update { it.copy(visaNumber = v) }
    fun setVisaExpiry(v: String) = _uiState.update { it.copy(visaExpiry = v) }
    fun setRtwStatus(v: String) = _uiState.update { it.copy(rtwStatus = v) }
    fun setRtwExpiry(v: String) = _uiState.update { it.copy(rtwExpiry = v) }

    // EDI
    fun setEthnicity(v: String) = _uiState.update { it.copy(ethnicity = v) }
    fun setNationality(v: String) = _uiState.update { it.copy(nationality = v) }
    fun setDisability(v: String) = _uiState.update { it.copy(disability = v) }
    fun setReligion(v: String) = _uiState.update { it.copy(religion = v) }
    fun setSexualOrientation(v: String) = _uiState.update { it.copy(sexualOrientation = v) }

    // Vetting
    fun setDbsCheckDate(v: String) = _uiState.update { it.copy(dbsCheckDate = v) }
    fun setDbsCertificateNumber(v: String) = _uiState.update { it.copy(dbsCertificateNumber = v) }
    fun setReference1Name(v: String) = _uiState.update { it.copy(reference1Name = v) }
    fun setReference1Contact(v: String) = _uiState.update { it.copy(reference1Contact = v) }
    fun setReference2Name(v: String) = _uiState.update { it.copy(reference2Name = v) }
    fun setReference2Contact(v: String) = _uiState.update { it.copy(reference2Contact = v) }

    // Password
    fun setCurrentPassword(v: String) = _uiState.update { it.copy(currentPassword = v) }
    fun setNewPassword(v: String) = _uiState.update { it.copy(newPassword = v) }
    fun setConfirmPassword(v: String) = _uiState.update { it.copy(confirmPassword = v) }

    // Resignation
    fun setResignationLastWorkingDay(v: String) = _uiState.update { it.copy(resignationLastWorkingDay = v) }
    fun setResignationReason(v: String) = _uiState.update { it.copy(resignationReason = v) }

    // ─────────────────────────────────────────────────────────────────────────
    // Addresses
    // ─────────────────────────────────────────────────────────────────────────

    fun addAddress() {
        _uiState.update { state ->
            state.copy(addresses = state.addresses + UserAddressUiState())
        }
    }

    fun updateAddress(index: Int, updated: UserAddressUiState) {
        _uiState.update { state ->
            val list = state.addresses.toMutableList()
            if (index in list.indices) list[index] = updated
            state.copy(addresses = list)
        }
    }

    fun deleteAddress(index: Int) {
        _uiState.update { state ->
            val list = state.addresses.toMutableList()
            if (index in list.indices) list.removeAt(index)
            state.copy(addresses = list)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Emergency Contacts
    // ─────────────────────────────────────────────────────────────────────────

    fun addEmergencyContact() {
        _uiState.update { state ->
            state.copy(emergencyContacts = state.emergencyContacts + EmergencyContactUiState())
        }
    }

    fun updateEmergencyContact(index: Int, updated: EmergencyContactUiState) {
        _uiState.update { state ->
            val list = state.emergencyContacts.toMutableList()
            if (index in list.indices) list[index] = updated
            state.copy(emergencyContacts = list)
        }
    }

    fun deleteEmergencyContact(index: Int) {
        _uiState.update { state ->
            val list = state.emergencyContacts.toMutableList()
            if (index in list.indices) list.removeAt(index)
            state.copy(emergencyContacts = list)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Training Courses
    // ─────────────────────────────────────────────────────────────────────────

    fun addTrainingCourse() {
        _uiState.update { state ->
            state.copy(trainingCourses = state.trainingCourses + TrainingCourseUiState())
        }
    }

    fun updateTrainingCourse(index: Int, updated: TrainingCourseUiState) {
        _uiState.update { state ->
            val list = state.trainingCourses.toMutableList()
            if (index in list.indices) list[index] = updated
            state.copy(trainingCourses = list)
        }
    }

    fun deleteTrainingCourse(index: Int) {
        _uiState.update { state ->
            val list = state.trainingCourses.toMutableList()
            if (index in list.indices) list.removeAt(index)
            state.copy(trainingCourses = list)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Save operations
    // ─────────────────────────────────────────────────────────────────────────

    /** Builds a UserInfo from current UI state, using cached data as base. */
    private fun buildUserInfo(): UserInfo? {
        val base = cachedUserInfo ?: return null
        val s = _uiState.value
        return base.copy(
            firstName = s.firstName,
            lastName = s.lastName,
            displayName = s.displayName,
            email = s.email,
            phoneNumber = s.phone,
            gender = s.gender,
            dateOfBirth = s.dateOfBirth,
            maritalStatus = s.maritalStatus,
            nextOfKinName = s.nextOfKinName,
            nextOfKinRelationship = s.nextOfKinRelationship,
            nextOfKinPhone = s.nextOfKinPhone,
            bankAccName = s.bankAccName,
            bankAccNumber = s.bankAccNumber,
            bankRoutingNumber = s.bankRoutingNumber,
            bankAddress = s.bankAddress,
            declarationSigned = s.declarationSigned,
            niNumber = s.niNumber,
            passportNumber = s.passportNumber,
            passportExpiry = s.passportExpiry,
            visaNumber = s.visaNumber,
            visaExpiry = s.visaExpiry,
            rtwStatus = s.rtwStatus,
            rtwExpiry = s.rtwExpiry,
            ethnicity = s.ethnicity,
            nationality = s.nationality,
            disability = s.disability,
            religion = s.religion,
            sexualOrientation = s.sexualOrientation,
            dbsCheckDate = s.dbsCheckDate,
            dbsCertificateNumber = s.dbsCertificateNumber,
            reference1Name = s.reference1Name,
            reference1Contact = s.reference1Contact,
            reference2Name = s.reference2Name,
            reference2Contact = s.reference2Contact,
        )
    }

    private fun performSave() {
        val userInfo = buildUserInfo() ?: run {
            _uiState.update { it.copy(errorMessage = "Profile data not loaded yet") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            when (val result = updateUserProfileUseCase(userInfo)) {
                is ResultWrapper.Success -> {
                    cachedUserInfo = result.data
                    _uiState.update { it.copy(isSaving = false, successMessage = "Saved successfully") }
                }
                is ResultWrapper.Error -> _uiState.update {
                    it.copy(isSaving = false, errorMessage = result.message)
                }
                else -> _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun savePersonalDetails() {
        val s = _uiState.value
        if (s.firstName.isBlank()) { _uiState.update { it.copy(errorMessage = "First name is required") }; return }
        if (s.lastName.isBlank()) { _uiState.update { it.copy(errorMessage = "Last name is required") }; return }
        if (s.displayName.isBlank()) { _uiState.update { it.copy(errorMessage = "Display name is required") }; return }
        if (s.email.isBlank()) { _uiState.update { it.copy(errorMessage = "Email is required") }; return }
        performSave()
    }

    fun saveContactDetails() = performSave()

    fun saveBankDetails() = performSave()

    fun saveDeclaration() = performSave()

    fun saveRtwDetails() = performSave()

    fun saveEdiDetails() = performSave()

    fun saveVettingDetails() = performSave()

    fun saveAddresses() {
        // TODO: wire up to dedicated repository methods when data layer exposes them
        _uiState.update { it.copy(successMessage = "Addresses saved (stub)") }
    }

    fun saveEmergencyContacts() {
        // TODO: wire up to dedicated repository methods when data layer exposes them
        _uiState.update { it.copy(successMessage = "Emergency contacts saved (stub)") }
    }

    fun saveTrainingCourses() {
        // TODO: wire up to dedicated repository methods when data layer exposes them
        _uiState.update { it.copy(successMessage = "Training courses saved (stub)") }
    }

    fun changePassword() {
        val s = _uiState.value
        if (s.currentPassword.isBlank()) { _uiState.update { it.copy(errorMessage = "Current password is required") }; return }
        if (s.newPassword.isBlank()) { _uiState.update { it.copy(errorMessage = "New password is required") }; return }
        if (s.newPassword != s.confirmPassword) { _uiState.update { it.copy(errorMessage = "Passwords do not match") }; return }
        viewModelScope.launch {
            // TODO: wire up to PasswordResetUseCase or dedicated change-password endpoint
            _uiState.update { it.copy(successMessage = "Password change request submitted (stub)") }
        }
    }

    fun submitResignation() {
        val s = _uiState.value
        if (s.resignationLastWorkingDay.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Last working day is required") }
            return
        }
        viewModelScope.launch {
            // TODO: wire up to dedicated resignation repository method
            _uiState.update { it.copy(successMessage = "Resignation submitted (stub)") }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Profile image upload (preserving original logic)
    // ─────────────────────────────────────────────────────────────────────────

    fun uploadProfileImage(imageBytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = uploadProfileImageUseCase(imageBytes)) {
                is ResultWrapper.Success -> _uiState.update {
                    it.copy(isLoading = false, successMessage = "Profile image uploaded successfully")
                }
                is ResultWrapper.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Message clearing
    // ─────────────────────────────────────────────────────────────────────────

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    /** Back-compat alias used by older screen code. */
    fun clearMessage() = clearMessages()
}

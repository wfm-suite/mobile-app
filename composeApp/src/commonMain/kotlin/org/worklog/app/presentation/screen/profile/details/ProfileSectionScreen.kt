package org.worklog.app.presentation.screen.profile.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.core.platform.ImagePicker
import org.worklog.app.core.util.resizeAndCompress
import org.worklog.app.presentation.component.AppTopbarWithBack
import org.worklog.app.presentation.screen.profile.ProfileSectionType
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.LocalSnackBarHostState
import org.worklog.app.presentation.theme.dimens

@Composable
fun ProfileSectionScreen(
    type: String,
    viewModel: ProfileDetailsViewModel = koinViewModel()
) {
    val navController = LocalNavController.current
    val snackBarHostState = LocalSnackBarHostState.current
    val uiState by viewModel.uiState.collectAsState()
    val imagePicker = remember { ImagePicker() }

    imagePicker.RegisterPicker { imageBytes ->
        viewModel.uploadProfileImage(imageBytes.resizeAndCompress(1920, 1080, 75))
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackBarHostState.showSnackbar(msg)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { msg ->
            snackBarHostState.showSnackbar(msg)
            viewModel.clearMessages()
        }
    }

    var isEditMode by remember { mutableStateOf(false) }
    val title = titleFor(type)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopbarWithBack(
                title = title,
                onBackClick = { navController.navigateUp() },
                actions = {
                    IconButton(onClick = { isEditMode = !isEditMode }) {
                        Icon(
                            imageVector = if (isEditMode) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditMode) "Done" else "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { padding ->
        CompositionLocalProvider(LocalProfileEditMode provides isEditMode) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = dimens.horizontalPadding)
                    .padding(top = 12.dp, bottom = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (type) {
                    ProfileSectionType.PERSONAL_DETAILS -> PersonalDetailsSection(
                        uiState = uiState,
                        onImageClick = { imagePicker.pickImage() },
                        viewModel = viewModel
                    )
                    ProfileSectionType.CONTACT_DETAILS -> ContactDetailsSection(uiState = uiState, viewModel = viewModel)
                    ProfileSectionType.ADDRESSES -> AddressesSection(uiState = uiState, viewModel = viewModel)
                    ProfileSectionType.EMERGENCY_CONTACTS -> EmergencyContactsSection(uiState = uiState, viewModel = viewModel)
                    ProfileSectionType.ASSIGNMENT_SUMMARY -> AssignmentSummarySection(uiState = uiState)
                    ProfileSectionType.JOB_STATUS -> JobStatusSection(uiState = uiState)
                    ProfileSectionType.RTW_DETAILS -> RtwDetailsSection(uiState = uiState, viewModel = viewModel)
                    ProfileSectionType.EDI -> EdiSection(uiState = uiState, viewModel = viewModel)
                    ProfileSectionType.EMPLOYEE_DECLARATION -> EmployeeDeclarationSection(uiState = uiState, viewModel = viewModel)
                    ProfileSectionType.VETTING_DETAILS -> VettingDetailsSection(uiState = uiState, viewModel = viewModel)
                    ProfileSectionType.TRAINING_COURSES -> TrainingCoursesSection(uiState = uiState, viewModel = viewModel)
                    ProfileSectionType.BANK_DETAILS -> BankDetailsSection(uiState = uiState, viewModel = viewModel)
                    ProfileSectionType.PASSWORD -> PasswordSection(uiState = uiState, viewModel = viewModel)
                    ProfileSectionType.RESIGNATION -> ResignationSection(uiState = uiState, viewModel = viewModel)
                }
            }
        }
    }
}

private fun titleFor(type: String): String = when (type) {
    ProfileSectionType.PERSONAL_DETAILS -> "Personal Details"
    ProfileSectionType.CONTACT_DETAILS -> "Contact Details"
    ProfileSectionType.ADDRESSES -> "Addresses"
    ProfileSectionType.EMERGENCY_CONTACTS -> "Emergency Contacts"
    ProfileSectionType.ASSIGNMENT_SUMMARY -> "Assignment Summary"
    ProfileSectionType.JOB_STATUS -> "Job Status"
    ProfileSectionType.RTW_DETAILS -> "RTW Details"
    ProfileSectionType.EDI -> "Equality / Diversity / Inclusion"
    ProfileSectionType.EMPLOYEE_DECLARATION -> "Employee Declaration"
    ProfileSectionType.VETTING_DETAILS -> "Vetting Details"
    ProfileSectionType.TRAINING_COURSES -> "Training Courses"
    ProfileSectionType.BANK_DETAILS -> "Bank Details"
    ProfileSectionType.PASSWORD -> "My Password"
    ProfileSectionType.RESIGNATION -> "Submit Resignation"
    else -> "Profile"
}

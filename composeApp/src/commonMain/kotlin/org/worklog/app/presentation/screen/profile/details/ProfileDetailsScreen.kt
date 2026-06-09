package org.worklog.app.presentation.screen.profile.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.core.platform.ImagePicker
import org.worklog.app.core.util.resizeAndCompress
import org.worklog.app.presentation.component.AppTopbarWithBack
import org.worklog.app.presentation.component.PlatformDatePicker
import org.worklog.app.presentation.component.PrimaryButton
import org.worklog.app.presentation.component.ShimmerBox
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.LocalSnackBarHostState
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.ic_calendar
import worklog.composeapp.generated.resources.ic_user
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.compositionLocalOf

// Default to view-only. The per-section screen wraps content in
// CompositionLocalProvider(LocalProfileEditMode provides isEditMode) so all
// shared form composables and Save buttons gate themselves automatically.
val LocalProfileEditMode = compositionLocalOf { false }

// ─────────────────────────────────────────────────────────────────────────────
// Entry Point
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ProfileDetailsScreen(
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

    when {
        uiState.isLoading -> ProfileDetailsShimmerContent()
        else -> ProfileDetailsContent(
            uiState = uiState,
            onBackClick = { navController.navigateUp() },
            onImageClick = { imagePicker.pickImage() },
            viewModel = viewModel
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shimmer placeholder (preserved from original)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileDetailsShimmerContent() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppTopbarWithBack(title = "Profile Details") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = dimens.horizontalPadding)
                .padding(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShimmerBox(
                    modifier = Modifier.size(150.dp),
                    height = 150.dp,
                    width = 150.dp,
                    cornerRadius = dimens.cornerRadiusMedium
                )
                ShimmerBox(modifier = Modifier.fillMaxWidth(), height = 40.dp, cornerRadius = dimens.cornerRadius)
            }
            repeat(6) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ShimmerBox(height = 16.dp, width = 100.dp, cornerRadius = 4.dp)
                    ShimmerBox(height = dimens.inputHeight, cornerRadius = dimens.cornerRadiusMedium)
                }
            }
            ShimmerBox(height = 50.dp, cornerRadius = 25.dp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main content scaffold
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileDetailsContent(
    uiState: ProfileDetailsUiState,
    onBackClick: () -> Unit,
    onImageClick: () -> Unit,
    viewModel: ProfileDetailsViewModel
) {
    val snackBarHostState = LocalSnackBarHostState.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopbarWithBack(
                title = "Profile Details",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Hero Card — name, designation, photo at a glance
            item { ProfileHeroCard(uiState = uiState) }

            // 1. Personal Details
            item {
                ProfileSection(
                    title = "Personal Details",
                    icon = Icons.Default.Person,
                    iconColor = Color(0xFF007B99),
                    initiallyExpanded = true
                ) {
                    PersonalDetailsSection(uiState = uiState, onImageClick = onImageClick, viewModel = viewModel)
                }
            }

            // 2. Assignment Summary
            item {
                ProfileSection(
                    title = "Assignment Summary",
                    icon = Icons.Default.Business,
                    iconColor = Color(0xFF5C6BC0)
                ) {
                    AssignmentSummarySection(uiState = uiState)
                }
            }

            // 3. Employee Declaration
            item {
                ProfileSection(
                    title = "Employee Declaration",
                    icon = Icons.Default.Assignment,
                    iconColor = Color(0xFF388E3C)
                ) {
                    EmployeeDeclarationSection(uiState = uiState, viewModel = viewModel)
                }
            }

            // 4. Job Status
            item {
                ProfileSection(
                    title = "Job Status",
                    icon = Icons.Default.Work,
                    iconColor = Color(0xFFF57C00)
                ) {
                    JobStatusSection(uiState = uiState)
                }
            }

            // 5. RTW Details
            item {
                ProfileSection(
                    title = "RTW Details",
                    icon = Icons.Default.Description,
                    iconColor = Color(0xFFD32F2F)
                ) {
                    RtwDetailsSection(uiState = uiState, viewModel = viewModel)
                }
            }

            // 6. Equality / Diversity / Inclusion
            item {
                ProfileSection(
                    title = "Equality / Diversity / Inclusion",
                    icon = Icons.Default.People,
                    iconColor = Color(0xFF7B1FA2)
                ) {
                    EdiSection(uiState = uiState, viewModel = viewModel)
                }
            }

            // 7. Training Courses
            item {
                ProfileSection(
                    title = "Training Courses",
                    icon = Icons.Default.School,
                    iconColor = Color(0xFF00796B),
                    badge = uiState.trainingCourses.size.takeIf { it > 0 }?.toString()
                ) {
                    TrainingCoursesSection(uiState = uiState, viewModel = viewModel)
                }
            }

            // 8. Contact Details
            item {
                ProfileSection(
                    title = "Contact Details",
                    icon = Icons.Default.ContactPhone,
                    iconColor = Color(0xFF1565C0)
                ) {
                    ContactDetailsSection(uiState = uiState, viewModel = viewModel)
                }
            }

            // 9. Addresses
            item {
                ProfileSection(
                    title = "Addresses",
                    icon = Icons.Default.Home,
                    iconColor = Color(0xFF2E7D32),
                    badge = uiState.addresses.size.takeIf { it > 0 }?.toString()
                ) {
                    AddressesSection(uiState = uiState, viewModel = viewModel)
                }
            }

            // 10. Emergency Contacts
            item {
                ProfileSection(
                    title = "Emergency Contacts",
                    icon = Icons.Default.ContactPhone,
                    iconColor = Color(0xFFC62828),
                    badge = uiState.emergencyContacts.size.takeIf { it > 0 }?.toString()
                ) {
                    EmergencyContactsSection(uiState = uiState, viewModel = viewModel)
                }
            }

            // 11. Bank Details
            item {
                ProfileSection(
                    title = "Bank Details",
                    icon = Icons.Default.AccountBalance,
                    iconColor = Color(0xFF1B5E20)
                ) {
                    BankDetailsSection(uiState = uiState, viewModel = viewModel)
                }
            }

            // 12. Vetting Details
            item {
                ProfileSection(
                    title = "Vetting Details",
                    icon = Icons.Default.Security,
                    iconColor = Color(0xFF37474F)
                ) {
                    VettingDetailsSection(uiState = uiState, viewModel = viewModel)
                }
            }

            // 13. My Password
            item {
                ProfileSection(
                    title = "My Password",
                    icon = Icons.Default.Lock,
                    iconColor = Color(0xFF546E7A)
                ) {
                    PasswordSection(uiState = uiState, viewModel = viewModel)
                }
            }

            // 14. Submit Resignation
            item {
                ProfileSection(
                    title = "Submit Resignation",
                    icon = Icons.Default.ExitToApp,
                    iconColor = Color(0xFFB71C1C)
                ) {
                    ResignationSection(uiState = uiState, viewModel = viewModel)
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero Profile Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileHeroCard(uiState: ProfileDetailsUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                AsyncImage(
                    model = uiState.profilePicture,
                    contentDescription = "Profile Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(Res.drawable.ic_user),
                    error = painterResource(Res.drawable.ic_user)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val fullName = listOf(uiState.firstName, uiState.lastName)
                    .filter { it.isNotBlank() }.joinToString(" ")
                Text(
                    text = fullName.ifEmpty { "Your Name" },
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (uiState.designation.isNotBlank()) {
                    Text(
                        text = uiState.designation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                    )
                }
                if (uiState.branchName.isNotBlank()) {
                    Text(
                        text = uiState.branchName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.55f)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Accordion Section Component
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileSection(
    title: String,
    icon: ImageVector? = null,
    iconColor: Color = Color(0xFF007B99),
    badge: String? = null,
    initiallyExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(iconColor.copy(alpha = 0.13f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    content()
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Section 1 – Personal Details
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun PersonalDetailsSection(
    uiState: ProfileDetailsUiState,
    onImageClick: () -> Unit,
    viewModel: ProfileDetailsViewModel
) {
    // Profile picture — circular, Apple-style, with camera badge
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .border(2.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .clickable { onImageClick() }
        ) {
            AsyncImage(
                model = uiState.profilePicture,
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(Res.drawable.ic_user),
                error = painterResource(Res.drawable.ic_user)
            )
        }
        // Camera badge bottom-right
        Box(
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.BottomCenter)
                .offset(x = 30.dp, y = (-4).dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onImageClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Change photo",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(14.dp)
            )
        }
    }

    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

    ProfileTextField(label = "First Name", value = uiState.firstName, onValueChange = viewModel::setFirstName)
    ProfileTextField(label = "Last Name", value = uiState.lastName, onValueChange = viewModel::setLastName)
    ProfileTextField(label = "Display Name", value = uiState.displayName, onValueChange = viewModel::setDisplayName)
    ProfileTextField(label = "Email", value = uiState.email, onValueChange = viewModel::setEmail, keyboardType = KeyboardType.Email)
    ProfileTextField(label = "Phone Number", value = uiState.phone, onValueChange = viewModel::setPhone, keyboardType = KeyboardType.Phone)

    ProfileDropdownField(
        label = "Gender",
        value = uiState.gender,
        options = listOf("Male", "Female", "Other", "Prefer not to say"),
        onValueChange = viewModel::setGender
    )

    ProfileDateField(label = "Date of Birth", value = uiState.dateOfBirth, onValueChange = viewModel::setDateOfBirth)

    ProfileDropdownField(
        label = "Marital Status",
        value = uiState.maritalStatus,
        options = listOf("Single", "Married", "Divorced", "Widowed", "Civil Partnership"),
        onValueChange = viewModel::setMaritalStatus
    )

    SaveButton(onClick = viewModel::savePersonalDetails)
}

// ─────────────────────────────────────────────────────────────────────────────
// Section 2 – Assignment Summary (read-only)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun AssignmentSummarySection(uiState: ProfileDetailsUiState) {
    ReadOnlyField(label = "Company Name", value = uiState.companyName)
    ReadOnlyField(label = "Company Address", value = uiState.companyAddress)
    ReadOnlyField(label = "Branch Name", value = uiState.branchName)
    ReadOnlyField(label = "Branch Address", value = uiState.branchAddress)
    ReadOnlyField(label = "Floor", value = uiState.floor)
    ReadOnlyField(label = "Department", value = uiState.department)
    ReadOnlyField(label = "Designation", value = uiState.designation)
}

// ─────────────────────────────────────────────────────────────────────────────
// Section 3 – Employee Declaration
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun EmployeeDeclarationSection(
    uiState: ProfileDetailsUiState,
    viewModel: ProfileDetailsViewModel
) {
    Text(
        text = "I confirm that all information provided is accurate and complete to the best of my knowledge.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { viewModel.setDeclarationSigned(!uiState.declarationSigned) }
    ) {
        Checkbox(
            checked = uiState.declarationSigned,
            onCheckedChange = viewModel::setDeclarationSigned
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "I agree to this declaration",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    if (!uiState.declarationSignedAt.isNullOrBlank()) {
        Text(
            text = "Signed at: ${uiState.declarationSignedAt}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }

    SaveButton(onClick = viewModel::saveDeclaration)
}

// ─────────────────────────────────────────────────────────────────────────────
// Section 4 – Job Status (read-only)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun JobStatusSection(uiState: ProfileDetailsUiState) {
    ReadOnlyField(label = "Employee Status", value = uiState.employeeStatus)
    ReadOnlyField(label = "Employment Type", value = uiState.employmentType)
    ReadOnlyField(label = "Contract Start Date", value = uiState.contractStartDate)
    ReadOnlyField(label = "Contract End Date", value = uiState.contractEndDate)
}

// ─────────────────────────────────────────────────────────────────────────────
// Section 5 – RTW Details
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun RtwDetailsSection(
    uiState: ProfileDetailsUiState,
    viewModel: ProfileDetailsViewModel
) {
    ProfileTextField(label = "NI Number", value = uiState.niNumber, onValueChange = viewModel::setNiNumber)
    ProfileTextField(label = "Passport Number", value = uiState.passportNumber, onValueChange = viewModel::setPassportNumber)
    ProfileDateField(label = "Passport Expiry", value = uiState.passportExpiry, onValueChange = viewModel::setPassportExpiry)
    ProfileTextField(label = "Visa Number", value = uiState.visaNumber, onValueChange = viewModel::setVisaNumber)
    ProfileDateField(label = "Visa Expiry", value = uiState.visaExpiry, onValueChange = viewModel::setVisaExpiry)
    ProfileTextField(label = "RTW Status", value = uiState.rtwStatus, onValueChange = viewModel::setRtwStatus)
    ProfileDateField(label = "RTW Expiry", value = uiState.rtwExpiry, onValueChange = viewModel::setRtwExpiry)
    SaveButton(onClick = viewModel::saveRtwDetails)
}

// ─────────────────────────────────────────────────────────────────────────────
// Section 6 – Equality / Diversity / Inclusion
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun EdiSection(
    uiState: ProfileDetailsUiState,
    viewModel: ProfileDetailsViewModel
) {
    ProfileDropdownField(
        label = "Ethnicity",
        value = uiState.ethnicity,
        options = listOf(
            "White - British", "White - Irish", "White - Other",
            "Mixed - White and Black Caribbean", "Mixed - White and Black African",
            "Mixed - White and Asian", "Mixed - Other",
            "Asian or Asian British - Indian", "Asian or Asian British - Pakistani",
            "Asian or Asian British - Bangladeshi", "Asian or Asian British - Other",
            "Black or Black British - Caribbean", "Black or Black British - African",
            "Black or Black British - Other",
            "Other Ethnic Group", "Prefer not to say"
        ),
        onValueChange = viewModel::setEthnicity
    )
    ProfileTextField(label = "Nationality", value = uiState.nationality, onValueChange = viewModel::setNationality)
    ProfileDropdownField(
        label = "Disability",
        value = uiState.disability,
        options = listOf("None", "Yes - Physical", "Yes - Mental", "Yes - Other", "Prefer not to say"),
        onValueChange = viewModel::setDisability
    )
    ProfileDropdownField(
        label = "Religion",
        value = uiState.religion,
        options = listOf(
            "No religion", "Christian", "Buddhist", "Hindu", "Jewish",
            "Muslim", "Sikh", "Other", "Prefer not to say"
        ),
        onValueChange = viewModel::setReligion
    )
    ProfileDropdownField(
        label = "Sexual Orientation",
        value = uiState.sexualOrientation,
        options = listOf("Heterosexual/Straight", "Gay/Lesbian", "Bisexual", "Other", "Prefer not to say"),
        onValueChange = viewModel::setSexualOrientation
    )
    SaveButton(onClick = viewModel::saveEdiDetails)
}

// ─────────────────────────────────────────────────────────────────────────────
// Section 7 – Training Courses
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun TrainingCoursesSection(
    uiState: ProfileDetailsUiState,
    viewModel: ProfileDetailsViewModel
) {
    uiState.trainingCourses.forEachIndexed { index, course ->
        TrainingCourseItem(
            course = course,
            onUpdate = { viewModel.updateTrainingCourse(index, it) },
            onDelete = { viewModel.deleteTrainingCourse(index) }
        )
        if (index < uiState.trainingCourses.lastIndex) Divider()
    }

    if (uiState.trainingCourses.isEmpty()) {
        Text(
            text = "No training courses added yet.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PrimaryButton(
            modifier = Modifier.weight(1f),
            label = "Add Course",
            onClick = viewModel::addTrainingCourse
        )
        if (uiState.trainingCourses.isNotEmpty()) {
            PrimaryButton(
                modifier = Modifier.weight(1f),
                label = "Save",
                onClick = viewModel::saveTrainingCourses
            )
        }
    }
}

@Composable
private fun TrainingCourseItem(
    course: TrainingCourseUiState,
    onUpdate: (TrainingCourseUiState) -> Unit,
    onDelete: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = course.courseName.ifEmpty { "New Course" },
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        ProfileTextField(
            label = "Course Name",
            value = course.courseName,
            onValueChange = { onUpdate(course.copy(courseName = it)) }
        )
        ProfileTextField(
            label = "Provider",
            value = course.provider,
            onValueChange = { onUpdate(course.copy(provider = it)) }
        )
        ProfileDateField(
            label = "Completed Date",
            value = course.completedDate,
            onValueChange = { onUpdate(course.copy(completedDate = it)) }
        )
        ProfileDateField(
            label = "Expiry Date",
            value = course.expiryDate,
            onValueChange = { onUpdate(course.copy(expiryDate = it)) }
        )
        ProfileTextField(
            label = "Certificate Number",
            value = course.certificateNumber,
            onValueChange = { onUpdate(course.copy(certificateNumber = it)) }
        )
        ProfileDropdownField(
            label = "Status",
            value = course.status,
            options = listOf("completed", "in_progress", "planned"),
            onValueChange = { onUpdate(course.copy(status = it)) }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Section 8 – Contact Details
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun ContactDetailsSection(
    uiState: ProfileDetailsUiState,
    viewModel: ProfileDetailsViewModel
) {
    ProfileTextField(label = "Next of Kin Name", value = uiState.nextOfKinName, onValueChange = viewModel::setNextOfKinName)
    ProfileTextField(label = "Relationship", value = uiState.nextOfKinRelationship, onValueChange = viewModel::setNextOfKinRelationship)
    ProfileTextField(label = "Phone", value = uiState.nextOfKinPhone, onValueChange = viewModel::setNextOfKinPhone, keyboardType = KeyboardType.Phone)
    SaveButton(onClick = viewModel::saveContactDetails)
}

// ─────────────────────────────────────────────────────────────────────────────
// Section 9 – Addresses
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun AddressesSection(
    uiState: ProfileDetailsUiState,
    viewModel: ProfileDetailsViewModel
) {
    uiState.addresses.forEachIndexed { index, address ->
        AddressItem(
            address = address,
            onUpdate = { viewModel.updateAddress(index, it) },
            onDelete = { viewModel.deleteAddress(index) }
        )
        if (index < uiState.addresses.lastIndex) Divider()
    }

    if (uiState.addresses.isEmpty()) {
        Text(
            text = "No addresses added yet.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PrimaryButton(
            modifier = Modifier.weight(1f),
            label = "Add Address",
            onClick = viewModel::addAddress
        )
        if (uiState.addresses.isNotEmpty()) {
            PrimaryButton(
                modifier = Modifier.weight(1f),
                label = "Save",
                onClick = viewModel::saveAddresses
            )
        }
    }
}

@Composable
private fun AddressItem(
    address: UserAddressUiState,
    onUpdate: (UserAddressUiState) -> Unit,
    onDelete: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = address.addressLine1.ifEmpty { "New Address" },
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
        ProfileDropdownField(
            label = "Address Type",
            value = address.addressType,
            options = listOf("home", "correspondence", "other"),
            onValueChange = { onUpdate(address.copy(addressType = it)) }
        )
        ProfileTextField(
            label = "Address Line 1",
            value = address.addressLine1,
            onValueChange = { onUpdate(address.copy(addressLine1 = it)) }
        )
        ProfileTextField(
            label = "Address Line 2",
            value = address.addressLine2,
            onValueChange = { onUpdate(address.copy(addressLine2 = it)) }
        )
        ProfileTextField(
            label = "City",
            value = address.city,
            onValueChange = { onUpdate(address.copy(city = it)) }
        )
        ProfileTextField(
            label = "County",
            value = address.county,
            onValueChange = { onUpdate(address.copy(county = it)) }
        )
        ProfileTextField(
            label = "Postcode",
            value = address.postcode,
            onValueChange = { onUpdate(address.copy(postcode = it)) }
        )
        ProfileTextField(
            label = "Country",
            value = address.country,
            onValueChange = { onUpdate(address.copy(country = it)) }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onUpdate(address.copy(isPrimary = !address.isPrimary)) }
        ) {
            Checkbox(
                checked = address.isPrimary,
                onCheckedChange = { onUpdate(address.copy(isPrimary = it)) }
            )
            Spacer(Modifier.width(4.dp))
            Text(text = "Primary Address", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Section 10 – Emergency Contacts
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun EmergencyContactsSection(
    uiState: ProfileDetailsUiState,
    viewModel: ProfileDetailsViewModel
) {
    uiState.emergencyContacts.forEachIndexed { index, contact ->
        EmergencyContactItem(
            contact = contact,
            onUpdate = { viewModel.updateEmergencyContact(index, it) },
            onDelete = { viewModel.deleteEmergencyContact(index) }
        )
        if (index < uiState.emergencyContacts.lastIndex) Divider()
    }

    if (uiState.emergencyContacts.isEmpty()) {
        Text(
            text = "No emergency contacts added yet.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PrimaryButton(
            modifier = Modifier.weight(1f),
            label = "Add Contact",
            onClick = viewModel::addEmergencyContact
        )
        if (uiState.emergencyContacts.isNotEmpty()) {
            PrimaryButton(
                modifier = Modifier.weight(1f),
                label = "Save",
                onClick = viewModel::saveEmergencyContacts
            )
        }
    }
}

@Composable
private fun EmergencyContactItem(
    contact: EmergencyContactUiState,
    onUpdate: (EmergencyContactUiState) -> Unit,
    onDelete: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = contact.name.ifEmpty { "New Contact" },
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
        ProfileTextField(label = "Name", value = contact.name, onValueChange = { onUpdate(contact.copy(name = it)) })
        ProfileTextField(label = "Relationship", value = contact.relationship, onValueChange = { onUpdate(contact.copy(relationship = it)) })
        ProfileTextField(label = "Phone", value = contact.phone, onValueChange = { onUpdate(contact.copy(phone = it)) }, keyboardType = KeyboardType.Phone)
        ProfileTextField(label = "Email", value = contact.email, onValueChange = { onUpdate(contact.copy(email = it)) }, keyboardType = KeyboardType.Email)
        ProfileTextField(label = "Address", value = contact.address, onValueChange = { onUpdate(contact.copy(address = it)) })
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onUpdate(contact.copy(isPrimary = !contact.isPrimary)) }
        ) {
            Checkbox(
                checked = contact.isPrimary,
                onCheckedChange = { onUpdate(contact.copy(isPrimary = it)) }
            )
            Spacer(Modifier.width(4.dp))
            Text(text = "Primary Contact", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Section 11 – Bank Details
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun BankDetailsSection(
    uiState: ProfileDetailsUiState,
    viewModel: ProfileDetailsViewModel
) {
    ProfileTextField(label = "Account Name", value = uiState.bankAccName, onValueChange = viewModel::setBankAccName)
    ProfileTextField(label = "Account Number", value = uiState.bankAccNumber, onValueChange = viewModel::setBankAccNumber, keyboardType = KeyboardType.Number)
    ProfileTextField(label = "Sort Code / Routing Number", value = uiState.bankRoutingNumber, onValueChange = viewModel::setBankRoutingNumber)
    ProfileTextField(label = "Bank Address", value = uiState.bankAddress, onValueChange = viewModel::setBankAddress)
    SaveButton(onClick = viewModel::saveBankDetails)
}

// ─────────────────────────────────────────────────────────────────────────────
// Section 12 – Vetting Details
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun VettingDetailsSection(
    uiState: ProfileDetailsUiState,
    viewModel: ProfileDetailsViewModel
) {
    ProfileDateField(label = "DBS Check Date", value = uiState.dbsCheckDate, onValueChange = viewModel::setDbsCheckDate)
    ProfileTextField(label = "DBS Certificate Number", value = uiState.dbsCertificateNumber, onValueChange = viewModel::setDbsCertificateNumber)
    ProfileTextField(label = "Reference 1 Name", value = uiState.reference1Name, onValueChange = viewModel::setReference1Name)
    ProfileTextField(label = "Reference 1 Contact", value = uiState.reference1Contact, onValueChange = viewModel::setReference1Contact)
    ProfileTextField(label = "Reference 2 Name", value = uiState.reference2Name, onValueChange = viewModel::setReference2Name)
    ProfileTextField(label = "Reference 2 Contact", value = uiState.reference2Contact, onValueChange = viewModel::setReference2Contact)
    SaveButton(onClick = viewModel::saveVettingDetails)
}

// ─────────────────────────────────────────────────────────────────────────────
// Section 13 – My Password
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun PasswordSection(
    uiState: ProfileDetailsUiState,
    viewModel: ProfileDetailsViewModel
) {
    ProfileTextField(
        label = "Current Password",
        value = uiState.currentPassword,
        onValueChange = viewModel::setCurrentPassword,
        isPassword = true
    )
    ProfileTextField(
        label = "New Password",
        value = uiState.newPassword,
        onValueChange = viewModel::setNewPassword,
        isPassword = true
    )
    ProfileTextField(
        label = "Confirm New Password",
        value = uiState.confirmPassword,
        onValueChange = viewModel::setConfirmPassword,
        isPassword = true
    )
    SaveButton(text = "Change Password", onClick = viewModel::changePassword)
}

// ─────────────────────────────────────────────────────────────────────────────
// Section 14 – Submit Resignation
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun ResignationSection(
    uiState: ProfileDetailsUiState,
    viewModel: ProfileDetailsViewModel
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (uiState.existingResignation != null) {
        val r = uiState.existingResignation
        Text(
            text = "You have already submitted a resignation request.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        ReadOnlyField(label = "Last Working Day", value = r.lastWorkingDay)
        ReadOnlyField(label = "Reason", value = r.reason)
        ReadOnlyField(label = "Status", value = r.status)
        ReadOnlyField(label = "Submitted At", value = r.submittedAt)
    } else {
        Text(
            text = "Submit a resignation request. This action cannot be undone.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        ProfileDateField(
            label = "Last Working Day",
            value = uiState.resignationLastWorkingDay,
            onValueChange = viewModel::setResignationLastWorkingDay
        )
        ProfileTextField(
            label = "Reason for Leaving",
            value = uiState.resignationReason,
            onValueChange = viewModel::setResignationReason,
            maxLines = 4
        )
        SaveButton(text = "Submit Resignation", onClick = { showConfirmDialog = true })
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Resignation") },
            text = {
                Text(
                    text = "Are you sure you want to submit your resignation? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.submitResignation()
                    }
                ) {
                    Text("Submit", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared form field helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLines: Int = 1
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        val editMode = LocalProfileEditMode.current
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            enabled = editMode,
            readOnly = readOnly,
            singleLine = maxLines == 1,
            maxLines = maxLines,
            shape = RoundedCornerShape(dimens.cornerRadiusMedium),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun ReadOnlyField(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value.ifEmpty { "—" },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}

@Composable
private fun ProfileDropdownField(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        val editMode = LocalProfileEditMode.current
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimens.inputHeight)
                    .clip(RoundedCornerShape(dimens.cornerRadiusMedium))
                    .background(if (editMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(dimens.cornerRadiusMedium))
                    .clickable(enabled = editMode) { expanded = true }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = value.ifEmpty { "Select $label" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (value.isEmpty()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(text = option, style = MaterialTheme.typography.bodyLarge)
                        },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileDateField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        val editMode = LocalProfileEditMode.current
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimens.inputHeight)
                    .clip(RoundedCornerShape(dimens.cornerRadiusMedium))
                    .background(if (editMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(dimens.cornerRadiusMedium))
                    .clickable(enabled = editMode) { showDatePicker = true }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = value.ifEmpty { "Select date" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (value.isEmpty()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .clickable(enabled = editMode) { showDatePicker = true },
                    painter = painterResource(Res.drawable.ic_calendar),
                    contentDescription = "Select Date",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            if (showDatePicker) {
                PlatformDatePicker(
                    show = showDatePicker,
                    initialDate = value,
                    onConfirm = { selected ->
                        onValueChange(selected)
                        showDatePicker = false
                    },
                    onDismiss = { showDatePicker = false }
                )
            }
        }
    }
}

@Composable
private fun SaveButton(
    text: String = "Save Changes",
    onClick: () -> Unit
) {
    if (!LocalProfileEditMode.current) return
    PrimaryButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        label = text,
        onClick = onClick
    )
}

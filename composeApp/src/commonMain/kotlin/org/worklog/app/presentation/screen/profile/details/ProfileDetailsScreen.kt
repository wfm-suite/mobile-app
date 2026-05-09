package org.worklog.app.presentation.screen.profile.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.core.platform.ImagePicker
import org.worklog.app.core.util.resizeAndCompress
import org.worklog.app.domain.model.UserInfo
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

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackBarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    when {
        uiState.isLoading -> {
            ProfileDetailsShimmerContent()
        }

        uiState.userInfo != null -> {
            ProfileDetailsScreenContent(
                userInfo = uiState.userInfo!!,
                onBackClick = { navController.navigateUp() },
                onNotificationClick = { },
                onImageClick = {
                    imagePicker.pickImage()
                },
                onFirstNameChange = viewModel::onFirstNameChange,
                onLastNameChange = viewModel::onLastNameChange,
                onDisplayNameChange = viewModel::onDisplayNameChange,
                onGenderChange = viewModel::onGenderChange,
                onDateOfBirthChange = viewModel::onDateOfBirthChange,
                onMaritalStatusChange = viewModel::onMaritalStatusChange,
                onEmailChange = viewModel::onEmailChange,
                onPhoneNumberChange = viewModel::onPhoneNumberChange,
                onSaveClick = viewModel::onSaveClick
            )
        }
    }
}

@Composable
private fun ProfileDetailsShimmerContent() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopbarWithBack(
                title = "Profile Details"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
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
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth(),
                    height = 40.dp,
                    cornerRadius = dimens.cornerRadius
                )
            }

            repeat(8) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ShimmerBox(height = 16.dp, width = 100.dp, cornerRadius = 4.dp)
                    ShimmerBox(
                        height = dimens.inputHeight,
                        cornerRadius = dimens.cornerRadiusMedium
                    )
                }
            }

            ShimmerBox(height = 50.dp, cornerRadius = 25.dp)
        }
    }
}

@Composable
fun ProfileDetailsScreenContent(
    userInfo: UserInfo,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onImageClick: () -> Unit = {},
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onDisplayNameChange: (String) -> Unit = {},
    onGenderChange: (String) -> Unit = {},
    onDateOfBirthChange: (String) -> Unit = {},
    onMaritalStatusChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPhoneNumberChange: (String) -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopbarWithBack(
                title = "Profile Details",
                onBackClick = onBackClick,
                onNotificationClick = onNotificationClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.horizontalPadding)
                .padding(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = userInfo.profilePicture,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(dimens.cornerRadiusMedium))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(dimens.cornerRadiusMedium)
                        ),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(Res.drawable.ic_user),
                    error = painterResource(Res.drawable.ic_user),
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(dimens.cornerRadius))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable { onImageClick() }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Upload Profile Picture",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }

            // First Name
            FormField(
                label = "First Name",
                value = userInfo.firstName,
                onValueChange = onFirstNameChange
            )

            // Last Name
            FormField(
                label = "Last Name",
                value = userInfo.lastName,
                onValueChange = onLastNameChange
            )

            // Display Name
            FormField(
                label = "Display Name",
                value = userInfo.displayName,
                onValueChange = onDisplayNameChange
            )

            // Gender Dropdown
            DropdownField(
                label = "Gender",
                value = userInfo.gender,
                placeholder = "Select Gender",
                options = listOf("Male", "Female", "Other"),
                onValueChange = onGenderChange
            )

            // Date of Birth
            DateField(
                label = "Date of Birth",
                value = userInfo.dateOfBirth,
                onValueChange = onDateOfBirthChange
            )

            // Marital Status Dropdown
            DropdownField(
                label = "Marital Status",
                value = userInfo.maritalStatus,
                placeholder = "Select Marital Status",
                options = listOf("Single", "Married", "Divorced", "Widowed"),
                onValueChange = onMaritalStatusChange
            )

            // Email
            FormField(
                label = "Email",
                value = userInfo.email ?: "",
                onValueChange = onEmailChange
            )

            // Display Name
            FormField(
                label = "Phone Number",
                value = userInfo.phoneNumber,
                onValueChange = onPhoneNumberChange
            )

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                label = "Save",
                onClick = onSaveClick
            )
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )
        CustomFormField(
            value = value,
            onValueChange = onValueChange,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Composable
private fun CustomFormField(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String = "",
    isError: Boolean = false,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    trailingIconVector: ImageVector? = null,
    onClick: () -> Unit = {}
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth()
            .height(dimens.inputHeight).then(
                if (onClick != {}) Modifier.clickable { onClick() } else Modifier
            ),
        shape = RoundedCornerShape(dimens.cornerRadiusMedium),
        value = value,
        enabled = enabled,
        onValueChange = onValueChange,
        singleLine = true,
        isError = isError,
        textStyle = MaterialTheme.typography.bodyLarge,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.outline
                )
            )
        },
        trailingIcon = {
            if (trailingIconVector != null) {
                Icon(
                    imageVector = trailingIconVector,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedContainerColor = containerColor,
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (containerColor == Color.Transparent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun DateField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimens.inputHeight)
                    .clip(RoundedCornerShape(dimens.cornerRadiusMedium))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(dimens.cornerRadiusMedium)
                    )
                    .clickable { showDatePicker = true },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = value.ifEmpty { label },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = if (value.isEmpty())
                            MaterialTheme.colorScheme.outline
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                )
                Icon(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .clickable { showDatePicker = true },
                    painter = painterResource(Res.drawable.ic_calendar),
                    contentDescription = "Select Date",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            if (showDatePicker) {
                PlatformDatePicker(
                    show = showDatePicker,
                    initialDate = value,
                    onConfirm = { sel ->
                        onValueChange(sel)
                        showDatePicker = false
                    },
                    onDismiss = { showDatePicker = false }
                )
            }
        }
    }
}

@Composable
private fun DropdownField(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Dropdown trigger
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimens.inputHeight)
                    .clip(RoundedCornerShape(dimens.cornerRadiusMedium))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(dimens.cornerRadiusMedium)
                    )
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = value.ifEmpty { placeholder },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = if (value.isEmpty())
                            MaterialTheme.colorScheme.outline
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Dropdown Menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge
                            )
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

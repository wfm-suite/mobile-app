package org.worklog.app.presentation.screen.password_reset

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.presentation.component.AppTopbarWithBack
import org.worklog.app.presentation.component.CustomTextField
import org.worklog.app.presentation.component.PrimaryButton
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.LocalSnackBarHostState
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.code
import worklog.composeapp.generated.resources.confirm_password
import worklog.composeapp.generated.resources.email
import worklog.composeapp.generated.resources.ic_app_banner
import worklog.composeapp.generated.resources.password
import worklog.composeapp.generated.resources.token

@Composable
fun PasswordResetScreen(
    viewModel: PasswordResetViewModel = koinViewModel()
) {
    val navController = LocalNavController.current
    val snackbarHostState = LocalSnackBarHostState.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState.hasPasswordReset) {
            navController.navigateUp()
        }
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    PasswordResetScreenContent(
        uiState = uiState,
        onBackClick = { navController.navigateUp() },
        onEmailChange = viewModel::onEmailChange,
        onTokenChange = viewModel::onTokenChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onSendEmail = viewModel::onSendEmail,
        onResetPassword = viewModel::onResetPassword
    )
}

@Composable
fun PasswordResetScreenContent(
    uiState: PasswordResetUiState,
    onBackClick: () -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onTokenChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onResetPassword: () -> Unit = {},
    onSendEmail: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
            .navigationBarsPadding(),
        topBar = {
            AppTopbarWithBack(
                title = "Reset Password",
                onBackClick = onBackClick,
                showNotification = false
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(it)
                .padding(dimens.contentPadding)
        ) {
            if (uiState.hasEmailSent) {
                PasswordResetForm(
                    token = uiState.token,
                    password = uiState.password,
                    confirmPassword = uiState.confirmPassword,
                    tokenError = uiState.tokenError,
                    passwordError = uiState.passwordError,
                    confirmPasswordError = uiState.confirmPasswordError,
                    isLoading = uiState.isLoading,
                    onTokenChange = onTokenChange,
                    onPasswordChange = onPasswordChange,
                    onConfirmPasswordChange = onConfirmPasswordChange,
                    onResetPassword = onResetPassword
                )
            } else {
                EmailSentScreen(
                    isLoading = uiState.isLoading,
                    email = uiState.email,
                    emailError = uiState.emailError,
                    onEmailChange = onEmailChange,
                    onSendEmail = onSendEmail
                )
            }
        }
    }
}

@Composable
fun PasswordResetForm(
    modifier: Modifier = Modifier,
    token: String = "",
    password: String = "",
    confirmPassword: String = "",
    tokenError: String? = null,
    passwordError: String? = null,
    confirmPasswordError: String? = null,
    isLoading: Boolean = false,
    onTokenChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onResetPassword: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_app_banner),
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "We have sent you an email to reset your password with 6 digit code in your entered email address. Please enter the code and your new password below.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(40.dp))

        CustomTextField(
            placeholder = stringResource(Res.string.code),
            value = token,
            isError = tokenError != null,
            onValueChange = onTokenChange
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomTextField(
            placeholder = stringResource(Res.string.password),
            value = password,
            isError = passwordError != null,
            onValueChange = onPasswordChange,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomTextField(
            placeholder = stringResource(Res.string.confirm_password),
            value = confirmPassword,
            isError = confirmPasswordError != null,
            onValueChange = onConfirmPasswordChange,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(38.dp))

        PrimaryButton(
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
            buttonHeight = dimens.inputHeight,
            label = "Reset Password",
            isLoading = isLoading,
            onClick = onResetPassword
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun EmailSentScreen(
    modifier: Modifier = Modifier,
    email: String = "",
    emailError: String? = null,
    onEmailChange: (String) -> Unit = {},
    onSendEmail: () -> Unit = {},
    isLoading: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(.2f))
        Image(
            painter = painterResource(Res.drawable.ic_app_banner),
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "We will send you an email to reset your password with 6 digit code in your entered email address.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(40.dp))

        CustomTextField(
            placeholder = stringResource(Res.string.email),
            value = email,
            isError = emailError != null,
            onValueChange = onEmailChange
        )

        Spacer(modifier = Modifier.height(38.dp))

        PrimaryButton(
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
            buttonHeight = dimens.inputHeight,
            label = "Send verification email",
            isLoading = isLoading,
            onClick = onSendEmail
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}
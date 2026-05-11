package org.worklog.app.presentation.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.presentation.component.CustomTextField
import org.worklog.app.presentation.component.PrimaryButton
import org.worklog.app.presentation.navigation.ScreenRoute
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.LocalSnackBarHostState
import org.worklog.app.presentation.theme.WorkLogTheme
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.change_number
import worklog.composeapp.generated.resources.copyright_text
import worklog.composeapp.generated.resources.enter_otp
import worklog.composeapp.generated.resources.ic_app_banner
import worklog.composeapp.generated.resources.log_in
import worklog.composeapp.generated.resources.login
import worklog.composeapp.generated.resources.otp_sent
import worklog.composeapp.generated.resources.phone_number
import worklog.composeapp.generated.resources.send_otp
import worklog.composeapp.generated.resources.verify

// -- email login imports (commented out, restore if needed) --
// import worklog.composeapp.generated.resources.email
// import worklog.composeapp.generated.resources.password
// import worklog.composeapp.generated.resources.forgot_password

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel()
) {
    val navController = LocalNavController.current
    val snackbarHostState = LocalSnackBarHostState.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    LaunchedEffect(uiState.userInfo) {
        uiState.userInfo?.let {
            navController.navigate(ScreenRoute.Main) {
                popUpTo(0)
            }
        }
    }

    LoginScreenContent(
        uiState = uiState,
        onPhoneChange = viewModel::onPhoneChange,
        onOtpChange = viewModel::onOtpChange,
        onSendOtp = viewModel::sendOtp,
        onVerifyOtp = viewModel::verifyOtp,
        onChangeNumber = viewModel::goBackToPhone,
    )
}

@Composable
private fun LoginScreenContent(
    uiState: LoginUiState,
    onPhoneChange: (String) -> Unit = {},
    onOtpChange: (String) -> Unit = {},
    onSendOtp: () -> Unit = {},
    onVerifyOtp: () -> Unit = {},
    onChangeNumber: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimens.contentPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(.4f))

            Image(
                painter = painterResource(Res.drawable.ic_app_banner),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(Res.string.login),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (uiState.step == LoginStep.PHONE) {
                // Step 1 — enter phone number
                CustomTextField(
                    placeholder = stringResource(Res.string.phone_number),
                    value = uiState.phone,
                    isError = uiState.phoneError != null,
                    onValueChange = onPhoneChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(38.dp))

                PrimaryButton(
                    modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
                    buttonHeight = dimens.inputHeight,
                    label = stringResource(Res.string.send_otp),
                    isLoading = uiState.isLoading,
                    onClick = onSendOtp
                )
            } else {
                // Step 2 — enter OTP
                Text(
                    text = stringResource(Res.string.otp_sent),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                CustomTextField(
                    placeholder = stringResource(Res.string.enter_otp),
                    value = uiState.otp,
                    isError = uiState.otpError != null,
                    onValueChange = onOtpChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .clickable { onChangeNumber() },
                    text = stringResource(Res.string.change_number),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(38.dp))

                PrimaryButton(
                    modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
                    buttonHeight = dimens.inputHeight,
                    label = stringResource(Res.string.verify),
                    isLoading = uiState.isLoading,
                    onClick = onVerifyOtp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(Res.string.copyright_text),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

// -- email login screen (commented out, restore if needed) --
// @Composable
// private fun LoginScreenContent(email, password, onEmailChange, onPasswordChange, onLogin, onForgotPassword) { ... }

@Composable
@Preview
fun LoginScreenPreview() {
    WorkLogTheme {
        // LoginScreenContent()
    }
}

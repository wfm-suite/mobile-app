package org.worklog.app.presentation.screen.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.presentation.component.CustomTextField
import org.worklog.app.presentation.component.PrimaryButton
import org.worklog.app.presentation.navigation.ScreenRoute
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.LocalSnackBarHostState
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.copyright_text
import worklog.composeapp.generated.resources.email
import worklog.composeapp.generated.resources.enter_otp
import worklog.composeapp.generated.resources.enter_otp_title
import worklog.composeapp.generated.resources.enter_phone_title
import worklog.composeapp.generated.resources.forgot_password
import worklog.composeapp.generated.resources.ic_forest_logo
import worklog.composeapp.generated.resources.log_in
import worklog.composeapp.generated.resources.login_with_email
import worklog.composeapp.generated.resources.login_with_phone
import worklog.composeapp.generated.resources.otp_sent_to
import worklog.composeapp.generated.resources.password
import worklog.composeapp.generated.resources.phone_number
import worklog.composeapp.generated.resources.resend_in
import worklog.composeapp.generated.resources.resend_otp
import worklog.composeapp.generated.resources.send_otp
import worklog.composeapp.generated.resources.verify

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel()
) {
    val navController = LocalNavController.current
    val snackbarHostState = LocalSnackBarHostState.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it) }
    }

    LaunchedEffect(uiState.userInfo) {
        uiState.userInfo?.let {
            navController.navigate(ScreenRoute.Main) { popUpTo(0) }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(dimens.contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(.3f))

            Image(
                painter = painterResource(Res.drawable.ic_forest_logo),
                contentDescription = null,
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tab switcher
            TabRow(
                selectedTabIndex = if (uiState.loginMethod == LoginMethod.PHONE) 0 else 1,
                modifier = Modifier.clip(RoundedCornerShape(dimens.cornerRadius))
            ) {
                Tab(
                    selected = uiState.loginMethod == LoginMethod.PHONE,
                    onClick = { viewModel.switchLoginMethod(LoginMethod.PHONE) },
                    text = { Text(stringResource(Res.string.login_with_phone)) }
                )
                Tab(
                    selected = uiState.loginMethod == LoginMethod.EMAIL,
                    onClick = { viewModel.switchLoginMethod(LoginMethod.EMAIL) },
                    text = { Text(stringResource(Res.string.login_with_email)) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedContent(
                targetState = uiState.loginMethod,
                transitionSpec = {
                    if (targetState == LoginMethod.EMAIL) {
                        (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it } + fadeOut())
                    } else {
                        (slideInHorizontally { -it } + fadeIn()) togetherWith (slideOutHorizontally { it } + fadeOut())
                    }
                }
            ) { method ->
                if (method == LoginMethod.PHONE) {
                    AnimatedContent(
                        targetState = uiState.otpSent,
                        transitionSpec = {
                            if (targetState) {
                                (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it } + fadeOut())
                            } else {
                                (slideInHorizontally { -it } + fadeIn()) togetherWith (slideOutHorizontally { it } + fadeOut())
                            }
                        }
                    ) { otpSent ->
                        if (otpSent) {
                            OtpStepContent(
                                uiState = uiState,
                                onOtpChange = viewModel::onOtpChange,
                                onVerify = viewModel::verifyOtp,
                                onResend = viewModel::resendOtp,
                                onBack = viewModel::goBackToPhone
                            )
                        } else {
                            PhoneStepContent(
                                uiState = uiState,
                                onPhoneChange = viewModel::onPhoneChange,
                                onSendOtp = viewModel::sendOtp
                            )
                        }
                    }
                } else {
                    EmailLoginContent(
                        uiState = uiState,
                        onEmailChange = viewModel::onEmailChange,
                        onPasswordChange = viewModel::onPasswordChange,
                        onLogin = viewModel::loginWithEmail,
                        onForgotPassword = { navController.navigate(ScreenRoute.PasswordReset) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(Res.string.copyright_text),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PhoneStepContent(
    uiState: LoginUiState,
    onPhoneChange: (String) -> Unit,
    onSendOtp: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.enter_phone_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "We'll send a one-time password to verify your number",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomTextField(
            placeholder = stringResource(Res.string.phone_number),
            value = uiState.phone,
            isError = uiState.phoneError != null,
            onValueChange = onPhoneChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        if (uiState.phoneError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = uiState.phoneError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
            buttonHeight = dimens.inputHeight,
            label = stringResource(Res.string.send_otp),
            isLoading = uiState.isLoading,
            onClick = onSendOtp
        )
    }
}

@Composable
private fun OtpStepContent(
    uiState: LoginUiState,
    onOtpChange: (String) -> Unit,
    onVerify: () -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onBack() }
                    .padding(4.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.enter_otp_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "${stringResource(Res.string.otp_sent_to)} ${uiState.phone}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
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

        if (uiState.otpError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = uiState.otpError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.resendCountdown > 0) {
            Text(
                text = "${stringResource(Res.string.resend_in)} ${uiState.resendCountdown}s",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        } else {
            Text(
                text = stringResource(Res.string.resend_otp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onResend() }
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
            buttonHeight = dimens.inputHeight,
            label = stringResource(Res.string.verify),
            isLoading = uiState.isLoading,
            onClick = onVerify
        )
    }
}

@Composable
private fun EmailLoginContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onForgotPassword: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomTextField(
            placeholder = stringResource(Res.string.email),
            value = uiState.email,
            isError = uiState.emailError != null,
            onValueChange = onEmailChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        if (uiState.emailError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = uiState.emailError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            placeholder = stringResource(Res.string.password),
            value = uiState.password,
            isError = uiState.passwordError != null,
            onValueChange = onPasswordChange,
            isPassword = true
        )

        if (uiState.passwordError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = uiState.passwordError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .clickable { onForgotPassword() },
            text = stringResource(Res.string.forgot_password),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
            buttonHeight = dimens.inputHeight,
            label = stringResource(Res.string.log_in),
            isLoading = uiState.isLoading,
            onClick = onLogin
        )
    }
}

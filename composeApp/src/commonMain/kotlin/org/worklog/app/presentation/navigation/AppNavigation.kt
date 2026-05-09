package org.worklog.app.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.toRoute
import org.worklog.app.presentation.screen.leave.request.LeaveRequestScreen
import org.worklog.app.presentation.screen.login.LoginScreen
import org.worklog.app.presentation.screen.main.MainScreen
import org.worklog.app.presentation.screen.onboarding.OnboardingScreen
import org.worklog.app.presentation.screen.password_reset.PasswordResetScreen
import org.worklog.app.presentation.screen.profile.details.ProfileDetailsScreen
import org.worklog.app.presentation.screen.shift.ShiftScreen
import org.worklog.app.presentation.screen.swap.RotaSwapScreen
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.LocalSnackBarHostState

@Composable
fun AppNavigation(
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    initialScreen: ScreenRoute
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .padding(
                        bottom = 20.dp
                    )
                    .windowInsetsPadding(WindowInsets.ime.add(WindowInsets.navigationBars))
            ) {
                Snackbar(
                    snackbarData = it
                )
            }
        },
    ) {
        CompositionLocalProvider(
            LocalSnackBarHostState provides snackBarHostState,
            LocalNavController provides navController
        ) {
            AppNavHost(
                navController = navController,
                initialScreen = initialScreen
            )
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    initialScreen: ScreenRoute
) {
    NavHost(
        navController = navController,
        startDestination = initialScreen
    ) {
        appNavComposable<ScreenRoute.Onboarding> {
            OnboardingScreen()
        }
        appNavComposable<ScreenRoute.Login> {
            LoginScreen()
        }
        appNavComposable<ScreenRoute.PasswordReset> {
            PasswordResetScreen()
        }
        appNavComposable<ScreenRoute.Main> {
            MainScreen()
        }
        appNavComposable<ScreenRoute.LeaveRequest> {
            val accruedHoliday = it.toRoute<ScreenRoute.LeaveRequest>().accruedHoliday
            LeaveRequestScreen(accruedHoliday = accruedHoliday)
        }
        appNavComposable<ScreenRoute.Shift> {
            val rota = it.toRoute<ScreenRoute.Shift>().rota
            ShiftScreen(rota = rota)
        }
        appNavComposable<ScreenRoute.ProfileDetail> {
            ProfileDetailsScreen()
        }
        appNavComposable<ScreenRoute.RotaSwap> {
            val rota = it.toRoute<ScreenRoute.RotaSwap>().rota
            RotaSwapScreen(rota = rota)
        }
    }
}
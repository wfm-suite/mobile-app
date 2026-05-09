package org.worklog.app.presentation.screen.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.worklog.app.presentation.component.AppBottomNavigation
import org.worklog.app.presentation.navigation.ScreenRoute
import org.worklog.app.presentation.navigation.appNavComposable
import org.worklog.app.presentation.screen.home.HomeScreen
import org.worklog.app.presentation.screen.leave.screen.LeaveScreen
import org.worklog.app.presentation.screen.message.MessageScreen
import org.worklog.app.presentation.screen.profile.ProfileScreen
import org.worklog.app.presentation.screen.rota.RotaScreen

@Composable
fun MainScreen() {
    MainScreenContent()
}

@Composable
private fun MainScreenContent() {

    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AppBottomNavigation(
                modifier = Modifier.navigationBarsPadding(),
                navController = navController
            )
        }
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = ScreenRoute.Home
        ) {
            appNavComposable<ScreenRoute.Home> {
                HomeScreen()
            }
            appNavComposable<ScreenRoute.Rota> {
                RotaScreen()
            }
            appNavComposable<ScreenRoute.Message> {
                MessageScreen()
            }
            appNavComposable<ScreenRoute.Leave> {
                LeaveScreen()
            }
            appNavComposable<ScreenRoute.Profile> {
                ProfileScreen()
            }
        }
    }
}
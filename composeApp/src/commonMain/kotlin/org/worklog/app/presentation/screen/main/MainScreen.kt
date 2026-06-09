package org.worklog.app.presentation.screen.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.worklog.app.presentation.component.AppBottomNavigation
import org.worklog.app.presentation.navigation.ScreenRoute
import org.worklog.app.presentation.navigation.appNavComposable
import org.worklog.app.presentation.screen.home.HomeScreen
import org.worklog.app.presentation.screen.leave.request.LeaveRequestScreen
import org.worklog.app.presentation.screen.leave.screen.LeaveScreen
import org.worklog.app.presentation.screen.map.MapScreen
import org.worklog.app.presentation.screen.message.MessageScreen
import org.worklog.app.presentation.screen.profile.ProfileScreen
import org.worklog.app.presentation.screen.profile.details.ProfileDetailsScreen
import org.worklog.app.presentation.screen.profile.details.ProfileSectionScreen
import org.worklog.app.presentation.screen.rota.RotaScreen
import org.worklog.app.presentation.screen.shift.ShiftScreen
import org.worklog.app.presentation.screen.notification.NotificationScreen
import org.worklog.app.presentation.screen.swap.RotaSwapScreen

import androidx.compose.runtime.CompositionLocalProvider
import org.worklog.app.presentation.theme.LocalNavController

@Composable
fun MainScreen(notificationCount: Int = 0) {
    MainScreenContent(notificationCount = notificationCount)
}

@Composable
private fun MainScreenContent(notificationCount: Int = 0) {

    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
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
                    HomeScreen(notificationCount = notificationCount)
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
                appNavComposable<ScreenRoute.Map> {
                    val route = it.toRoute<ScreenRoute.Map>()
                    MapScreen(
                        latitude = route.latitude,
                        longitude = route.longitude,
                        label = route.label
                    )
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
                appNavComposable<ScreenRoute.ProfileSection> {
                    val type = it.toRoute<ScreenRoute.ProfileSection>().type
                    ProfileSectionScreen(type = type)
                }
                appNavComposable<ScreenRoute.RotaSwap> {
                    val rota = it.toRoute<ScreenRoute.RotaSwap>().rota
                    RotaSwapScreen(rota = rota)
                }
                appNavComposable<ScreenRoute.Notifications> {
                    NotificationScreen()
                }
            }
        }
    }
}

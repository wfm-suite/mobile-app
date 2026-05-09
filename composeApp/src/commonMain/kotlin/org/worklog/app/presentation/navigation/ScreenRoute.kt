package org.worklog.app.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRoute {

    @Serializable
    data object Onboarding : ScreenRoute()

    @Serializable
    data object Login : ScreenRoute()

    @Serializable
    data object Registration : ScreenRoute()

    @Serializable
    data object PasswordReset : ScreenRoute()

    @Serializable
    data object Main : ScreenRoute()

    @Serializable
    data object Home : ScreenRoute()

    @Serializable
    data object Rota : ScreenRoute()

    @Serializable
    data object Leave : ScreenRoute()

    @Serializable
    data class LeaveRequest(
        val accruedHoliday: Int
    ) : ScreenRoute()

    @Serializable
    data class Shift(
        val rota: String
    ) : ScreenRoute()

    @Serializable
    data object Message : ScreenRoute()

    @Serializable
    data object Profile : ScreenRoute()

    @Serializable
    data object ProfileDetail : ScreenRoute()

    @Serializable
    data object Settings : ScreenRoute()

    @Serializable
    data class RotaSwap(
        val rota: String
    ) : ScreenRoute()

}
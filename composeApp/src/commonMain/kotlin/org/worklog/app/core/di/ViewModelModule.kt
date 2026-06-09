package org.worklog.app.core.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.worklog.app.presentation.app.AppViewModel
import org.worklog.app.presentation.screen.home.HomeViewModel
import org.worklog.app.presentation.screen.leave.request.holiday.HolidayRequestViewModel
import org.worklog.app.presentation.screen.leave.screen.LeaveViewModel
import org.worklog.app.presentation.screen.login.LoginViewModel
import org.worklog.app.presentation.screen.message.MessageViewModel
import org.worklog.app.presentation.screen.onboarding.OnboardingViewModel
import org.worklog.app.presentation.screen.password_reset.PasswordResetViewModel
import org.worklog.app.presentation.screen.profile.ProfileViewModel
import org.worklog.app.presentation.screen.profile.details.ProfileDetailsViewModel
import org.worklog.app.presentation.screen.rota.my_team.MyTeamViewModel
import org.worklog.app.presentation.screen.rota.time_card.TimeCardViewModel
import org.worklog.app.presentation.screen.shift.ShiftViewModel
import org.worklog.app.presentation.screen.notification.NotificationViewModel
import org.worklog.app.presentation.screen.swap.RotaSwapViewModel

val viewModelModule = module {
    viewModel { AppViewModel(get(), get(), get(), get(), get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { MyTeamViewModel(get(), get(), get(), get()) }
    viewModel { LeaveViewModel(get(), get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { ShiftViewModel(get(), get(), get(), get()) }
    viewModel { HolidayRequestViewModel(get(), get(), get(), get()) }
    viewModel { ProfileDetailsViewModel(get(), get(), get()) }
    viewModel { MessageViewModel(get(), get()) }
    viewModel { PasswordResetViewModel(get()) }
    viewModel { RotaSwapViewModel(get(), get(), get(), get()) }
    viewModel { TimeCardViewModel(get()) }
    viewModel { NotificationViewModel(get(), get(), get(), get(), get(), get()) }
}
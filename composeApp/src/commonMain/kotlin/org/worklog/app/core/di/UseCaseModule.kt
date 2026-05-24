package org.worklog.app.core.di

import org.koin.dsl.module
import org.worklog.app.domain.usecase.GetMonthlyTimeCardUseCase
import org.worklog.app.domain.usecase.leave.GetLeaveDetailsUseCase
import org.worklog.app.domain.usecase.leave.SubmitHolidayRequestUseCase
import org.worklog.app.domain.usecase.preference.AuthTokenUseCase
import org.worklog.app.domain.usecase.preference.FirstLaunchUseCase
import org.worklog.app.domain.usecase.rota.EmployeeRotaUseCase
import org.worklog.app.domain.usecase.user.GetAllEmployeesUseCase
import org.worklog.app.domain.usecase.rota.GetAuthUserRotaUseCase
import org.worklog.app.domain.usecase.rota.RotaSwapHandoverUseCase
import org.worklog.app.domain.usecase.rota.ToggleShiftUseCase
import org.worklog.app.domain.usecase.user.AuthenticationUseCase
import org.worklog.app.domain.usecase.user.GetRotaUseCase
import org.worklog.app.domain.usecase.user.PasswordResetUseCase
import org.worklog.app.domain.usecase.user.UpdateUserProfileUseCase
import org.worklog.app.domain.usecase.user.UploadProfileImageUseCase
import org.worklog.app.domain.usecase.user.UserProfileUseCase

val useCaseModule = module {
    single { AuthTokenUseCase(get()) }
    single { FirstLaunchUseCase(get()) }

    // User
    single { AuthenticationUseCase(get()) }
    single { UserProfileUseCase(get()) }
    single { UpdateUserProfileUseCase(get()) }
    single { UploadProfileImageUseCase(get()) }
    single { GetAllEmployeesUseCase(get()) }
    single { PasswordResetUseCase(get()) }

    //Rota
    single { GetRotaUseCase(get(), get()) }
    single { EmployeeRotaUseCase(get()) }
    single { GetAuthUserRotaUseCase(get()) }
    single { ToggleShiftUseCase(get(), get()) }
    single { RotaSwapHandoverUseCase(get()) }

    // Leave
    single { GetLeaveDetailsUseCase(get()) }
    single { SubmitHolidayRequestUseCase(get()) }

    // TimeCard
    single { GetMonthlyTimeCardUseCase(get()) }
}
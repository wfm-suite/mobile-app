package org.worklog.app.core.di

import org.koin.dsl.module
import org.worklog.app.domain.usecase.GetMonthlyTimeCardUseCase
import org.worklog.app.domain.usecase.notification.DeleteNotificationUseCase
import org.worklog.app.domain.usecase.notification.GetNotificationsUseCase
import org.worklog.app.domain.usecase.notification.GetUnreadCountUseCase
import org.worklog.app.domain.usecase.notification.MarkAllReadUseCase
import org.worklog.app.domain.usecase.notification.MarkNotificationReadUseCase
import org.worklog.app.domain.usecase.notification.SaveDeviceTokenUseCase
import org.worklog.app.domain.usecase.leave.GetBlockedLeaveDatesUseCase
import org.worklog.app.domain.usecase.leave.GetLeaveDetailsUseCase
import org.worklog.app.domain.usecase.leave.SubmitHolidayRequestUseCase
import org.worklog.app.domain.usecase.preference.AuthTokenUseCase
import org.worklog.app.domain.usecase.preference.FirstLaunchUseCase
import org.worklog.app.domain.usecase.profile.AddAddressUseCase
import org.worklog.app.domain.usecase.profile.AddEmergencyContactUseCase
import org.worklog.app.domain.usecase.profile.AddTrainingCourseUseCase
import org.worklog.app.domain.usecase.profile.ChangePasswordUseCase
import org.worklog.app.domain.usecase.profile.DeleteAddressUseCase
import org.worklog.app.domain.usecase.profile.DeleteEmergencyContactUseCase
import org.worklog.app.domain.usecase.profile.DeleteTrainingCourseUseCase
import org.worklog.app.domain.usecase.profile.SubmitResignationUseCase
import org.worklog.app.domain.usecase.profile.UpdateAddressUseCase
import org.worklog.app.domain.usecase.profile.UpdateEmergencyContactUseCase
import org.worklog.app.domain.usecase.profile.UpdateTrainingCourseUseCase
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
    single { GetBlockedLeaveDatesUseCase(get()) }
    single { SubmitHolidayRequestUseCase(get()) }

    // TimeCard
    single { GetMonthlyTimeCardUseCase(get()) }

    // Notifications
    single { GetNotificationsUseCase(get()) }
    single { GetUnreadCountUseCase(get()) }
    single { MarkNotificationReadUseCase(get()) }
    single { MarkAllReadUseCase(get()) }
    single { DeleteNotificationUseCase(get()) }
    single { SaveDeviceTokenUseCase(get()) }
}
package org.worklog.app.core.di

import org.koin.dsl.module
import org.worklog.app.core.notification.RefreshEvents

val appModule = module {
    includes(
        networkModule,
        localModule,
        platformModule,
        repositoryModule,
        useCaseModule,
        viewModelModule
    )

    // Single process-wide pub/sub for "FCM push arrived → refresh this screen"
    single { RefreshEvents() }
}
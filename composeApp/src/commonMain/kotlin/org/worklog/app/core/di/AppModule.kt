package org.worklog.app.core.di

import org.koin.dsl.module

val appModule = module {
    includes(
        networkModule,
        localModule,
        platformModule,
        repositoryModule,
        useCaseModule,
        viewModelModule
    )
}
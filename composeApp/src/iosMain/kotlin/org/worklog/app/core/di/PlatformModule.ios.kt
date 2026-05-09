package org.worklog.app.core.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.worklog.app.core.platform.LocationService
import org.worklog.app.core.platform.LocationServiceImpl
import org.worklog.app.core.util.AppActions
import org.worklog.app.data.source.local.JetpackDataStoreFactory

actual val platformModule = module {
    single { JetpackDataStoreFactory() }
    single { AppActions() }
    single<LocationService> { LocationServiceImpl() }
}
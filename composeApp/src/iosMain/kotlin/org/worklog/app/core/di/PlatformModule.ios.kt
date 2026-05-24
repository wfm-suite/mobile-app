package org.worklog.app.core.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.worklog.app.core.util.AppActions
import org.worklog.app.data.source.local.JetpackDataStoreFactory

import org.worklog.app.core.util.IosLocationTracker
import org.worklog.app.core.util.LocationTracker

actual val platformModule = module {
    single { JetpackDataStoreFactory() }
    single { AppActions() }
    single<LocationTracker> { IosLocationTracker() }
}
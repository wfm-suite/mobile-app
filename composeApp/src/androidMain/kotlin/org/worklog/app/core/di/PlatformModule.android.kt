package org.worklog.app.core.di

import android.content.Context
import org.koin.dsl.module
import org.worklog.app.core.util.AppActions
import org.worklog.app.data.source.local.JetpackDataStoreFactory

import org.worklog.app.core.util.AndroidLocationTracker
import org.worklog.app.core.util.LocationTracker

actual val platformModule = module {
    single { JetpackDataStoreFactory(get<Context>()) }
    single { AppActions() }
    single<LocationTracker> { AndroidLocationTracker(get()) }
}
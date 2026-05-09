package org.worklog.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.worklog.app.core.di.appModule

class WorkLogApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@WorkLogApp)
            modules(appModule)
        }
    }
}
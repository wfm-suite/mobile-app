package org.worklog.app

import android.app.Application
import com.mapbox.common.MapboxOptions
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.worklog.app.core.di.appModule

class WorkLogApp : Application() {

    override fun onCreate() {
        super.onCreate()

        MapboxOptions.accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN

        startKoin {
            androidContext(this@WorkLogApp)
            modules(appModule)
        }
    }
}

package org.worklog.app.core.di

import org.koin.dsl.module
import org.worklog.app.data.source.local.JetpackDataStoreFactory
import org.worklog.app.data.source.local.PreferenceDataSource
import org.worklog.app.data.source.local.PreferenceDataSourceImpl

val localModule = module {
    single { get<JetpackDataStoreFactory>().createDataStore() }
    single<PreferenceDataSource> { PreferenceDataSourceImpl(get()) }
}
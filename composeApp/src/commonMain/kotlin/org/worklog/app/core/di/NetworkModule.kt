package org.worklog.app.core.di

import org.koin.dsl.module
import org.worklog.app.data.source.remote.createHttpClient
import org.worklog.app.data.source.remote.ApiService
import org.worklog.app.data.source.remote.RemoteDataSource
import org.worklog.app.data.source.remote.RemoteDataSourceImpl

val networkModule = module {
    single { createHttpClient(get()) }
    single { ApiService(get()) }
    single<RemoteDataSource> { RemoteDataSourceImpl(get()) }
}
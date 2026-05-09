package org.worklog.app.core.di

import org.koin.dsl.module
import org.worklog.app.data.provider.AuthTokenProvider
import org.worklog.app.data.repository.PreferenceRepositoryImpl
import org.worklog.app.data.repository.RotaRepositoryImpl
import org.worklog.app.data.repository.UserRepositoryImpl
import org.worklog.app.domain.repository.PreferenceRepository
import org.worklog.app.domain.repository.RotaRepository
import org.worklog.app.domain.repository.UserRepository

val repositoryModule = module {
    single<PreferenceRepository> { PreferenceRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<RotaRepository> { RotaRepositoryImpl(get()) }

    //Provider
    single { AuthTokenProvider(get()) }
}
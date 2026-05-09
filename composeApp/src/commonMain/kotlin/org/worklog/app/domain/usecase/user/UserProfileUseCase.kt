package org.worklog.app.domain.usecase.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.model.UserInfo
import org.worklog.app.domain.repository.UserRepository

class UserProfileUseCase(
    private val repository: UserRepository
) {
    val getUserProfile: Flow<ResultWrapper<UserInfo>> = flow {
        val cachedUser = repository.userProfile.first()

        if (cachedUser is ResultWrapper.Success) {
            emit(cachedUser)
        } else {
            // Otherwise trigger a remote load
            emit(ResultWrapper.Loading)
            emit(repository.loadUserProfile())
        }
    }
}
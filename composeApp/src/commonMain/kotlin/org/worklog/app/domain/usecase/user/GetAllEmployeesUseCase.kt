package org.worklog.app.domain.usecase.user

import org.worklog.app.domain.repository.UserRepository

class GetAllEmployeesUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() = userRepository.getAllEmployees()
}
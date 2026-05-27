package org.worklog.app.presentation.screen.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.worklog.app.core.util.AppActions
import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.domain.usecase.user.GetAllEmployeesUseCase

class MessageViewModel(
    private val getAllEmployeesUseCase: GetAllEmployeesUseCase,
    private val appActions: AppActions,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessageUiState())
    val uiState: StateFlow<MessageUiState> = _uiState.asStateFlow()

    init {
        // Always force fresh fetch — bypasses stale cache after API fix
        loadEmployees(forceRefresh = true)
    }

    fun refreshData() {
        loadEmployees(forceRefresh = true)
    }

    private fun loadEmployees(forceRefresh: Boolean) {
        viewModelScope.launch {
            if (_uiState.value.employees.isEmpty()) {
                _uiState.update { it.copy(isLoading = true) }
            }
            when (val result = getAllEmployeesUseCase.invoke(forceRefresh)) {
                is ResultWrapper.Success -> {
                    _uiState.update {
                        it.copy(
                            employees = result.data,
                            filteredEmployees = result.data,
                            isLoading = false
                        )
                    }
                }

                is ResultWrapper.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            message = result.message
                        )
                    }
                }

                is ResultWrapper.Loading -> {
                    // Handle loading
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredEmployees = filterEmployees(query, it.employees)
            )
        }
    }

    private fun filterEmployees(query: String, employees: List<org.worklog.app.domain.model.EmployeeInfo>): List<org.worklog.app.domain.model.EmployeeInfo> {
        if (query.isBlank()) return employees
        
        val searchQuery = query.trim().lowercase()
        return employees.filter { employee ->
            employee.displayName.lowercase().contains(searchQuery) ||
            employee.email.lowercase().contains(searchQuery) ||
            employee.phone.contains(searchQuery)
        }
    }

    fun openDialer(phoneNumber: String) {
        appActions.openDialer(phoneNumber)
    }

    fun openMessageCompose(phoneNumber: String) {
        appActions.openMessageCompose(phoneNumber)
    }
}
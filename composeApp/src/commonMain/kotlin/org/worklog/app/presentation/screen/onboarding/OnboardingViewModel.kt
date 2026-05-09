package org.worklog.app.presentation.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.worklog.app.domain.usecase.preference.FirstLaunchUseCase

class OnboardingViewModel(
    private val firstLaunchUseCase: FirstLaunchUseCase
) : ViewModel() {

    fun updateFirstLaunch() {
        viewModelScope.launch {
            firstLaunchUseCase.updateFirstLaunch()
        }
    }
}
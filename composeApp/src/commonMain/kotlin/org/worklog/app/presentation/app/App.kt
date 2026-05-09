package org.worklog.app.presentation.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.presentation.navigation.AppNavigation
import org.worklog.app.presentation.navigation.ScreenRoute
import org.worklog.app.presentation.screen.home.HomeShimmerScreen
import org.worklog.app.presentation.theme.WorkLogTheme

@Composable
fun App(
    viewModel: AppViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WorkLogTheme(
        darkTheme = false
    ) {
        val navController = rememberNavController()
        val snackBarHostState = remember { SnackbarHostState() }

        when {
            uiState.showHomeShimmer -> {
                HomeShimmerScreen()
            }

            uiState.isLoading -> {
                LoadingScreen()
            }

            uiState.initialScreen != null -> {
                AppNavigation(
                    navController = navController,
                    snackBarHostState = snackBarHostState,
                    initialScreen = uiState.initialScreen ?: ScreenRoute.Login
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true)
@Composable
private fun AppPreview() {
    WorkLogTheme {
        LoadingScreen()
    }
}

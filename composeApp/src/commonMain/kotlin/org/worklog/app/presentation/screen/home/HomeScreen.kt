package org.worklog.app.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.domain.model.Rota
import org.worklog.app.presentation.component.CurrentShiftContent
import org.worklog.app.presentation.component.CustomCard
import org.worklog.app.presentation.component.MainHeaderContent
import org.worklog.app.presentation.component.UpcomingShiftCard
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.my_shifts
import worklog.composeapp.generated.resources.see_all

import org.worklog.app.presentation.component.ShimmerBox
import org.worklog.app.presentation.component.shimmerEffect
import org.worklog.app.presentation.navigation.ScreenRoute
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.LocalSnackBarHostState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val snackbarHostState = LocalSnackBarHostState.current
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    HomeScreenContent(
        isLoading = uiState.isLoading,
        currentRota = uiState.currentRota,
        isShiftStarted = uiState.isShiftStarted,
        isShiftEnabled = uiState.isShiftEnabled,
        isShiftToggling = uiState.isShiftToggling,
        upcomingShifts = uiState.rotas,
        userName = uiState.userInfo?.displayName ?: "",
        greetingText = uiState.greetingText,
        currentDate = uiState.currentDate,
        onShiftStartClick = viewModel::toggleShift,
        onUpcomingShiftClick = {
            val rotaString = Json.encodeToString(it)
            navController.navigate(ScreenRoute.RotaSwap(rotaString))
        }
    )
}

@Composable
fun HomeShimmerScreen() {
    HomeScreenContent(
        isLoading = true,
        currentRota = null,
        upcomingShifts = emptyList(),
        userName = "",
        greetingText = "",
        currentDate = ""
    )
}

@Composable
private fun HomeScreenContent(
    isLoading: Boolean,
    currentRota: Rota? = null,
    upcomingShifts: List<Rota>,
    isShiftStarted: Boolean = false,
    isShiftEnabled: Boolean = false,
    isShiftToggling: Boolean = false,
    userName: String,
    greetingText: String,
    currentDate: String,
    onShiftStartClick: () -> Unit = {},
    onUpcomingShiftClick: (Rota) -> Unit = {}
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .height(maxHeight * 0.20f)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(
                        bottomStart = dimens.cornerRadiusMedium,
                        bottomEnd = dimens.cornerRadiusMedium
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = dimens.horizontalPadding)
                .systemBarsPadding()
                .padding(bottom = dimens.bottomBarHeight)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            MainHeaderContent(
                isLoading = isLoading,
                greetingText = greetingText,
                userName = userName,
                date = currentDate,
                onNotificationClick = {}
            )
            Spacer(modifier = Modifier.height(23.dp))
            CurrentShiftContent(
                isLoading = isLoading,
                isShiftToggling = isShiftToggling,
                isEnabled = isShiftEnabled,
                isShiftStarted = isShiftStarted,
                currentRota = currentRota,
                onStartShiftClick = onShiftStartClick
            )
            Spacer(modifier = Modifier.height(23.dp))
            UpcomingShiftContent(
                isLoading = isLoading,
                upcomingShifts = upcomingShifts,
                onUpcomingShiftClick = onUpcomingShiftClick
            )
        }
    }
}

@Composable
fun UpcomingShiftContent(
    isLoading: Boolean = false,
    upcomingShifts: List<Rota>,
    onUpcomingShiftClick: (Rota) -> Unit = {}
) {
    CustomCard {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.my_shifts),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
            /*TextButton(onClick = { *//* see all *//* }) {
                Text(
                    stringResource(Res.string.see_all),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                    )
                )
            }*/
        }

        Spacer(Modifier.height(12.dp))

        if (isLoading) {
            repeat(3) {
                UpcomingShiftShimmerCard()
                Spacer(Modifier.height(12.dp))
            }
        } else {
            upcomingShifts.forEach {
                UpcomingShiftCard(shift = it, onClick = onUpcomingShiftClick)
                Spacer(Modifier.height(12.dp))
            }
        }
    }

    Spacer(modifier = Modifier.height(dimens.verticalPadding))
}

@Composable
fun UpcomingShiftShimmerCard() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBox(
            modifier = Modifier.width(60.dp),
            height = 50.dp,
            cornerRadius = dimens.cornerRadius
        )
        Spacer(modifier = Modifier.width(12.dp))
        ShimmerBox(
            modifier = Modifier.weight(1f),
            height = 50.dp,
            cornerRadius = dimens.cornerRadius
        )
    }
}
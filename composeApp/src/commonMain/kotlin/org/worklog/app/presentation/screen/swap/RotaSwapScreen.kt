package org.worklog.app.presentation.screen.swap

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.domain.model.EmployeeRota
import org.worklog.app.domain.model.Rota
import org.worklog.app.presentation.component.AppTopbarWithBack
import org.worklog.app.presentation.component.CalendarHeader
import org.worklog.app.presentation.component.CalendarLayout
import org.worklog.app.presentation.component.CustomCard
import org.worklog.app.presentation.component.PrimaryButton
import org.worklog.app.presentation.component.ShimmerBox
import org.worklog.app.presentation.component.TeamShiftCard
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.LocalSnackBarHostState
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.ic_handover
import worklog.composeapp.generated.resources.ic_swap

@Composable
fun RotaSwapScreen(
    rota: String,
    viewModel: RotaSwapViewModel = koinViewModel()
) {
    val navController = LocalNavController.current
    val snackbarHostState = LocalSnackBarHostState.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userRota = Json.decodeFromString<Rota>(rota)

    LaunchedEffect(userRota) {
        viewModel.setUserRota(userRota)
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(uiState.isRequestSent) {
        if (uiState.isRequestSent) {
            delay(500)
            navController.navigateUp()
        }
    }

    RotaSwapScreenContent(
        uiState = uiState,
        onBackClick = {
            navController.navigateUp()
        },
        onRotaActionClick = viewModel::onRotaActionClick,
        onCalendarToggle = viewModel::onCalendarToggle,
        onRotaSelected = viewModel::onRotaSelected,
        onRequestSwap = viewModel::onRequestSwap,
        onRequestHandover = viewModel::onRequestHandover
    )
}

@Composable
private fun RotaSwapScreenContent(
    uiState: RotaSwapUiState,
    onBackClick: () -> Unit,
    onRotaActionClick: (RotaSwapAction) -> Unit = {},
    onCalendarToggle: () -> Unit = {},
    onRotaSelected: (EmployeeRota) -> Unit = {},
    onRequestSwap: () -> Unit = {},
    onRequestHandover: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopbarWithBack(
                title = "My Shift",
                onBackClick = onBackClick,
                showNotification = false
            )
        }
    ) {
        CustomCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
                .padding(dimens.contentPadding)
        ) {
            Row {
                Column(
                    modifier = Modifier
                        .widthIn(min = 50.dp)
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(dimens.cornerRadius)
                        ).border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(dimens.cornerRadius)
                        ).padding(
                            vertical = 4.dp,
                            horizontal = 6.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = uiState.userRota?.date ?: "",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = uiState.userRota?.dayName ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                TeamShiftCard(
                    modifier = Modifier.weight(1f),
                    shift = "${uiState.userRota?.shiftStartTime ?: ""} - ${uiState.userRota?.shiftEndTime ?: ""}",
                    name = uiState.userInfo?.displayName + " (You)",
                    profileImage = uiState.userInfo?.profilePicture ?: ""
                )
            }

            Spacer(modifier = Modifier.height(dimens.verticalPadding))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShiftActionItem(
                    text = "Swap",
                    iconRes = Res.drawable.ic_swap,
                    isSelected = uiState.rotaAction == RotaSwapAction.SWAP,
                    onClick = { onRotaActionClick(RotaSwapAction.SWAP) }
                )

                ShiftActionItem(
                    text = "Handover",
                    iconRes = Res.drawable.ic_handover,
                    isSelected = uiState.rotaAction == RotaSwapAction.HANDOVER,
                    onClick = { onRotaActionClick(RotaSwapAction.HANDOVER) }
                )
            }

            Spacer(modifier = Modifier.height(dimens.verticalPadding))

            when {
                uiState.rotaAction == RotaSwapAction.SWAP -> {
                    RotaSwapLayout(
                        isDataLoading = uiState.isLoading,
                        isCalendarExpanded = uiState.isCalendarExpanded,
                        rotas = uiState.displayRotas,
                        selectedRota = uiState.selectedRota,
                        onCalendarToggle = onCalendarToggle,
                        onRotaSelected = onRotaSelected,
                        onRequestSwap = onRequestSwap,
                        isSwapRequesting = uiState.isSwapRequesting
                    )
                }

                uiState.rotaAction == RotaSwapAction.HANDOVER -> {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Request handover",
                        isLoading = uiState.isSwapRequesting,
                        onClick = onRequestHandover
                    )
                }
            }
        }
    }
}

@Composable
private fun RotaSwapLayout(
    isDataLoading: Boolean = false,
    isSwapRequesting: Boolean = false,
    isCalendarExpanded: Boolean = false,
    rotas: List<EmployeeRota> = emptyList(),
    selectedRota: EmployeeRota? = null,
    onCalendarToggle: () -> Unit = {},
    onRotaSelected: (EmployeeRota) -> Unit = {},
    onRequestSwap: () -> Unit = {}
) {
    selectedRota?.let {
        Row {
            Column(
                modifier = Modifier
                    .widthIn(min = 50.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(dimens.cornerRadius)
                    ).border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(dimens.cornerRadius)
                    ).padding(
                        vertical = 4.dp,
                        horizontal = 6.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = selectedRota.rota.date,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = selectedRota.rota.dayName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            TeamShiftCard(
                name = selectedRota.employee.displayName,
                profileImage = selectedRota.employee.profilePicture,
                shift = "${selectedRota.rota.shiftStartTime} - ${selectedRota.rota.shiftEndTime}"
            )
        }
        Spacer(modifier = Modifier.height(dimens.verticalPadding))
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            label = "Request swap",
            isLoading = isSwapRequesting,
            onClick = onRequestSwap
        )
    } ?: run {
        CalendarHeader(
            isCalendarExpanded = isCalendarExpanded,
            onCalendarClick = onCalendarToggle
        )
        CalendarLayout(
            isExpanded = isCalendarExpanded,
            rotas = rotas.map { it.rota }
        )
        ShiftList(
            modifier = Modifier.padding(top = dimens.innerVerticalPadding),
            isLoading = isDataLoading,
            rotas = rotas,
            onClick = onRotaSelected
        )
    }
}

@Composable
private fun ShiftActionItem(
    text: String,
    iconRes: DrawableResource,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = dimens.verticalPadding)
                .size(48.dp)
                .clip(RoundedCornerShape(dimens.cornerRadiusMedium))
                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(dimens.iconSize),
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Spacer(modifier = Modifier.size(6.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ShiftList(
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    rotas: List<EmployeeRota> = emptyList(),
    onClick: (EmployeeRota) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(dimens.innerVerticalPadding),
    ) {
        if (isLoading) {
            repeat(5) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(dimens.cornerRadius))
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(dimens.cornerRadius)
                        )
                        .padding(
                            vertical = 6.dp,
                            horizontal = 12.dp
                        )
                ) {
                    ShimmerBox(
                        modifier = Modifier.size(40.dp),
                        height = 40.dp,
                        width = 40.dp,
                        cornerRadius = 20.dp
                    )
                    Spacer(modifier = Modifier.width(dimens.innerVerticalPadding))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        ShimmerBox(height = 20.dp, width = 120.dp, cornerRadius = 4.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                        ShimmerBox(height = 14.dp, width = 80.dp, cornerRadius = 4.dp)
                    }
                }
            }
        } else {
            rotas.forEach {
                TeamShiftCard(
                    name = it.employee.displayName,
                    profileImage = it.employee.profilePicture,
                    shift = "${it.rota.shiftStartTime} - ${it.rota.shiftEndTime}",
                    onClick = { onClick(it) }
                )
            }
        }
    }
}

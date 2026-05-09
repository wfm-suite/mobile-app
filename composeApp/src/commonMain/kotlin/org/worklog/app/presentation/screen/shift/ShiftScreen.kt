package org.worklog.app.presentation.screen.shift

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.domain.model.EmployeeRota
import org.worklog.app.domain.model.Rota
import org.worklog.app.domain.model.UserInfo
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
import worklog.composeapp.generated.resources.ic_swap

@Composable
fun ShiftScreen(
    rota: String,
    viewModel: ShiftViewModel = koinViewModel()
) {
    val navController = LocalNavController.current
    val snackbarHostState = LocalSnackBarHostState.current
    val uiState by viewModel.uiState.collectAsState()

    val employeeRota = Json.decodeFromString<EmployeeRota>(rota)

    LaunchedEffect(Unit) {
        viewModel.setEmployeeRota(employeeRota)
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

    ShiftScreenContent(
        isLoading = uiState.isLoading,
        isSwapRequesting = uiState.isSwapRequesting,
        isCalendarExpanded = uiState.isCalendarExpanded,
        employeeRota = employeeRota,
        userInfo = uiState.userInfo,
        calendarRotas = uiState.baseRotas,
        rotas = uiState.displayRotas,
        selectedRota = uiState.selectedRota,
        selectedDates = if (uiState.selectedDate != null) listOf(uiState.selectedDate!!) else emptyList(),
        onBackClick = navController::navigateUp,
        onCalendarToggle = viewModel::onCalendarToggle,
        onDateSelected = viewModel::onDateSelected,
        onRotaSelected = viewModel::onRotaSelected,
        onRequestSwap = viewModel::onRequestSwap
    )
}

@Composable
fun ShiftScreenContent(
    isLoading: Boolean = false,
    isCalendarExpanded: Boolean = false,
    isSwapRequesting: Boolean = false,
    employeeRota: EmployeeRota,
    userInfo: UserInfo? = null,
    calendarRotas: List<Rota> = emptyList(),
    rotas: List<Rota> = emptyList(),
    selectedRota: Rota? = null,
    selectedDates: List<String> = emptyList(),
    onBackClick: () -> Unit = {},
    onCalendarToggle: () -> Unit = {},
    onDateSelected: (String) -> Unit = {},
    onRotaSelected: (Rota) -> Unit = {},
    onRequestSwap: () -> Unit = {}
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
                        text = employeeRota.rota.date,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = employeeRota.rota.dayName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                TeamShiftCard(
                    modifier = Modifier.weight(1f),
                    shift = "${employeeRota.rota.shiftStartTime} - ${employeeRota.rota.shiftStartTime}",
                    name = employeeRota.employee.displayName
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .padding(dimens.verticalPadding)
                        .size(48.dp)
                        .clip(shape = RoundedCornerShape(dimens.cornerRadiusMedium))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(dimens.iconSize),
                        painter = painterResource(Res.drawable.ic_swap),
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            selectedRota?.let { rota ->
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
                            text = rota.date,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = rota.dayName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    TeamShiftCard(
                        name = userInfo?.displayName ?: "",
                        profileImage = userInfo?.profilePicture ?: "",
                        shift = "${rota.shiftStartTime} - ${rota.shiftEndTime}"
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
                    selectedDays = selectedDates,
                    onDateSelected = onDateSelected,
                    rotas = calendarRotas
                )
                ShiftList(
                    modifier = Modifier.padding(top = dimens.innerVerticalPadding),
                    isLoading = isLoading,
                    userInfo = userInfo,
                    rotas = rotas,
                    onClick = onRotaSelected
                )
            }
        }
    }
}

@Composable
private fun ShiftList(
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    userInfo: UserInfo? = null,
    rotas: List<Rota> = emptyList(),
    onClick: (Rota) -> Unit = {}
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
                    name = userInfo?.displayName ?: "",
                    profileImage = userInfo?.profilePicture ?: "",
                    shift = "${it.shiftStartTime} - ${it.shiftStartTime}",
                    onClick = { onClick(it) }
                )
            }
        }
    }
}
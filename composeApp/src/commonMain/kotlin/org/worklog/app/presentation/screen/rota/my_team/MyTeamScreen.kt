package org.worklog.app.presentation.screen.rota.my_team

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.domain.model.EmployeeRota
import org.worklog.app.domain.model.UserInfo
import org.worklog.app.presentation.component.CalendarHeader
import org.worklog.app.presentation.component.CalendarLayout
import org.worklog.app.presentation.component.CustomCard
import org.worklog.app.presentation.component.CustomTabLayout
import org.worklog.app.presentation.component.ShimmerBox
import org.worklog.app.presentation.component.TeamShiftCard
import org.worklog.app.presentation.navigation.ScreenRoute
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.LocalSnackBarHostState
import org.worklog.app.presentation.theme.dimens
import kotlin.time.Clock

@Composable
fun MyTeamScreen(
    viewModel: MyTeamViewModel = koinViewModel()
) {
    val navController = LocalNavController.current
    val snackBarHostState = LocalSnackBarHostState.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    MyTeamScreenContent(
        isLoading = uiState.isLoading,
        isCalendarExpanded = uiState.isCalendarExpanded,
        userInfo = uiState.userInfo,
        calendarRotas = if (uiState.isCalendarExpanded) {
            uiState.monthlyRotas.filter { it.employee.id.toString() == uiState.userInfo?.id.toString() }
        } else {
            uiState.weeklyRotas.filter { it.employee.id.toString() == uiState.userInfo?.id.toString() }
        },
        rotas = uiState.displayRotas,
        shiftTypes = uiState.shiftTypes,
        selectedShiftType = uiState.selectedShiftStatus,
        floorNames = uiState.floorNames,
        selectedFloorName = uiState.selectedFloorName,
        selectedMonth = uiState.selectedMonth,
        selectedYear = uiState.selectedYear,
        onCalendarToggle = viewModel::onCalendarToggle,
        selectedDates = if (uiState.selectedDate != null) listOf(
            uiState.selectedDate ?: ""
        ) else emptyList(),
        onDateSelected = viewModel::onDateSelected,
        onShiftTypeSelected = viewModel::onShiftTypeSelected,
        onFloorNameSelected = viewModel::onFloorNameSelected,
        onMonthYearSelected = viewModel::onMonthYearSelected,
        onRotaClick = { rota ->
            val currentUserId = uiState.userInfo?.id
            val rotaEmployeeId = rota.employee.id.toString()

            if (currentUserId == rotaEmployeeId) {
                viewModel.showError("You can't swap with your own rota")
                return@MyTeamScreenContent
            }

            // Check if rota date is in the past
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val rotaDate = LocalDate.parse(rota.rota.fullDate)
            
            if (rotaDate < today) {
                viewModel.showError("You can't swap previous rota")
                return@MyTeamScreenContent
            }

            val rotaJson = Json.encodeToString(rota)
            viewModel.clearMessage()
            navController.navigate(ScreenRoute.Shift(rotaJson))
        }
    )
}

@Composable
private fun MyTeamScreenContent(
    isLoading: Boolean,
    isCalendarExpanded: Boolean,
    selectedDates: List<String>,
    userInfo: UserInfo?,
    calendarRotas: List<EmployeeRota>,
    rotas: List<EmployeeRota>,
    shiftTypes: List<String>,
    selectedShiftType: String?,
    floorNames: List<String>,
    selectedFloorName: String?,
    selectedMonth: Int?,
    selectedYear: Int?,
    onCalendarToggle: () -> Unit,
    onRotaClick: (EmployeeRota) -> Unit = {},
    onDateSelected: (String) -> Unit = {},
    onShiftTypeSelected: (String) -> Unit = {},
    onFloorNameSelected: (String) -> Unit = {},
    onMonthYearSelected: (Int, Int) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimens.contentPadding)
    ) {
        CustomCard(
            spaceBetween = dimens.innerVerticalPadding
        ) {
            CalendarHeader(
                isCalendarExpanded = isCalendarExpanded,
                onCalendarClick = onCalendarToggle,
                floorNames = floorNames,
                selectedFloorName = selectedFloorName,
                onFloorNameSelected = onFloorNameSelected,
                selectedMonth = selectedMonth,
                selectedYear = selectedYear,
                onMonthYearSelected = onMonthYearSelected,
                isLoading = isLoading,
                showFloorDropdown = true
            )
            CalendarLayout(
                isExpanded = isCalendarExpanded,
                selectedDays = selectedDates,
                onDateSelected = onDateSelected,
                rotas = calendarRotas.map { it.rota },
                selectedMonth = selectedMonth,
                selectedYear = selectedYear
            )
            if (isLoading) {
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    height = 40.dp, // Approximate height of the tab layout
                    cornerRadius = dimens.cornerRadiusMedium
                )
                /*ShimmerBox(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    height = 40.dp, // Approximate height of the tab layout
                    cornerRadius = dimens.cornerRadiusMedium
                )*/
            } else {
                /*CustomTabLayout(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 2.dp),
                    tabTitles = floorNames,
                    innerPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp),
                    tabPadding = PaddingValues(4.dp),
                    cardCornerRadius = dimens.cornerRadiusMedium,
                    textStyle = MaterialTheme.typography.labelSmall,
                    selectedIndex = floorNames.indexOf(selectedFloorName),
                    onTabSelected = { index ->
                        onFloorNameSelected(floorNames[index])
                    }
                )*/
                CustomTabLayout(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 2.dp),
                    tabTitles = shiftTypes,
                    innerPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp),
                    tabPadding = PaddingValues(4.dp),
                    cardCornerRadius = dimens.cornerRadiusMedium,
                    textStyle = MaterialTheme.typography.labelLarge,
                    selectedIndex = shiftTypes.indexOf(selectedShiftType),
                    onTabSelected = { index ->
                        onShiftTypeSelected(shiftTypes[index])
                    }
                )
            }
            MyTeamList(
                isLoading = isLoading,
                modifier = Modifier.padding(top = 8.dp),
                userInfo = userInfo,
                rotas = rotas,
                onClick = onRotaClick
            )
        }

        Spacer(Modifier.height(dimens.bottomBarHeight))
    }
}

/* --------------------------- LIST --------------------------- */

@Composable
private fun MyTeamList(
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    userInfo: UserInfo?,
    rotas: List<EmployeeRota> = emptyList(),
    onClick: (EmployeeRota) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimens.innerVerticalPadding)
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
                        ShimmerBox(height = 20.dp, width = 220.dp, cornerRadius = 4.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                        ShimmerBox(height = 14.dp, width = 180.dp, cornerRadius = 4.dp)
                    }
                }
            }
        } else {
            rotas.forEach {
                val isCurrentUser = userInfo?.id.toString() == it.employee.id.toString()
                TeamShiftCard(
                    shift = "${it.rota.shiftStartTime} - ${it.rota.shiftEndTime}",
                    name = it.employee.displayName + if (isCurrentUser) " (You)" else "",
                    profileImage = it.employee.profilePicture,
                    status = it.rota.status,
                    onClick = { onClick(it) }
                )
            }
        }
    }
}

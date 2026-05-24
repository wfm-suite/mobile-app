package org.worklog.app.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.domain.model.Rota
import org.worklog.app.presentation.component.CurrentShiftContent
import org.worklog.app.presentation.component.MainHeaderContent
import org.worklog.app.presentation.component.ShimmerBox
import org.worklog.app.presentation.component.UpcomingShiftCard
import org.worklog.app.presentation.navigation.ScreenRoute
import org.worklog.app.core.util.rememberOpenMapAction
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.LocalRootNavController
import org.worklog.app.presentation.theme.LocalSnackBarHostState
import org.worklog.app.presentation.theme.dimens
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val snackbarHostState = LocalSnackBarHostState.current
    val navController = LocalNavController.current
    val rootNavController = LocalRootNavController.current
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

    val openMap = rememberOpenMapAction { lat, lon ->
        viewModel.updateLocation(lat, lon)
    }

    HomeScreenContent(
        isLoading = uiState.isLoading,
        currentRota = uiState.currentRota,
        hasCurrentRota = uiState.hasCurrentRota,
        isShiftStarted = uiState.isShiftStarted,
        isShiftEnabled = uiState.isShiftEnabled,
        isShiftToggling = uiState.isShiftToggling,
        monthlyShifts = uiState.monthlyRotas,
        userName = uiState.userInfo?.displayName ?: "",
        branchName = uiState.userInfo?.branchName ?: "",
        greetingText = uiState.greetingText,
        currentDate = uiState.currentDate,
        selectedMonth = uiState.selectedMonth,
        selectedYear = uiState.selectedYear,
        rotaStartDate = uiState.rotaStartDate,
        rotaEndDate = uiState.rotaEndDate,
        onShiftStartClick = viewModel::toggleShift,
        onMapClick = openMap,
        onSeeAllClick = { navController.navigate(ScreenRoute.Rota) },
        onUpcomingShiftClick = {
            val rotaString = Json.encodeToString(it)
            rootNavController.navigate(ScreenRoute.RotaSwap(rotaString))
        }
    )
}

private fun formatRotaDate(isoDate: String): String {
    if (isoDate.isBlank()) return ""
    return try {
        val parts = isoDate.split("-")
        val year = parts[0]
        val month = parts[1].toInt()
        val day = parts[2].toInt()
        val monthName = when (month) {
            1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"
            5 -> "May"; 6 -> "Jun"; 7 -> "Jul"; 8 -> "Aug"
            9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
            else -> ""
        }
        "$day $monthName $year"
    } catch (_: Exception) {
        isoDate
    }
}

@Composable
private fun HomeScreenContent(
    isLoading: Boolean,
    currentRota: Rota? = null,
    hasCurrentRota: Boolean = false,
    monthlyShifts: List<Rota>,
    isShiftStarted: Boolean = false,
    isShiftEnabled: Boolean = false,
    isShiftToggling: Boolean = false,
    userName: String,
    branchName: String,
    greetingText: String,
    currentDate: String,
    selectedMonth: Int,
    selectedYear: Int,
    rotaStartDate: String = "",
    rotaEndDate: String = "",
    onShiftStartClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    onSeeAllClick: () -> Unit = {},
    onUpcomingShiftClick: (Rota) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

    LaunchedEffect(monthlyShifts) {
        if (monthlyShifts.isNotEmpty()) {
            val todayIndex = monthlyShifts.indexOfFirst { it.fullDate == today }
            val targetIndex = if (todayIndex >= 0) todayIndex else 0
             // offset by 1 for the one title item in the LazyColumn
            listState.scrollToItem(targetIndex + 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
    ) {
        // Fixed Header Container with Dynamic Teal Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFF007991),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = dimens.horizontalPadding)
            ) {
                MainHeaderContent(
                    greetingText = greetingText,
                    userName = userName,
                    date = currentDate,
                    onNotificationClick = {},
                    onMapClick = onMapClick
                )
                Spacer(modifier = Modifier.height(12.dp))

                CurrentShiftContent(
                    isLoading = isLoading,
                    hasCurrentRota = hasCurrentRota,
                    isShiftToggling = isShiftToggling,
                    isEnabled = isShiftEnabled,
                    isShiftStarted = isShiftStarted,
                    currentRota = currentRota,
                    branchName = branchName,
                    onStartShiftClick = onShiftStartClick,
                    onLocateMeClick = onMapClick
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "My Shifts",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        )
                        if (rotaStartDate.isNotBlank() && rotaEndDate.isNotBlank()) {
                            Text(
                                text = "${formatRotaDate(rotaStartDate)} – ${formatRotaDate(rotaEndDate)}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                    Text(
                        text = "see all",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White,
                            fontSize = 13.sp
                        ),
                        modifier = Modifier.clickable { onSeeAllClick() }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = dimens.bottomBarHeight + 32.dp
            )
        ) {
            if (isLoading && monthlyShifts.isEmpty()) {
                items(5) {
                    Box(modifier = Modifier.padding(horizontal = dimens.horizontalPadding)) {
                        UpcomingShiftShimmerCard()
                    }
                    Spacer(Modifier.height(12.dp))
                }
            } else {
                itemsIndexed(monthlyShifts) { _, rota ->
                    Box(modifier = Modifier.padding(horizontal = dimens.horizontalPadding)) {
                        UpcomingShiftCard(
                            shift = rota,
                            isToday = rota.fullDate == today,
                            onClick = onUpcomingShiftClick
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun UpcomingShiftShimmerCard() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBox(modifier = Modifier.width(60.dp), height = 54.dp, cornerRadius = 10.dp)
        Spacer(modifier = Modifier.width(16.dp))
        ShimmerBox(modifier = Modifier.weight(1f), height = 54.dp, cornerRadius = 12.dp)
    }
}

@Composable
fun HomeShimmerScreen() {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    HomeScreenContent(
        isLoading = true,
        monthlyShifts = emptyList(),
        userName = "",
        branchName = "",
        greetingText = "",
        currentDate = "",
        selectedMonth = now.month.number,
        selectedYear = now.year
    )
}

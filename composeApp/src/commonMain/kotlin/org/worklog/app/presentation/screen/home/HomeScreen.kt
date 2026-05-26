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
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.LocalRootNavController
import org.worklog.app.presentation.theme.LocalSnackBarHostState
import org.worklog.app.presentation.theme.dimens
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

// Figma tokens not in Color.kt — kept here for visual parity with Node 2129:16725
private val FigmaCardBackground = Color(0xFFF2FCFF)
private val FigmaOuterPadding = 24.dp

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val snackbarHostState = LocalSnackBarHostState.current
    val navController = LocalNavController.current
    val rootNavController = LocalRootNavController.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    val openMap = { lat: String, lon: String ->
        navController.navigate(
            ScreenRoute.Map(
                latitude = lat.toDoubleOrNull() ?: 51.5079111,
                longitude = lon.toDoubleOrNull() ?: -0.0903026,
                label = uiState.userInfo?.branchName ?: "Location"
            )
        )
    }

    HomeScreenContent(
        isLoading = uiState.isLoading,
        currentRota = uiState.currentRota,
        hasCurrentRota = uiState.hasCurrentRota,
        isShiftStarted = uiState.isShiftStarted,
        isShiftEnabled = uiState.isShiftEnabled,
        isShiftToggling = uiState.isShiftToggling,
        monthlyShifts = uiState.monthlyRotas,
        userName = uiState.userInfo?.firstName ?: "",
        branchName = uiState.userInfo?.branchName ?: "",
        userFloor = uiState.userInfo?.floor ?: "",
        greetingText = uiState.greetingText,
        currentDate = uiState.currentDate,
        selectedMonth = uiState.selectedMonth,
        selectedYear = uiState.selectedYear,
        rotaStartDate = uiState.rotaStartDate,
        rotaEndDate = uiState.rotaEndDate,
        onShiftStartClick = viewModel::toggleShift,
        onMapClick = { lat, lon -> openMap(lat, lon) },
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
    branchName: String = "",
    userFloor: String = "",
    greetingText: String,
    currentDate: String,
    selectedMonth: Int,
    selectedYear: Int,
    rotaStartDate: String = "",
    rotaEndDate: String = "",
    onShiftStartClick: () -> Unit = {},
    onMapClick: (String, String) -> Unit = { _, _ -> },
    onSeeAllClick: () -> Unit = {},
    onUpcomingShiftClick: (Rota) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
    var hasScrolledToToday by remember { mutableStateOf(false) }

    // Auto-scroll the inner LazyColumn so today's row anchors at the top.
    LaunchedEffect(monthlyShifts) {
        if (monthlyShifts.isNotEmpty() && !hasScrolledToToday) {
            val todayIndex = monthlyShifts.indexOfFirst { it.fullDate == today }
            val targetIndex = if (todayIndex >= 0) todayIndex else 0
            listState.scrollToItem(targetIndex)
            hasScrolledToToday = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Figma: Rectangle 11 — teal banner, 200dp tall, bottom corners 24dp
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
        )

        // Fixed top section + scrollable My Shifts card filling remaining height
        Column(modifier = Modifier.fillMaxSize()) {
            // Greeting row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = FigmaOuterPadding)
            ) {
                MainHeaderContent(
                    greetingText = greetingText,
                    userName = userName,
                    branchName = branchName,
                    date = currentDate,
                    onNotificationClick = {},
                    onMapClick = onMapClick
                )
            }

            // Active Shift Card wrapper
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 24.dp,
                        bottom = 12.dp,
                        start = FigmaOuterPadding,
                        end = FigmaOuterPadding
                    )
            ) {
                CurrentShiftContent(
                    isLoading = isLoading,
                    hasCurrentRota = hasCurrentRota,
                    isShiftToggling = isShiftToggling,
                    isEnabled = isShiftEnabled,
                    isShiftStarted = isShiftStarted,
                    currentRota = currentRota,
                    onStartShiftClick = onShiftStartClick,
                    onLocateMeClick = onMapClick
                )
            }

            // My Shifts Card — FIXED POSITION, fills remaining vertical space
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(
                        start = FigmaOuterPadding,
                        end = FigmaOuterPadding,
                        bottom = 12.dp
                    ),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = FigmaCardBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header row: "My Shifts" | "see all"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "My Shifts",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 20.sp,
                                    lineHeight = 28.sp
                                )
                            )
                            if (rotaStartDate.isNotBlank() && rotaEndDate.isNotBlank()) {
                                Text(
                                    text = "${formatRotaDate(rotaStartDate)} – ${formatRotaDate(rotaEndDate)}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                        Text(
                            text = "see all",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                letterSpacing = 0.4.sp
                            ),
                            modifier = Modifier.clickable { onSeeAllClick() }
                        )
                    }

                    // Shift list
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = dimens.bottomBarHeight)
                    ) {
                        if (isLoading && monthlyShifts.isEmpty()) {
                            items(5) {
                                UpcomingShiftShimmerCard()
                            }
                        } else {
                            itemsIndexed(
                                items = monthlyShifts,
                                key = { _, rota -> rota.id }
                            ) { _, rota ->
                                UpcomingShiftCard(
                                    shift = rota,
                                    userFloor = userFloor,
                                    isToday = rota.fullDate == today,
                                    onClick = onUpcomingShiftClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpcomingShiftShimmerCard() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShimmerBox(modifier = Modifier.size(50.dp), height = 50.dp, cornerRadius = 12.dp)
        ShimmerBox(modifier = Modifier.weight(1f), height = 50.dp, cornerRadius = 12.dp)
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

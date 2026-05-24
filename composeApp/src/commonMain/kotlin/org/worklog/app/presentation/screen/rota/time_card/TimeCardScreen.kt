package org.worklog.app.presentation.screen.rota.time_card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.domain.model.MonthYear
import org.worklog.app.domain.model.TimeCardDay
import org.worklog.app.presentation.component.CustomCard
import org.worklog.app.presentation.component.CustomRow
import org.worklog.app.presentation.component.DropdownSelector
import org.worklog.app.presentation.component.ShimmerBox
import org.worklog.app.presentation.theme.LocalSnackBarHostState
import org.worklog.app.presentation.theme.WorkLogTheme
import org.worklog.app.presentation.theme.dimens

@Composable
fun TimeCardScreen(
    viewModel: TimeCardViewModel = koinViewModel()
) {
    val snackBarHostState = LocalSnackBarHostState.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    TimeCardScreenContent(
        isLoading = uiState.isLoading,
        availableMonths = uiState.availableMonths,
        selectedMonthYear = uiState.selectedMonthYear,
        totalWorkDays = uiState.timeCard?.totalWorkDays ?: 0,
        totalWorkedHours = uiState.timeCard?.totalWorkedHours ?: "0.00 hrs",
        days = uiState.timeCard?.days ?: emptyList(),
        onMonthYearSelected = viewModel::onMonthYearSelected
    )
}

@Composable
private fun TimeCardScreenContent(
    isLoading: Boolean = false,
    availableMonths: List<MonthYear> = emptyList(),
    selectedMonthYear: MonthYear? = null,
    totalWorkDays: Int = 0,
    totalWorkedHours: String = "0.00 hrs",
    days: List<TimeCardDay> = emptyList(),
    onMonthYearSelected: (MonthYear) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimens.contentPadding)
    ) {
        CustomCard(spaceBetween = dimens.innerVerticalPadding) {
            TimeCardHeader(
                availableMonths = availableMonths,
                selectedMonthYear = selectedMonthYear,
                onMonthYearSelected = onMonthYearSelected
            )
            TimeCardSummary(
                totalWorkDays = totalWorkDays,
                totalWorkedHours = totalWorkedHours
            )
            
            if (isLoading) {
                TimeCardLoadingList()
            } else {
                TimeCardList(days = days)
            }
        }

        Spacer(Modifier.height(dimens.bottomBarHeight))
    }
}

/* ---------------------------- HEADER ---------------------------- */

@Composable
private fun TimeCardHeader(
    availableMonths: List<MonthYear>,
    selectedMonthYear: MonthYear?,
    onMonthYearSelected: (MonthYear) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DropdownSelector(
            items = availableMonths.map { it.displayName },
            selectedItem = selectedMonthYear?.displayName,
            onItemSelected = { displayName ->
                availableMonths.find { it.displayName == displayName }?.let {
                    onMonthYearSelected(it)
                }
            }
        )

        Spacer(Modifier.weight(1f))
    }
}

/* ---------------------------- SUMMARY ---------------------------- */

@Composable
private fun TimeCardSummary(
    totalWorkDays: Int,
    totalWorkedHours: String
) {
    CustomRow(horizontalArrangement = Arrangement.SpaceBetween) {
        SummaryColumn(
            title = "Total Time",
            value = "$totalWorkDays Days",
            titleStyle = MaterialTheme.typography.titleMedium,
            valueStyle = MaterialTheme.typography.labelSmall
        )

        Text(
            text = totalWorkedHours,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun SummaryColumn(
    title: String,
    value: String,
    titleStyle: TextStyle,
    valueStyle: TextStyle
) {
    Column {
        Text(title, style = titleStyle)
        Text(value, style = valueStyle)
    }
}

/* ---------------------------- LIST ---------------------------- */

@Composable
private fun TimeCardList(days: List<TimeCardDay>) {
    if (days.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No timecard data available",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    } else {
        days.forEach { day ->
            TimeCardItem(
                date = formatDate(day.date),
                totalHours = day.workedHours,
                checkInTime = day.checkIn ?: "--:--",
                breakCount = day.breakCount.toString(),
                breakTime = formatBreakTime(day.breakMinutes),
                checkoutTime = day.checkOut ?: "--:--"
            )
        }
    }
}

@Composable
private fun TimeCardLoadingList() {
    repeat(5) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            ShimmerBox(
                modifier = Modifier.width(3.dp).fillMaxHeight(),
                height = 60.dp,
                width = 3.dp,
                cornerRadius = 3.dp
            )
            Spacer(modifier = Modifier.width(dimens.innerVerticalPadding))
            Column(modifier = Modifier.weight(1f)) {
                ShimmerBox(height = 20.dp, width = 200.dp, cornerRadius = 4.dp)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ShimmerBox(height = 16.dp, width = 60.dp, cornerRadius = 4.dp)
                    ShimmerBox(height = 16.dp, width = 60.dp, cornerRadius = 4.dp)
                    ShimmerBox(height = 16.dp, width = 60.dp, cornerRadius = 4.dp)
                }
            }
        }
        Spacer(Modifier.height(dimens.innerVerticalPadding))
    }
}

/* ---------------------------- ITEM ---------------------------- */

@Composable
private fun TimeCardItem(
    date: String,
    totalHours: String,
    checkInTime: String,
    breakCount: String,
    breakTime: String,
    checkoutTime: String
) {
    Row(
        modifier = Modifier.height(IntrinsicSize.Max)
    ) {
        SideIndicator()
        Spacer(modifier = Modifier.width(dimens.innerVerticalPadding))

        Column(modifier = Modifier.weight(1f)) {
            TimeCardHeaderRow(
                date = date,
                totalHours = totalHours
            )
            Spacer(Modifier.height(6.dp))
            TimeCardDetailsRow(
                checkInTime = checkInTime,
                breakCount = breakCount,
                breakTime = breakTime,
                checkoutTime = checkoutTime
            )
        }
    }
}

@Composable
private fun SideIndicator() {
    Box(
        modifier = Modifier
            .width(3.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.primary)
    )
}

@Composable
private fun TimeCardHeaderRow(
    date: String,
    totalHours: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = totalHours,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun TimeCardDetailsRow(
    checkInTime: String,
    breakCount: String,
    breakTime: String,
    checkoutTime: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DetailColumn(
            label = "Check in",
            value = checkInTime,
            labelStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )

        DetailColumn(
            label = "$breakCount Break",
            value = breakTime,
            labelStyle = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            valueStyle = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )

        DetailColumn(
            label = "Check out",
            value = checkoutTime,
            labelStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun DetailColumn(
    label: String,
    value: String,
    labelStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    valueStyle: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Column {
        Text(
            text = label,
            style = labelStyle
        )
        Text(
            text = value,
            style = valueStyle
        )
    }
}

/* ---------------------------- HELPERS ---------------------------- */

private fun formatDate(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        val dayOfWeek = when (date.dayOfWeek.name) {
            "MONDAY" -> "Mon"
            "TUESDAY" -> "Tue"
            "WEDNESDAY" -> "Wed"
            "THURSDAY" -> "Thu"
            "FRIDAY" -> "Fri"
            "SATURDAY" -> "Sat"
            "SUNDAY" -> "Sun"
            else -> ""
        }
        val month = when (date.month.number) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> ""
        }
        "$dayOfWeek ${date.dayOfMonth.toString().padStart(2, '0')}, $month ${date.year}"
    } catch (e: Exception) {
        dateString
    }
}

private fun formatBreakTime(minutes: Int): String {
    return if (minutes == 0) {
        "0 mins"
    } else {
        "$minutes mins"
    }
}

@Preview
@Composable
private fun TimeCardScreenPreview() {
    WorkLogTheme {
        TimeCardScreenContent()
    }
}
package org.worklog.app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.ic_calendar
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun CalendarHeader(
    isCalendarExpanded: Boolean,
    onCalendarClick: () -> Unit = {},
    floorNames: List<String> = emptyList(),
    selectedFloorName: String? = null,
    onFloorNameSelected: (String) -> Unit = {},
    selectedMonth: Int? = null,
    selectedYear: Int? = null,
    onMonthYearSelected: (Int, Int) -> Unit = { _, _ -> },
    isLoading: Boolean = false,
    showFloorDropdown: Boolean = false
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    
    // Generate previous 12 months
    val monthYearOptions = remember {
        buildList {
            var currentMonth = today.month.number
            var currentYear = today.year
            
            repeat(12) {
                add(MonthYearOption(currentMonth, currentYear))
                currentMonth--
                if (currentMonth < 1) {
                    currentMonth = 12
                    currentYear--
                }
            }
        }
    }
    
    val currentMonthYear = selectedMonth?.let { month ->
        selectedYear?.let { year ->
            val monthName = getMonthName(month)
            "$monthName $year"
        }
    } ?: run {
        val monthName = getMonthName(today.month.number)
        "$monthName ${today.year}"
    }
    
    val monthYearLabels = monthYearOptions.map { option ->
        "${getMonthName(option.month)} ${option.year}"
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        DropdownSelector(
            modifier = Modifier.wrapContentWidth(),
            items = monthYearLabels,
            selectedItem = currentMonthYear,
            onItemSelected = { selected ->
                val index = monthYearLabels.indexOf(selected)
                if (index >= 0) {
                    val option = monthYearOptions[index]
                    onMonthYearSelected(option.month, option.year)
                }
            }
        )

        Spacer(Modifier.weight(1f))

        if (showFloorDropdown) {
            if (isLoading) {
                ShimmerBox(
                    modifier = Modifier.weight(1f),
                    height = 28.dp,
                    cornerRadius = dimens.cornerRadiusMedium
                )
            } else {
                DropdownSelector(
                    modifier = Modifier.wrapContentWidth(),
                    items = floorNames,
                    selectedItem = selectedFloorName,
                    onItemSelected = onFloorNameSelected
                )
            }
        }

        Spacer(Modifier.width(dimens.spaceBetween))

        CurrentDayDisplay(day = today.day.toString())

        Spacer(Modifier.width(dimens.spaceBetween))

        HeaderIconButton(
            isActivated = isCalendarExpanded,
            icon = Res.drawable.ic_calendar,
            onClick = onCalendarClick
        )
    }
}

private data class MonthYearOption(
    val month: Int,
    val year: Int
)

private fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"
        else -> "Unknown"
    }
}

@Composable
private fun CurrentDayDisplay(day: String) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .size(dimens.vectorImageSize) // Set size to match HeaderIconButton
            .padding(3.dp) // Adjust internal padding to fit text
            .background(
                color = primaryColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(dimens.cornerRadiusSmall)
            )
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.bodyLarge.copy( // Use bodyLarge for better fit
                color = primaryColor,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun HeaderIconButton(
    isActivated: Boolean = false,
    icon: DrawableResource,
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier.size(dimens.vectorImageSize),
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.padding(5.dp),
            tint = if (isActivated) MaterialTheme.colorScheme.primary else Color.Unspecified
        )
    }
}
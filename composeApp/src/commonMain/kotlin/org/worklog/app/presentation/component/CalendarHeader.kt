package org.worklog.app.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.ic_calendar
import worklog.composeapp.generated.resources.ic_filter
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun CalendarHeader(
    isCalendarExpanded: Boolean,
    showCalendarIcon: Boolean = true,
    onCalendarClick: () -> Unit = {},
    floorNames: List<String> = emptyList(),
    selectedFloorName: String? = null,
    onFloorNameSelected: (String) -> Unit = {},
    selectedMonth: Int? = null,
    selectedYear: Int? = null,
    onMonthYearSelected: (Int, Int) -> Unit = { _, _ -> },
    isLoading: Boolean = false,
    showFloorDropdown: Boolean = false,
    // Figma: filter icon shown right of the calendar icon (mage:filter)
    showFilterIcon: Boolean = false,
    onFilterClick: () -> Unit = {}
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

        // Figma: calendar icon (toggle expand/collapse)
        if (showCalendarIcon) {
            HeaderIconButton(
                isActivated = isCalendarExpanded,
                icon = Res.drawable.ic_calendar,
                onClick = onCalendarClick
            )
        }

        // Figma: filter/settings icon — shown on My Team screen (mage:filter node 2305:793)
        if (showFilterIcon) {
            Spacer(Modifier.width(dimens.spaceBetween))
            HeaderIconButton(
                isActivated = false,
                icon = Res.drawable.ic_filter,
                onClick = onFilterClick
            )
        }
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
package org.worklog.app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.ic_arrow_down
import worklog.composeapp.generated.resources.ic_calendar
import worklog.composeapp.generated.resources.ic_filter
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
    isLoading: Boolean = false,
    showFloorDropdown: Boolean = false
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val monthName = when (today.month) {
        Month.JANUARY -> "January"
        Month.FEBRUARY -> "February"
        Month.MARCH -> "March"
        Month.APRIL -> "April"
        Month.MAY -> "May"
        Month.JUNE -> "June"
        Month.JULY -> "July"
        Month.AUGUST -> "August"
        Month.SEPTEMBER -> "September"
        Month.OCTOBER -> "October"
        Month.NOVEMBER -> "November"
        Month.DECEMBER -> "December"
    }
    val currentMonthYear = "$monthName ${today.year}"
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        MonthSelector(
            month = currentMonthYear,
            onClick = {}
        )

        Spacer(Modifier.weight(1f))

        if (showFloorDropdown) {
            if (isLoading) {
                ShimmerBox(
                    modifier = Modifier.weight(1f),
                    height = 28.dp, // Approximate height of the tab layout
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

        /*Spacer(Modifier.width(dimens.spaceBetween))

        HeaderIconButton(
            icon = Res.drawable.ic_filter,
            onClick = {},
        )*/
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
private fun MonthSelector(
    month: String,
    onClick: () -> Unit
) {
    Row(
        //modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = month,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.secondary
            )
        )

        Spacer(Modifier.width(5.dp))

        Icon(
            painter = painterResource(Res.drawable.ic_arrow_down),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(10.dp)
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
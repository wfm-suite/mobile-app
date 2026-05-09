package org.worklog.app.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.worklog.app.domain.model.Rota
import org.worklog.app.presentation.model.CalendarDay
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun CalendarLayout(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    rotas: List<Rota> = emptyList(),
    selectedDays: List<String> = emptyList(),
    onDateSelected: (String) -> Unit = {}
) {
    AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        ExpandedCalendarView(
            modifier = modifier,
            rotas = rotas,
            selectedDays = selectedDays,
            onDateSelected = onDateSelected
        )
    }

    AnimatedVisibility(
        visible = !isExpanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        CollapsedCalendarView(
            modifier = modifier,
            rotas = rotas,
            selectedDays = selectedDays,
            onDateSelected = onDateSelected
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun CollapsedCalendarView(
    modifier: Modifier = Modifier,
    rotas: List<Rota>,
    selectedDays: List<String>,
    onDateSelected: (String) -> Unit = {}
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val startOfWeek = today.minus(today.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)

    val rotaMap = remember(rotas) {
        rotas.associateBy { it.fullDate }
    }

    val weekDays = (0..6).map { offset ->
        val date = startOfWeek.plus(offset, DateTimeUnit.DAY)
        val fullDate = date.toString()

        CalendarDay(
            fullDate = fullDate,
            dayNumber = date.day.toString(),
            dayName = date.dayOfWeek.name.take(3),
            shift = rotaMap[fullDate]?.shiftStatus ?: "OFF"
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        weekDays.forEach { day ->
            DateCellItem(
                modifier = Modifier.weight(1f),
                dayName = day.dayName,
                date = day.dayNumber,
                shift = day.shift,
                isSelected = day.fullDate in selectedDays,
                onClick = { onDateSelected(day.fullDate) }
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun ExpandedCalendarView(
    modifier: Modifier = Modifier,
    rotas: List<Rota>,
    selectedDays: List<String>,
    onDateSelected: (String) -> Unit = {}
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val rotaMap = remember(rotas) {
        rotas.associateBy { it.fullDate }
    }

    val calendarWeeks = generateMonthCalendar(today, rotaMap)

    Column(modifier = modifier.fillMaxWidth()) {

        // Header
        listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT").let { days ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                days.forEach {
                    Text(
                        modifier = Modifier.weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(vertical = 4.dp),
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        calendarWeeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                week.forEach { day ->
                    DateCellItem(
                        modifier = Modifier.weight(1f),
                        dayName = null,
                        date = day.dayNumber,
                        shift = day.shift,
                        isEnabled = day.isCurrentMonth,
                        isSelected = day.fullDate in selectedDays,
                        onClick = { onDateSelected(day.fullDate) }
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
        }
    }
}

private fun generateMonthCalendar(
    currentDate: LocalDate,
    rotaMap: Map<String, Rota>
): List<List<CalendarDay>> {

    val firstDayOfMonth = LocalDate(currentDate.year, currentDate.month, 1)

    // Sunday-based calendar offset
    // If 1st is Monday (1), offset is 1. If 1st is Sunday (7), offset is 0.
    val startOffset = firstDayOfMonth.dayOfWeek.isoDayNumber % 7
    val startDate = firstDayOfMonth.minus(startOffset, DateTimeUnit.DAY)

    // Strictly generate 35 days (7 days * 5 weeks)
    val totalDaysToShow = 35
    val days = (0 until totalDaysToShow).map { offset ->
        val date = startDate.plus(offset, DateTimeUnit.DAY)
        val fullDate = date.toString()

        CalendarDay(
            fullDate = fullDate,
            dayNumber = date.day.toString(), // Use dayOfMonth for clarity
            shift = rotaMap[fullDate]?.shiftStatus ?: "OFF",
            isCurrentMonth = date.month == currentDate.month
        )
    }

    return days.chunked(7)
}
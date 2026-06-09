package org.worklog.app.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.worklog.app.domain.model.Rota
import org.worklog.app.presentation.model.CalendarDay
import kotlin.time.Clock

@Composable
fun CalendarLayout(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    rotas: List<Rota> = emptyList(),
    selectedDays: List<String> = emptyList(),
    selectedMonth: Int? = null,
    selectedYear: Int? = null,
    disablePastDates: Boolean = false,
    onDateSelected: (String) -> Unit = {},
    onMonthYearSelected: (Int, Int) -> Unit = { _, _ -> },
    onVisibleMonthChanged: (Int, Int) -> Unit = { _, _ -> }
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
            selectedMonth = selectedMonth,
            selectedYear = selectedYear,
            disablePastDates = disablePastDates,
            onDateSelected = onDateSelected,
            onMonthYearSelected = onMonthYearSelected
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
            selectedMonth = selectedMonth,
            selectedYear = selectedYear,
            onDateSelected = onDateSelected,
            onVisibleMonthChanged = onVisibleMonthChanged
        )
    }
}

@Composable
private fun CollapsedCalendarView(
    modifier: Modifier = Modifier,
    rotas: List<Rota>,
    selectedDays: List<String>,
    selectedMonth: Int? = null,
    selectedYear: Int? = null,
    onDateSelected: (String) -> Unit = {},
    onVisibleMonthChanged: (Int, Int) -> Unit = { _, _ -> }
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val todayStr = today.toString()
    val rotaMap = remember(rotas) { rotas.associateBy { it.fullDate } }
    val monthsWithRota = remember(rotaMap) { rotaMap.keys.map { it.take(7) }.toSet() }

    // Fixed window (12 months back, 6 forward) aligned to Mon→Sun weeks — the
    // SAME window the expanded month view uses. This lets the strip always swipe
    // across month boundaries regardless of whether a month's rota is published
    // yet; days with no rota show "-" (unpublished month) or "OFF" (published).
    val (firstMonday, lastSunday) = remember(today) {
        val start = today.minus(12, DateTimeUnit.MONTH)
        val end = today.plus(6, DateTimeUnit.MONTH)
        val mondayOfStart = start.minus(start.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)
        val sundayOfEnd = end.plus(7 - end.dayOfWeek.isoDayNumber, DateTimeUnit.DAY)
        mondayOfStart to sundayOfEnd
    }

    // Group days into weeks (each page in the pager = one Mon-Sun week)
    val weeks = remember(firstMonday, lastSunday, rotaMap, monthsWithRota) {
        // Latest date that actually has a rota = the publish frontier. Days past
        // it are "not published yet" → "-", even if the month has earlier shifts.
        val lastPublished = rotaMap.keys.maxOrNull()
        buildList {
            var weekStart = firstMonday
            while (weekStart <= lastSunday) {
                val week = (0..6).map { offset ->
                    val d = weekStart.plus(offset, DateTimeUnit.DAY)
                    val fullDate = d.toString()
                    val rota = rotaMap[fullDate]
                    val shift = when {
                        rota != null -> rota.toCellCode()
                        // beyond the last published day → not published yet
                        lastPublished != null && fullDate > lastPublished -> "-"
                        // published month, no shift this day → genuine day off
                        fullDate.take(7) in monthsWithRota -> "OFF"
                        else -> "-"
                    }
                    CalendarDay(
                        fullDate = fullDate,
                        dayNumber = d.day.toString(),
                        dayName = d.dayOfWeek.name.take(3),
                        shift = shift
                    )
                }
                add(week)
                weekStart = weekStart.plus(7, DateTimeUnit.DAY)
            }
        }
    }

    if (weeks.isEmpty()) return

    // Initial page = the week containing today (stable across data reloads, so
    // swiping never snaps back).
    val initialPage = remember(weeks.size) {
        weeks.indexOfFirst { week -> week.any { it.fullDate == todayStr } }.coerceAtLeast(0)
    }
    val pagerState = rememberPagerState(initialPage = initialPage) { weeks.size }

    // The week's Thursday (ISO) decides which month/year the week belongs to.
    fun weekMonthYear(page: Int): Pair<Int, Int>? =
        weeks.getOrNull(page)?.getOrNull(3)?.fullDate
            ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            ?.let { it.month.number to it.year }

    // Internal swipe → tell the parent to load that month's rota (mirrors the
    // expanded view). Guard prevents a reload loop with the effect below.
    LaunchedEffect(pagerState.currentPage) {
        weekMonthYear(pagerState.currentPage)?.let { (m, y) ->
            if (m != selectedMonth || y != selectedYear) onVisibleMonthChanged(m, y)
        }
    }

    // External month change (e.g. the header dropdown) → scroll to that month's
    // week. Skipped when the change came from our own swipe (months already match).
    LaunchedEffect(selectedMonth, selectedYear) {
        if (selectedMonth != null && selectedYear != null) {
            val current = weekMonthYear(pagerState.currentPage)
            if (current != (selectedMonth to selectedYear)) {
                val firstOfMonth = LocalDate(selectedYear, selectedMonth, 1).toString()
                val target = weeks.indexOfFirst { w -> w.any { it.fullDate == firstOfMonth } }
                if (target >= 0) pagerState.scrollToPage(target)
            }
        }
    }

    // Each page is ONE Mon-Sun week — snap-to-week swipe (no day-by-day scroll)
    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) { pageIndex ->
        val week = weeks[pageIndex]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            week.forEach { day ->
                DateCellItem(
                    modifier = Modifier.weight(1f),
                    dayName = day.dayName?.let { it.uppercase() },
                    date = day.dayNumber,
                    shift = day.shift,
                    isSelected = day.fullDate in selectedDays,
                    isToday = day.fullDate == todayStr,
                    onClick = { onDateSelected(day.fullDate) }
                )
            }
        }
    }
}

@Composable
private fun ExpandedCalendarView(
    modifier: Modifier = Modifier,
    rotas: List<Rota>,
    selectedDays: List<String>,
    selectedMonth: Int? = null,
    selectedYear: Int? = null,
    disablePastDates: Boolean = false,
    onDateSelected: (String) -> Unit = {},
    onMonthYearSelected: (Int, Int) -> Unit = { _, _ -> }
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val todayStr = today.toString()

    // Build a sliding window of months around today: 12 back + 6 forward.
    // Each page in the HorizontalPager renders one month.
    val months = remember(today) {
        buildList {
            var year = today.year
            var month = today.month.number - 12
            while (month < 1) { month += 12; year-- }
            repeat(19) {
                add(MonthYear(month, year))
                month++
                if (month > 12) { month = 1; year++ }
            }
        }
    }

    val activeMonth = selectedMonth ?: today.month.number
    val activeYear = selectedYear ?: today.year
    val activeIndex = remember(months, activeMonth, activeYear) {
        months.indexOfFirst { it.month == activeMonth && it.year == activeYear }
            .coerceAtLeast(0)
    }

    val pagerState = rememberPagerState(initialPage = activeIndex) { months.size }

    // External month change (dropdown) → scroll the pager to that month
    LaunchedEffect(activeIndex) {
        if (pagerState.currentPage != activeIndex) {
            pagerState.scrollToPage(activeIndex)
        }
    }

    // Internal swipe → notify parent so the right month's rota loads
    LaunchedEffect(pagerState.currentPage) {
        val page = pagerState.currentPage
        if (page in months.indices) {
            val my = months[page]
            if (my.month != activeMonth || my.year != activeYear) {
                onMonthYearSelected(my.month, my.year)
            }
        }
    }

    val rotaMap = remember(rotas) {
        rotas.associateBy { it.fullDate }
    }

    Column(modifier = modifier.fillMaxWidth()) {

        // Header
        listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN").let { days ->
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

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { pageIndex ->
            val my = months[pageIndex]
            val referenceDate = LocalDate(my.year, my.month, 1)
            val calendarWeeks = generateMonthCalendar(referenceDate, rotaMap)

            Column(modifier = Modifier.fillMaxWidth()) {
                calendarWeeks.forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        week.forEach { day ->
                            val isPast = disablePastDates &&
                                day.fullDate < todayStr
                            DateCellItem(
                                modifier = Modifier.weight(1f),
                                dayName = null,
                                date = day.dayNumber,
                                shift = day.shift,
                                isEnabled = day.isCurrentMonth && !isPast,
                                isSelected = day.fullDate in selectedDays,
                                isToday = day.fullDate == todayStr,
                                onClick = { onDateSelected(day.fullDate) }
                            )
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                }
            }
        }
    }
}

private data class MonthYear(val month: Int, val year: Int)

private fun generateMonthCalendar(
    currentDate: LocalDate,
    rotaMap: Map<String, Rota>
): List<List<CalendarDay>> {

    val firstDayOfMonth = LocalDate(currentDate.year, currentDate.month, 1)

    // Monday-based calendar offset
    // isoDayNumber returns 1 for Monday, 7 for Sunday.
    // So, if 1st is Monday (1), offset is 0. If 1st is Sunday (7), offset is 6.
    val startOffset = (firstDayOfMonth.dayOfWeek.isoDayNumber + 6) % 7
    val startDate = firstDayOfMonth.minus(startOffset, DateTimeUnit.DAY)

    // If this month has NO rota rows at all, treat it as "not yet published"
    // and render its in-month cells with a "-" placeholder instead of "OFF".
    val monthPrefix = "${currentDate.year}-" +
        currentDate.month.number.toString().padStart(2, '0')
    val monthHasAnyRota = rotaMap.keys.any { it.startsWith(monthPrefix) }

    // Latest date that actually has a rota = the publish frontier. In-month days
    // past it are "not published yet" → "-", even if the month has earlier shifts.
    val lastPublished = rotaMap.keys.maxOrNull()

    // Strictly generate 35 days (7 days * 5 weeks)
    val totalDaysToShow = 35
    val days = (0 until totalDaysToShow).map { offset ->
        val date = startDate.plus(offset, DateTimeUnit.DAY)
        val fullDate = date.toString()
        val rota = rotaMap[fullDate]
        val isCurrentMonth = date.month == currentDate.month
        val shift = when {
            rota != null -> rota.toCellCode()
            // beyond the last published day → not published yet
            isCurrentMonth && lastPublished != null && fullDate > lastPublished -> "-"
            // published month, no shift this day → genuine day off
            isCurrentMonth && monthHasAnyRota -> "OFF"
            // whole month unpublished → dash for in-month days
            isCurrentMonth -> "-"
            // adjacent-month padding cells (shown disabled)
            else -> "OFF"
        }

        CalendarDay(
            fullDate = fullDate,
            dayNumber = date.day.toString(),
            shift = shift,
            isCurrentMonth = isCurrentMonth
        )
    }

    return days.chunked(7)
}

// Calendar cells display the short code (LD, N, OFF, A/L, BAL …) rather than the
// raw shiftStatus. Falls back to OFF when nothing meaningful is set.
private fun Rota.toCellCode(): String {
    val code = shortCode.uppercase()
    return when {
        code == "AL"   -> "A/L"
        code.isBlank() -> if (isLeave) "A/L" else "OFF"
        else           -> code
    }
}
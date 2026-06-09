package org.worklog.app.presentation.screen.leave.request.holiday

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.domain.model.Rota
import org.worklog.app.presentation.component.CalendarLayout
import org.worklog.app.presentation.component.CustomCard
import org.worklog.app.presentation.component.PrimaryButton
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.LocalSnackBarHostState
import org.worklog.app.presentation.theme.dimens
import kotlin.time.ExperimentalTime

@Composable
fun HolidayRequestScreen(
    accruedHoliday: Int,
    viewModel: HolidayRequestViewModel = koinViewModel()
) {
    val navController = LocalNavController.current
    val snackBarHostState = LocalSnackBarHostState.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(accruedHoliday) {
        viewModel.setAccruedHoliday(accruedHoliday)
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearMessage()
            navController.navigateUp()
        }
    }

    HolidayRequestScreenContent(
        selectedMonth = uiState.selectedMonth,
        selectedDates = uiState.selectedDates.toList(),
        comment = uiState.comment,
        rotas = uiState.rotas,
        isLoading = uiState.isLoading,
        submitHolidayRequest = viewModel::submitHolidayRequest,
        onDateToggle = viewModel::onDateToggle,
        onCommentChange = viewModel::onCommentChange
    )
}

@Composable
private fun HolidayRequestScreenContent(
    selectedMonth: LocalDate,
    selectedDates: List<String>,
    rotas: List<Rota>,
    comment: String,
    isLoading: Boolean,
    submitHolidayRequest: () -> Unit = {},
    onDateToggle: (String) -> Unit = {},
    onCommentChange: (String) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(dimens.contentPadding)
            // Tap anywhere outside a text field to dismiss the keyboard
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Calendar
        HolidayCalendar(
            currentMonth = selectedMonth,
            rotas = rotas,
            selectedDates = selectedDates,
            onDateToggle = onDateToggle
        )

        // Total Days and Effective Days
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DayCountCard(
                modifier = Modifier.weight(1f),
                label = "Total Days",
                count = selectedDates.size
            )
            DayCountCard(
                modifier = Modifier.weight(1f),
                label = "Effective Days",
                count = 0 // This would be calculated based on business logic
            )
        }

        // Comment Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Comment",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
            TextField(
                value = comment,
                onValueChange = onCommentChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(dimens.cornerRadiusMedium))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(dimens.cornerRadiusMedium)
                    ),
                placeholder = {
                    Text(
                        text = "Write Comments Here",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }

        // Apply Button
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            label = "Apply",
            isLoading = isLoading,
            onClick = submitHolidayRequest
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun MonthSelector(
    selectedMonth: LocalDate
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Holidays",
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(dimens.cornerRadius))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${
                    selectedMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }
                } ${selectedMonth.year}",
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Select Month",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun HolidayCalendar(
    currentMonth: LocalDate,
    rotas: List<Rota>,
    selectedDates: List<String>,
    onDateToggle: (String) -> Unit
) {
    CustomCard(
        innerPadding = PaddingValues(12.dp),
    ) {
        MonthSelector(
            selectedMonth = currentMonth
        )

        Spacer(Modifier.height(16.dp))

        CalendarLayout(
            isExpanded = true,
            rotas = rotas,
            selectedDays = selectedDates,
            disablePastDates = true,
            onDateSelected = onDateToggle
        )
    }
}

@Composable
private fun DayCountCard(
    modifier: Modifier = Modifier,
    label: String,
    count: Int
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(dimens.cornerRadiusMedium))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
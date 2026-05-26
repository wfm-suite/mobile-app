package org.worklog.app.presentation.screen.leave.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.domain.model.LeaveHistory
import org.worklog.app.domain.model.LeaveSummary
import org.worklog.app.presentation.component.CustomCard
import org.worklog.app.presentation.component.CustomRow
import org.worklog.app.presentation.component.PrimaryButton
import org.worklog.app.presentation.component.ShimmerBox
import org.worklog.app.presentation.component.TopbarWithLogo
import org.worklog.app.presentation.navigation.ScreenRoute
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.WorkLogTheme
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.ic_arrow_down

@Composable
fun LeaveScreen(
    viewModel: LeaveViewModel = koinViewModel()
) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            LeaveScreenShimmerContent()
        }

        uiState.leaveSummary != null -> {
            LeaveScreenContent(
                leaveSummary = uiState.leaveSummary!!,
                onRequestLeaveClick = {
                    navController.navigate(
                        ScreenRoute.LeaveRequest(
                            accruedHoliday = uiState.leaveSummary?.accruedHolidays?.toInt() ?: 0
                        )
                    )
                }
            )
        }

        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(uiState.error!!)
            }
        }
    }
}

@Composable
private fun LeaveScreenShimmerContent() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            TopbarWithLogo(onNotificationClick = {})
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = dimens.horizontalPadding)
                .verticalScroll(rememberScrollState())
        ) {
            CustomCard(
                modifier = Modifier.padding(top = 2.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ShimmerBox(
                        modifier = Modifier.weight(1f),
                        height = 24.dp,
                        width = 150.dp,
                        cornerRadius = 4.dp
                    )
                    ShimmerBox(height = 24.dp, width = 80.dp, cornerRadius = 4.dp)
                }
                Spacer(Modifier.height(dimens.innerVerticalPadding))

                // Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f).height(130.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ShimmerBox(
                            modifier = Modifier.size(130.dp),
                            height = 130.dp,
                            cornerRadius = 65.dp
                        )
                    }

                    Spacer(Modifier.width(dimens.innerVerticalPadding))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(dimens.innerVerticalPadding)
                    ) {
                        repeat(3) {
                            ShimmerBox(height = 40.dp, cornerRadius = dimens.cornerRadius)
                        }
                    }
                }
                Spacer(Modifier.height(dimens.innerVerticalPadding * 2))

                // Accrued
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        ShimmerBox(height = 20.dp, width = 120.dp, cornerRadius = 4.dp)
                        Spacer(Modifier.height(4.dp))
                        ShimmerBox(height = 14.dp, width = 100.dp, cornerRadius = 4.dp)
                    }
                    ShimmerBox(height = 40.dp, width = 60.dp, cornerRadius = dimens.cornerRadius)
                }
                Spacer(Modifier.height(dimens.innerVerticalPadding * 2))

                // Button
                ShimmerBox(height = 50.dp, cornerRadius = 25.dp)
            }

            Spacer(modifier = Modifier.height(dimens.innerVerticalPadding * 2))

            // History
            CustomCard(
                spaceBetween = dimens.innerVerticalPadding
            ) {
                ShimmerBox(height = 24.dp, width = 150.dp, cornerRadius = 4.dp)
                repeat(3) {
                    Spacer(Modifier.height(8.dp))
                    ShimmerBox(height = 80.dp, cornerRadius = dimens.cornerRadius)
                }
            }
            Spacer(modifier = Modifier.height(dimens.bottomBarHeight * 1.5f))
        }
    }
}

@Composable
private fun LeaveScreenContent(
    leaveSummary: LeaveSummary,
    onRequestLeaveClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            TopbarWithLogo(onNotificationClick = {})
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = dimens.horizontalPadding)
                .verticalScroll(rememberScrollState())
        ) {
            LeaveOverview(
                leaveSummary = leaveSummary,
                onRequestLeaveClick = onRequestLeaveClick
            )
            Spacer(modifier = Modifier.height(dimens.innerVerticalPadding * 2))
            LeaveHistories(leaveSummary.history)
            Spacer(modifier = Modifier.height(dimens.bottomBarHeight * 1.5f))
        }
    }
}

@Composable
fun LeaveHistories(
    leaveHistories: List<LeaveHistory> = emptyList()
) {
    CustomCard(
        spaceBetween = dimens.innerVerticalPadding
    ) {
        Text(
            text = "Absence History",
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )
        leaveHistories.forEach {
            LeaveHistoryItem(it)
        }
    }
}

@Composable
private fun LeaveHistoryItem(
    leaveHistory: LeaveHistory
) {
    CustomRow(
        innerPadding = PaddingValues(dimens.innerVerticalPadding),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Absence Type",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = leaveHistory.leaveType.name,
            style = MaterialTheme.typography.bodySmall
        )
    }
    Row(
        modifier = Modifier.height(IntrinsicSize.Max)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .clip(shape = RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(dimens.innerVerticalPadding))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            HistoryInnerInfo(
                title = "From - To",
                content = "${leaveHistory.requestedFromDate} - ${leaveHistory.requestedToDate}"
            )
            HistoryInnerInfo(
                title = "Status",
                content = leaveHistory.status
            )
            HistoryInnerInfo(
                title = "Days Allowance",
                content = leaveHistory.requestedTotalDay
            )
            HistoryInnerInfo(
                title = "Comments",
                content = leaveHistory.comments ?: ""
            )
        }
    }
}

@Composable
private fun HistoryInnerInfo(
    title: String,
    content: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            modifier = Modifier.weight(1f),
            text = content,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun LeaveOverview(
    leaveSummary: LeaveSummary,
    onRequestLeaveClick: () -> Unit = {}
) {
    CustomCard(
        modifier = Modifier.padding(top = 2.dp)
    ) {
        HeaderSection(
            allowanceYear = leaveSummary.allowanceYear
        )
        Spacer(Modifier.height(dimens.innerVerticalPadding))
        LeaveDetailsSection(
            remainingLeave = leaveSummary.remainingLeave,
            totalLeave = leaveSummary.totalAllowance,
            takenLeave = leaveSummary.daysTaken,
            pendingLeave = leaveSummary.pendingLeave
        )
        Spacer(Modifier.height(dimens.innerVerticalPadding * 2))
        AccruedHolidaysSection(
            holidayCount = leaveSummary.accruedHolidays.toString()
        )
        Spacer(Modifier.height(dimens.innerVerticalPadding * 2))
        PrimaryButton(
            label = "Request for Absence",
            onClick = onRequestLeaveClick
        )
    }
}

@Composable
private fun AccruedHolidaysSection(
    holidayCount: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Accrued Holidays",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Accrued to Date",
                style = MaterialTheme.typography.labelSmall
            )
        }

        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(dimens.cornerRadius))
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .padding(horizontal = 16.dp, vertical = 9.dp),
            text = holidayCount,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        )
    }
}

@Composable
private fun HeaderSection(
    allowanceYear: String = "2025/26"
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Absence Details",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )

        Text(
            text = allowanceYear,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.secondary
            )
        )
        Spacer(Modifier.width(5.dp))

        Icon(
            painter = painterResource(Res.drawable.ic_arrow_down),
            contentDescription = null,
            modifier = Modifier.size(10.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun LeaveDetailsSection(
    remainingLeave: Float = 16f,
    totalLeave: Float = 16f,
    takenLeave: Float = 2f,
    pendingLeave: Float = 0f
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LeaveProgressCircle(
            modifier = Modifier.weight(1f),
            progress = 1 - (takenLeave / remainingLeave).coerceAtMost(1f),
            value = remainingLeave.format(),
            label = "Days"
        )

        Spacer(Modifier.width(dimens.innerVerticalPadding))

        LeaveSummaryColumn(
            modifier = Modifier.weight(1f),
            taken = takenLeave.format(),
            remaining = remainingLeave.format(),
            pending = pendingLeave,
            total = totalLeave.format()
        )
    }
}

@Composable
private fun LeaveProgressCircle(
    modifier: Modifier = Modifier,
    progress: Float,
    value: String,
    label: String
) {
    Box(
        modifier = modifier.size(130.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(130.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = Color.Transparent,
            strokeCap = StrokeCap.Round,
            strokeWidth = 12.dp
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    }
}

@Composable
private fun LeaveSummaryColumn(
    modifier: Modifier = Modifier,
    taken: String,
    remaining: String,
    pending: Float,
    total: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimens.innerVerticalPadding)
    ) {
        LeaveSummaryRow(value = taken, label = "Days taken")
        if (pending > 0) {
            LeaveSummaryRow(value = pending.format(), label = "Pending")
        } else {
            LeaveSummaryRow(value = remaining, label = "Remaining")
        }
        LeaveSummaryRow(value = total, label = "Total")
    }
}

@Composable
private fun LeaveSummaryRow(
    value: String,
    label: String
) {
    CustomRow {
        Text(
            modifier = Modifier.padding(start = dimens.spaceBetween),
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
        Spacer(Modifier.width(dimens.innerVerticalPadding))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
    }
}

@Composable
@Preview
private fun LeaveScreenPreview() {
    WorkLogTheme {
        //LeaveScreenContent()
    }
}
fun Float.format(): String {
    return if (this % 1f == 0f) {
        this.toInt().toString()
    } else {
        this.toString()
    }
}
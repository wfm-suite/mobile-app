package org.worklog.app.presentation.screen.leave.screen

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.LifecycleResumeEffect
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

    // Refresh on each resume so a newly submitted holiday request appears in Absence History
    // once the user returns from the request screen.
    LifecycleResumeEffect(Unit) {
        viewModel.refreshData()
        onPauseOrDispose { }
    }

    when {
        uiState.isLoading -> {
            LeaveScreenShimmerContent(
                onNotificationClick = { navController.navigate(ScreenRoute.Notifications) }
            )
        }

        uiState.leaveSummary != null -> {
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = viewModel::onPullToRefresh
            ) {
                LeaveScreenContent(
                    leaveSummary = uiState.leaveSummary!!,
                    onNotificationClick = { navController.navigate(ScreenRoute.Notifications) },
                    onRequestLeaveClick = {
                        navController.navigate(
                            ScreenRoute.LeaveRequest(
                                accruedHoliday = uiState.leaveSummary?.accruedHolidays?.toInt() ?: 0
                            )
                        )
                    }
                )
            }
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
private fun LeaveScreenShimmerContent(
    onNotificationClick: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            TopbarWithLogo(onNotificationClick = onNotificationClick)
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(dimens.innerVerticalPadding)
            ) {
                ShimmerBox(height = 24.dp, width = 150.dp, cornerRadius = 4.dp)
                repeat(3) {
                    CustomCard(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        cardElevation = 2.dp
                    ) {
                        ShimmerBox(height = 100.dp, cornerRadius = dimens.cornerRadius)
                    }
                }
            }
            Spacer(modifier = Modifier.height(dimens.bottomBarHeight * 1.5f))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LeaveScreenContent(
    leaveSummary: LeaveSummary,
    onNotificationClick: () -> Unit = {},
    onRequestLeaveClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            TopbarWithLogo(onNotificationClick = onNotificationClick)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = dimens.horizontalPadding),
            verticalArrangement = Arrangement.spacedBy(dimens.innerVerticalPadding),
            contentPadding = PaddingValues(bottom = dimens.bottomBarHeight * 1.5f)
        ) {
            item {
                Spacer(modifier = Modifier.height(2.dp))
                LeaveOverview(
                    leaveSummary = leaveSummary,
                    onRequestLeaveClick = onRequestLeaveClick
                )
            }

            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "Absence History",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            if (leaveSummary.history.isEmpty()) {
                item {
                    CustomCard {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            text = "No absence history found.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(
                    items = leaveSummary.history
                ) { historyItem ->
                    LeaveHistoryItem(historyItem)
                }
            }
        }
    }
}

// Map status to the app's theme tokens (same convention as TeamShiftCard / RotaStatus chips):
// accepted → primary container, rejected → error container, pending → tertiary container.
@Composable
private fun statusChipColors(status: String): Pair<Color, Color> {
    return when (status.trim().lowercase()) {
        "approved", "accepted", "approve" ->
            MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "rejected", "declined", "reject" ->
            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        "pending", "awaiting", "requested" ->
            MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        else ->
            MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }
}

// Side-bar color mirrors the Absence Overview donut segment for that status,
// so users can visually link a history row to its donut wedge.
@Composable
private fun donutSidebarColor(status: String): Color {
    return when (status.trim().lowercase()) {
        "approved", "accepted", "approve" -> SegApproved
        "pending", "awaiting", "requested" -> SegRequested
        "taken", "used" -> SegTaken
        "rejected", "declined", "reject" -> MaterialTheme.colorScheme.error
        else -> SegTaken
    }
}

@Composable
private fun LeaveHistoryItem(
    leaveHistory: LeaveHistory
) {
    val (chipBg, chipFg) = statusChipColors(leaveHistory.status)
    val sidebarColor = donutSidebarColor(leaveHistory.status)
    val statusLabel = leaveHistory.status.trim()
        .replaceFirstChar { it.uppercaseChar() }

    CustomCard(
        cardElevation = 2.dp,
        spaceBetween = dimens.innerVerticalPadding
    ) {
        // Header row: leave type name + status badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = leaveHistory.leaveType.name,
                style = MaterialTheme.typography.titleMedium
            )
            // Status chip — uses the app's theme container colors
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(chipBg)
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = statusLabel,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = chipFg
                    )
                )
            }
        }

        // Details with coloured left bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(sidebarColor)
            )
            Spacer(modifier = Modifier.width(dimens.innerVerticalPadding))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                HistoryInnerInfo(
                    title = "From",
                    content = leaveHistory.requestedFromDate
                )
                HistoryInnerInfo(
                    title = "To",
                    content = leaveHistory.requestedToDate
                )
                HistoryInnerInfo(
                    title = "Days",
                    content = leaveHistory.requestedTotalDay
                )
                if (!leaveHistory.comments.isNullOrBlank()) {
                    HistoryInnerInfo(
                        title = "Reason",
                        content = leaveHistory.comments
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryInnerInfo(
    title: String,
    content: String,
    contentColor: Color = Color.Unspecified
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
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (contentColor == Color.Unspecified)
                    MaterialTheme.colorScheme.onSurface
                else
                    contentColor
            )
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
        AbsenceDonutWithLegend(
            taken = leaveSummary.daysTaken,
            approved = leaveSummary.approvedLeave,
            remaining = leaveSummary.remainingLeave,
            requested = leaveSummary.pendingLeave,
            total = leaveSummary.totalAllowance,
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

// ─────────────────────────────────────────────────────────────────────────────
// Absence Overview donut + legend — 4 segments (Taken / Approved / Remaining /
// Requested) with center total, mirroring the design reference image.
// ─────────────────────────────────────────────────────────────────────────────

private val SegTaken     = Color(0xFF8E9A9F) // grey
private val SegApproved  = Color(0xFF2E9E73) // green
private val SegRemaining = Color(0xFF7FD4F0) // light blue
private val SegRequested = Color(0xFFF57F17) // amber (Requested/Pending)

@Composable
private fun AbsenceDonutWithLegend(
    taken: Float,
    approved: Float,
    remaining: Float,
    requested: Float,
    total: Float,
) {
    val textMeasurer = rememberTextMeasurer()
    val sum = (taken + approved + remaining + requested).coerceAtLeast(1f)
    val displayTotal = if (total > 0) total.format() else sum.format()

    val segments = listOf(
        Triple(taken,     SegTaken,     "Taken - ${taken.format()}"),
        Triple(approved,  SegApproved,  "Approved - ${approved.format()}"),
        Triple(remaining, SegRemaining, "Remaining - ${remaining.format()}"),
        Triple(requested, SegRequested, "Requested - ${requested.format()}")
    ).filter { it.first > 0f }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx       = size.width  / 2f
            val cy       = size.height / 2f
            val outerR   = minOf(cx, cy) * 0.56f
            val strokeW  = outerR * 0.38f

            // ── 1. Draw arc segments ─────────────────────────────────────────
            var startAngle = -90f
            segments.forEach { (value, color, _) ->
                val sweep = (value / sum) * 360f
                drawArc(
                    color      = color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter  = false,
                    topLeft    = Offset(cx - outerR, cy - outerR),
                    size       = Size(outerR * 2f, outerR * 2f),
                    style      = Stroke(width = strokeW, cap = StrokeCap.Butt)
                )
                startAngle += sweep
            }

            // ── 2. Draw arrow lines + labels ─────────────────────────────────
            startAngle = -90f
            segments.forEach { (value, _, label) ->
                val sweep  = (value / sum) * 360f
                val midDeg = startAngle + sweep / 2f
                val midRad = midDeg.toDouble() * PI / 180.0
                val cosA   = cos(midRad).toFloat()
                val sinA   = sin(midRad).toFloat()

                // Point on arc outer edge → outward extension
                val r0 = outerR + strokeW / 2f + 4.dp.toPx()
                val r1 = r0 + 26.dp.toPx()
                val p0 = Offset(cx + r0 * cosA, cy + r0 * sinA)
                val p1 = Offset(cx + r1 * cosA, cy + r1 * sinA)

                // Diagonal stem
                drawLine(
                    color       = Color(0xFF888888),
                    start       = p0,
                    end         = p1,
                    strokeWidth = 1.5.dp.toPx()
                )

                // Horizontal tail
                val isRight = cosA >= 0f
                val p2 = Offset(if (isRight) p1.x + 18.dp.toPx() else p1.x - 18.dp.toPx(), p1.y)
                drawLine(
                    color       = Color(0xFF888888),
                    start       = p1,
                    end         = p2,
                    strokeWidth = 1.5.dp.toPx()
                )

                // Text label
                val measured = textMeasurer.measure(label)
                val textX = if (isRight) p2.x + 4.dp.toPx()
                            else p2.x - measured.size.width - 4.dp.toPx()
                drawText(
                    textMeasurer = textMeasurer,
                    text         = label,
                    topLeft      = Offset(textX, p2.y - measured.size.height / 2f)
                )

                startAngle += sweep
            }

            // ── 3. Centre total number ────────────────────────────────────────
            val centerStyle = TextStyle(
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF333333)
            )
            val cm = textMeasurer.measure(displayTotal, style = centerStyle)
            drawText(
                textMeasurer = textMeasurer,
                text         = displayTotal,
                topLeft      = Offset(cx - cm.size.width / 2f, cy - cm.size.height / 2f),
                style        = centerStyle
            )
        }
    }
}

@Composable
private fun AbsenceDonut(
    modifier: Modifier = Modifier,
    total: Float,
    segments: List<Pair<Color, Float>>
) {
    val sum = segments.sumOf { it.second.toDouble() }.toFloat()
    val displayCenter = if (total > 0) total.format() else sum.format()
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (sum <= 0f) {
            // Empty donut — single grey ring
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = SegTaken.copy(alpha = 0.25f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 18.dp.toPx())
                )
            }
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 18.dp.toPx()
                val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                var start = -90f
                segments.forEach { (color, value) ->
                    if (value <= 0f) return@forEach
                    val sweep = (value / sum) * 360f
                    drawArc(
                        color = color,
                        startAngle = start,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )
                    start += sweep
                }
            }
        }
        Text(
            text = displayCenter,
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
private fun LegendRow(color: Color, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "$label - $value",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
private fun LeaveDetailsSection(
    remainingLeave: Float = 16f,
    totalLeave: Float = 16f,
    takenLeave: Float = 2f,
    pendingLeave: Float = 0f,
    approvedLeave: Float = 0f
) {
    val textMeasurer = rememberTextMeasurer()

    val remainingColor = Color(0xFF4FC3F7) // light blue
    val takenColor     = Color(0xFF78909C) // dark grey
    val pendingColor   = Color(0xFFF57F17) // amber
    val approvedColor  = Color(0xFF43A047) // green

    val total = (remainingLeave + takenLeave + pendingLeave + approvedLeave).coerceAtLeast(1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val outerRadius = minOf(cx, cy) * 0.60f
            val strokeW     = outerRadius * 0.42f

            val segments = listOf(
                Triple(remainingLeave / total * 360f, remainingColor, "Remaining - ${remainingLeave.format()}"),
                Triple(takenLeave     / total * 360f, takenColor,     "Taken - ${takenLeave.format()}"),
                Triple(pendingLeave   / total * 360f, pendingColor,   "Pending - ${pendingLeave.format()}"),
                Triple(approvedLeave  / total * 360f, approvedColor,  "Approved - ${approvedLeave.format()}")
            )

            var startAngle = -90f

            segments.forEach { (sweep, color, label) ->
                if (sweep > 0.5f) {
                    // Draw arc segment
                    drawArc(
                        color      = color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter  = false,
                        topLeft    = Offset(cx - outerRadius, cy - outerRadius),
                        size       = Size(outerRadius * 2f, outerRadius * 2f),
                        style      = Stroke(width = strokeW, cap = StrokeCap.Butt)
                    )

                    // Mid-angle for the arrow
                    val midDeg = startAngle + sweep / 2f
                    val midRad = midDeg.toDouble() * PI / 180.0
                    val cosA   = cos(midRad).toFloat()
                    val sinA   = sin(midRad).toFloat()

                    // Arrow start: just outside the arc
                    val r0 = outerRadius + strokeW / 2f + 5.dp.toPx()
                    val r1 = r0 + 22.dp.toPx()
                    val p0 = Offset(cx + r0 * cosA, cy + r0 * sinA)
                    val p1 = Offset(cx + r1 * cosA, cy + r1 * sinA)

                    // Diagonal stem
                    drawLine(color = Color(0xFF888888), start = p0, end = p1, strokeWidth = 1.4.dp.toPx())

                    // Horizontal tail
                    val isRight = cosA >= 0f
                    val tailLen = 14.dp.toPx()
                    val p2 = Offset(if (isRight) p1.x + tailLen else p1.x - tailLen, p1.y)
                    drawLine(color = Color(0xFF888888), start = p1, end = p2, strokeWidth = 1.4.dp.toPx())

                    // Label text
                    val measured = textMeasurer.measure(label)
                    val textX = if (isRight) p2.x + 3.dp.toPx()
                                else p2.x - measured.size.width - 3.dp.toPx()
                    val textY = p2.y - measured.size.height / 2f
                    drawText(textMeasurer = textMeasurer, text = label, topLeft = Offset(textX, textY))
                }
                startAngle += sweep
            }

            // Centre total number
            val centerStyle = TextStyle(
                fontSize   = 26.sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF333333)
            )
            val centerLabel    = totalLeave.format()
            val centerMeasured = textMeasurer.measure(centerLabel, style = centerStyle)
            drawText(
                textMeasurer = textMeasurer,
                text         = centerLabel,
                topLeft      = Offset(
                    cx - centerMeasured.size.width  / 2f,
                    cy - centerMeasured.size.height / 2f
                ),
                style = centerStyle
            )
        }
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
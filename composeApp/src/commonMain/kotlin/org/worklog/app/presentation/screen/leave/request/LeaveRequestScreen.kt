package org.worklog.app.presentation.screen.leave.request

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.worklog.app.presentation.component.AppTopbarWithBack
import org.worklog.app.presentation.component.CustomTabLayout
import org.worklog.app.presentation.screen.leave.request.holiday.HolidayRequestScreen
import org.worklog.app.presentation.screen.leave.request.planned.RequestPlannedLeaveScreen
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.holidays
import worklog.composeapp.generated.resources.planned_leave

@Composable
fun LeaveRequestScreen(
    accruedHoliday: Int
) {
    val navController = LocalNavController.current
    val pagerState = rememberPagerState(pageCount = { 2 })

    // Create screens once per RotaScreenContent composition
    val screens = remember {
        listOf<@Composable () -> Unit>(
            { HolidayRequestScreen(accruedHoliday = accruedHoliday) },
            { RequestPlannedLeaveScreen() }
        )
    }

    LeaveRequestScreenContent(
        pagerState = pagerState,
        screens = screens,
        onBackClick = { navController.navigateUp() },
        onNotificationClick = { }
    )
}

@Composable
fun LeaveRequestScreenContent(
    pagerState: PagerState,
    screens: List<@Composable (() -> Unit)>,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopbarWithBack(
                title = "Absence Request",
                onBackClick = onBackClick,
                onNotificationClick = onNotificationClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            /*CustomTabLayout(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 2.dp)
                    .padding(horizontal = dimens.horizontalPadding),
                tabTitles = listOf(
                    stringResource(Res.string.holidays),
                    stringResource(Res.string.planned_leave)
                ),
                selectedIndex = pagerState.currentPage,
                onTabSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )*/

            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                userScrollEnabled = false
            ) { page ->
                screens[page]()
            }
        }
    }
}
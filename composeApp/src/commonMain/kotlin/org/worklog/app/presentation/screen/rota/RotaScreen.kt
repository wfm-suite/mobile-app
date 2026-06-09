package org.worklog.app.presentation.screen.rota

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.worklog.app.presentation.component.CustomTabLayout
import org.worklog.app.presentation.component.TopbarWithLogo
import org.worklog.app.presentation.screen.rota.my_team.MyTeamScreen
import org.worklog.app.presentation.screen.rota.time_card.TimeCardScreen
import org.worklog.app.presentation.navigation.ScreenRoute
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.LocalRootNavController
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.my_team
import worklog.composeapp.generated.resources.time_card

@Composable
fun RotaScreen() {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val rootNavController = LocalRootNavController.current

    // Create screens once per RotaScreenContent composition
    val screens = remember {
        listOf<@Composable () -> Unit>(
            { MyTeamScreen() },
            { TimeCardScreen() }
        )
    }

    RotaScreenContent(
        pagerState = pagerState,
        coroutineScope = coroutineScope,
        screens = screens,
        onBackClick = { rootNavController.navigateUp() },
        onNotificationClick = { navController.navigate(ScreenRoute.Notifications) }
    )
}

@Composable
private fun RotaScreenContent(
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    screens: List<@Composable (() -> Unit)>,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {

    Scaffold(
        modifier = Modifier.fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            TopbarWithLogo(
                showBack = true,
                onBackClick = onBackClick,
                onNotificationClick = onNotificationClick
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(it),
        ) {
            CustomTabLayout(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 2.dp)
                    .padding(horizontal = dimens.horizontalPadding),
                tabTitles = listOf(
                    stringResource(Res.string.my_team),
                    stringResource(Res.string.time_card)
                ),
                selectedIndex = pagerState.currentPage,
                onTabSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )

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
package org.worklog.app.presentation.screen.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.presentation.component.PrimaryButton
import org.worklog.app.presentation.navigation.ScreenRoute
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.ic_app_banner
import worklog.composeapp.generated.resources.onboarding_btn_continue
import worklog.composeapp.generated.resources.onboarding_btn_get_started
import worklog.composeapp.generated.resources.onboarding_desc_1
import worklog.composeapp.generated.resources.onboarding_desc_2
import worklog.composeapp.generated.resources.onboarding_desc_3
import worklog.composeapp.generated.resources.onboarding_title_1
import worklog.composeapp.generated.resources.onboarding_title_2
import worklog.composeapp.generated.resources.onboarding_title_3

data class OnBoardingModel(
    val image: DrawableResource,
    val title: StringResource,
    val subtitle: StringResource,
    val buttonText: StringResource
)

val onBoardingList = listOf(
    OnBoardingModel(
        Res.drawable.ic_app_banner,
        Res.string.onboarding_title_1,
        Res.string.onboarding_desc_1,
        Res.string.onboarding_btn_continue
    ),
    OnBoardingModel(
        Res.drawable.ic_app_banner,
        Res.string.onboarding_title_2,
        Res.string.onboarding_desc_2,
        Res.string.onboarding_btn_continue
    ),

    OnBoardingModel(
        Res.drawable.ic_app_banner,
        Res.string.onboarding_title_3,
        Res.string.onboarding_desc_3,
        Res.string.onboarding_btn_get_started
    )
)


@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val navController = LocalNavController.current

    val pageCount = onBoardingList.size
    val state = rememberPagerState(pageCount = { pageCount })
    val title = onBoardingList[state.currentPage].title
    val subtitle = onBoardingList[state.currentPage].subtitle
    val buttonText = onBoardingList[state.currentPage].buttonText
    val scope = rememberCoroutineScope()

    OnBoardingScreenContent(
        title,
        subtitle,
        buttonText,
        state,
        pageCount,
        buttonClick = {
            scope.launch {
                val isLastPage = state.currentPage == state.pageCount - 1

                if (!isLastPage) {
                    state.animateScrollToPage(state.currentPage + 1)
                    return@launch
                }

                navController.navigate(ScreenRoute.Login) {
                    popUpTo(0)
                }

                viewModel.updateFirstLaunch()
            }
        }
    )
}

@Composable
fun OnBoardingScreenContent(
    title: StringResource,
    subtitle: StringResource,
    buttonText: StringResource,
    state: PagerState,
    pageCount: Int,
    buttonClick: () -> Unit
) {
    OnboardingContent(state, pageCount, title, subtitle, buttonText, buttonClick)
}

@Composable
fun OnboardingContent(
    state: PagerState,
    pageCount: Int,
    title: StringResource,
    subtitle: StringResource,
    buttonText: StringResource,
    buttonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(dimens.contentPadding)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
    ) {
        HorizontalPager(
            state,
            modifier = Modifier.weight(0.6f)
        ) { page ->
            val pageImage = onBoardingList[page].image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimens.cornerRadiusSmall)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(pageImage),
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .padding(dimens.contentPadding),
                    contentDescription = null
                )
            }
        }

        TextContent(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.35f),
            title = stringResource(title),
            subtitle = stringResource(subtitle),
            pageCount = pageCount,
            state = state
        )

        PrimaryButton(
            label = stringResource(buttonText), onClick = { buttonClick.invoke() })
    }
}

@Composable
private fun TextContent(
    title: String,
    subtitle: String,
    pageCount: Int,
    state: PagerState,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {

            repeat(pageCount) {
                val color =
                    if (state.currentPage == it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                val width = if (state.currentPage == it) 27.dp else 8.dp

                Box(
                    modifier = Modifier
                        .padding(end = 3.dp)
                        .background(color = color, shape = CircleShape)
                        .size(width = width, height = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.W700, fontSize = 24.sp, lineHeight = 1.4.em
            )
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(11.dp),
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp, textAlign = TextAlign.Center
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
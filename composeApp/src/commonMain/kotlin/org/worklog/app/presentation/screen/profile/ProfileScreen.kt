package org.worklog.app.presentation.screen.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.domain.model.UserInfo
import org.worklog.app.presentation.component.CustomRow
import org.worklog.app.presentation.component.PrimaryButton
import org.worklog.app.presentation.component.ShimmerBox
import org.worklog.app.presentation.component.TopbarWithLogo
import org.worklog.app.presentation.navigation.ScreenRoute
import org.worklog.app.presentation.theme.LocalNavController
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.bank_details
import worklog.composeapp.generated.resources.employee_declaration
import worklog.composeapp.generated.resources.ic_next
import worklog.composeapp.generated.resources.ic_user
import worklog.composeapp.generated.resources.logout
import worklog.composeapp.generated.resources.manage_account
import worklog.composeapp.generated.resources.profile_details
import worklog.composeapp.generated.resources.submit_resignation
import worklog.composeapp.generated.resources.training_courses

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel()
) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            navController.navigate(ScreenRoute.Login) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val optionMenus = listOf(
        OptionMenu.ProfileDetails,
        OptionMenu.ManageAccount,
        OptionMenu.BankDetails,
        OptionMenu.EmployeeDeclaration,
        OptionMenu.TrainingCourses,
        OptionMenu.SubmitResignation
    )
    ProfileScreenContent(
        isLoading = uiState.isLoading,
        optionMenus = optionMenus,
        userInfo = uiState.userInfo,
        onMenuClick = {
            when (it) {
                OptionMenu.ProfileDetails -> {
                    navController.navigate(ScreenRoute.ProfileDetail)
                }

                else -> {}
            }
        },
        onLogoutClick = viewModel::logout
    )
}

@Composable
private fun ProfileScreenContent(
    isLoading: Boolean = false,
    optionMenus: List<OptionMenu>,
    userInfo: UserInfo?,
    onMenuClick: (OptionMenu) -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            TopbarWithLogo(
                onNotificationClick = {}
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(it)
                .padding(dimens.contentPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimens.innerVerticalPadding)
        ) {
            if (isLoading) {
                CustomRow(
                    innerPadding = PaddingValues(dimens.innerHorizontalPadding)
                ) {
                    ShimmerBox(
                        modifier = Modifier.size(72.dp),
                        height = 72.dp,
                        width = 72.dp,
                        cornerRadius = dimens.cornerRadius
                    )
                    Spacer(modifier = Modifier.width(dimens.spaceBetween))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ShimmerBox(height = 20.dp, width = 150.dp, cornerRadius = 4.dp)
                        ShimmerBox(height = 14.dp, width = 100.dp, cornerRadius = 4.dp)
                        ShimmerBox(height = 12.dp, width = 200.dp, cornerRadius = 4.dp)
                    }
                }
            } else {
                userInfo?.let { user ->
                    CustomRow(
                        innerPadding = PaddingValues(dimens.innerHorizontalPadding)
                    ) {
                        AsyncImage(
                            model = user.profilePicture,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(72.dp)
                                .clip(RoundedCornerShape(dimens.cornerRadius))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(dimens.cornerRadius)
                                ),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(Res.drawable.ic_user),
                            error = painterResource(Res.drawable.ic_user),
                        )
                        Spacer(modifier = Modifier.width(dimens.spaceBetween))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${user.firstName} ${user.lastName}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = user.designation,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = "${user.branchName} - ${user.companyName} - ${user.branchAddress}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimens.innerVerticalPadding))

            optionMenus.forEach { menu ->
                MenuItem(
                    title = stringResource(menu.titleRes),
                    onClick = { onMenuClick(menu) }
                )
            }

            Spacer(modifier = Modifier.height(dimens.spaceBetween))

            PrimaryButton(
                label = stringResource(Res.string.logout),
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = onLogoutClick
            )
            Spacer(modifier = Modifier.height(dimens.bottomBarHeight))
        }
    }
}

@Composable
private fun MenuItem(
    title: String = "",
    icon: DrawableResource = Res.drawable.ic_next,
    onClick: () -> Unit = {}
) {
    CustomRow(
        innerPadding = PaddingValues(vertical = 9.dp, horizontal = 12.dp),
        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        onClick = onClick
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        Icon(
            modifier = Modifier.size(14.dp),
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

sealed class OptionMenu(
    val titleRes: StringResource
) {
    object ProfileDetails : OptionMenu(
        titleRes = Res.string.profile_details
    )

    object ManageAccount : OptionMenu(
        titleRes = Res.string.manage_account
    )

    object BankDetails : OptionMenu(
        titleRes = Res.string.bank_details
    )

    object EmployeeDeclaration : OptionMenu(
        titleRes = Res.string.employee_declaration
    )

    object TrainingCourses : OptionMenu(
        titleRes = Res.string.training_courses
    )

    object SubmitResignation : OptionMenu(
        titleRes = Res.string.submit_resignation
    )
}
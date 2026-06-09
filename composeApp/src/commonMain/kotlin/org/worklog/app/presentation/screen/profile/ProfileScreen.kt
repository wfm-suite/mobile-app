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
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.DrawableResource
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
import worklog.composeapp.generated.resources.ic_next
import worklog.composeapp.generated.resources.ic_user
import worklog.composeapp.generated.resources.logout

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel()
) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            navController.navigate(ScreenRoute.Login) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    ProfileScreenContent(
        isLoading = uiState.isLoading,
        groups = profileMenuGroups,
        userInfo = uiState.userInfo,
        onNotificationClick = { navController.navigate(ScreenRoute.Notifications) },
        onMenuClick = { menu ->
            navController.navigate(ScreenRoute.ProfileSection(menu.type))
        },
        onLogoutClick = viewModel::logout
    )
}

@Composable
private fun ProfileScreenContent(
    isLoading: Boolean = false,
    groups: List<OptionGroup>,
    userInfo: UserInfo?,
    onNotificationClick: () -> Unit = {},
    onMenuClick: (OptionMenu) -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            TopbarWithLogo(
                onNotificationClick = onNotificationClick
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

            groups.forEachIndexed { idx, group ->
                if (idx > 0) Spacer(modifier = Modifier.height(dimens.innerVerticalPadding))
                Text(
                    text = group.title.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.outline,
                        letterSpacing = 0.8.sp
                    ),
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 6.dp)
                )
                group.items.forEach { menu ->
                    MenuItem(
                        title = menu.title,
                        onClick = { onMenuClick(menu) }
                    )
                }
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

data class OptionMenu(val title: String, val type: String)

data class OptionGroup(val title: String, val items: List<OptionMenu>)

// Section type IDs — kept stable, used by ScreenRoute.ProfileSection(type) and
// switched on inside ProfileSectionScreen to pick the section composable.
object ProfileSectionType {
    const val PERSONAL_DETAILS = "personal_details"
    const val CONTACT_DETAILS = "contact_details"
    const val ADDRESSES = "addresses"
    const val EMERGENCY_CONTACTS = "emergency_contacts"
    const val ASSIGNMENT_SUMMARY = "assignment_summary"
    const val JOB_STATUS = "job_status"
    const val RTW_DETAILS = "rtw_details"
    const val EDI = "edi"
    const val EMPLOYEE_DECLARATION = "employee_declaration"
    const val VETTING_DETAILS = "vetting_details"
    const val TRAINING_COURSES = "training_courses"
    const val BANK_DETAILS = "bank_details"
    const val PASSWORD = "password"
    const val RESIGNATION = "resignation"
}

val profileMenuGroups = listOf(
    OptionGroup(
        title = "Personal Info",
        items = listOf(
            OptionMenu("Personal Details", ProfileSectionType.PERSONAL_DETAILS),
            OptionMenu("Contact Details", ProfileSectionType.CONTACT_DETAILS),
            OptionMenu("Addresses", ProfileSectionType.ADDRESSES),
            OptionMenu("Emergency Contacts", ProfileSectionType.EMERGENCY_CONTACTS),
        )
    ),
    OptionGroup(
        title = "Employment",
        items = listOf(
            OptionMenu("Assignment Summary", ProfileSectionType.ASSIGNMENT_SUMMARY),
            OptionMenu("Job Status", ProfileSectionType.JOB_STATUS),
            OptionMenu("RTW Details", ProfileSectionType.RTW_DETAILS),
            OptionMenu("Vetting Details", ProfileSectionType.VETTING_DETAILS),
        )
    ),
    OptionGroup(
        title = "Compliance & Training",
        items = listOf(
            OptionMenu("Employee Declaration", ProfileSectionType.EMPLOYEE_DECLARATION),
            OptionMenu("Equality / Diversity / Inclusion", ProfileSectionType.EDI),
            OptionMenu("Training Courses", ProfileSectionType.TRAINING_COURSES),
        )
    ),
    OptionGroup(
        title = "Money",
        items = listOf(
            OptionMenu("Bank Details", ProfileSectionType.BANK_DETAILS),
        )
    ),
    OptionGroup(
        title = "Account",
        items = listOf(
            OptionMenu("My Password", ProfileSectionType.PASSWORD),
            OptionMenu("Submit Resignation", ProfileSectionType.RESIGNATION),
        )
    ),
)
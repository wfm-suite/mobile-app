package org.worklog.app.presentation.screen.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.worklog.app.domain.model.EmployeeInfo
import org.worklog.app.presentation.component.CustomRow
import org.worklog.app.presentation.component.ShimmerBox
import org.worklog.app.presentation.component.TopbarWithLogo
import org.worklog.app.presentation.component.UserInfoItem
import org.worklog.app.presentation.theme.dimens

@Composable
fun MessageScreen(
    viewModel: MessageViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    MessageScreenContent(
        isLoading = uiState.isLoading,
        onNotificationClick = {},
        employees = uiState.filteredEmployees,
        searchQuery = uiState.searchQuery,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onCallClick = viewModel::openDialer,
        onMessageClick = viewModel::openMessageCompose
    )
}

@Composable
fun MessageScreenContent(
    isLoading: Boolean = false,
    employees: List<EmployeeInfo>,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onCallClick: (String) -> Unit = {},
    onMessageClick: (String) -> Unit = {},
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomSearchBar(
                modifier = Modifier.fillMaxWidth(),
                value = searchQuery,
                onSearch = onSearchQueryChange,
                hint = "Search by name, email, or phone"
            )

            Spacer(modifier = Modifier.height(8.dp))
            if (isLoading) {
                repeat(8) {
                    CustomRow(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(shape = RoundedCornerShape(dimens.cornerRadius))
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(dimens.cornerRadius)
                            )
                    ) {
                        ShimmerBox(
                            modifier = Modifier.size(40.dp),
                            height = 40.dp,
                            width = 40.dp,
                            cornerRadius = 20.dp
                        )
                        Spacer(modifier = Modifier.width(dimens.innerVerticalPadding))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            ShimmerBox(height = 20.dp, width = 120.dp, cornerRadius = 4.dp)
                            Spacer(modifier = Modifier.height(4.dp))
                            ShimmerBox(height = 14.dp, width = 80.dp, cornerRadius = 4.dp)
                        }
                    }
                }
            } else {

                employees.forEach { employee ->
                    UserInfoItem(
                        name = employee.displayName,
                        designation = employee.designation,
                        profileImage = employee.profilePicture,
                        onCallClick = {
                            onCallClick(employee.phone)
                        },
                        onMessageClick = {
                            onMessageClick(employee.phone)
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.height(dimens.bottomBarHeight))
        }
    }
}

@Composable
private fun CustomSearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    value: String = "",
    onSearch: (String) -> Unit = {}
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onSearch,
        placeholder = {
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        shape = RoundedCornerShape(dimens.cornerRadiusMedium),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium
    )
}
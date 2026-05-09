package org.worklog.app.presentation.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.worklog.app.presentation.navigation.ScreenRoute
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.home
import worklog.composeapp.generated.resources.ic_home
import worklog.composeapp.generated.resources.ic_leave
import worklog.composeapp.generated.resources.ic_message
import worklog.composeapp.generated.resources.ic_profile
import worklog.composeapp.generated.resources.ic_rota
import worklog.composeapp.generated.resources.leave
import worklog.composeapp.generated.resources.message
import worklog.composeapp.generated.resources.profile
import worklog.composeapp.generated.resources.rota

@Composable
fun AppBottomNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unSelectedContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    val items = listOf(
        AppBottomNavItem.Home,
        AppBottomNavItem.Rota,
        AppBottomNavItem.Message,
        AppBottomNavItem.Leave,
        AppBottomNavItem.Profile,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = modifier.fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(
                    topStart = dimens.cornerRadiusLarge,
                    topEnd = dimens.cornerRadiusLarge
                )
            )
            .background(containerColor)
            .topBorderWithRadius(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                cornerRadius = dimens.cornerRadiusLarge
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route::class.qualifiedName
            AppBottomNavigationItem(
                modifier = Modifier.weight(1f),
                isSelected = isSelected,
                item = item,
                unSelectedContentColor = unSelectedContentColor,
                selectedColor = selectedColor,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun AppBottomNavigationItem(
    modifier: Modifier = Modifier,
    item: AppBottomNavItem,
    isSelected: Boolean,
    unSelectedContentColor: Color,
    selectedColor: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.10f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )

    val textStyle = if (isSelected) MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.SemiBold,
        color = selectedColor
    ) else MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Normal,
        color = unSelectedContentColor
    )

    Column(
        modifier = modifier
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Image(
            painter = painterResource(if (isSelected) item.selectedIcon else item.unselectedIcon),
            contentDescription = stringResource(item.label),
            colorFilter = ColorFilter.tint(if (isSelected) selectedColor else unSelectedContentColor),
            modifier = Modifier
                .size(dimens.iconSize)
                .graphicsLayer(scaleX = scale, scaleY = scale)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = stringResource(item.label),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = textStyle
        )
    }
}

sealed class AppBottomNavItem(
    val label: StringResource,
    val route: ScreenRoute,
    val selectedIcon: DrawableResource,
    val unselectedIcon: DrawableResource,
) {
    object Home : AppBottomNavItem(
        label = Res.string.home,
        route = ScreenRoute.Home,
        selectedIcon = Res.drawable.ic_home,
        unselectedIcon = Res.drawable.ic_home,
    )

    object Rota : AppBottomNavItem(
        label = Res.string.rota,
        route = ScreenRoute.Rota,
        selectedIcon = Res.drawable.ic_rota,
        unselectedIcon = Res.drawable.ic_rota,
    )

    object Message : AppBottomNavItem(
        label = Res.string.message,
        route = ScreenRoute.Message,
        selectedIcon = Res.drawable.ic_message,
        unselectedIcon = Res.drawable.ic_message,
    )

    object Leave : AppBottomNavItem(
        label = Res.string.leave,
        route = ScreenRoute.Leave,
        selectedIcon = Res.drawable.ic_leave,
        unselectedIcon = Res.drawable.ic_leave,
    )

    object Profile : AppBottomNavItem(
        label = Res.string.profile,
        route = ScreenRoute.Profile,
        selectedIcon = Res.drawable.ic_profile,
        unselectedIcon = Res.drawable.ic_profile,
    )
}

fun Modifier.topBorderWithRadius(
    width: Dp,
    color: Color,
    cornerRadius: Dp
): Modifier = this.then(
    Modifier.drawBehind {
        val strokeWidth = width.toPx()
        val radius = cornerRadius.toPx()

        val path = Path().apply {
            // Start from left corner
            moveTo(0f, radius)
            // Top-left arc
            arcTo(
                rect = Rect(0f, 0f, radius * 2, radius * 2),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            // Top line
            lineTo(size.width - radius, 0f)
            // Top-right arc
            arcTo(
                rect = Rect(size.width - radius * 2, 0f, size.width, radius * 2),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
)
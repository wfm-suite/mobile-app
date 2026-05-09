package org.worklog.app.presentation.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppDimensions(
    val iconSize: Dp = 24.dp,
    val smallIconSize: Dp = 18.dp,
    val mediumIconSize: Dp = 22.dp,
    val vectorImageSize: Dp = 30.dp,
    val bottomBarHeight: Dp = 55.dp,
    val horizontalPadding: Dp = 12.dp,
    val verticalPadding: Dp = 16.dp,
    val innerHorizontalPadding: Dp = 16.dp,
    val innerVerticalPadding: Dp = 12.dp,
    val spaceBetween: Dp = 8.dp,
    val inputHeight: Dp = 54.dp,
    val buttonHeight: Dp = 42.dp,
    val drawerMinWidth: Dp = 275.dp,
    val topBarHeight: Dp = 55.dp,
    val cornerRadius: Dp = 12.dp,
    val cornerRadiusSmall: Dp = 8.dp,
    val cornerRadiusMedium: Dp = 16.dp,
    val cornerRadiusLarge: Dp = 24.dp,
    val contentPadding: PaddingValues = PaddingValues(
        horizontal = horizontalPadding,
        vertical = verticalPadding
    ),
    val innerContentPadding: PaddingValues = PaddingValues(
        horizontal = innerHorizontalPadding,
        vertical = innerVerticalPadding
    )
)

@Composable
fun rememberAppDimensions(): AppDimensions = remember { AppDimensions() }

val dimens: AppDimensions
    @Composable
    @ReadOnlyComposable
    get() = LocalDimensions.current

val LocalDimensions =
    staticCompositionLocalOf<AppDimensions> { error("AppDimensions must be provided") }
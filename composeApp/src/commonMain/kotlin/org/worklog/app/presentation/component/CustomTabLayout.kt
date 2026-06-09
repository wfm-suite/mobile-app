package org.worklog.app.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.worklog.app.presentation.theme.dimens

@Composable
fun CustomTabLayout(
    modifier: Modifier = Modifier,
    tabTitles: List<String> = listOf(),
    selectedIndex: Int,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    innerPadding: PaddingValues = PaddingValues(12.dp),
    tabPadding: PaddingValues = PaddingValues(8.dp),
    cornerRadius: Dp = dimens.cornerRadius,
    cardCornerRadius: Dp = dimens.cornerRadiusLarge,
    onTabSelected: (Int) -> Unit
) {
    require(tabTitles.isNotEmpty()) { "tabTitles cannot be empty" }

    CustomCard(
        modifier = modifier,
        innerPadding = innerPadding,
        cornerRadius = cardCornerRadius
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                TabButton(
                    modifier = Modifier.weight(1f),
                    label = title,
                    cornerRadius = cornerRadius,
                    tabPadding = tabPadding,
                    textStyle = textStyle,
                    selected = selectedIndex == index,
                    onClick = { onTabSelected(index) }
                )
            }
        }
    }
}

@Composable
fun TabButton(
    modifier: Modifier = Modifier,
    label: String = "",
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    selected: Boolean = false,
    cornerRadius: Dp = dimens.cornerRadius,
    tabPadding: PaddingValues = PaddingValues(8.dp),
    onClick: () -> Unit = {}
) {
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val textColor =
        if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(tabPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            maxLines = 1,
            color = textColor,
            style = textStyle
        )
    }
}
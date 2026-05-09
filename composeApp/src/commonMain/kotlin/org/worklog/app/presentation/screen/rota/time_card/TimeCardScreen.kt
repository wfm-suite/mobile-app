package org.worklog.app.presentation.screen.rota.time_card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.worklog.app.presentation.component.CustomCard
import org.worklog.app.presentation.component.CustomRow
import org.worklog.app.presentation.theme.WorkLogTheme
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.ic_arrow_down
import worklog.composeapp.generated.resources.ic_filter

@Composable
fun TimeCardScreen(

) {
    TimeCardScreenContent()
}

@Composable
private fun TimeCardScreenContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimens.contentPadding)
    ) {
        CustomCard(spaceBetween = dimens.innerVerticalPadding) {
            TimeCardHeader()
            TimeCardSummary()
            TimeCardList()
        }

        Spacer(Modifier.height(dimens.bottomBarHeight))
    }
}

/* ---------------------------- HEADER ---------------------------- */

@Composable
private fun TimeCardHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MonthSelector(month = "December 2025")

        Spacer(Modifier.weight(1f))

        Text(
            text = "Monthly",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(Modifier.width(dimens.spaceBetween))

        IconButton(
            modifier = Modifier.size(dimens.vectorImageSize),
            onClick = {}
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_filter),
                contentDescription = null,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

@Composable
private fun MonthSelector(
    month: String,
    onClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = month,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.secondary
            )
        )
        Spacer(Modifier.width(5.dp))
        Icon(
            painter = painterResource(Res.drawable.ic_arrow_down),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(10.dp)
        )
    }
}

/* ---------------------------- SUMMARY ---------------------------- */

@Composable
private fun TimeCardSummary() {
    CustomRow(horizontalArrangement = Arrangement.SpaceBetween) {

        SummaryColumn(
            title = "Total Time",
            value = "20 Days",
            titleStyle = MaterialTheme.typography.titleMedium,
            valueStyle = MaterialTheme.typography.labelSmall
        )

        Text(
            text = "164.60 hrs",
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun SummaryColumn(
    title: String,
    value: String,
    titleStyle: TextStyle,
    valueStyle: TextStyle
) {
    Column {
        Text(title, style = titleStyle)
        Text(value, style = valueStyle)
    }
}

/* ---------------------------- LIST ---------------------------- */

@Composable
private fun TimeCardList() {
    repeat(20) {
        TimeCardItem()
    }
}

/* ---------------------------- ITEM ---------------------------- */

@Composable
private fun TimeCardItem() {
    Row(
        modifier = Modifier.height(IntrinsicSize.Max)
    ) {
        SideIndicator()
        Spacer(modifier = Modifier.width(dimens.innerVerticalPadding))

        Column(modifier = Modifier.weight(1f)) {
            TimeCardHeaderRow()
            Spacer(Modifier.height(6.dp))
            TimeCardDetailsRow()
        }
    }
}

@Composable
private fun SideIndicator() {
    Box(
        modifier = Modifier
            .width(3.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.primary)
    )
}

@Composable
private fun TimeCardHeaderRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Wed 01, Oct 2025",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "8.02 hrs",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun TimeCardDetailsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DetailColumn(
            label = "Check in",
            value = "12:15",
            labelStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )

        DetailColumn(
            label = "1 Break",
            value = "21 mins",
            labelStyle = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            valueStyle = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )

        DetailColumn(
            label = "Check out",
            value = "17:20",
            labelStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun DetailColumn(
    label: String,
    value: String,
    labelStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    valueStyle: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Column {
        Text(
            text = label,
            style = labelStyle
        )
        Text(text = value, style = valueStyle)
    }
}


@Preview
@Composable
private fun TimeCardScreenPreview() {
    WorkLogTheme {
        TimeCardScreenContent()
    }
}
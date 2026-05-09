package org.worklog.app.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import org.worklog.app.presentation.theme.dimens
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.ic_call
import worklog.composeapp.generated.resources.ic_message
import worklog.composeapp.generated.resources.ic_user

@Composable
fun UserInfoItem(
    name: String = "Person name",
    designation: String = "You: Ok, See you in To",
    profileImage: String = "",
    onCallClick: () -> Unit = {},
    onMessageClick: () -> Unit = {}
) {
    CustomRow {
        AsyncImage(
            model = profileImage,
            contentDescription = "Profile Picture",
            modifier = Modifier.size(40.dp)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(Res.drawable.ic_user),
            error = painterResource(Res.drawable.ic_user),
        )
        Spacer(modifier = Modifier.width(dimens.innerVerticalPadding))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = designation,
                style = MaterialTheme.typography.labelSmall
            )
        }
        Spacer(modifier = Modifier.width(dimens.spaceBetween))
        IconButton(
            modifier = Modifier.size(dimens.vectorImageSize),
            onClick = onCallClick
        ) {
            Icon(
                modifier = Modifier.padding(5.dp),
                painter = painterResource(Res.drawable.ic_call),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(dimens.spaceBetween))
        IconButton(
            modifier = Modifier.size(dimens.vectorImageSize),
            onClick = onMessageClick
        ) {
            Icon(
                modifier = Modifier.padding(5.dp),
                painter = painterResource(Res.drawable.ic_message),
                contentDescription = null
            )
        }
    }
}
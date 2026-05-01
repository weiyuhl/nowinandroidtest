package com.lhzkml.nowtest.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lhzkml.nowtest.core.designsystem.component.DynamicAsyncImage
import com.lhzkml.nowtest.core.designsystem.icon.NtIcons
import com.lhzkml.nowtest.core.designsystem.theme.NtTheme

@Composable
fun InterestsItem(
    name: String,
    topicImageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    description: String = "",
    isSelected: Boolean = false,
) {
    ListItem(
        leadingContent = {
            InterestsIcon(topicImageUrl, iconModifier.size(48.dp))
        },
        headlineContent = {
            Text(text = name)
        },
        supportingContent = {
            Text(text = description)
        },
        colors = ListItemDefaults.colors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                Color.Transparent
            },
        ),
        modifier = modifier
            .semantics(mergeDescendants = true) {
                selected = isSelected
            }
            .clickable(enabled = true, onClick = onClick),
    )
}

@Composable
private fun InterestsIcon(topicImageUrl: String, modifier: Modifier = Modifier) {
    if (topicImageUrl.isEmpty()) {
        Icon(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp),
            imageVector = NtIcons.Person,
            // decorative image
            contentDescription = null,
        )
    } else {
        DynamicAsyncImage(
            imageUrl = topicImageUrl,
            contentDescription = null,
            modifier = modifier,
        )
    }
}

@Preview
@Composable
private fun InterestsCardPreview() {
    NtTheme {
        Surface {
            InterestsItem(
                name = "Compose",
                description = "Description",
                topicImageUrl = "",
                onClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun InterestsCardLongNamePreview() {
    NtTheme {
        Surface {
            InterestsItem(
                name = "This is a very very very very long name",
                description = "Description",
                topicImageUrl = "",
                onClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun InterestsCardLongDescriptionPreview() {
    NtTheme {
        Surface {
            InterestsItem(
                name = "Compose",
                description = "This is a very very very very very very very " +
                    "very very very long description",
                topicImageUrl = "",
                onClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun InterestsCardWithEmptyDescriptionPreview() {
    NtTheme {
        Surface {
            InterestsItem(
                name = "Compose",
                description = "",
                topicImageUrl = "",
                onClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun InterestsCardSelectedPreview() {
    NtTheme {
        Surface {
            InterestsItem(
                name = "Compose",
                description = "",
                topicImageUrl = "",
                onClick = { },
                isSelected = true,
            )
        }
    }
}

package com.lhzkml.nowtest.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.lhzkml.nowtest.core.designsystem.icon.NtIcons
import com.lhzkml.nowtest.core.designsystem.theme.NtTheme

data class NtTopNavigationDestination<K : NavKey>(
    val key: K,
    val icon: ImageVector,
    val contentDescription: String,
)

@Composable
fun <K : NavKey> NtTopNavigationBar(
    destinations: List<NtTopNavigationDestination<K>>,
    onNavigateToDestination: (K) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.testTag("ntTopNavigationBar"),
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                .height(64.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            destinations.forEach { destination ->
                NtTopNavigationBarItem(
                    icon = destination.icon,
                    contentDescription = destination.contentDescription,
                    onClick = { onNavigateToDestination(destination.key) },
                )
            }
        }
    }
}

@Composable
private fun RowScope.NtTopNavigationBarItem(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview("Top Navigation Bar")
@Composable
private fun NtTopNavigationBarPreview() {
    NtTheme {
        NtTopNavigationBar(
            destinations = listOf(
                NtTopNavigationDestination(
                    key = PreviewSearchNavKey,
                    icon = NtIcons.Search,
                    contentDescription = "Search",
                ),
                NtTopNavigationDestination(
                    key = PreviewSettingsNavKey,
                    icon = NtIcons.Settings,
                    contentDescription = "Settings",
                ),
            ),
            onNavigateToDestination = {},
        )
    }
}

private object PreviewSearchNavKey : NavKey

private object PreviewSettingsNavKey : NavKey

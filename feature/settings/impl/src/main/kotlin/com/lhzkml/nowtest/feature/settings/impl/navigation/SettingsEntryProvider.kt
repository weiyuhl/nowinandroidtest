package com.lhzkml.nowtest.feature.settings.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.nowtest.core.navigation.Navigator
import com.lhzkml.nowtest.feature.settings.api.navigation.SettingsNavKey
import com.lhzkml.nowtest.feature.settings.impl.SettingsScreen

fun EntryProviderScope<NavKey>.settingsEntry(navigator: Navigator) {
    entry<SettingsNavKey> {
        SettingsScreen(
            onBackClick = { navigator.goBack() },
        )
    }
}

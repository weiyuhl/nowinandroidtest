package com.google.samples.apps.nowinandroid.feature.search.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.google.samples.apps.nowinandroid.core.navigation.Navigator
import com.google.samples.apps.nowinandroid.feature.interests.api.navigation.InterestsNavKey
import com.google.samples.apps.nowinandroid.feature.search.api.navigation.SearchNavKey
import com.google.samples.apps.nowinandroid.feature.search.impl.SearchScreen
import com.google.samples.apps.nowinandroid.feature.topic.api.navigation.navigateToTopic

fun EntryProviderScope<NavKey>.searchEntry(navigator: Navigator) {
    entry<SearchNavKey> {
        SearchScreen(
            onBackClick = { navigator.goBack() },
            onInterestsClick = { navigator.navigate(InterestsNavKey()) },
            onTopicClick = navigator::navigateToTopic,
        )
    }
}

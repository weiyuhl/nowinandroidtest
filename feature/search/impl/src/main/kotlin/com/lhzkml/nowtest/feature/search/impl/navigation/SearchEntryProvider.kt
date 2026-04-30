package com.lhzkml.nowtest.feature.search.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.nowtest.core.navigation.Navigator
import com.lhzkml.nowtest.feature.interests.api.navigation.InterestsNavKey
import com.lhzkml.nowtest.feature.search.api.navigation.SearchNavKey
import com.lhzkml.nowtest.feature.search.impl.SearchScreen
import com.lhzkml.nowtest.feature.topic.api.navigation.navigateToTopic

fun EntryProviderScope<NavKey>.searchEntry(navigator: Navigator) {
    entry<SearchNavKey> {
        SearchScreen(
            onBackClick = { navigator.goBack() },
            onInterestsClick = { navigator.navigate(InterestsNavKey()) },
            onTopicClick = navigator::navigateToTopic,
        )
    }
}

package com.lhzkml.nowtest.feature.topic.impl.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.nowtest.core.navigation.Navigator
import com.lhzkml.nowtest.feature.topic.api.navigation.TopicNavKey
import com.lhzkml.nowtest.feature.topic.api.navigation.navigateToTopic
import com.lhzkml.nowtest.feature.topic.impl.TopicScreen
import com.lhzkml.nowtest.feature.topic.impl.TopicViewModel
import com.lhzkml.nowtest.feature.topic.impl.TopicViewModel.Factory

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.topicEntry(navigator: Navigator) {
    entry<TopicNavKey>(
        metadata = ListDetailSceneStrategy.detailPane(),
    ) { key ->
        val id = key.id
        TopicScreen(
            showBackButton = true,
            onBackClick = { navigator.goBack() },
            onTopicClick = navigator::navigateToTopic,
            viewModel = hiltViewModel<TopicViewModel, Factory>(
                key = id,
            ) { factory ->
                factory.create(id)
            },
        )
    }
}

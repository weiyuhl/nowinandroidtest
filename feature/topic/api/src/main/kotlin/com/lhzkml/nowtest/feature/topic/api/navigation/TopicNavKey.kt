package com.lhzkml.nowtest.feature.topic.api.navigation

import androidx.navigation3.runtime.NavKey
import com.lhzkml.nowtest.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class TopicNavKey(val id: String) : NavKey

fun Navigator.navigateToTopic(
    topicId: String,
) {
    navigate(TopicNavKey(topicId))
}

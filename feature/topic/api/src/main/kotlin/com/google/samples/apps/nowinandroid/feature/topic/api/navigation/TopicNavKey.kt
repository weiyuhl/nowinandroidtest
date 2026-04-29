package com.google.samples.apps.nowinandroid.feature.topic.api.navigation

import androidx.navigation3.runtime.NavKey
import com.google.samples.apps.nowinandroid.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class TopicNavKey(val id: String) : NavKey

fun Navigator.navigateToTopic(
    topicId: String,
) {
    navigate(TopicNavKey(topicId))
}

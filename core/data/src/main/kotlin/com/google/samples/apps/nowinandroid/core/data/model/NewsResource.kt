package com.google.samples.apps.nowinandroid.core.data.model

import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceEntity
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceTopicCrossRef
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkNewsResource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkTopic
import com.google.samples.apps.nowinandroid.core.network.model.asExternalModel

fun NetworkNewsResource.asEntity() = NewsResourceEntity(
    id = id,
    title = title,
    content = content,
    url = url,
    headerImageUrl = headerImageUrl,
    publishDate = publishDate,
    type = type,
)

/**
 * A shell [TopicEntity] to fulfill the foreign key constraint when inserting
 * a [NewsResourceEntity] into the DB
 */
fun NetworkNewsResource.topicEntityShells() =
    topics.map { topicId ->
        TopicEntity(
            id = topicId,
            name = "",
            url = "",
            imageUrl = "",
            shortDescription = "",
            longDescription = "",
        )
    }

fun NetworkNewsResource.topicCrossReferences(): List<NewsResourceTopicCrossRef> =
    topics.map { topicId ->
        NewsResourceTopicCrossRef(
            newsResourceId = id,
            topicId = topicId,
        )
    }

fun NetworkNewsResource.asExternalModel(topics: List<NetworkTopic>) =
    NewsResource(
        id = id,
        title = title,
        content = content,
        url = url,
        headerImageUrl = headerImageUrl,
        publishDate = publishDate,
        type = type,
        topics = topics
            .filter { networkTopic -> this.topics.contains(networkTopic.id) }
            .map(NetworkTopic::asExternalModel),
    )

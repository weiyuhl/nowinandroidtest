package com.google.samples.apps.nowinandroid.core.database.model

import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

class PopulatedNewsResourceKtTest {
    @Test
    fun populated_news_resource_can_be_mapped_to_news_resource() {
        val populatedNewsResource = PopulatedNewsResource(
            entity = NewsResourceEntity(
                id = "1",
                title = "news",
                content = "Hilt",
                url = "url",
                headerImageUrl = "headerImageUrl",
                type = "Video 📺",
                publishDate = Instant.fromEpochMilliseconds(1),
            ),
            topics = listOf(
                TopicEntity(
                    id = "3",
                    name = "name",
                    shortDescription = "short description",
                    longDescription = "long description",
                    url = "URL",
                    imageUrl = "image URL",
                ),
            ),
        )
        val newsResource = populatedNewsResource.asExternalModel()

        assertEquals(
            NewsResource(
                id = "1",
                title = "news",
                content = "Hilt",
                url = "url",
                headerImageUrl = "headerImageUrl",
                type = "Video 📺",
                publishDate = Instant.fromEpochMilliseconds(1),
                topics = listOf(
                    Topic(
                        id = "3",
                        name = "name",
                        shortDescription = "short description",
                        longDescription = "long description",
                        url = "URL",
                        imageUrl = "image URL",
                    ),
                ),
            ),
            newsResource,
        )
    }
}

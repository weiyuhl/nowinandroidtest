package com.lhzkml.nowtest.core.data.testdoubles

import com.lhzkml.nowtest.core.database.dao.NewsResourceDao
import com.lhzkml.nowtest.core.database.model.NewsResourceEntity
import com.lhzkml.nowtest.core.database.model.NewsResourceTopicCrossRef
import com.lhzkml.nowtest.core.database.model.PopulatedNewsResource
import com.lhzkml.nowtest.core.database.model.TopicEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

val filteredTopicIds = setOf("1")
val nonPresentTopicIds = setOf("2")

/**
 * Test double for [NewsResourceDao]
 */
class TestNewsResourceDao : NewsResourceDao {

    private val entitiesStateFlow = MutableStateFlow(emptyList<NewsResourceEntity>())

    internal var topicCrossReferences: List<NewsResourceTopicCrossRef> = emptyList()

    override fun getNewsResources(
        useFilterTopicIds: Boolean,
        filterTopicIds: Set<String>,
        useFilterNewsIds: Boolean,
        filterNewsIds: Set<String>,
    ): Flow<List<PopulatedNewsResource>> =
        entitiesStateFlow
            .map { newsResourceEntities ->
                newsResourceEntities.map { entity ->
                    entity.asPopulatedNewsResource(topicCrossReferences)
                }
            }
            .map { resources ->
                var result = resources
                if (useFilterTopicIds) {
                    result = result.filter { resource ->
                        resource.topics.any { it.id in filterTopicIds }
                    }
                }
                if (useFilterNewsIds) {
                    result = result.filter { resource ->
                        resource.entity.id in filterNewsIds
                    }
                }
                result
            }

    override fun getNewsResourceIds(
        useFilterTopicIds: Boolean,
        filterTopicIds: Set<String>,
        useFilterNewsIds: Boolean,
        filterNewsIds: Set<String>,
    ): Flow<List<String>> =
        entitiesStateFlow
            .map { newsResourceEntities ->
                newsResourceEntities.map { entity ->
                    entity.asPopulatedNewsResource(topicCrossReferences)
                }
            }
            .map { resources ->
                var result = resources
                if (useFilterTopicIds) {
                    result = result.filter { resource ->
                        resource.topics.any { it.id in filterTopicIds }
                    }
                }
                if (useFilterNewsIds) {
                    result = result.filter { resource ->
                        resource.entity.id in filterNewsIds
                    }
                }
                result.map { it.entity.id }
            }

    override suspend fun upsertNewsResources(newsResourceEntities: List<NewsResourceEntity>) {
        entitiesStateFlow.update { oldValues ->
            // New values come first so they overwrite old values
            (newsResourceEntities + oldValues)
                .distinctBy(NewsResourceEntity::id)
                .sortedWith(
                    compareBy(NewsResourceEntity::publishDate).reversed(),
                )
        }
    }

    override suspend fun insertOrIgnoreTopicCrossRefEntities(
        newsResourceTopicCrossReferences: List<NewsResourceTopicCrossRef>,
    ) {
        // Keep old values over new ones
        topicCrossReferences = (topicCrossReferences + newsResourceTopicCrossReferences)
            .distinctBy { it.newsResourceId to it.topicId }
    }

    override suspend fun deleteNewsResources(ids: List<String>) {
        val idSet = ids.toSet()
        entitiesStateFlow.update { entities ->
            entities.filterNot { it.id in idSet }
        }
    }
}

private fun NewsResourceEntity.asPopulatedNewsResource(
    topicCrossReferences: List<NewsResourceTopicCrossRef>,
) = PopulatedNewsResource(
    entity = this,
    topics = topicCrossReferences
        .filter { it.newsResourceId == id }
        .map { newsResourceTopicCrossRef ->
            TopicEntity(
                id = newsResourceTopicCrossRef.topicId,
                name = "name",
                shortDescription = "short description",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            )
        },
)

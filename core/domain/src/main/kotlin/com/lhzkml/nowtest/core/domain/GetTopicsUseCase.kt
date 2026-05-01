package com.lhzkml.nowtest.core.domain

import com.lhzkml.nowtest.core.data.repository.TopicsRepository
import com.lhzkml.nowtest.core.domain.TopicSortField.NAME
import com.lhzkml.nowtest.core.domain.TopicSortField.NONE
import com.lhzkml.nowtest.core.model.data.Topic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * A use case which obtains a list of topics.
 */
class GetTopicsUseCase @Inject constructor(
    private val topicsRepository: TopicsRepository,
) {
    /**
     * Returns a list of topics.
     *
     * @param sortBy - the field used to sort the topics. Default NONE = no sorting.
     */
    operator fun invoke(sortBy: TopicSortField = NONE): Flow<List<Topic>> =
        topicsRepository.getTopics().map { topics ->
            when (sortBy) {
                NAME -> topics.sortedBy { it.name }
                else -> topics
            }
        }
}

enum class TopicSortField {
    NONE,
    NAME,
}

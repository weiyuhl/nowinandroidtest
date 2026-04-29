package com.google.samples.apps.nowinandroid.core.domain

import com.google.samples.apps.nowinandroid.core.domain.TopicSortField.NAME
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.testing.repository.TestTopicsRepository
import com.google.samples.apps.nowinandroid.core.testing.repository.TestUserDataRepository
import com.google.samples.apps.nowinandroid.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetFollowableTopicsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val topicsRepository = TestTopicsRepository()
    private val userDataRepository = TestUserDataRepository()

    val useCase = GetFollowableTopicsUseCase(
        topicsRepository,
        userDataRepository,
    )

    @Test
    fun whenNoParams_followableTopicsAreReturnedWithNoSorting() = runTest {
        // Obtain a stream of followable topics.
        val followableTopics = useCase()

        // Send some test topics and their followed state.
        topicsRepository.sendTopics(testTopics)
        userDataRepository.setFollowedTopicIds(setOf(testTopics[0].id, testTopics[2].id))

        // Check that the order hasn't changed and that the correct topics are marked as followed.
        assertEquals(
            listOf(
                FollowableTopic(testTopics[0], true),
                FollowableTopic(testTopics[1], false),
                FollowableTopic(testTopics[2], true),
            ),
            followableTopics.first(),
        )
    }

    @Test
    fun whenSortOrderIsByName_topicsSortedByNameAreReturned() = runTest {
        // Obtain a stream of followable topics, sorted by name.
        val followableTopics = useCase(
            sortBy = NAME,
        )

        // Send some test topics and their followed state.
        topicsRepository.sendTopics(testTopics)
        userDataRepository.setFollowedTopicIds(setOf())

        // Check that the followable topics are sorted by the topic name.
        assertEquals(
            followableTopics.first(),
            testTopics
                .sortedBy { it.name }
                .map {
                    FollowableTopic(it, false)
                },
        )
    }
}

private val testTopics = listOf(
    Topic("1", "Headlines", "", "", "", ""),
    Topic("2", "Android Studio", "", "", "", ""),
    Topic("3", "Compose", "", "", "", ""),
)

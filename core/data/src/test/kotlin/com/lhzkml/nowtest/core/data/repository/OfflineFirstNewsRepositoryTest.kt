package com.lhzkml.nowtest.core.data.repository

import com.lhzkml.nowtest.core.data.Synchronizer
import com.lhzkml.nowtest.core.data.model.asEntity
import com.lhzkml.nowtest.core.data.model.topicCrossReferences
import com.lhzkml.nowtest.core.data.model.topicEntityShells
import com.lhzkml.nowtest.core.data.testdoubles.CollectionType
import com.lhzkml.nowtest.core.data.testdoubles.TestNewsResourceDao
import com.lhzkml.nowtest.core.data.testdoubles.TestNtNetworkDataSource
import com.lhzkml.nowtest.core.data.testdoubles.TestTopicDao
import com.lhzkml.nowtest.core.data.testdoubles.filteredInterestsIds
import com.lhzkml.nowtest.core.data.testdoubles.nonPresentInterestsIds
import com.lhzkml.nowtest.core.database.model.NewsResourceEntity
import com.lhzkml.nowtest.core.database.model.NewsResourceTopicCrossRef
import com.lhzkml.nowtest.core.database.model.PopulatedNewsResource
import com.lhzkml.nowtest.core.database.model.TopicEntity
import com.lhzkml.nowtest.core.database.model.asExternalModel
import com.lhzkml.nowtest.core.datastore.NtPreferencesDataSource
import com.lhzkml.nowtest.core.datastore.UserPreferences
import com.lhzkml.nowtest.core.datastore.test.InMemoryDataStore
import com.lhzkml.nowtest.core.model.data.NewsResource
import com.lhzkml.nowtest.core.network.model.NetworkChangeList
import com.lhzkml.nowtest.core.network.model.NetworkNewsResource
import com.lhzkml.nowtest.core.testing.notifications.TestNotifier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OfflineFirstNewsRepositoryTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: OfflineFirstNewsRepository

    private lateinit var ntPreferencesDataSource: NtPreferencesDataSource

    private lateinit var newsResourceDao: TestNewsResourceDao

    private lateinit var topicDao: TestTopicDao

    private lateinit var network: TestNtNetworkDataSource

    private lateinit var notifier: TestNotifier

    private lateinit var synchronizer: Synchronizer

    @Before
    fun setup() {
        ntPreferencesDataSource = NtPreferencesDataSource(InMemoryDataStore(UserPreferences.getDefaultInstance()))
        newsResourceDao = TestNewsResourceDao()
        topicDao = TestTopicDao()
        network = TestNtNetworkDataSource()
        notifier = TestNotifier()
        synchronizer = TestSynchronizer(
            ntPreferencesDataSource,
        )

        subject = OfflineFirstNewsRepository(
            ntPreferencesDataSource = ntPreferencesDataSource,
            newsResourceDao = newsResourceDao,
            topicDao = topicDao,
            network = network,
            notifier = notifier,
        )
    }

    @Test
    fun offlineFirstNewsRepository_news_resources_stream_is_backed_by_news_resource_dao() =
        testScope.runTest {
            subject.syncWith(synchronizer)
            assertEquals(
                newsResourceDao.getNewsResources()
                    .first()
                    .map(PopulatedNewsResource::asExternalModel),
                subject.getNewsResources()
                    .first(),
            )
        }

    @Test
    fun offlineFirstNewsRepository_news_resources_for_topic_is_backed_by_news_resource_dao() =
        testScope.runTest {
            assertEquals(
                expected = newsResourceDao.getNewsResources(
                    filterTopicIds = filteredInterestsIds,
                    useFilterTopicIds = true,
                )
                    .first()
                    .map(PopulatedNewsResource::asExternalModel),
                actual = subject.getNewsResources(
                    query = NewsResourceQuery(
                        filterTopicIds = filteredInterestsIds,
                    ),
                )
                    .first(),
            )

            assertEquals(
                expected = emptyList(),
                actual = subject.getNewsResources(
                    query = NewsResourceQuery(
                        filterTopicIds = nonPresentInterestsIds,
                    ),
                )
                    .first(),
            )
        }

    @Test
    fun offlineFirstNewsRepository_sync_pulls_from_network() =
        testScope.runTest {
            // User has not onboarded
            ntPreferencesDataSource.setShouldHideOnboarding(false)
            subject.syncWith(synchronizer)

            val newsResourcesFromNetwork = network.getNewsResources()
                .map(NetworkNewsResource::asEntity)
                .map(NewsResourceEntity::asExternalModel)

            val newsResourcesFromDb = newsResourceDao.getNewsResources()
                .first()
                .map(PopulatedNewsResource::asExternalModel)

            assertEquals(
                newsResourcesFromNetwork.map(NewsResource::id).sorted(),
                newsResourcesFromDb.map(NewsResource::id).sorted(),
            )

            // After sync version should be updated
            assertEquals(
                expected = network.latestChangeListVersion(CollectionType.NewsResources),
                actual = synchronizer.getChangeListVersions().newsResourceVersion,
            )

            // Notifier should not have been called
            assertTrue(notifier.addedNewsResources.isEmpty())
        }

    @Test
    fun offlineFirstNewsRepository_sync_deletes_items_marked_deleted_on_network() =
        testScope.runTest {
            // User has not onboarded
            ntPreferencesDataSource.setShouldHideOnboarding(false)

            val newsResourcesFromNetwork = network.getNewsResources()
                .map(NetworkNewsResource::asEntity)
                .map(NewsResourceEntity::asExternalModel)

            // Delete half of the items on the network
            val deletedItems = newsResourcesFromNetwork
                .map(NewsResource::id)
                .partition { it.chars().sum() % 2 == 0 }
                .first
                .toSet()

            deletedItems.forEach {
                network.editCollection(
                    collectionType = CollectionType.NewsResources,
                    id = it,
                    isDelete = true,
                )
            }

            subject.syncWith(synchronizer)

            val newsResourcesFromDb = newsResourceDao.getNewsResources()
                .first()
                .map(PopulatedNewsResource::asExternalModel)

            // Assert that items marked deleted on the network have been deleted locally
            assertEquals(
                expected = (newsResourcesFromNetwork.map(NewsResource::id) - deletedItems).sorted(),
                actual = newsResourcesFromDb.map(NewsResource::id).sorted(),
            )

            // After sync version should be updated
            assertEquals(
                expected = network.latestChangeListVersion(CollectionType.NewsResources),
                actual = synchronizer.getChangeListVersions().newsResourceVersion,
            )

            // Notifier should not have been called
            assertTrue(notifier.addedNewsResources.isEmpty())
        }

    @Test
    fun offlineFirstNewsRepository_incremental_sync_pulls_from_network() =
        testScope.runTest {
            // User has not onboarded
            ntPreferencesDataSource.setShouldHideOnboarding(false)

            // Set news version to 7
            synchronizer.updateChangeListVersions {
                copy(newsResourceVersion = 7)
            }

            subject.syncWith(synchronizer)

            val changeList = network.changeListsAfter(
                CollectionType.NewsResources,
                version = 7,
            )
            val changeListIds = changeList
                .map(NetworkChangeList::id)
                .toSet()

            val newsResourcesFromNetwork = network.getNewsResources()
                .map(NetworkNewsResource::asEntity)
                .map(NewsResourceEntity::asExternalModel)
                .filter { it.id in changeListIds }

            val newsResourcesFromDb = newsResourceDao.getNewsResources()
                .first()
                .map(PopulatedNewsResource::asExternalModel)

            assertEquals(
                expected = newsResourcesFromNetwork.map(NewsResource::id).sorted(),
                actual = newsResourcesFromDb.map(NewsResource::id).sorted(),
            )

            // After sync version should be updated
            assertEquals(
                expected = changeList.last().changeListVersion,
                actual = synchronizer.getChangeListVersions().newsResourceVersion,
            )

            // Notifier should not have been called
            assertTrue(notifier.addedNewsResources.isEmpty())
        }

    @Test
    fun offlineFirstNewsRepository_sync_saves_shell_topic_entities() =
        testScope.runTest {
            subject.syncWith(synchronizer)

            assertEquals(
                expected = network.getNewsResources()
                    .map(NetworkNewsResource::topicEntityShells)
                    .flatten()
                    .distinctBy(TopicEntity::id)
                    .sortedBy(TopicEntity::toString),
                actual = topicDao.getTopicEntities()
                    .first()
                    .sortedBy(TopicEntity::toString),
            )
        }

    @Test
    fun offlineFirstNewsRepository_sync_saves_topic_cross_references() =
        testScope.runTest {
            subject.syncWith(synchronizer)

            assertEquals(
                expected = network.getNewsResources()
                    .map(NetworkNewsResource::topicCrossReferences)
                    .flatten()
                    .distinct()
                    .sortedBy(NewsResourceTopicCrossRef::toString),
                actual = newsResourceDao.topicCrossReferences
                    .sortedBy(NewsResourceTopicCrossRef::toString),
            )
        }

    @Test
    fun offlineFirstNewsRepository_sync_marks_as_read_on_first_run() =
        testScope.runTest {
            subject.syncWith(synchronizer)

            assertEquals(
                network.getNewsResources().map { it.id }.toSet(),
                ntPreferencesDataSource.userData.first().viewedNewsResources,
            )
        }

    @Test
    fun offlineFirstNewsRepository_sync_does_not_mark_as_read_on_subsequent_run() =
        testScope.runTest {
            // Pretend that we already have up to change list 7
            synchronizer.updateChangeListVersions {
                copy(newsResourceVersion = 7)
            }

            subject.syncWith(synchronizer)

            assertEquals(
                emptySet(),
                ntPreferencesDataSource.userData.first().viewedNewsResources,
            )
        }

    @Test
    fun offlineFirstNewsRepository_sends_notifications_for_newly_synced_news_when_onboarded() =
        testScope.runTest {
            // User has onboarded
            ntPreferencesDataSource.setShouldHideOnboarding(true)

            val networkNewsResources = network.getNewsResources()

            subject.syncWith(synchronizer)

            val newsResourceIdsFromNetwork = networkNewsResources
                .map(NetworkNewsResource::id)
                .sorted()

            // Notifier should have been called with newly synced news resources.
            assertEquals(
                expected = newsResourceIdsFromNetwork,
                actual = notifier.addedNewsResources.first().map(NewsResource::id).sorted(),
            )
        }

    @Test
    fun offlineFirstNewsRepository_does_not_send_notifications_for_existing_news_resources() =
        testScope.runTest {
            // User has onboarded
            ntPreferencesDataSource.setShouldHideOnboarding(true)

            val networkNewsResources = network.getNewsResources()
                .map(NetworkNewsResource::asEntity)

            val newsResources = networkNewsResources
                .map(NewsResourceEntity::asExternalModel)

            // Prepopulate dao with news resources
            newsResourceDao.upsertNewsResources(networkNewsResources)

            subject.syncWith(synchronizer)

            // Notifier should not have been called bc all news resources existed previously
            assertTrue(notifier.addedNewsResources.isEmpty())
        }
}

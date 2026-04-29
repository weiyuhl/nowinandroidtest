package com.google.samples.apps.nowinandroid.core.data.test.repository

import com.google.samples.apps.nowinandroid.core.common.network.Dispatcher
import com.google.samples.apps.nowinandroid.core.common.network.NiaDispatchers.IO
import com.google.samples.apps.nowinandroid.core.data.Synchronizer
import com.google.samples.apps.nowinandroid.core.data.model.asExternalModel
import com.google.samples.apps.nowinandroid.core.data.repository.NewsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.NewsResourceQuery
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.network.demo.DemoNiaNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Fake implementation of the [NewsRepository] that retrieves the news resources from a JSON String.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
class FakeNewsRepository @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val datasource: DemoNiaNetworkDataSource,
) : NewsRepository {

    override fun getNewsResources(
        query: NewsResourceQuery,
    ): Flow<List<NewsResource>> =
        flow {
            val newsResources = datasource.getNewsResources()
            val topics = datasource.getTopics()

            emit(
                newsResources
                    .filter { networkNewsResource ->
                        // Filter out any news resources which don't match the current query.
                        // If no query parameters (filterTopicIds or filterNewsIds) are specified
                        // then the news resource is returned.
                        listOfNotNull(
                            true,
                            query.filterNewsIds?.contains(networkNewsResource.id),
                            query.filterTopicIds?.let { filterTopicIds ->
                                networkNewsResource.topics.intersect(filterTopicIds).isNotEmpty()
                            },
                        )
                            .all(true::equals)
                    }
                    .map { it.asExternalModel(topics) },
            )
        }.flowOn(ioDispatcher)

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}

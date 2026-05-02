package com.lhzkml.nowtest.route.search.scene

import androidx.lifecycle.SavedStateHandle
import com.lhzkml.nowtest.core.analytics.NoOpAnalyticsHelper
import com.lhzkml.nowtest.core.testing.util.MainDispatcherRule
import com.lhzkml.nowtest.route.search.scene.SearchResultUiState.EmptyQuery
import com.lhzkml.nowtest.route.search.scene.SearchResultUiState.Loading
import com.lhzkml.nowtest.route.search.scene.SearchResultUiState.Success
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class SearchViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        viewModel = SearchViewModel(
            savedStateHandle = SavedStateHandle(),
            analyticsHelper = NoOpAnalyticsHelper(),
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(Loading, viewModel.searchResultUiState.value)
    }

    @Test
    fun stateIsEmptyQuery_withEmptySearchQuery() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.searchResultUiState.collect() }

        viewModel.onSearchQueryChanged("")

        assertEquals(EmptyQuery, viewModel.searchResultUiState.value)
    }

    @Test
    fun stateIsEmptyQuery_withOneCharacterSearchQuery() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.searchResultUiState.collect() }

        viewModel.onSearchQueryChanged("1")

        assertEquals(EmptyQuery, viewModel.searchResultUiState.value)
    }

    @Test
    fun localTestContentIsReturned_with1234SearchQuery() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.searchResultUiState.collect() }

        viewModel.onSearchQueryChanged("1234")

        val result = assertIs<Success>(viewModel.searchResultUiState.value)
        assertEquals(listOf("test-content-1234"), result.results.map { it.id })
    }

    @Test
    fun localTestContentSearchIsCaseInsensitive() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.searchResultUiState.collect() }

        viewModel.onSearchQueryChanged("compose")

        val result = assertIs<Success>(viewModel.searchResultUiState.value)
        assertEquals(listOf("test-content-compose"), result.results.map { it.id })
    }

    @Test
    fun emptyResultIsReturned_withNotMatchingQuery() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.searchResultUiState.collect() }

        viewModel.onSearchQueryChanged("not-found")

        val result = assertIs<Success>(viewModel.searchResultUiState.value)
        assertEquals(emptyList(), result.results)
    }
}

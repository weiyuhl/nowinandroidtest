package com.lhzkml.nowtest.route.settings.scene

import com.lhzkml.nowtest.core.model.data.DarkThemeConfig.DARK
import com.lhzkml.nowtest.core.testing.repository.TestUserDataRepository
import com.lhzkml.nowtest.core.testing.util.MainDispatcherRule
import com.lhzkml.nowtest.route.settings.scene.SettingsUiState.Loading
import com.lhzkml.nowtest.route.settings.scene.SettingsUiState.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userDataRepository = TestUserDataRepository()
    private val appLanguageRepository = FakeAppLanguageRepository()

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        viewModel = SettingsViewModel(userDataRepository, appLanguageRepository)
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(Loading, viewModel.settingsUiState.value)
    }

    @Test
    fun stateIsSuccessAfterUserDataLoaded() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.settingsUiState.collect() }

        userDataRepository.setDarkThemeConfig(DARK)

        assertEquals(
            Success(
                UserEditableSettings(
                    darkThemeConfig = DARK,
                ),
            ),
            viewModel.settingsUiState.value,
        )
    }

    @Test
    fun stateIncludesCurrentAppLanguage() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.settingsUiState.collect() }

        userDataRepository.setDarkThemeConfig(DARK)
        appLanguageRepository.setAppLanguage(AppLanguage.SIMPLIFIED_CHINESE)

        val settings = (viewModel.settingsUiState.value as Success).settings
        assertEquals(AppLanguage.SIMPLIFIED_CHINESE, settings.appLanguage)
    }

    @Test
    fun updateLanguage_changesAppLanguageRepository() = runTest {
        viewModel.updateLanguage(AppLanguage.ENGLISH)

        assertEquals(AppLanguage.ENGLISH, appLanguageRepository.appLanguage.value)
    }

    @Test
    fun updateLanguage_updatesSettingsUiState() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.settingsUiState.collect() }

        userDataRepository.setDarkThemeConfig(DARK)
        viewModel.updateLanguage(AppLanguage.SIMPLIFIED_CHINESE)

        val settings = (viewModel.settingsUiState.value as Success).settings
        assertEquals(AppLanguage.SIMPLIFIED_CHINESE, settings.appLanguage)
    }

    private class FakeAppLanguageRepository : AppLanguageRepository {
        private val mutableAppLanguage = MutableStateFlow(AppLanguage.SYSTEM_DEFAULT)

        override val appLanguage: StateFlow<AppLanguage> = mutableAppLanguage

        override fun setAppLanguage(appLanguage: AppLanguage) {
            mutableAppLanguage.value = appLanguage
        }
    }
}

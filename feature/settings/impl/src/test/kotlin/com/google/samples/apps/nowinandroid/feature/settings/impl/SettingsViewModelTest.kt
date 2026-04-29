package com.google.samples.apps.nowinandroid.feature.settings.impl

import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig.DARK
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand.ANDROID
import com.google.samples.apps.nowinandroid.core.testing.repository.TestUserDataRepository
import com.google.samples.apps.nowinandroid.core.testing.util.MainDispatcherRule
import com.google.samples.apps.nowinandroid.feature.settings.impl.SettingsUiState.Loading
import com.google.samples.apps.nowinandroid.feature.settings.impl.SettingsUiState.Success
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

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        viewModel = SettingsViewModel(userDataRepository)
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(Loading, viewModel.settingsUiState.value)
    }

    @Test
    fun stateIsSuccessAfterUserDataLoaded() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.settingsUiState.collect() }

        userDataRepository.setThemeBrand(ANDROID)
        userDataRepository.setDarkThemeConfig(DARK)

        assertEquals(
            Success(
                UserEditableSettings(
                    brand = ANDROID,
                    darkThemeConfig = DARK,
                    useDynamicColor = false,
                ),
            ),
            viewModel.settingsUiState.value,
        )
    }
}

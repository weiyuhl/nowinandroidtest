package com.lhzkml.nowtest.route.settings.scene

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lhzkml.nowtest.core.data.repository.UserDataRepository
import com.lhzkml.nowtest.core.model.data.DarkThemeConfig
import com.lhzkml.nowtest.route.settings.scene.SettingsUiState.Loading
import com.lhzkml.nowtest.route.settings.scene.SettingsUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val appLanguageRepository: AppLanguageRepository,
) : ViewModel() {
    val settingsUiState: StateFlow<SettingsUiState> =
        combine(
            userDataRepository.userData,
            appLanguageRepository.appLanguage,
        ) { userData, appLanguage ->
            Success(
                settings = UserEditableSettings(
                    darkThemeConfig = userData.darkThemeConfig,
                    appLanguage = appLanguage,
                ),
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(5.seconds.inWholeMilliseconds),
                initialValue = Loading,
            )

    fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            userDataRepository.setDarkThemeConfig(darkThemeConfig)
        }
    }

    fun updateLanguage(appLanguage: AppLanguage) {
        appLanguageRepository.setAppLanguage(appLanguage)
    }
}

/**
 * Represents the settings which the user can edit within the app.
 */
data class UserEditableSettings(
    val darkThemeConfig: DarkThemeConfig,
    val appLanguage: AppLanguage = AppLanguage.SYSTEM_DEFAULT,
)

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val settings: UserEditableSettings) : SettingsUiState
}

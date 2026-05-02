@file:Suppress("ktlint:standard:max-line-length")

package com.lhzkml.nowtest.feature.settings.impl

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.lhzkml.nowtest.core.designsystem.component.NtTextButton
import com.lhzkml.nowtest.core.designsystem.icon.NtIcons
import com.lhzkml.nowtest.core.designsystem.theme.NtTheme
import com.lhzkml.nowtest.core.model.data.DarkThemeConfig
import com.lhzkml.nowtest.core.model.data.DarkThemeConfig.DARK
import com.lhzkml.nowtest.core.model.data.DarkThemeConfig.FOLLOW_SYSTEM
import com.lhzkml.nowtest.core.model.data.DarkThemeConfig.LIGHT
import com.lhzkml.nowtest.core.ui.TrackScreenViewEvent
import com.lhzkml.nowtest.feature.settings.impl.R.string
import com.lhzkml.nowtest.feature.settings.impl.SettingsUiState.Loading
import com.lhzkml.nowtest.feature.settings.impl.SettingsUiState.Success
import com.lhzkml.nowtest.core.ui.R as coreUiR

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()
    SettingsScreen(
        modifier = modifier,
        settingsUiState = settingsUiState,
        onBackClick = onBackClick,
        onChangeDarkThemeConfig = viewModel::updateDarkThemeConfig,
        onChangeLanguage = viewModel::updateLanguage,
    )
}

@Composable
fun SettingsScreen(
    settingsUiState: SettingsUiState,
    onBackClick: () -> Unit = {},
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    onChangeLanguage: (appLanguage: AppLanguage) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    TrackScreenViewEvent(screenName = "Settings")
    Column(modifier = modifier.fillMaxSize()) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        SettingsToolbar(onBackClick = onBackClick)
        HorizontalDivider()
        SettingsContent(
            settingsUiState = settingsUiState,
            onChangeDarkThemeConfig = onChangeDarkThemeConfig,
            onChangeLanguage = onChangeLanguage,
        )
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
    }
}

@Composable
private fun SettingsToolbar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = NtIcons.ArrowBack,
                contentDescription = stringResource(id = coreUiR.string.core_ui_back),
            )
        }
        Text(
            text = stringResource(string.feature_settings_impl_title),
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
private fun ColumnScope.SettingsContent(
    settingsUiState: SettingsUiState,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    onChangeLanguage: (appLanguage: AppLanguage) -> Unit,
) {
    Column(
        Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp),
    ) {
        when (settingsUiState) {
            Loading -> {
                Text(
                    text = stringResource(string.feature_settings_impl_loading),
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }

            is Success -> {
                SettingsPanel(
                    settings = settingsUiState.settings,
                    onChangeDarkThemeConfig = onChangeDarkThemeConfig,
                    onChangeLanguage = onChangeLanguage,
                )
            }
        }
        HorizontalDivider(Modifier.padding(top = 8.dp))
        LinksPanel()
    }
}

// [ColumnScope] is used for using the [ColumnScope.AnimatedVisibility] extension overload composable.
@Composable
private fun ColumnScope.SettingsPanel(
    settings: UserEditableSettings,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    onChangeLanguage: (appLanguage: AppLanguage) -> Unit,
) {
    SettingsSectionTitle(text = stringResource(string.feature_settings_impl_theme))
    SettingsSectionTitle(text = stringResource(string.feature_settings_impl_dark_mode_preference))
    Column(Modifier.selectableGroup()) {
        SettingsThemeChooserRow(
            text = stringResource(string.feature_settings_impl_dark_mode_config_system_default),
            selected = settings.darkThemeConfig == FOLLOW_SYSTEM,
            onClick = { onChangeDarkThemeConfig(FOLLOW_SYSTEM) },
        )
        SettingsThemeChooserRow(
            text = stringResource(string.feature_settings_impl_dark_mode_config_light),
            selected = settings.darkThemeConfig == LIGHT,
            onClick = { onChangeDarkThemeConfig(LIGHT) },
        )
        SettingsThemeChooserRow(
            text = stringResource(string.feature_settings_impl_dark_mode_config_dark),
            selected = settings.darkThemeConfig == DARK,
            onClick = { onChangeDarkThemeConfig(DARK) },
        )
    }
    SettingsSectionTitle(text = stringResource(string.feature_settings_impl_language_preference))
    Column(Modifier.selectableGroup()) {
        SettingsThemeChooserRow(
            text = stringResource(string.feature_settings_impl_language_system_default),
            selected = settings.appLanguage == AppLanguage.SYSTEM_DEFAULT,
            onClick = { onChangeLanguage(AppLanguage.SYSTEM_DEFAULT) },
        )
        SettingsThemeChooserRow(
            text = stringResource(string.feature_settings_impl_language_english),
            selected = settings.appLanguage == AppLanguage.ENGLISH,
            onClick = { onChangeLanguage(AppLanguage.ENGLISH) },
        )
        SettingsThemeChooserRow(
            text = stringResource(string.feature_settings_impl_language_simplified_chinese),
            selected = settings.appLanguage == AppLanguage.SIMPLIFIED_CHINESE,
            onClick = { onChangeLanguage(AppLanguage.SIMPLIFIED_CHINESE) },
        )
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    )
}

@Composable
fun SettingsThemeChooserRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LinksPanel() {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        val context = LocalContext.current
        NtTextButton(
            onClick = {
                context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            },
        ) {
            Text(text = stringResource(string.feature_settings_impl_licenses))
        }
    }
}

@Preview
@Composable
private fun PreviewSettingsScreen() {
    NtTheme {
        SettingsScreen(
            settingsUiState = Success(
                UserEditableSettings(
                    darkThemeConfig = FOLLOW_SYSTEM,
                ),
            ),
            onChangeDarkThemeConfig = {},
        )
    }
}

@Preview
@Composable
private fun PreviewSettingsScreenLoading() {
    NtTheme {
        SettingsScreen(
            settingsUiState = Loading,
            onChangeDarkThemeConfig = {},
        )
    }
}

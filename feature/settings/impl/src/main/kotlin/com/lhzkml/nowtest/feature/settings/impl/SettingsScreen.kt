@file:Suppress("ktlint:standard:max-line-length")

package com.lhzkml.nowtest.feature.settings.impl

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.LocalUriHandler
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
import com.lhzkml.nowtest.core.designsystem.theme.supportsDynamicTheming
import com.lhzkml.nowtest.core.model.data.DarkThemeConfig
import com.lhzkml.nowtest.core.model.data.DarkThemeConfig.DARK
import com.lhzkml.nowtest.core.model.data.DarkThemeConfig.FOLLOW_SYSTEM
import com.lhzkml.nowtest.core.model.data.DarkThemeConfig.LIGHT
import com.lhzkml.nowtest.core.model.data.ThemeBrand
import com.lhzkml.nowtest.core.model.data.ThemeBrand.ANDROID
import com.lhzkml.nowtest.core.model.data.ThemeBrand.DEFAULT
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
        onChangeThemeBrand = viewModel::updateThemeBrand,
        onChangeDynamicColorPreference = viewModel::updateDynamicColorPreference,
        onChangeDarkThemeConfig = viewModel::updateDarkThemeConfig,
    )
}

@Composable
fun SettingsScreen(
    settingsUiState: SettingsUiState,
    supportDynamicColor: Boolean = supportsDynamicTheming(),
    onBackClick: () -> Unit = {},
    onChangeThemeBrand: (themeBrand: ThemeBrand) -> Unit,
    onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    modifier: Modifier = Modifier,
) {
    TrackScreenViewEvent(screenName = "Settings")
    Column(modifier = modifier.fillMaxSize()) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        SettingsToolbar(onBackClick = onBackClick)
        HorizontalDivider()
        SettingsContent(
            settingsUiState = settingsUiState,
            supportDynamicColor = supportDynamicColor,
            onChangeThemeBrand = onChangeThemeBrand,
            onChangeDynamicColorPreference = onChangeDynamicColorPreference,
            onChangeDarkThemeConfig = onChangeDarkThemeConfig,
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
    supportDynamicColor: Boolean,
    onChangeThemeBrand: (themeBrand: ThemeBrand) -> Unit,
    onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
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
                    supportDynamicColor = supportDynamicColor,
                    onChangeThemeBrand = onChangeThemeBrand,
                    onChangeDynamicColorPreference = onChangeDynamicColorPreference,
                    onChangeDarkThemeConfig = onChangeDarkThemeConfig,
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
    supportDynamicColor: Boolean,
    onChangeThemeBrand: (themeBrand: ThemeBrand) -> Unit,
    onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
) {
    SettingsSectionTitle(text = stringResource(string.feature_settings_impl_theme))
    Column(Modifier.selectableGroup()) {
        SettingsThemeChooserRow(
            text = stringResource(string.feature_settings_impl_brand_default),
            selected = settings.brand == DEFAULT,
            onClick = { onChangeThemeBrand(DEFAULT) },
        )
        SettingsThemeChooserRow(
            text = stringResource(string.feature_settings_impl_brand_android),
            selected = settings.brand == ANDROID,
            onClick = { onChangeThemeBrand(ANDROID) },
        )
    }
    AnimatedVisibility(visible = settings.brand == DEFAULT && supportDynamicColor) {
        Column {
            SettingsSectionTitle(text = stringResource(string.feature_settings_impl_dynamic_color_preference))
            Column(Modifier.selectableGroup()) {
                SettingsThemeChooserRow(
                    text = stringResource(string.feature_settings_impl_dynamic_color_yes),
                    selected = settings.useDynamicColor,
                    onClick = { onChangeDynamicColorPreference(true) },
                )
                SettingsThemeChooserRow(
                    text = stringResource(string.feature_settings_impl_dynamic_color_no),
                    selected = !settings.useDynamicColor,
                    onClick = { onChangeDynamicColorPreference(false) },
                )
            }
        }
    }
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
        val uriHandler = LocalUriHandler.current
        NtTextButton(
            onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) },
        ) {
            Text(text = stringResource(string.feature_settings_impl_privacy_policy))
        }
        val context = LocalContext.current
        NtTextButton(
            onClick = {
                context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            },
        ) {
            Text(text = stringResource(string.feature_settings_impl_licenses))
        }
        NtTextButton(
            onClick = { uriHandler.openUri(BRAND_GUIDELINES_URL) },
        ) {
            Text(text = stringResource(string.feature_settings_impl_brand_guidelines))
        }
        NtTextButton(
            onClick = { uriHandler.openUri(FEEDBACK_URL) },
        ) {
            Text(text = stringResource(string.feature_settings_impl_feedback))
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
                    brand = DEFAULT,
                    darkThemeConfig = FOLLOW_SYSTEM,
                    useDynamicColor = false,
                ),
            ),
            onChangeThemeBrand = {},
            onChangeDynamicColorPreference = {},
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
            onChangeThemeBrand = {},
            onChangeDynamicColorPreference = {},
            onChangeDarkThemeConfig = {},
        )
    }
}

private const val PRIVACY_POLICY_URL = "https://policies.google.com/privacy"
private const val BRAND_GUIDELINES_URL = "https://developer.android.com/distribute/marketing-tools/brand-guidelines"
private const val FEEDBACK_URL = "https://goo.gle/nt-app-feedback"

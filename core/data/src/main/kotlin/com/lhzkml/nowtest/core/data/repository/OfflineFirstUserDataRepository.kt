package com.lhzkml.nowtest.core.data.repository

import com.lhzkml.nowtest.core.analytics.AnalyticsHelper
import com.lhzkml.nowtest.core.datastore.NtPreferencesDataSource
import com.lhzkml.nowtest.core.model.data.DarkThemeConfig
import com.lhzkml.nowtest.core.model.data.ThemeBrand
import com.lhzkml.nowtest.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class OfflineFirstUserDataRepository @Inject constructor(
    private val ntPreferencesDataSource: NtPreferencesDataSource,
    private val analyticsHelper: AnalyticsHelper,
) : UserDataRepository {

    override val userData: Flow<UserData> =
        ntPreferencesDataSource.userData

    override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) =
        ntPreferencesDataSource.setNewsResourceViewed(newsResourceId, viewed)

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        ntPreferencesDataSource.setThemeBrand(themeBrand)
        analyticsHelper.logThemeChanged(themeBrand.name)
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        ntPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
        analyticsHelper.logDarkThemeConfigChanged(darkThemeConfig.name)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        ntPreferencesDataSource.setDynamicColorPreference(useDynamicColor)
        analyticsHelper.logDynamicColorPreferenceChanged(useDynamicColor)
    }
}

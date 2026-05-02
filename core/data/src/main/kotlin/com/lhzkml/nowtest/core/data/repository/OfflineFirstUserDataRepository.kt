package com.lhzkml.nowtest.core.data.repository

import com.lhzkml.nowtest.core.analytics.AnalyticsHelper
import com.lhzkml.nowtest.core.datastore.NtPreferencesDataSource
import com.lhzkml.nowtest.core.model.data.DarkThemeConfig
import com.lhzkml.nowtest.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class OfflineFirstUserDataRepository @Inject constructor(
    private val ntPreferencesDataSource: NtPreferencesDataSource,
    private val analyticsHelper: AnalyticsHelper,
) : UserDataRepository {

    override val userData: Flow<UserData> =
        ntPreferencesDataSource.userData

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        ntPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
        analyticsHelper.logDarkThemeConfigChanged(darkThemeConfig.name)
    }
}

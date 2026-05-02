package com.lhzkml.nowtest.core.data.test.repository

import com.lhzkml.nowtest.core.data.repository.UserDataRepository
import com.lhzkml.nowtest.core.datastore.NtPreferencesDataSource
import com.lhzkml.nowtest.core.model.data.DarkThemeConfig
import com.lhzkml.nowtest.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Fake implementation of the [UserDataRepository] that returns hardcoded user data.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
class FakeUserDataRepository @Inject constructor(
    private val ntPreferencesDataSource: NtPreferencesDataSource,
) : UserDataRepository {

    override val userData: Flow<UserData> =
        ntPreferencesDataSource.userData

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        ntPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }
}

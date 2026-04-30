package com.lhzkml.nowtest.core.data.repository

import com.lhzkml.nowtest.core.data.Synchronizer
import com.lhzkml.nowtest.core.datastore.ChangeListVersions
import com.lhzkml.nowtest.core.datastore.NtPreferencesDataSource

/**
 * Test synchronizer that delegates to [NtPreferencesDataSource]
 */
class TestSynchronizer(
    private val ntPreferences: NtPreferencesDataSource,
) : Synchronizer {
    override suspend fun getChangeListVersions(): ChangeListVersions =
        ntPreferences.getChangeListVersions()

    override suspend fun updateChangeListVersions(
        update: ChangeListVersions.() -> ChangeListVersions,
    ) = ntPreferences.updateChangeListVersion(update)
}

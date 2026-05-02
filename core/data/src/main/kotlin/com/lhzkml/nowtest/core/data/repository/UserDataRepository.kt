package com.lhzkml.nowtest.core.data.repository

import com.lhzkml.nowtest.core.model.data.DarkThemeConfig
import com.lhzkml.nowtest.core.model.data.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    /**
     * Stream of [UserData]
     */
    val userData: Flow<UserData>

    /**
     * Sets the desired dark theme config.
     */
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)
}

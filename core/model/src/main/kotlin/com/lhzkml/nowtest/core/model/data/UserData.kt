package com.lhzkml.nowtest.core.model.data

data class UserData(
    val viewedNewsResources: Set<String>,
    val themeBrand: ThemeBrand,
    val darkThemeConfig: DarkThemeConfig,
    val useDynamicColor: Boolean,
)

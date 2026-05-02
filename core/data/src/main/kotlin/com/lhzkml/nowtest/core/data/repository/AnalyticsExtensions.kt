package com.lhzkml.nowtest.core.data.repository

import com.lhzkml.nowtest.core.analytics.AnalyticsEvent
import com.lhzkml.nowtest.core.analytics.AnalyticsEvent.Param
import com.lhzkml.nowtest.core.analytics.AnalyticsHelper

internal fun AnalyticsHelper.logDarkThemeConfigChanged(darkThemeConfigName: String) =
    logEvent(
        AnalyticsEvent(
            type = "dark_theme_config_changed",
            extras = listOf(
                Param(key = "dark_theme_config", value = darkThemeConfigName),
            ),
        ),
    )

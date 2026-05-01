package com.lhzkml.nowtest.core.data.repository

import com.lhzkml.nowtest.core.analytics.AnalyticsEvent
import com.lhzkml.nowtest.core.analytics.AnalyticsEvent.Param
import com.lhzkml.nowtest.core.analytics.AnalyticsHelper

internal fun AnalyticsHelper.logThemeChanged(themeName: String) =
    logEvent(
        AnalyticsEvent(
            type = "theme_changed",
            extras = listOf(
                Param(key = "theme_name", value = themeName),
            ),
        ),
    )

internal fun AnalyticsHelper.logDarkThemeConfigChanged(darkThemeConfigName: String) =
    logEvent(
        AnalyticsEvent(
            type = "dark_theme_config_changed",
            extras = listOf(
                Param(key = "dark_theme_config", value = darkThemeConfigName),
            ),
        ),
    )

internal fun AnalyticsHelper.logDynamicColorPreferenceChanged(useDynamicColor: Boolean) =
    logEvent(
        AnalyticsEvent(
            type = "dynamic_color_preference_changed",
            extras = listOf(
                Param(key = "dynamic_color_preference", value = useDynamicColor.toString()),
            ),
        ),
    )

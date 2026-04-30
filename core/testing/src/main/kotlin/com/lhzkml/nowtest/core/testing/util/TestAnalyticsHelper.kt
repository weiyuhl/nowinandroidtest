package com.lhzkml.nowtest.core.testing.util

import com.lhzkml.nowtest.core.analytics.AnalyticsEvent
import com.lhzkml.nowtest.core.analytics.AnalyticsHelper

class TestAnalyticsHelper : AnalyticsHelper {

    private val events = mutableListOf<AnalyticsEvent>()
    override fun logEvent(event: AnalyticsEvent) {
        events.add(event)
    }

    fun hasLogged(event: AnalyticsEvent) = event in events
}

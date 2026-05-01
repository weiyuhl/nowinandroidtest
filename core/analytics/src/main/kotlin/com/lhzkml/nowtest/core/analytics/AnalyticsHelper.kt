package com.lhzkml.nowtest.core.analytics

/**
 * Interface for logging analytics events.
 */
interface AnalyticsHelper {
    fun logEvent(event: AnalyticsEvent)
}

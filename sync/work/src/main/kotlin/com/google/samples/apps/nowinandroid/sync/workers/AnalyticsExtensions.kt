package com.google.samples.apps.nowinandroid.sync.workers

import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsHelper

internal fun AnalyticsHelper.logSyncStarted() =
    logEvent(
        AnalyticsEvent(type = "network_sync_started"),
    )

internal fun AnalyticsHelper.logSyncFinished(syncedSuccessfully: Boolean) {
    val eventType = if (syncedSuccessfully) "network_sync_successful" else "network_sync_failed"
    logEvent(
        AnalyticsEvent(type = eventType),
    )
}

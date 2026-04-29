package com.google.samples.apps.nowinandroid.core.testing.notifications

import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.notifications.Notifier

/**
 * Aggregates news resources that have been notified for addition
 */
class TestNotifier : Notifier {

    private val mutableAddedNewResources = mutableListOf<List<NewsResource>>()

    val addedNewsResources: List<List<NewsResource>> = mutableAddedNewResources

    override fun postNewsNotifications(newsResources: List<NewsResource>) {
        mutableAddedNewResources.add(newsResources)
    }
}

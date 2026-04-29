package com.google.samples.apps.nowinandroid.core.notifications

import com.google.samples.apps.nowinandroid.core.model.data.NewsResource

/**
 * Interface for creating notifications in the app
 */
interface Notifier {
    fun postNewsNotifications(newsResources: List<NewsResource>)
}

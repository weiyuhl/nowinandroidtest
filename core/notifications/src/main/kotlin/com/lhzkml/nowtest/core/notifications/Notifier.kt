package com.lhzkml.nowtest.core.notifications

import com.lhzkml.nowtest.core.model.data.NewsResource

/**
 * Interface for creating notifications in the app
 */
interface Notifier {
    fun postNewsNotifications(newsResources: List<NewsResource>)
}

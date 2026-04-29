package com.google.samples.apps.nowinandroid.core.notifications

import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import javax.inject.Inject

/**
 * Implementation of [Notifier] which does nothing. Useful for tests and previews.
 */
internal class NoOpNotifier @Inject constructor() : Notifier {
    override fun postNewsNotifications(newsResources: List<NewsResource>) = Unit
}

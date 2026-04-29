package com.google.samples.apps.nowinandroid.sync.status

/**
 * Subscribes to backend requested synchronization
 */
interface SyncSubscriber {
    suspend fun subscribe()
}

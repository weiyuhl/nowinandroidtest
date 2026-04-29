package com.google.samples.apps.nowinandroid.core.data.test

import com.google.samples.apps.nowinandroid.core.data.util.TimeZoneMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.TimeZone
import javax.inject.Inject

class DefaultZoneIdTimeZoneMonitor @Inject constructor() : TimeZoneMonitor {
    override val currentTimeZone: Flow<TimeZone> = flowOf(TimeZone.of("Europe/Warsaw"))
}

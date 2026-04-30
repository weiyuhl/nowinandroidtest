package com.lhzkml.nowtest.core.common.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val ntDispatcher: NtDispatchers)

enum class NtDispatchers {
    Default,
    IO,
}

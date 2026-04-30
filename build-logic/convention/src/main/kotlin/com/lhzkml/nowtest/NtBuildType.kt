package com.lhzkml.nowtest

/**
 * This is shared between :app and :benchmarks module to provide configurations type safety.
 */
enum class NtBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE,
}

plugins {
    alias(libs.plugins.nowinandroid.android.library)
    alias(libs.plugins.nowinandroid.android.library.jacoco)
    alias(libs.plugins.nowinandroid.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.google.samples.apps.nowinandroid.core.data"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    api(projects.core.common)
    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.network)

    implementation(projects.core.analytics)
    implementation(projects.core.notifications)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(projects.core.datastoreTest)
    testImplementation(projects.core.testing)
}

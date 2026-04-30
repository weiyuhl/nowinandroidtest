plugins {
    alias(libs.plugins.nowtest.android.library)
    alias(libs.plugins.nowtest.android.library.jacoco)
    alias(libs.plugins.nowtest.hilt)
}

android {
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
    namespace = "com.lhzkml.nowtest.core.datastore"
}

dependencies {
    api(libs.androidx.dataStore)
    api(projects.core.datastoreProto)
    api(projects.core.model)

    implementation(projects.core.common)

    testImplementation(projects.core.datastoreTest)
    testImplementation(libs.kotlinx.coroutines.test)
}

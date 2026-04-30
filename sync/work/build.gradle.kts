plugins {
    alias(libs.plugins.nowtest.android.library)
    alias(libs.plugins.nowtest.android.library.jacoco)
    alias(libs.plugins.nowtest.hilt)
}

android {
    defaultConfig {
        testInstrumentationRunner = "com.lhzkml.nowtest.core.testing.NtTestRunner"
    }
    namespace = "com.lhzkml.nowtest.sync"
}

dependencies {
    ksp(libs.hilt.ext.compiler)

    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)
    implementation(projects.core.analytics)
    implementation(projects.core.data)
    implementation(projects.core.notifications)

    prodImplementation(libs.firebase.cloud.messaging)
    prodImplementation(platform(libs.firebase.bom))

    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.kotlinx.coroutines.guava)
    androidTestImplementation(projects.core.testing)
}

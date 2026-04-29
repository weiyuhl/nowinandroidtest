plugins {
    alias(libs.plugins.nowinandroid.android.library)
    alias(libs.plugins.nowinandroid.android.library.jacoco)
    alias(libs.plugins.nowinandroid.hilt)
}

android {
    defaultConfig {
        testInstrumentationRunner = "com.google.samples.apps.nowinandroid.core.testing.NiaTestRunner"
    }
    namespace = "com.google.samples.apps.nowinandroid.sync"
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

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.google.samples.apps.nowinandroid.configureBadgingTasks
import com.google.samples.apps.nowinandroid.configureGradleManagedDevices
import com.google.samples.apps.nowinandroid.configureKotlinAndroid
import com.google.samples.apps.nowinandroid.configurePrintApksTask
import com.google.samples.apps.nowinandroid.configureSpotlessForAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

abstract class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "nowinandroid.android.lint")
            apply(plugin = "com.dropbox.dependency-guard")

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 36
                testOptions.animationsDisabled = true
                configureGradleManagedDevices(this)
            }
            extensions.configure<ApplicationAndroidComponentsExtension> {
                configurePrintApksTask(this)
                configureBadgingTasks(this)
            }
            configureSpotlessForAndroid()
        }
    }
}

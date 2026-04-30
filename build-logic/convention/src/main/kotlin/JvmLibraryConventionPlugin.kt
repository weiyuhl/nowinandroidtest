import com.lhzkml.nowtest.configureKotlinJvm
import com.lhzkml.nowtest.configureSpotlessForJvm
import com.lhzkml.nowtest.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

abstract class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jetbrains.kotlin.jvm")
            apply(plugin = "nowtest.android.lint")

            configureKotlinJvm()
            configureSpotlessForJvm()
            dependencies {
                "testImplementation"(libs.findLibrary("kotlin.test").get())
            }
        }
    }
}

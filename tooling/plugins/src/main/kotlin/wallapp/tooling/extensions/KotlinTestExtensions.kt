package wallapp.tooling.extensions

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

internal fun Project.configureKotlinTestExtensions() {

    dependencies {
        add("commonTestImplementation", (kotlin("test")))
        add("commonTestImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
    }
}
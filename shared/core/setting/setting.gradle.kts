import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget()
    desktop()
    iOS()

    sourceSets {
        configureAllSourceSets()

        commonMain {
            dependencies {
                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:unit"))

                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.multiplatform.settings)
            }
        }

        commonTest.dependencies {
            implementation(libs.multiplatform.settings.test)
        }

        androidMain {
            dependencies {
                api(libs.androidx.core)
                api(libs.androidx.lifecycle.livedata)
                api(libs.androidx.lifecycle.runtime)
            }
        }
    }
}

android {
    namespace = "wallapp.settings"
}


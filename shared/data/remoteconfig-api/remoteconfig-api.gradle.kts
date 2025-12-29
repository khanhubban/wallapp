import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
    alias(libs.plugins.serialization)
}
apply(from = "${rootProject.projectDir}/moduleflavors.gradle")

kotlin {
    androidTarget()
    desktop()
    iOS()

    sourceSets {
        configureAllSourceSets()

        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.skie.annotations)
                implementation(project(":shared:core:common"))
                implementation(project(":shared:data:base"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.core)
                implementation(libs.androidx.lifecycle.extensions)
            }
        }
    }
}

android {
    namespace = "wallapp.remoteconfig.api"
}


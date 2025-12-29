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
                implementation(project(":shared:core:common"))
                api(project(":shared:core:download"))
                implementation(project(":shared:core:security"))
                implementation(project(":shared:data:base"))
            }
        }
    }
}

android {
    namespace = "wallapp.remoteapi"
}


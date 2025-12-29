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
                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:network"))
                implementation(project(":shared:core:download"))
                implementation(project(":shared:data:account-api"))
                implementation(project(":shared:data:base"))
                implementation(project(":shared:data:remoteapi"))
                implementation(project(":shared:data:resources"))
            }
        }
    }
}

android {
    namespace = "wallapp.remoteendpoint"
}

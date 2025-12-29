
import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget()
    iOS()
    desktop()

    sourceSets {
        configureAllSourceSets()

        commonMain
        commonTest

        val desktopMain by getting {
            dependencies {
                implementation(libs.google.cloud.storage)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.okio)

                implementation(project(":shared:core:common"))
                api(project(":shared:core:download"))
                implementation(project(":shared:core:monitoring:monitoring-api"))
                implementation(project(":shared:core:unit"))
                implementation(project(":shared:domain:content-state"))
            }
        }
    }
}

android {
    namespace = "wallapp.admin"
}
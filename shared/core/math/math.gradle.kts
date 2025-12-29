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
        sourceSets["commonMain"].dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(project(":shared:core:unit"))
        }
    }
}

android {
    namespace = "wallapp.math"
}
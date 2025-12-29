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
                implementation(libs.koin.core)

                implementation(project(":shared:app:app-adapter"))
                implementation(project(":shared:data:billing:api"))
                implementation(project(":shared:domain:content-state"))
                implementation(project(":shared:domain:license-state"))
                implementation(project(":shared:domain:licensing-billing"))
            }
        }
    }
}

android {
    namespace = "wallapp.di.buildconfig.release"

    buildFeatures {
        buildConfig = false
    }
}

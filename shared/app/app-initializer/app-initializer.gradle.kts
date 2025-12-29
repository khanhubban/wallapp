import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
}

kotlin {
    androidTarget()
    desktop()
    iOS()

    sourceSets {
        configureAllSourceSets()

        commonMain {
            dependencies {
                implementation(libs.koin.core)
                implementation(project(":shared:core:common"))
                implementation(project(":shared:domain:content-state"))
                implementation(project(":shared:di:di-base"))
            }
        }

        val desktopMain by getting
    }
}

android {
    namespace = "wallapp.app.initializer"
}
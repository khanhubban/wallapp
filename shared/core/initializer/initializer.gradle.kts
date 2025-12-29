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
                api(project(":shared:core:di"))
                api(project(":shared:core:monitoring:monitoring-api"))
            }
        }

        val desktopMain by getting
    }
}

android {
    namespace = "wallapp.initializer"
}
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
                api(libs.androidx.lifecycle.viewmodel)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.okio)
                api(libs.precompose.core)
                api(libs.precompose.viewmodel)
                implementation(project(":shared:core:monitoring:monitoring-api"))
                implementation(project(":shared:core:common"))
            }
        }
    }
}

android {
    namespace = "wallapp.viewmodel"
}
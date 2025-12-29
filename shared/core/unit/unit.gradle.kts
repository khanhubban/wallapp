import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
    id("wallapp.compose")
}

kotlin {
    androidTarget()
    desktop()
    iOS()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.skie.annotations)
                api(compose.runtime)
                api(compose.foundation)
                api(compose.ui)
            }
        }
    }
}

android {
    namespace = "wallapp.unit"
}
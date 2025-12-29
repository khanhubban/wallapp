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

        androidMain {
            dependencies {
                implementation(libs.androidx.annotation)
                implementation(libs.androidx.core)
            }
        }
    }
}

android {
//    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "wallapp.platform.common"
}

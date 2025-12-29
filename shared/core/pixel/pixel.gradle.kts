import wallapp.tooling.extensions.configureAllSourceSets
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
        configureAllSourceSets()

        commonMain {
            dependencies {
                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:monitoring:monitoring-api"))
                implementation(project(":shared:core:resource"))
                implementation(project(":shared:core:unit"))

                implementation(compose.material3)
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(libs.skie.annotations)
            }
        }

        androidMain {
            dependencies {
                implementation((libs.androidx.activity.compose))
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.coil)
                implementation(libs.coil.base)
            }
        }
    }
}

android {
    namespace = "wallapp.pixel"
}
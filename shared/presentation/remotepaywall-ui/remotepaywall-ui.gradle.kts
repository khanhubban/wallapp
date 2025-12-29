import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
    id("wallapp.compose")
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
                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:pixel"))
                api(project(":shared:data:remotepaywall"))

                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.runtime)
                implementation(compose.ui)
            }
        }

        val androidDebug by creating {
            dependencies {
                implementation(compose.preview)
                implementation(compose.uiTooling)
                implementation(libs.compose.ui.tooling)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.revenuecat.purchases)
            }
        }
    }
}

android {
    namespace = "wallapp.billing.remotepaywall.ui"
}
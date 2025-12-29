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
                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:pixel"))
                implementation(project(":shared:core:viewmodel"))
                implementation(project(":shared:data:resources"))
                implementation(project(":shared:data:toolkit"))

                implementation(libs.kotlin.reflect)
                implementation(libs.skie.annotations)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.interpolator)
                implementation(libs.coil)
            }
        }
    }
}

android {
    buildFeatures {
        buildConfig = true
    }
    namespace = "wallapp.ads.api"
}

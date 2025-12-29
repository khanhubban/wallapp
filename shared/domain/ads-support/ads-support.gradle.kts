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
                implementation(project(":shared:data:ads-api"))
                implementation(project(":shared:data:ads-internal"))
                implementation(project(":shared:core:common"))
                implementation(project(":shared:data:resources"))
                implementation(project(":shared:data:toolkit"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.constraintlayout)
                implementation(libs.androidx.core)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.robolectric)
                implementation(libs.androidx.arch.core.testing)
            }
        }
    }

}

android {
    buildFeatures {
        resValues = true
    }
    namespace = "wallapp.ads_support"
}

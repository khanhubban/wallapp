import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
    id("wallapp.compose")
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
                implementation(project(":shared:data:account-data"))
                implementation(project(":shared:data:ads-api"))
                implementation(project(":shared:data:privacymessaging-api"))
                implementation(project(":shared:data:toolkit"))
                implementation(project(":shared:domain:ads-support"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.constraintlayout)
                implementation(libs.androidx.core)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.gms.play.services.ads)
                implementation(libs.material)

                implementation(project(":shared:data:resources"))
                implementation(project(":shared:presentation:pixel-ui"))
                implementation(project(":shared:presentation:wallapp-ui"))
            }
        }
    }
}

android {
    namespace = "wallapp.ads_admob"

    buildFeatures {
        resValues = true
    }
}


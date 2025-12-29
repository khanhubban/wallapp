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
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.koin.core)

                implementation(project(":shared:app:app-adapter"))
                implementation(project(":shared:app:app-adapter-debug"))
                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:download"))
                implementation(project(":shared:core:monitoring:monitoring-impl"))
                implementation(project(":shared:core:network"))
                implementation(project(":shared:core:pixel"))
                implementation(project(":shared:core:resource"))
                implementation(project(":shared:core:viewmodel"))
                implementation(project(":shared:data:account"))
                implementation(project(":shared:data:account-data"))
                implementation(project(":shared:data:account-signin"))
                implementation(project(":shared:data:ads-admob"))
                implementation(project(":shared:data:ads-api"))
                implementation(project(":shared:data:billing:api"))
                implementation(project(":shared:data:billing:debug"))
                implementation(project(":shared:data:billing:revenuecat"))
                implementation(project(":shared:data:cloudmessaging-api"))
                implementation(project(":shared:data:cloudmessaging-firebase"))
                implementation(project(":shared:data:content"))
                implementation(project(":shared:data:content-network"))
                implementation(project(":shared:data:datafactory"))
                implementation(project(":shared:data:deeplink"))
                implementation(project(":shared:data:licensing-api"))
                implementation(project(":shared:data:mediamap"))
                implementation(project(":shared:data:mediamap-network"))
                implementation(project(":shared:data:profileimage-api"))
                implementation(project(":shared:data:profileimage-impl"))
                implementation(project(":shared:data:purchase"))
                implementation(project(":shared:data:remoteconfig-api"))
                implementation(project(":shared:data:remoteendpoint"))
                implementation(project(":shared:data:remotepaywall"))
                implementation(project(":shared:data:resources"))
                implementation(project(":shared:data:search"))
                implementation(project(":shared:data:server-firebase"))
                implementation(project(":shared:data:toolkit"))
                implementation(project(":shared:data:wallpaper"))
                implementation(project(":shared:domain:ads-support"))
                implementation(project(":shared:domain:billing-support"))
                implementation(project(":shared:domain:content-state"))
                implementation(project(":shared:domain:license-state"))
                implementation(project(":shared:domain:licensing-billing"))
                implementation(project(":shared:domain:remotepaywall-support"))
                implementation(project(":shared:interop:bridge"))
                implementation(project(":shared:presentation:pixel-ui"))
                implementation(project(":shared:presentation:wallapp-ui"))
                implementation(project(":shared:system:common"))
            }
        }
    }
}

android {
    namespace = "wallapp.di.buildconfig.debug"

    buildFeatures {
        buildConfig = false
    }
}

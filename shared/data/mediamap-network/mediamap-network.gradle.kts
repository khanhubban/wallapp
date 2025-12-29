import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget()
    desktop()
    iOS()

    sourceSets {
        configureAllSourceSets()

        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:network"))
                implementation(project(":shared:core:resource"))
                implementation(project(":shared:core:unit"))
                implementation(project(":shared:data:base"))
                implementation(project(":shared:data:content-model"))
                implementation(project(":shared:data:remoteapi"))
                implementation(project(":shared:data:remoteendpoint"))
                implementation(project(":shared:data:resources"))
            }
        }
    }
}

android {
    namespace = "wallapp.data.mediamap.network"
}
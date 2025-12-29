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
                implementation(project(":shared:core:download"))
                implementation(project(":shared:core:network"))
                implementation(project(":shared:core:unit"))
                implementation(project(":shared:data:base"))
                implementation(project(":shared:data:remoteapi"))
                implementation(project(":shared:data:remoteendpoint"))
                implementation(project(":shared:data:resources"))
                implementation(project(":shared:data:search-model"))
                implementation(project(":shared:data:toolkit"))
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlinx.coroutines.test)
                implementation(project(":shared:test:test-common"))
            }
        }
    }
}

android {
    namespace = "wallapp.search.network"
}
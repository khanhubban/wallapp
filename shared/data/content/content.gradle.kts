import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
    alias(libs.plugins.serialization)
//    alias(libs.plugins.kotlin.kapt)
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
                implementation(libs.ktor.client.core)

                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:pixel"))
                implementation(project(":shared:core:resource"))
                api(project(":shared:data:account-api"))
                api(project(":shared:data:account-data"))
                api(project(":shared:data:base"))
                implementation(project(":shared:data:billing:api"))
                api(project(":shared:data:content-api"))
                api(project(":shared:data:content-model"))
                implementation(project(":shared:data:content-network"))
                api(project(":shared:data:deeplink"))
                implementation(project(":shared:data:mediamap"))
                implementation(project(":shared:data:mediamap-network"))
                implementation(project(":shared:data:purchase"))
                implementation(project(":shared:data:remoteconfig-api"))
                implementation(project(":shared:data:remoteendpoint"))
                implementation(project(":shared:data:resources"))
                implementation(project(":shared:data:search-api"))
                implementation(project(":shared:data:search-model"))
                implementation(project(":shared:data:search-network"))
                implementation(project(":shared:data:toolkit"))
                implementation(project(":shared:domain:license-state"))
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.junit)
                implementation(libs.androidx.arch.core.testing)
                implementation(libs.mockito.core)
                implementation(libs.robolectric)
                implementation(libs.turbine)
            }
        }
    }
}

android {
    namespace = "wallapp.content"
}

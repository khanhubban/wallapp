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
                api(project(":shared:data:base"))
                api(project(":shared:data:content-model"))
                implementation(project(":shared:data:account-api"))
                implementation(project(":shared:data:content-api"))
                implementation(project(":shared:data:content-network"))
                implementation(project(":shared:data:mediamap-network"))
                implementation(project(":shared:data:remoteapi"))
                implementation(project(":shared:data:remoteconfig-api"))
                implementation(project(":shared:data:remoteendpoint"))
                implementation(project(":shared:data:resources"))
                implementation(project(":shared:data:toolkit"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
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
    namespace = "wallapp.data.mediamap"
}

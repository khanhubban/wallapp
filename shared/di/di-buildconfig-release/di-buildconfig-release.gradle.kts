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
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.koin.core)

                implementation(project(":shared:app:app-adapter"))
                implementation(project(":shared:data:billing:api"))
                implementation(project(":shared:data:remoteconfig-api"))
                implementation(project(":shared:data:remoteendpoint"))
                implementation(project(":shared:domain:content-state"))
                implementation(project(":shared:domain:license-state"))
                implementation(project(":shared:domain:licensing-billing"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.koin.core)
                implementation(project(":shared:data:remoteconfig-api"))
                implementation(project(":shared:data:remoteendpoint"))
            }
        }
    }
}

android {
    namespace = "wallapp.di.buildconfig.release"

    buildFeatures {
        buildConfig = false
    }
}

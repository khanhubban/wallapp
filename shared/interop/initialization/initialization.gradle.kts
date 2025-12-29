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
                implementation(compose.foundation)
                implementation(compose.runtime)
                implementation(libs.koin.core)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.precompose.core)

                api(project(":shared:app:app-adapter"))
                implementation(project(":shared:app:app-initializer"))
                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:di"))
                implementation(project(":shared:core:viewmodel"))
                implementation(project(":shared:data:account-api"))
                implementation(project(":shared:data:remoteendpoint"))
                implementation(project(":shared:data:toolkit"))
                implementation(project(":shared:di:di-base"))
                api(project(":shared:domain:content-state"))
                implementation(project(":shared:system:common"))
            }
        }

        androidMain
        val desktopMain by getting {
            dependencies {
                implementation(libs.ktor.client.core)
            }
        }
    }
}

android {
    namespace = "wallapp.interop.initialization"
}

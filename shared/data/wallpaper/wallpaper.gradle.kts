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

                implementation(project(":shared:core:common"))
                implementation(project(":shared:data:resources"))
                implementation(project(":shared:core:download"))
                api(project(":shared:data:base"))
                implementation(project(":shared:data:content"))
                implementation(project(":shared:data:mediamap"))
                implementation(project(":shared:data:toolkit"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.koin.android)
                implementation(libs.androidx.core)
                implementation(libs.androidx.work.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.turbine)
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
    namespace = "wallapp.wallpaper"
}

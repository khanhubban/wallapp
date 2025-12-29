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
                implementation(libs.kotlinx.serialization.properties)
                api(libs.skie.annotations)

                implementation(project(":shared:core:common"))
                api(project(":shared:data:account-api"))
                implementation(project(":shared:data:account-data"))
                api(project(":shared:data:account-model"))
                implementation(project(":shared:data:content-model"))
                implementation(project(":shared:data:resources"))
                implementation(project(":shared:data:wallpaper"))
                implementation(project(":shared:data:toolkit"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.gms.play.services.auth)
                implementation(libs.kotlinx.coroutines.play.services)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.junit)
                implementation(libs.androidx.arch.core.testing)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.mockito.core)
                implementation(libs.mockk)
                implementation(libs.robolectric)
                implementation(libs.turbine)
            }
        }
    }
}

android {
    namespace = "wallapp.account"
}

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
                implementation(project(":shared:core:network"))
                api(project(":shared:data:content"))
                api(project(":shared:data:content-model"))
                api(project(":shared:data:content-network"))
                api(project(":shared:data:mediamap"))
                implementation(project(":shared:data:purchase"))
                implementation(project(":shared:data:remoteendpoint"))
                implementation(project(":shared:data:resources"))
                api(project(":shared:data:search-api"))
                api(project(":shared:data:search-model"))
                api(project(":shared:data:search-network"))
                api(project(":shared:data:toolkit"))
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlinx.coroutines.test)
                implementation(project(":shared:test:test-common"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.gms.play.services.auth)
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
    namespace = "wallapp.search"
}

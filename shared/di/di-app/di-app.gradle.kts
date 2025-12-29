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
                implementation(libs.koin.core)

                api(project(":shared:di:di-base"))

                implementation(project(":shared:app:app-adapter"))
                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:pixel"))
                implementation(project(":shared:core:resource"))
                implementation(project(":shared:core:viewmodel"))
                implementation(project(":shared:data:ads-admob"))
                implementation(project(":shared:data:ads-api"))
                implementation(project(":shared:data:billing:api"))
                implementation(project(":shared:data:content"))
                implementation(project(":shared:data:content-network"))
                implementation(project(":shared:data:licensing-api"))
                implementation(project(":shared:data:resources"))
                implementation(project(":shared:data:remoteconfig-api"))
                implementation(project(":shared:data:toolkit"))
                implementation(project(":shared:data:wallpaper"))
                implementation(project(":shared:domain:ads-support"))
                implementation(project(":shared:domain:content-state"))
                implementation(project(":shared:domain:license-state"))
                implementation(project(":shared:domain:licensing-billing"))
                implementation(project(":shared:interop:bridge"))
                implementation(project(":shared:interop:initialization"))
                implementation(project(":shared:system:common"))
            }
        }

        commonTest {
            dependencies {
                implementation(project(":shared:test:test-common"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.koin.android)
                implementation(project(":shared:data:billing:revenuecat"))
                implementation(project(":shared:data:licensing-debug"))
                implementation(project(":shared:data:remoteconfig-firebase"))
                implementation(project(":shared:domain:billing-support"))
                implementation(project(":shared:presentation:wallapp-ui"))
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.androidx.arch.core.testing)
                implementation(libs.androidx.test.core)
                implementation(libs.koin.test.junit4)
                implementation(libs.robolectric)
            }
        }

        val desktopTest by getting {
            dependencies {
                implementation(libs.koin.test.junit4)
            }
        }

        iosMain {
            dependencies {
                implementation(project(":shared:data:remoteconfig-firebase"))
            }
        }
    }
}

android {
    namespace = "wallapp.di"
}

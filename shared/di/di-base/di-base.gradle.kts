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
                implementation(libs.koin.core)
                implementation(libs.ktor.client.core)
                implementation(libs.multiplatform.settings)
                implementation(libs.seiko.imageloader)

                implementation(project(":shared:app:app-adapter"))
                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:download"))
                implementation(project(":shared:core:monitoring:monitoring-impl"))
                implementation(project(":shared:core:network"))
                implementation(project(":shared:core:pixel"))
                implementation(project(":shared:core:resource"))
                implementation(project(":shared:core:security"))
                implementation(project(":shared:core:viewmodel"))
                implementation(project(":shared:data:account"))
                implementation(project(":shared:data:account-api"))
                implementation(project(":shared:data:account-data"))
                implementation(project(":shared:data:account-signin"))
                implementation(project(":shared:data:ads-admob"))
                implementation(project(":shared:data:ads-api"))
                implementation(project(":shared:data:ads-internal"))
                implementation(project(":shared:data:billing:api"))
                implementation(project(":shared:data:billing:revenuecat"))
                implementation(project(":shared:data:cloudmessaging-api"))
                implementation(project(":shared:data:cloudmessaging-firebase"))
                implementation(project(":shared:data:content"))
                implementation(project(":shared:data:content-network"))
                implementation(project(":shared:data:datafactory"))
                implementation(project(":shared:data:deeplink"))
                implementation(project(":shared:data:licensing-api"))
                implementation(project(":shared:data:mediamap"))
                implementation(project(":shared:data:mediamap-network"))
                implementation(project(":shared:data:privacymessaging-api"))
                implementation(project(":shared:data:privacymessaging-google"))
                implementation(project(":shared:data:profileimage-api"))
                implementation(project(":shared:data:profileimage-impl"))
                implementation(project(":shared:data:purchase"))
                implementation(project(":shared:data:remoteapi"))
                implementation(project(":shared:data:remoteconfig-api"))
                implementation(project(":shared:data:remoteendpoint"))
                implementation(project(":shared:data:remotepaywall"))
                implementation(project(":shared:data:resources"))
                implementation(project(":shared:data:search"))
                implementation(project(":shared:data:server-firebase"))
                implementation(project(":shared:data:toolkit"))
                implementation(project(":shared:data:wallpaper"))
                implementation(project(":shared:domain:ads-support"))
                implementation(project(":shared:domain:billing-support"))
                implementation(project(":shared:domain:content-state"))
                implementation(project(":shared:domain:license-state"))
                implementation(project(":shared:domain:licensing-billing"))
                implementation(project(":shared:domain:remotepaywall-support"))
                implementation(project(":shared:interop:bridge"))
                implementation(project(":shared:presentation:pixel-ui"))
                implementation(project(":shared:presentation:wallapp-ui"))
                implementation(project(":shared:system:common"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.security.crypto)
                implementation(libs.coil.base)
                implementation(libs.gms.play.services.auth)
                implementation(libs.koin.android)
                implementation(libs.okhttp3.logging.interceptor)
                implementation(libs.okhttp3.okhttp)

                implementation(project(":shared:data:account-data-firebase"))
                implementation(project(":shared:data:account-firebase"))
                implementation(project(":shared:data:licensing-debug"))
                implementation(project(":shared:data:privacymessaging-google"))
                implementation(project(":shared:data:remoteconfig-firebase"))
            }
        }

        // Defining different modules to link with in Debug/Release builds requires a unique
        // definition for each platform. #1925
        val androidDebug by creating {
            dependencies {
                implementation(project(":shared:di:di-buildconfig-debug"))
            }
        }
        val androidRelease by creating {
            dependencies {
                implementation(project(":shared:di:di-buildconfig-release"))
            }
        }

        iosMain {
            dependencies {
                implementation(project(":shared:data:account-data-firebase"))
                implementation(project(":shared:data:account-firebase"))
                implementation(project(":shared:data:remoteconfig-firebase"))
            }

            /**
             * Defining different modules to link with in Debug/Release builds requires a unique
             * definition for each platform. See #1925.
             */
            // Retrieve the iOS build configuration (Debug or Release)
            val buildConfiguration = System.getenv("CONFIGURATION") ?: "Release"
            println("** Xcode build configuration: $buildConfiguration")
            if (buildConfiguration == "Debug") {
                dependencies {
                    implementation(project(":shared:di:di-buildconfig-debug"))
                }
            } else {
                dependencies {
                    implementation(project(":shared:di:di-buildconfig-release"))
                }
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(project(":shared:admin:admin"))
                // Desktop always uses Debug for now
                implementation(project(":shared:di:di-buildconfig-debug"))
            }
        }
    }
}

android {
    namespace = "wallapp.di.base"

    buildFeatures {
        buildConfig = true
    }
}

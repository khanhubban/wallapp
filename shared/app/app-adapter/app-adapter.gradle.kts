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
                implementation(libs.koin.core)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.androidx.lifecycle.viewmodel.compose)
                implementation(libs.seiko.imageloader)

                implementation(compose.material3)
                implementation(compose.runtime)

                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:di"))
                implementation(project(":shared:core:download"))
                implementation(project(":shared:core:monitoring:monitoring-impl"))
                implementation(project(":shared:core:network"))
                implementation(project(":shared:core:pixel"))
                implementation(project(":shared:core:resource"))
                implementation(project(":shared:core:security"))
                implementation(project(":shared:core:viewmodel"))
                implementation(project(":shared:data:account"))
                implementation(project(":shared:data:account-data"))
                implementation(project(":shared:data:account-signin"))
                implementation(project(":shared:data:ads-admob"))
                implementation(project(":shared:data:ads-api"))
                implementation(project(":shared:data:ads-internal"))
                implementation(project(":shared:data:billing:api"))
                implementation(project(":shared:data:cloudmessaging-api"))
                implementation(project(":shared:data:cloudmessaging-firebase"))
                implementation(project(":shared:data:content"))
                implementation(project(":shared:data:datafactory"))
                implementation(project(":shared:data:licensing-api"))
                implementation(project(":shared:data:mediamap"))
                implementation(project(":shared:data:mediamap-network"))
                implementation(project(":shared:data:privacymessaging-api"))
                implementation(project(":shared:data:profileimage-api"))
                implementation(project(":shared:data:purchase"))
                implementation(project(":shared:data:remoteapi"))
                implementation(project(":shared:data:remoteconfig-api"))
                implementation(project(":shared:data:remoteendpoint"))
                implementation(project(":shared:data:remotepaywall"))
                implementation(project(":shared:data:resources"))
                implementation(project(":shared:data:search"))
                implementation(project(":shared:data:search-api"))
                implementation(project(":shared:data:search-network"))
                implementation(project(":shared:data:toolkit"))
                implementation(project(":shared:data:wallpaper"))
                implementation(project(":shared:domain:ads-support"))
                implementation(project(":shared:domain:billing-support"))
                implementation(project(":shared:domain:content-state"))
                implementation(project(":shared:domain:license-state"))
                implementation(project(":shared:domain:remotepaywall-support"))
                implementation(project(":shared:presentation:pixel-ui"))
                implementation(project(":shared:presentation:wallapp-ui"))
                implementation(project(":shared:system:common"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.firebase.admin)
                implementation(libs.koin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.multiplatform.settings)
                implementation(libs.multiplatform.settings.test)
                implementation(libs.turbine)
                implementation(project(":shared:test:test-common"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.lifecycle.process)
                api(libs.androidx.lifecycle.runtime)
                api(libs.androidx.navigation.runtime)
                api(libs.androidx.navigation.fragment)
                api(libs.androidx.work.runtime)
                implementation(libs.coil.base)
                implementation(libs.koin.android)
                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.auth)
                implementation(libs.firebase.common)
                implementation(libs.firebase.firestore)
                implementation(libs.firebase.messaging)
                implementation(libs.firebase.storage)
                implementation(libs.gms.play.services.auth)

                implementation(project(":shared:data:billing:revenuecat"))
                implementation(project(":shared:data:privacymessaging-google"))

                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.window)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.koin.test.junit4)
                implementation(libs.junit)
                implementation(libs.androidx.arch.core.testing)
                implementation(libs.androidx.test.core)
                implementation(libs.net.lachlanmckee.timber.junit.rule)
                implementation(libs.robolectric)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.gitlive.firebase.app)
            }
        }
        iosArm64Main {
            dependencies {
                implementation(libs.koin.core.iosArm64)
            }
        }
        iosSimulatorArm64Main {
            dependencies {
                implementation(libs.koin.core.iosSimulatorArm64)
            }
        }
    }
}

android {
    namespace = "wallapp.app.adapter"

    buildFeatures {
        buildConfig = true
        compose = true
    }
}

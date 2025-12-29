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
                implementation(libs.okio)
                implementation(libs.seiko.imageloader)
                implementation(libs.skie.annotations)
                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:download"))
                implementation(project(":shared:core:pixel"))
                implementation(project(":shared:core:resource"))
                implementation(project(":shared:core:security"))
                implementation(project(":shared:domain:license-state"))
                api(project(":shared:data:base"))
                implementation(project(":shared:data:content-model"))
                implementation(project(":shared:data:remoteconfig-api"))
                implementation(project(":shared:data:remoteendpoint"))
                implementation(project(":shared:data:resources"))
                implementation(project(":shared:system:common"))
            }
        }

        commonTest {
            dependencies {
                implementation(libs.junit)
                implementation(libs.kotlin.reflect)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.core)

                implementation(libs.compose.material3)
                implementation(libs.compose.runtime)
                implementation(libs.compose.ui)
                implementation(libs.compose.ui.graphics)

                implementation(libs.process.phoenix)
                implementation(libs.timber)
                implementation(libs.okhttp3.okhttp)
                implementation(libs.koin.android)

                implementation(libs.gms.play.services.instantapps)
                implementation(libs.play.app.update)
                implementation(libs.play.app.update.ktx)
                implementation(libs.play.review)
                implementation(libs.play.review.ktx)

                implementation(libs.zip4j)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.androidx.arch.core.testing)
                implementation(libs.net.lachlanmckee.timber.junit.rule)
                implementation(libs.robolectric)
                implementation(libs.androidx.test.core)
            }
        }

        androidInstrumentedTest {
            dependencies {
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.androidx.test.espresso.core)
                implementation(libs.androidx.test.espresso.contrib)
                implementation(libs.androidx.test.espresso.intents)
                implementation(libs.androidx.test.runner)
                implementation(libs.androidx.test.rules)
                implementation(libs.androidx.arch.core.testing)
            }
        }
    }
}

android {
    buildFeatures {
        aidl = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    namespace = "wallapp.toolkit"
}

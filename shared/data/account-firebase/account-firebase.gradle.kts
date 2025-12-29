import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
    alias(libs.plugins.serialization)
//    alias(libs.plugins.kotlin.kapt)
}
apply(from = "${rootProject.projectDir}/moduleflavors.gradle")

kotlin {
    androidTarget()
//    desktop()
    iOS()

    sourceSets {
        configureAllSourceSets(configureDesktop = false)

        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.gitlive.firebase.app)
                implementation(libs.gitlive.firebase.auth)
                implementation(libs.gitlive.firebase.firestore)

                implementation(project(":shared:core:common"))
                implementation(project(":shared:data:account"))
                implementation(project(":shared:data:base"))
                implementation(project(":shared:data:server-firebase"))
            }
        }

        androidMain {
            dependencies {
                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation("com.google.firebase:firebase-auth")
                implementation("com.google.firebase:firebase-firestore-ktx")
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

        androidInstrumentedTest {
            dependencies {
                implementation(project(":shared:data:account-data"))
                implementation(project(":shared:data:base"))
                implementation(project(":shared:data:toolkit"))
                implementation(libs.androidx.arch.core.testing)
                implementation(libs.androidx.test.core)
                implementation(libs.androidx.test.ext.junit)
                implementation(libs.androidx.test.rules)
                implementation(libs.androidx.test.runner)
                implementation(libs.gitlive.firebase.app)
                implementation(libs.gitlive.firebase.auth)
                implementation(libs.gitlive.firebase.firestore)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.mockk.android)
                implementation(libs.turbine)
            }
        }
    }
}

android {
    namespace = "wallapp.account.firebase"
}

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
                implementation(project(":shared:data:account"))
                implementation(project(":shared:data:account-data"))
                implementation(project(":shared:data:content-model"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.gms.play.services.auth)
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
    }
}

android {
    namespace = "wallapp.account.signin"
}


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

                implementation(project(":shared:core:common"))
                implementation(project(":shared:data:account"))
                implementation(project(":shared:data:content"))
                implementation(project(":shared:data:content-model"))
                implementation(project(":shared:data:server-firebase"))
                implementation(project(":shared:data:toolkit"))

                implementation(libs.gitlive.firebase.app)
                implementation(libs.gitlive.firebase.auth)
                implementation(libs.gitlive.firebase.firestore)
            }
        }
    }
}

android {
    namespace = "wallapp.server"
}

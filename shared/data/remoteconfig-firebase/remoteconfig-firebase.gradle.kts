import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
    alias(libs.plugins.serialization)
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
                implementation(libs.gitlive.firebase.remoteconfig)
                implementation(libs.skie.annotations)

                api(project(":shared:data:remoteconfig-api"))
                implementation(project(":shared:data:toolkit"))
                implementation(project(":shared:core:common"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.core)
                implementation(libs.androidx.lifecycle.extensions)

                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation("com.google.firebase:firebase-installations-ktx")
                implementation("com.google.firebase:firebase-config")
            }
        }
    }
}

android {
    namespace = "wallapp.remoteconfig.firebase"
}


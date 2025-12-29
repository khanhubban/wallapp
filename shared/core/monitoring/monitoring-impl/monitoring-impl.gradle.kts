import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
    alias(libs.plugins.serialization)
}

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
                api(project(":shared:core:monitoring:monitoring-api"))
                implementation(project(":shared:core:unit"))
            }
        }

        androidMain {
            dependencies {
                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation("com.google.firebase:firebase-crashlytics")
                implementation(libs.crashkios.crashlytics)
                implementation(libs.timber)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.crashkios.crashlytics)
            }
        }
    }
}

android {
    namespace = "wallapp.monitoring.impl"
}
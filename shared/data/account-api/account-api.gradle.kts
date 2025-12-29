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
                implementation(libs.kotlinx.serialization.properties)
                api(libs.skie.annotations)

                api(project(":shared:data:account-model"))
                implementation(project(":shared:core:common"))
//                implementation(project(":shared:data:account-data"))
//                implementation(project(":shared:data:content-model"))
//                implementation(project(":shared:data:resources"))
//                implementation(project(":shared:data:wallpaper"))
            }
        }
    }
}

android {
    namespace = "wallapp.account.api"
}

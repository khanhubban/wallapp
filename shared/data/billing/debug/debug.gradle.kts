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
                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:pixel"))
                implementation(project(":shared:data:billing:api"))

                implementation(libs.kotlinx.serialization.json)
            }
        }
    }

}

android {
    namespace = "wallapp.billing.debug"
}

import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
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
                implementation(libs.kotlinx.coroutines.core)
                implementation(project(":shared:core:common"))
                implementation(project(":shared:data:billing:api"))
                implementation(project(":shared:data:licensing-api"))
                implementation(project(":shared:data:purchase"))
                implementation(project(":shared:domain:billing-support"))
                implementation(project(":shared:domain:content-state"))
            }
        }
    }

}

android {
    namespace = "wallapp.licensing.billing"
}

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
                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:pixel"))
                implementation(project(":shared:data:account"))
                implementation(project(":shared:data:toolkit"))
                api(project(":shared:data:billing:api"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.revenuecat.purchases)
            }
        }
    }
}

android {
    namespace = "wallapp.billing.revenuecat"
}


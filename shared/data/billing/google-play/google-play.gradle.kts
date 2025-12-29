plugins {
    id("wallapp.kotlin.multiplatform")
}
apply(from = "${rootProject.projectDir}/moduleflavors.gradle")

kotlin {
    androidTarget()

    sourceSets {
        androidMain {
            dependencies {
                api(libs.billingclient.billing)

                implementation(project(":shared:core:common"))
                api(project(":shared:data:billing:api"))
            }
        }
    }
}

android {
    namespace = "wallapp.billing.play"
}


import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
    alias(libs.plugins.serialization)
    alias(libs.plugins.skie)
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
                implementation(project(":shared:core:download"))
                implementation(project(":shared:core:pixel"))
                implementation(project(":shared:core:resource"))
                implementation(project(":shared:core:viewmodel"))
                implementation(project(":shared:data:account"))
                implementation(project(":shared:data:account-signin"))
                implementation(project(":shared:data:ads-api"))
                implementation(project(":shared:data:ads-internal"))
                implementation(project(":shared:data:billing:api"))
                implementation(project(":shared:data:content"))
                implementation(project(":shared:data:datafactory"))
                implementation(project(":shared:data:deeplink"))
                implementation(project(":shared:data:mediamap"))
                implementation(project(":shared:data:privacymessaging-api"))
                implementation(project(":shared:data:profileimage-api"))
                implementation(project(":shared:data:purchase"))
                implementation(project(":shared:data:remoteapi"))
                implementation(project(":shared:data:remoteconfig-api"))
                implementation(project(":shared:data:remoteendpoint"))
                implementation(project(":shared:data:remotepaywall"))
                implementation(project(":shared:data:resources"))
                implementation(project(":shared:data:search"))
                implementation(project(":shared:data:search-api"))
                implementation(project(":shared:data:search-model"))
                implementation(project(":shared:data:toolkit"))
                implementation(project(":shared:data:wallpaper"))
                implementation(project(":shared:domain:ads-support"))
                implementation(project(":shared:domain:billing-support"))
                implementation(project(":shared:domain:license-state"))
                implementation(project(":shared:domain:remotepaywall-support"))

                implementation(libs.precompose.core)
                implementation(libs.skie.annotations)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.navigation.compose)
                implementation(libs.androidx.navigation.runtime)
                implementation(libs.androidx.work.runtime)
            }
        }
    }
}

android {
    namespace = "wallapp.content.state"
}

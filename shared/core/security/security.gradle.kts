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
                implementation(project.dependencies.platform(libs.whyoleg.cryptography.bom))
                implementation(libs.whyoleg.cryptography.core)

                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:unit"))
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlinx.coroutines.test)
                implementation(project(":shared:test:test-common"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.whyoleg.cryptography.provider.jdk)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.whyoleg.cryptography.provider.jdk)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.whyoleg.cryptography.provider.openssl3.prebuilt)
            }
        }
    }
}

android {
    namespace = "wallapp.network"
}
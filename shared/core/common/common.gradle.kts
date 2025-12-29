import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
    id("org.jetbrains.kotlinx.atomicfu")
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
                api(project(":shared:core:di"))
                api(project(":shared:core:initializer"))
                api(project(":shared:core:math"))
                api(project(":shared:core:monitoring:monitoring-api"))
                implementation(project(":shared:core:resource"))
                api(project(":shared:core:unit"))
                api(project(":shared:system:common"))

                implementation(libs.kotlinx.atomicfu)
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlin.reflect)
                implementation(libs.okio)
                implementation(libs.seiko.imageloader)
                implementation(libs.skie.annotations)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.browser)
                implementation(libs.androidx.exifinterface)
                implementation(libs.androidx.lifecycle.runtime)
                implementation(libs.androidx.navigation.runtime)
                implementation(libs.androidx.palette)
                implementation(libs.androidx.work.runtime)
                implementation(libs.coil)
                implementation(libs.coil.base)
                implementation(libs.coil.compose)
                api(libs.kotlinx.coroutines.android)
                implementation(libs.kpermissions.android)
                implementation(libs.okhttp3.okhttp)
                implementation(libs.zip4j)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.junit)
                implementation(libs.androidx.arch.core.testing)
                implementation(libs.net.lachlanmckee.timber.junit.rule)
                implementation(libs.robolectric)
                implementation(libs.mockito.core)
                implementation(libs.easytesting.fest.assert.core)
            }
        }
    }

}

android {
    namespace = "wallapp.common"

    buildFeatures {
        buildConfig = true
    }
}

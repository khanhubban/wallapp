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
        implementation(libs.androidx.appcompat)
        implementation(libs.androidx.constraintlayout)
        implementation(libs.androidx.localbroadcastmanager)
        implementation(libs.timber)

        implementation(project(":shared:core:common"))
        api(project(":shared:data:billing:google-play"))
      }
    }
    
    androidUnitTest {
      dependencies {
        implementation(libs.androidx.test.core)
        implementation(libs.junit)
        implementation(libs.net.lachlanmckee.timber.junit.rule)
        implementation(libs.truth)
        implementation(libs.nhaarman.mockito.kotlin)
        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlin.reflect)
        implementation(libs.robolectric)
      }
    }
  }
}

android {
  buildFeatures {
    aidl = true
    resValues = true
  }
  namespace = "wallapp.billing.play.debug"
}

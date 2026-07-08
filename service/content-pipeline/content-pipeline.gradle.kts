plugins {
    kotlin("jvm")
}

group = "com.wallapp.service.contentpipeline"
version = "1.0.0"

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.ktor.client.core)
    api(libs.ktor.client.okhttp)
    implementation(project(":shared:core:common"))
    implementation(project(":shared:data:content-network"))
    implementation(project(":shared:data:mediamap-network"))
    implementation(project(":shared:data:search-model"))
    testImplementation(kotlin("test"))
}

tasks.test { useJUnitPlatform() }

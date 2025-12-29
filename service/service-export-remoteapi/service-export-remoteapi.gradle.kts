plugins {
    kotlin("jvm")
}

group = "com.wallapp.service.remoteapiexport"
version = "1.0.0"

dependencies {
    implementation(project(":service:service-common"))
    implementation(project(":shared:core:security"))
    implementation(project(":shared:data:remoteapi"))
}

tasks.register<JavaExec>("run") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("wallapp.remoteapi.RemoteApiExportKt")
}

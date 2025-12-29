
plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.licensee.gradlePlugin)
//    compileOnly(libs.spotless.gradlePlugin)
//    implementation(libs.licensee.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatform") {
            id = "wallapp.kotlin.multiplatform"
            implementationClass = "wallapp.tooling.KotlinMultiplatformLibraryPlugin"
        }
        register("compose") {
            id = "wallapp.compose"
            implementationClass = "wallapp.tooling.ComposeConventionPlugin"
        }
    }
}

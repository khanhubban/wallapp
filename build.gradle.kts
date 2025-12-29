import com.android.build.gradle.BaseExtension
//import com.github.jk1.license.render.JsonReportRenderer
//import com.github.jk1.license.render.SimpleHtmlReportRenderer

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    extra.apply {
        set("buildToolsVersion", libs.versions.buildTools.get())
        set("compileSdkVersion", 35)
        set("minSdkVersion", 26)
        set("targetSdkVersion", 35)
    }

    repositories {
        flatDir { dirs("./libraries") }
    }
    dependencies {
        classpath(libs.android.gradlePlugin)
        classpath(libs.firebase.crashlytics.gradlePlugin)
        classpath(libs.gms.google.services.gradlePlugin)
        classpath(libs.kotlin.gradlePlugin)
        classpath(libs.kotlinx.atomicfu.gradlePlugin)
//        classpath(libs.autonomousapps.dependency.analysis.gradlePlugin)
//        classpath(libs.osacky.doctor.gradlePlugin)
//        classpath(libs.ben.manes.gradle.versions.gradlePlugin)
    }
}

// Disabled for now as it breaks when updating tools: https://issuetracker.google.com/issues/235538823
//apply plugin: "com.osacky.doctor"

plugins {
//    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.androidTest) apply false
    alias(libs.plugins.androidx.baselineprofile) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlinx.atomicfu) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.licensee) apply false
//    alias(libs.plugins.dependencyLicenseReport)
//    id("com.github.ben-manes.versions") version "0.51.0"
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

//    tasks.withType<KotlinCompilationTask<*>>().configureEach {
//        compilerOptions {
//            // Treat all Kotlin warnings as errors
//            allWarningsAsErrors.set(true)
//        }
//    }
}

task<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

subprojects {
    project.afterEvaluate {
        val project = this@afterEvaluate
        if (project.plugins.hasPlugin("com.android.application") || project.plugins.hasPlugin("com.android.library")) {
            addCommonConfigurationForAndroidModules(project)
        }

        if (project.plugins.hasPlugin("com.android.library")) {
            addCommonConfigurationForAndroidLibraries(project)
        }
    }
}

subprojects {
    tasks.register("listAllDependencies", DependencyReportTask::class)
}


fun addCommonConfigurationForAndroidModules(project: Project) {
    project.extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    project.extensions.configure<BaseExtension> {
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

//        (this as ExtensionAware).extensions.configure<KotlinJvmOptions> {
//            jvmTarget = "11"
//        }

        compileSdkVersion(project.rootProject.extra["compileSdkVersion"] as Int)
        buildToolsVersion(project.rootProject.extra["buildToolsVersion"] as String)

        defaultConfig {
            minSdk = project.rootProject.extra["minSdkVersion"] as Int
            targetSdk = project.rootProject.extra["targetSdkVersion"] as Int
            vectorDrawables.useSupportLibrary = true
            testApplicationId = "com.wallapp.test"
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            testInstrumentationRunnerArguments["clearPackageData"] = "true"
        }

        packagingOptions {
            resources {
                excludes.apply {
                    add("META-INF/LICENSE.txt")
                    add("META-INF/NOTICE.txt")
                    add("META-INF/LICENSE")
                    add("META-INF/LICENSE.md")
                    add("META-INF/LICENSE-notice.md")
                    add("META-INF/NOTICE")
                    add("META-INF/AL2.0")
                    add("META-INF/LGPL2.1")
                    add("META-INF/versions/9/previous-compilation-data.bin")
                    add(".readme")
                    add("README.txt")
                }
            }
        }
    }
}

fun addCommonConfigurationForAndroidLibraries(project: Project) {
    project.extensions.configure<BaseExtension> {
        signingConfigs {
//            create("debug") {
//                storeFile = project.file("app/android/debug.keystore")
//                storePassword = "android"
//                keyAlias = "androiddebugkey"
//                keyPassword = "android"
//            }
        }
    }
}


//dependencyUpdates.resolutionStrategy {
//    componentSelection { rules ->
//        rules.all { ComponentSelection selection ->
//            boolean rejected = ['alpha', 'beta', 'rc'].any { qualifier ->
//                selection.candidate.version ==~ /(?i).*[.-]${qualifier}[.\d-]*/
//            }
//            if (rejected) {
//                selection.reject('Release candidate')
//            }
//        }
//    }
//}

project.afterEvaluate {
    println("Configuration cache property \"org.gradle.unsafe.configuration-cache\": ${findProperty("org.gradle.unsafe.configuration-cache")}")

    gradle.startParameter.taskRequests.forEach { taskRequest ->
        if (taskRequest.args.contains("--no-configuration-cache")) {
            println("The --no-configuration-cache option was used")
//        } else {
//            println("[config] The --no-configuration-cache option was not used")
        }
        println("Task request args: ${taskRequest.args}")
    }
}

//licenseReport {
//    outputDir = "${layout.buildDirectory.get()}/reports/licenses"
//    renderers = arrayOf(SimpleHtmlReportRenderer(), JsonReportRenderer())
//}
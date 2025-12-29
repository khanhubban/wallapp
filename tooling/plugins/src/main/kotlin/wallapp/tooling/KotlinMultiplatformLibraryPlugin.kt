package wallapp.tooling

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import wallapp.tooling.extensions.configureKotlinMultiplatform
import wallapp.tooling.extensions.configureKotlinTestExtensions

class KotlinMultiplatformLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("com.android.library")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinMultiplatform(this)
                defaultConfig.targetSdk = 35
            }

            extensions.configure<KotlinMultiplatformExtension> {
                applyDefaultHierarchyTemplate()

//                jvm()
                if (pluginManager.hasPlugin("com.android.library")) {
                    androidTarget()
                }

                iosArm64()
                iosSimulatorArm64()

                targets.withType<KotlinNativeTarget>().configureEach {
                    compilations.configureEach {
                        compileTaskProvider.configure {
                            compilerOptions {
                                // Various opt-ins
                                freeCompilerArgs.addAll(
                                    "-opt-in=kotlinx.cinterop.ExperimentalForeignApi",
                                    "-opt-in=kotlinx.cinterop.BetaInteropApi",
                                )

                                // TODO: Check me with iOS "Dev" target.
                                if (name.startsWith("ios")) {
                                    freeCompilerArgs.add("-Xbinary=bundleId=com.example.wallpapers.appstore")
                                }
                            }
                        }
                    }
                }

                targets.configureEach {
                    compilations.configureEach {
                        compileTaskProvider.configure {
                            compilerOptions {
                                freeCompilerArgs.add("-Xexpect-actual-classes")
                            }
                        }
                    }
                }

//                jvm("desktop") {
//                    compilations.all { kotlinOptions.jvmTarget = "17" }
//
//                    testRuns["test"].executionTask.configure {
//                        javaLauncher.set(target.extensions.getByType<org.gradle.jvm.toolchain.JavaToolchainService>().launcherFor {
//                            languageVersion.set(JavaLanguageVersion.of(17))
//                        })
//                    }
//                }

                if (path.contains("shared")) {
                    configureLicensee()
                }
            }
            configureKotlinTestExtensions()
        }
    }
}
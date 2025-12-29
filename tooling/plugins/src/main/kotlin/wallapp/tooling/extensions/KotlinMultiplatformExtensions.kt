package wallapp.tooling.extensions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget


fun KotlinMultiplatformExtension.desktop() {
    jvm("desktop") {
        compilations.all { kotlinOptions.jvmTarget = "17" }

        testRuns["test"].executionTask.configure {
            javaLauncher
                .set(this@jvm.project.extensions.getByType<JavaToolchainService>()
                    .launcherFor {
                        languageVersion.set(JavaLanguageVersion.of(17))
                    }
                )
        }
    }
}

/**
 * Note: differs from [ios]. For whatever reason, there's link issues related to
 * "iosSimulatorArm64" when using the prebuilt `ios()` function. This override fixes that.
 *
 * This likely needs to be looked at again down the road, but for now, this works.
 */
fun KotlinMultiplatformExtension.iOS(): List<KotlinNativeTarget> {
    return listOf(
        iosArm64(),
        iosSimulatorArm64(),
    )
}

@Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
fun NamedDomainObjectContainer<KotlinSourceSet>.configureAllSourceSets(
    configureAndroid: Boolean = true,
    configureDesktop: Boolean = true,
    configureIos: Boolean = true,
) {
    val (commonMain, commonTest) = configureCommonSourceSets()
}

fun NamedDomainObjectContainer<KotlinSourceSet>.configureCommonSourceSets(): Pair<KotlinSourceSet, KotlinSourceSet> {
    val commonMain by getting
    val commonTest by getting {
        dependencies {
            implementation(kotlin("test"))
        }
    }

    return commonMain to commonTest
}

@Suppress("UnstableApiUsage")
internal fun Project.configureKotlinMultiplatform(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = 35

        defaultConfig {
            minSdk = 26
            manifestPlaceholders["appAuthRedirectScheme"] = "empty"
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }
}

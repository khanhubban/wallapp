package wallapp.initializer.app

import org.koin.core.module.Module
import wallapp.di.DiApplication
import wallapp.di.DiApplicationKoin
import wallapp.runmode.RunMode

private val AppInitializerTestDesktopClassName = "wallapp.test.initializer.AppInitializerTestDesktop"
private val AppInitializerCronDesktopClassName = "wallapp.test.initializer.AppInitializerCronDesktop"

fun createAppInitializerRunMode(runMode: RunMode): AppInitializer? {
    val className = when (runMode) {
        RunMode.Test -> AppInitializerTestDesktopClassName
        RunMode.Cron -> AppInitializerCronDesktopClassName
        else -> null
    }

    return try {
        val clazz = Class.forName(className)
        val constructor = clazz.getDeclaredConstructor()
        constructor.newInstance() as? AppInitializer
    } catch (e: ClassNotFoundException) {
        throw ClassNotFoundException("Could not find class \"${className}\" - ensure this test module links with \":shared:test:test-common\"")
    } catch (e: Exception) {
        throw e
    }
}

actual fun appInitializeRunMode(
    runMode: RunMode,
    additionalModules: List<Module>?,
): DiApplication {
    val appInitializer = createAppInitializerRunMode(runMode)
        ?: throw Exception("Could not initialize AppInitializerTest")
    return DiApplicationKoin(appInitialize(appInitializer, additionalModules))
}

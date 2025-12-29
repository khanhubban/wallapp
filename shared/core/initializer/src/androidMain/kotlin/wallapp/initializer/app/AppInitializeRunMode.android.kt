package wallapp.initializer.app

import android.content.Context
import org.koin.core.module.Module
import wallapp.di.DiApplication
import wallapp.di.DiApplicationKoin
import wallapp.runmode.RunMode

private val AppInitializerTestAndroidClassName = "wallapp.test.initializer.AppInitializerTestAndroid"

fun createAppInitializerTest(): AppInitializer? {
    return try {
        val clazz = Class.forName(AppInitializerTestAndroidClassName)
        val constructor = clazz.getDeclaredConstructor()
        constructor.newInstance() as? AppInitializer
    } catch (e: ClassNotFoundException) {
        throw ClassNotFoundException("Could not find class \"${AppInitializerTestAndroidClassName}\" - ensure this test module links with \":shared:test:test-common\"")
    } catch (e: Exception) {
        throw e
    }
}

fun createAppInitializerTest(application: Any?): AppInitializer? {
    return try {
        val clazz = Class.forName(AppInitializerTestAndroidClassName)
        val constructor = clazz.getConstructor(Context::class.java)
        constructor.newInstance(application as Context) as? AppInitializer
    } catch (e: ClassNotFoundException) {
        throw ClassNotFoundException("Could not find class \"${AppInitializerTestAndroidClassName}\" - ensure this test module links with \":shared:test:test-common\"")
    } catch (e: Exception) {
        throw e
    }

}

actual fun appInitializeRunMode(
    runMode: RunMode,
    additionalModules: List<Module>?,
): DiApplication {
    require(runMode == RunMode.Test) { "RunMode must be Test" }
    val appInitializer = createAppInitializerTest()
        ?: throw Exception("Could not initialize AppInitializerTest")
    return DiApplicationKoin(appInitialize(appInitializer, additionalModules))
}

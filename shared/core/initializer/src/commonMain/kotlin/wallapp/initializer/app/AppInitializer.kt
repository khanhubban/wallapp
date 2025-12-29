package wallapp.initializer.app

import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import wallapp.runmode.RunMode

interface AppInitializer {
    val allModules: List<Module>
    val application: Any?
    val interopBridge: Any
    val multiProcessAllowed: Boolean
        get() = false
    val runMode: RunMode
    val isDebug: Boolean?
    val onStartKoin: KoinAppDeclaration

    fun init(additionalModules: List<Module>?): KoinApplication
}

fun appInitialize(appInitializer: AppInitializer, additionalModules: List<Module>? = null): KoinApplication {
    return appInitializer.init(additionalModules)
}


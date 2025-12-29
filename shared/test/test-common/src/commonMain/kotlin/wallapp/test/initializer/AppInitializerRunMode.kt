package wallapp.test.initializer

import org.koin.core.KoinApplication
import org.koin.core.module.Module
import wallapp.di.module.AllModules
import wallapp.initializer.app.AppInitializer
import wallapp.initializer.app.initializeApp


abstract class AppInitializerRunMode : AppInitializer {

    override val allModules: List<Module>
        get() = AllModules
    override val application: Any?
        get() = null
//    override val interopBridge: Any
//        get() = InteropBridgeNoOp
    override val isDebug: Boolean
        get() = true
//    override val onStartKoin: KoinAppDeclaration
//        get() = TODO("Not yet implemented")

    override fun init(additionalModules: List<Module>?): KoinApplication {
        return initializeApp(
            application = application,
            allModules = allModules + additionalModules.orEmpty(),
            interopBridge = interopBridge,
            multiProcessAllowed = multiProcessAllowed,
            isDebug = isDebug,
            runMode = runMode,
            onStartKoin = onStartKoin,
        )
    }

}
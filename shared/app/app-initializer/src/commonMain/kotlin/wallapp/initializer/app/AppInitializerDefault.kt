package wallapp.initializer.app

import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import wallapp.runmode.RunMode

class AppInitializerDefault(
    override val application: Any?,
    override val allModules: List<Module>,
    override val interopBridge: Any,
    override val multiProcessAllowed: Boolean,
    override val isDebug: Boolean?,
    override val runMode: RunMode,
    override val onStartKoin: KoinAppDeclaration,
) : AppInitializer {

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


package wallapp.initializer.app

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import wallapp.di.Factory
import wallapp.di.initDependencyResolver
import wallapp.interop.InteropBridge
import wallapp.interop.registerInteropBridge
import wallapp.runmode.RunMode
import wallapp.system.platform.PlatformFeature


fun initializeApp(
    application: Any?,
    allModules: List<Module>,
    interopBridge: Any,
    multiProcessAllowed: Boolean,
    isDebug: Boolean?,
    runMode: RunMode,
    onStartKoin: KoinAppDeclaration,
): KoinApplication {
    Factory.multiProcessAllowed = multiProcessAllowed
    Factory.isDebug = isDebug
    Factory.runMode = runMode

    PlatformFeature.init(application = application)

    registerInteropBridge(interopBridge as InteropBridge)

    return startKoin {
        onStartKoin()
        modules(allModules)
    }.also {
        initDependencyResolver(it.koin)
    }
}
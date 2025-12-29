package wallapp.initializer.app

import org.koin.core.module.Module
import wallapp.di.DiApplication
import wallapp.runmode.RunMode

actual fun appInitializeRunMode(
    runMode: RunMode,
    additionalModules: List<Module>?,
): DiApplication {
    TODO("Not yet implemented")
}


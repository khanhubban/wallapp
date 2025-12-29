package wallapp.initializer.app

import org.koin.core.module.Module
import wallapp.di.DiApplication
import wallapp.runmode.RunMode

expect fun appInitializeRunMode(
    runMode: RunMode,
    additionalModules: List<Module>? = null,
): DiApplication

fun appInitializeTest(additionalModules: List<Module>? = null): DiApplication {
    return appInitializeRunMode(RunMode.Test, additionalModules)
}

fun appInitializeCron(additionalModules: List<Module>? = null): DiApplication {
    return appInitializeRunMode(RunMode.Cron, additionalModules)
}
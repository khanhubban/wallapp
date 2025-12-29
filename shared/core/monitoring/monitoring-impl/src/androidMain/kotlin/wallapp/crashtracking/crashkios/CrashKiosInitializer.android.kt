package wallapp.crashtracking.crashkios

import co.touchlab.crashkios.crashlytics.enableCrashlytics
import wallapp.initializer.module.ModuleInitializer

class CrashKiosInitializerAndroid : ModuleInitializer {

    override fun initialize() {
        enableCrashlytics()
    }
}
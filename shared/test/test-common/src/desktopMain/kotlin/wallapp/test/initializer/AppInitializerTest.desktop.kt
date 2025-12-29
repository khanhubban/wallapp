package wallapp.test.initializer

import org.koin.dsl.KoinAppDeclaration
import wallapp.interop.InteropBridgeDesktop
import wallapp.log.Log
import wallapp.runmode.RunMode


abstract class AppInitializerRunModeDesktop : AppInitializerRunMode() {

    override val application: Any?
        get() = null
    override val interopBridge: Any
        get() = InteropBridgeDesktop
    override val onStartKoin: KoinAppDeclaration
        get() = { }

    init {
        Log.d("Create AppInitializerRunModeDesktop")
    }
}

@Suppress("unused")
class AppInitializerTestDesktop : AppInitializerRunModeDesktop() {

    override val runMode: RunMode
        get() = RunMode.Test

    init {
        Log.d("Create AppInitializerTestDesktop")
    }
}

@Suppress("unused")
class AppInitializerCronDesktop : AppInitializerRunModeDesktop() {

    override val runMode: RunMode
        get() = RunMode.Cron

    init {
        Log.d("Create AppInitializerCronDesktop")
    }
}


//fun createTest(): AppInitializerTestAndroid {
//    return AppInitializerTestAndroid()
//}
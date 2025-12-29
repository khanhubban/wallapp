package wallapp.appversion

import wallapp.appstate.AppState
import wallapp.buildconfig.BuildConfig

abstract class AppVersionUpgrader(
    buildConfig: BuildConfig,
    appState: AppState,
) {

    abstract fun onUpgrade(fromVersionCode: Long, toVersionCode: Long)

    init {
        // TODO: Add back in with #1742
//        val lastAppRunVersionCode = appState.lastAppRunVersionName.value
//        if (buildConfig.appVersionCode > lastAppRunVersionCode) {
//            appState.lastAppRunVersionName.subscribe {
//                Log.d("onUpgrade(): $lastAppRunVersionCode -> $it")
//                onUpgrade(lastAppRunVersionCode, it)
//            }
//
//            appState.lastAppRunVersionCode.update(buildConfig.appVersionCode)
//        }
    }
}

class AppVersionUpgraderNoOp(
    buildConfig: BuildConfig,
    appState: AppState,
) : AppVersionUpgrader(buildConfig, appState) {

    override fun onUpgrade(fromVersionCode: Long, toVersionCode: Long) { }
}

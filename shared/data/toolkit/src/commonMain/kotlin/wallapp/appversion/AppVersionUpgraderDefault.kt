package wallapp.appversion

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import wallapp.appstate.AppState
import wallapp.buildconfig.BuildConfig
import wallapp.coroutine.CoroutineContexts
import wallapp.remoteconfig.RemoteConfig

class AppVersionUpgraderDefault(
    buildConfig: BuildConfig,
    appState: AppState,
    private val coroutineContexts: CoroutineContexts,
    private val remoteConfig: RemoteConfig,
) : AppVersionUpgrader(buildConfig, appState) {

    private val coroutineScope by lazy { CoroutineScope(coroutineContexts.io) }

    override fun onUpgrade(fromVersionCode: Long, toVersionCode: Long) {
        coroutineScope.launch {
            remoteConfig.update()
        }
    }

}
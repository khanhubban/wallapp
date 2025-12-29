package wallapp.inappupdate

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.buildconfig.BuildConfig
import wallapp.coroutine.collectIn
import wallapp.log.Logger
import wallapp.remoteconfig.data.RemoteConfigData


class InAppUpdateCheckerRemoteConfig(
    val buildConfig: BuildConfig,
    remoteConfigData: RemoteConfigData,
    coroutineScopeMain: CoroutineScope,
): InAppUpdateChecker {

    companion object {
        val Log = Logger("InAppUpdateChecker")
    }

    private val _appUpdateRequired = MutableStateFlow(false)
    override val appUpdateRequired: StateFlow<Boolean>
        get() = _appUpdateRequired

    init {
        remoteConfigData.appUpdateMinimumAllowedAppVersion_platformSpecific
            .collectIn(coroutineScopeMain) { appUpdateMinimumAllowedAppVersion ->
                if (appUpdateMinimumAllowedAppVersion?.isNewerThan(buildConfig.appVersion) == true) {
                    _appUpdateRequired.value = true
                }
                    Log.w("appUpdateRequired: %b", appUpdateRequired)
        }
    }

}
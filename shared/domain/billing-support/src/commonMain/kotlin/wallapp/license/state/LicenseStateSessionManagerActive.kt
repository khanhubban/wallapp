package wallapp.license.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import wallapp.log.Logger
import wallapp.remoteconfig.data.RemoteConfigData
import wallapp.remoteconfig.data.unlockAllForSessionUuid
import wallapp.util.combine

class LicenseStateSessionManagerActive(
    remoteConfigData: RemoteConfigData,
    coroutineScopeIo: CoroutineScope,
) : LicenseStateSessionManager {

    companion object {
        val Log = Logger("[LSSM]")
    }

    private val lastUrlId: MutableStateFlow<String?> = MutableStateFlow(null)

    override val granted: StateFlow<Boolean> by lazy {
        combine(
            remoteConfigData.unlockAllForSessionUuid,
            lastUrlId,
        ) { remoteConfigId, lastUrlId -> remoteConfigId == lastUrlId }
            .onEach { Log.d("granted: $it") }
            .stateIn(coroutineScopeIo, started = SharingStarted.Eagerly, initialValue = false)
    }

    override fun onDeepLinkUrl(url: String) {
        Log.d("checkForUrl: $url")
        extractIdFromAppUrl(url)?.let { id ->
            if (isUuid(id)) {
                lastUrlId.value = id
            }
        }
    }
}
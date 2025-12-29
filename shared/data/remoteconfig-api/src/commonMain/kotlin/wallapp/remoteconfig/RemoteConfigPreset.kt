package wallapp.remoteconfig

import kotlinx.coroutines.flow.MutableSharedFlow
import wallapp.log.Log

class RemoteConfigPreset : RemoteConfig {

    override val dataRefreshed = MutableSharedFlow<Unit>()

    override suspend fun update() { }

    init {
        Log.w("[FirebaseRemoteConfig] using RemoteConfigPreset...")
    }
}

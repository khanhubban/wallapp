package wallapp.remoteconfig

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.ios
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual fun addOnConfigUpdateListener(listener: (Boolean) -> Unit) {
    val remoteConfig = Firebase.remoteConfig.ios
    remoteConfig.addOnConfigUpdateListener { firRemoteConfigUpdate, nsError ->
        if (nsError != null) {
            listener(false)
        } else if (firRemoteConfigUpdate != null) {
            listener(firRemoteConfigUpdate.updatedKeys.isNotEmpty())
        }
    }
}
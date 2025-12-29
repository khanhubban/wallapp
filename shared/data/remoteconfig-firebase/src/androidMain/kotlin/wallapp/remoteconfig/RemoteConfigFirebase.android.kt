package wallapp.remoteconfig

import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.android
import dev.gitlive.firebase.remoteconfig.remoteConfig
import wallapp.log.Log

actual fun addOnConfigUpdateListener(listener: (Boolean) -> Unit) {
    val remoteConfig = Firebase.remoteConfig.android
    remoteConfig.addOnConfigUpdateListener(
        object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.fetch()
                Log.d("[RemoteConfig] Config updated: ${configUpdate.updatedKeys}")
                listener(configUpdate.updatedKeys.isNotEmpty())
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                listener(false)
            }
        }
    )
}
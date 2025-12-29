package wallapp.remoteconfig

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig
import dev.gitlive.firebase.remoteconfig.get
import dev.gitlive.firebase.remoteconfig.remoteConfig
import wallapp.remoteconfig.data.RemoteConfigEntry

object ConfigValueRepositoryFirebase: ConfigValueRepository {

    private val remoteConfig: FirebaseRemoteConfig by lazy { Firebase.remoteConfig }

    override fun getBooleanFromConfig(configEntry: RemoteConfigEntry<Boolean>): Boolean {
        return remoteConfig.get<Boolean>(configEntry.key)
    }

    override fun getStringFromConfig(configEntry: RemoteConfigEntry<String>): String {
        val value = remoteConfig.get<String>(configEntry.key)
        if (value.isEmpty() && !configEntry.allowEmptyValue) {
            return configEntry.default
        }
        return value
    }

    override fun getLongFromConfig(configEntry: RemoteConfigEntry<Long>): Long {
        return remoteConfig.get<Long>(configEntry.key)
    }

    override fun getDoubleFromConfig(configEntry: RemoteConfigEntry<Double>): Double {
        return remoteConfig.get<Double>(configEntry.key)
    }
}
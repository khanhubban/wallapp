package wallapp.remoteconfig

import wallapp.remoteconfig.data.RemoteConfigEntry

interface ConfigValueRepository {

    fun getBooleanFromConfig(configEntry: RemoteConfigEntry<Boolean>): Boolean

    fun getStringFromConfig(configEntry: RemoteConfigEntry<String>): String

    fun getLongFromConfig(configEntry: RemoteConfigEntry<Long>): Long

    fun getDoubleFromConfig(configEntry: RemoteConfigEntry<Double>): Double
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T: Any> ConfigValueRepository.get(configEntry: RemoteConfigEntry<T>): T =
    when (T::class) {
        Boolean::class -> getBooleanFromConfig(configEntry as RemoteConfigEntry<Boolean>) as T
        String::class -> getStringFromConfig(configEntry as RemoteConfigEntry<String>) as T
        Long::class -> getLongFromConfig(configEntry as RemoteConfigEntry<Long>) as T
        Double::class -> getDoubleFromConfig(configEntry as RemoteConfigEntry<Double>) as T

        Int::class -> throw IllegalArgumentException("[ConfigValueRepository] Int is not supported, use Long instead")
        else -> throw IllegalArgumentException("[ConfigValueRepository] Unhandled class: ${T::class.simpleName}")
    }
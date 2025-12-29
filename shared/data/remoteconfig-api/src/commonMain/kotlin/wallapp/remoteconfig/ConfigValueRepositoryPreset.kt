package wallapp.remoteconfig

import wallapp.remoteconfig.data.RemoteConfigEntry

class ConfigValueRepositoryPreset(
    private val preset: List<Pair<String, Any>> = listOf(),
) : ConfigValueRepository {

    private fun getPresetValueForKey(key: String): Any? = preset.firstOrNull { it.first == key }?.second

    override fun getBooleanFromConfig(configEntry: RemoteConfigEntry<Boolean>): Boolean {
        val r = getPresetValueForKey(configEntry.key)
        return if (r != null) r as Boolean else false
    }

    override fun getStringFromConfig(configEntry: RemoteConfigEntry<String>): String {
        val r = getPresetValueForKey(configEntry.key)
        return if (r != null) r as String else ""
    }

    override fun getLongFromConfig(configEntry: RemoteConfigEntry<Long>): Long {
        val r = getPresetValueForKey(configEntry.key)
        return if (r != null) r as Long else 0
    }

    override fun getDoubleFromConfig(configEntry: RemoteConfigEntry<Double>): Double {
        val r = getPresetValueForKey(configEntry.key)
        return if (r != null) r as Double else 0.0
    }
}
package wallapp.settings

import com.russhwolf.settings.contains
import com.russhwolf.settings.get
import wallapp.settings.Settings.OnSettingChangeListener
import com.russhwolf.settings.Settings as SettingsKm

class SettingsMultiplatform(
    private val settings: SettingsKm,
) : Settings {

    private val listeners = mutableSetOf<OnSettingChangeListener>()

    override fun registerOnSettingChangeListener(listener: OnSettingChangeListener) {
        listeners.add(listener)
    }

    override fun unregisterOnSettingChangeListener(listener: OnSettingChangeListener) {
        listeners.remove(listener)
    }

    override fun contains(key: String): Boolean {
        return settings.contains(key)
    }

    override fun getAll(): Map<String, *> {
        return mutableMapOf<String, Any>().apply {
            settings.keys.forEach { key ->
                val value = settings.get<Any>(key)
                if (value != null) {
                    put(key, value)
                }
            }
        }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return settings.getBoolean(key, defaultValue)
    }

    override fun putBoolean(key: String, value: Boolean) {
        settings.putBoolean(key, value)
        onSettingChanged(key)
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return settings.getInt(key, defaultValue)
    }

    override fun putInt(key: String, value: Int) {
        settings.putInt(key, value)
        onSettingChanged(key)
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return settings.getLong(key, defaultValue)
    }

    override fun putLong(key: String, value: Long) {
        settings.putLong(key, value)
        onSettingChanged(key)
    }

    override fun getString(key: String, defaultValue: String): String {
        return settings.getString(key, defaultValue)
    }

    override fun putString(key: String, value: String) {
        settings.putString(key, value)
        onSettingChanged(key)
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return settings.getFloat(key, defaultValue)
    }

    override fun putFloat(key: String, value: Float) {
        settings.putFloat(key, value)
        onSettingChanged(key)
    }

    private fun onSettingChanged(key: String) {
//        Log.v("[${process.processNameSuffix}] SettingsMultiplatform.onSettingChanged: $key")
        // Copy the listeners to avoid ConcurrentModificationException (#12)
        val listeners = listeners.toSet()
        listeners.forEach { it.onSettingChanged(this, key) }
    }

    override fun resetAll() {
        settings.clear()
    }
}
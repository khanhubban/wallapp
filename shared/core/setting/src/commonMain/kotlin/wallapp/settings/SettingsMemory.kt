package wallapp.settings

import wallapp.log.Log
import wallapp.settings.Settings.OnSettingChangeListener

class SettingsMemory(
    private val tag: String = "SettingsMemory",
) : Settings {

    private val settings = mutableMapOf<String, Any?>()
    private val listeners = mutableSetOf<OnSettingChangeListener>()

    override fun registerOnSettingChangeListener(listener: OnSettingChangeListener) {
        listeners.add(listener)
    }

    override fun unregisterOnSettingChangeListener(listener: OnSettingChangeListener) {
        listeners.remove(listener)
    }

    override fun contains(key: String): Boolean {
        return settings.containsKey(key)
    }

    override fun getAll(): Map<String, *> {
        return settings
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return settings[key]?.let { it as Boolean } ?: defaultValue
    }

    override fun putBoolean(key: String, value: Boolean) {
        settings[key] = value
        listeners.forEach { it.onSettingChanged(this, key) }
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return settings[key]?.let { it as Int } ?: defaultValue
    }

    override fun putInt(key: String, value: Int) {
        settings[key] = value
        listeners.forEach { it.onSettingChanged(this, key) }
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return settings[key]?.let { it as Long } ?: defaultValue
    }

    override fun putLong(key: String, value: Long) {
        settings[key] = value
        listeners.forEach { it.onSettingChanged(this, key) }
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return settings[key]?.let { it as Float } ?: defaultValue
    }

    override fun putFloat(key: String, value: Float) {
        settings[key] = value
        listeners.forEach { it.onSettingChanged(this, key) }
    }

    override fun getString(key: String, defaultValue: String): String {
        return settings[key]?.let { it as String } ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        settings[key] = value
        listeners.forEach { it.onSettingChanged(this, key) }
    }

    override fun resetAll() {
        settings.clear()
    }

    init {
        Log.d("SettingsMemory init [$this.tag]")
    }
}
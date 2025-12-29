package wallapp.di

import wallapp.preferences.PreferenceDefinitions
import java.util.prefs.Preferences

object PreferencesDesktop {

    private fun createPreferences(name: String): Preferences {
        return Preferences.userRoot().node(name)
//            .apply {
//                clear()
//            }
    }

    val cacheFileMediaMapPreferences: Preferences by lazy { createPreferences(PreferenceDefinitions.CacheFileMediaMapFilename) }
    val userPreferences: Preferences by lazy { createPreferences(PreferenceDefinitions.UserSettingsFilename) }
    val devicePreferences: Preferences by lazy { createPreferences(PreferenceDefinitions.DeviceSettingsFilename) }
    val licensePreferences: Preferences by lazy { createPreferences(PreferenceDefinitions.LicenseSettingsFilename) }
    val remoteServerContentPreferences: Preferences by lazy { createPreferences(PreferenceDefinitions.RemoteServerContentFilename) }
}
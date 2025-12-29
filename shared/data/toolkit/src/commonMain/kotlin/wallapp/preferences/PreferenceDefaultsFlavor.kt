package wallapp.preferences

import wallapp.flavorconfig.FlavorConfig
import wallapp.preference.PreferenceInfo
import wallapp.prefs.PreferenceDefaults
import wallapp.prefs.PreferenceDefaultsProvider
import wallapp.system.version.SystemVersion


class PreferenceDefaultsFlavor(
    preferenceDefaultsProvider: PreferenceDefaultsProvider,
    flavorConfig: FlavorConfig,
    systemVersion: SystemVersion,
) : PreferenceDefaults(preferenceDefaultsProvider, systemVersion) {

    override val cachedLicenseState = PreferenceInfo("pref_cached_license_state", flavorConfig.cachedLicenseState)
}
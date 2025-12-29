package wallapp.prefs

interface PreferenceDefaultsProviderConfig {

    val currentTime: Long

    val isEnglishLanguage: Boolean
        get() = true

    val debugLicenseState: Int
}

class PreferenceDefaultsProviderConfigPreset(
    override val currentTime: Long = 1000L,
    override val isEnglishLanguage: Boolean = true,
    override val debugLicenseState: Int = -1,
) : PreferenceDefaultsProviderConfig
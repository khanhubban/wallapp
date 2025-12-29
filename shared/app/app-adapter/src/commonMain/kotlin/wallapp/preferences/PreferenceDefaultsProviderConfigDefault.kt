package wallapp.preferences

import wallapp.flavorconfig.FlavorConfig
import wallapp.language.LanguageRepository
import wallapp.language.isEnglishLanguage
import wallapp.prefs.PreferenceDefaultsProviderConfig
import wallapp.time.TimeRepository

class PreferenceDefaultsProviderConfigDefault(
    private val timeRepository: TimeRepository,
    private val languageRepository: LanguageRepository,
    private val flavorConfig: FlavorConfig,
) : PreferenceDefaultsProviderConfig {

    override val currentTime: Long
        get() = timeRepository.currentTime
    override val isEnglishLanguage: Boolean
        get() = languageRepository.systemLanguageIsos?.any { it.isEnglishLanguage() } ?: true
    override val debugLicenseState: Int
        get() = flavorConfig.debugLicenseState
}
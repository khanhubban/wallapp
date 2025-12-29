package wallapp.resources.translation

import wallapp.language.Iso
import wallapp.language.LanguageRepository

class TranslationRepositoryConfigDefault(
    languageRepository: LanguageRepository,
) : TranslationRepositoryConfig {

    override val languageIsos: List<Iso>? = languageRepository.systemLanguageIsos

    override val availableTranslationLanguages: List<TranslationLanguage> by lazy {
        // Only support English for now. #1947.
        listOf(TranslationLanguage.English)
    }
}
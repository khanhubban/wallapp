package wallapp.resources.translation

import wallapp.language.Iso

interface TranslationRepositoryConfig {
    val languageIsos: List<Iso>?

    val availableTranslationLanguages: List<TranslationLanguage>
}
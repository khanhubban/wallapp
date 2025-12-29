package wallapp.resources.translation

import wallapp.language.Iso

class TranslationRepositoryConfigPreset(
    override val languageIsos: List<Iso>? = listOf(
        TranslationLanguage.Hindi.iso,
        TranslationLanguage.English.iso,
    ),

    override val availableTranslationLanguages: List<TranslationLanguage> = TranslationLanguage.AllLanguages,

    ) : TranslationRepositoryConfig
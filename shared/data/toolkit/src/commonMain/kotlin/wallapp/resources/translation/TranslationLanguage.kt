package wallapp.resources.translation

import wallapp.language.Iso
import wallapp.resource.Resource
import wallapp.resources.LocalFileResources
import wallapp.resources.string.StringsEn

sealed class TranslationLanguage {

    abstract val iso: Iso
    abstract val name: String
    abstract val resource: TranslationResource

    val isDefaultEnglish: Boolean
        get() = this == English

    data object English: TranslationLanguage() {
        override val iso: String
            get() = "en"
        override val name: String
            get() = "English"
        override val resource: TranslationResource by lazy {
            TranslationResource.Embedded(StringsEn)
        }
    }

    data object Hindi: TranslationLanguage() {
        override val iso: String
            get() = "hi"
        override val name: String
            get() = "Hindi"
        override val resource: TranslationResource by lazy {
            TranslationResource.Resource(Resource.from(LocalFileResources.StringsHi))
        }
    }

    data object Portuguese: TranslationLanguage() {
        override val iso: String
            get() = "pt"
        override val name: String
            get() = "Portuguese (Brazilian)"
        override val resource: TranslationResource by lazy {
            TranslationResource.Resource(Resource.from(LocalFileResources.StringsPt))
        }
    }

    data object Spanish: TranslationLanguage() {
        override val iso: String
            get() = "es"
        override val name: String
            get() = "Spanish (European)"
        override val resource: TranslationResource by lazy {
            TranslationResource.Resource(Resource.from(LocalFileResources.StringsEs))
        }
    }

    companion object {
        val AllLanguages: List<TranslationLanguage> by lazy {
            listOf(
                English,
                Hindi,
                Portuguese,
                Spanish,
            )
        }

        val NonEnglishLanguages by lazy { AllLanguages.filter { it != English } }

        fun List<TranslationLanguage>.fromIso(iso: Iso): TranslationLanguage? {
            return firstOrNull { it.iso == iso }
        }
    }
}
package wallapp.language


interface LanguageRepository {

    // Returns ISO 639-1 language codes
    val systemLanguageIsos: List<Iso>?

    // Returns combined language-country codes (ISO 639-1_ISO 3166-1)
    val systemLocaleIdentifiers: List<String>?
}

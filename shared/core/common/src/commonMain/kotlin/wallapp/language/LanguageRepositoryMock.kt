package wallapp.language

object LanguageRepositoryMock : LanguageRepository {

    override val systemLanguageIsos: List<Iso>
        get() = listOf("en")
    override val systemLocaleIdentifiers: List<String>
        get() = listOf("en_US")
}
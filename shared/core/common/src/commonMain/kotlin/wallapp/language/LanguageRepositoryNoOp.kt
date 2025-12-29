package wallapp.language

object LanguageRepositoryNoOp : LanguageRepository {

    override val systemLanguageIsos: List<Iso>?
        get() = null

    override val systemLocaleIdentifiers: List<String>?
        get() = null
}
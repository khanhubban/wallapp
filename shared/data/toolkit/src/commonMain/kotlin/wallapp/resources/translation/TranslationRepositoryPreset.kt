package wallapp.resources.translation

fun TranslationRepositoryPreset(): TranslationRepository = object : TranslationRepository {
    override fun getString(stringKey: String): String {
        return stringKey
    }
}
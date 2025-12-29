package wallapp.resources.translation

interface TranslationRepository {

    fun getString(stringKey: String): String?
}
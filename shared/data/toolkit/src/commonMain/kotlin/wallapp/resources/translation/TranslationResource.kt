package wallapp.resources.translation

/**
 * A dedicated class to handle the translation resources. Ideally this wouldn't be necessary, but
 * we must embed English strings with the app itself, and this is the easiest way to handle that.
 * #2206.
 */
sealed class TranslationResource {

    data class Resource(val resource: wallapp.resource.Resource) : TranslationResource()

    data class Embedded(val string: String) : TranslationResource()
}
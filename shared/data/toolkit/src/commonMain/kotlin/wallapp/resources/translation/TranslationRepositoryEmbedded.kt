package wallapp.resources.translation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A [TranslationRepositorySingle] that uses embedded translations. Crucially, [isReady] is always
 * true. #2204.
 */
class TranslationRepositoryEmbedded(
    private val language: TranslationLanguage,
) : TranslationRepositorySingle {

    override val isReady: StateFlow<Boolean> = MutableStateFlow(true)

    override val translations: TranslationData by lazy {
        val resource = language.resource
        require(resource is TranslationResource.Embedded) { "TranslationResource is not embedded" }
        TranslationData.fromResourceEmbedded(resource)
    }

    override fun getString(stringKey: String): String? {
        return translations.getString(stringKey)
    }
}
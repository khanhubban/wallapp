package wallapp.resources.translation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface TranslationRepositorySingle : TranslationRepository {

    val isReady: StateFlow<Boolean>

    val translations: TranslationData

    companion object {

        fun from(
            language: TranslationLanguage,
            coroutineScopeIo: CoroutineScope,
        ): TranslationRepositorySingle {
            val resource = language.resource
            return when (resource) {
                is TranslationResource.Embedded -> TranslationRepositoryEmbedded(language)
                is TranslationResource.Resource -> TranslationRepositoryAsync(language, coroutineScopeIo)
            }
        }
    }

}
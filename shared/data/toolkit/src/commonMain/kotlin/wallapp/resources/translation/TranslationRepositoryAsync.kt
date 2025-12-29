package wallapp.resources.translation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * A [TranslationRepositorySingle] that uses a resource for translations. [isReady] is false until
 * the translations are loaded.
 */
class TranslationRepositoryAsync(
    private val language: TranslationLanguage,
    coroutineScopeIo: CoroutineScope,
) : TranslationRepositorySingle {

    private val _isReady = MutableStateFlow(false)
    override val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    override lateinit var translations: TranslationData

    override fun getString(stringKey: String): String? {
        require(isReady.value) { "TranslationRepository is not ready yet" }
        return translations.getString(stringKey)
    }

    private suspend fun initialize() {
        require(isReady.value.not()) { "TranslationRepository is already initialized" }
        translations = TranslationData.fromResource(language.resource)
        // Uncomment this line to simulate a slow initialization and check for initialization errors
//        delay(5000)
        _isReady.value = true
    }

    init {
        val resource = language.resource
        require(resource !is TranslationResource.Embedded) { "resource is TranslationResource.Embedded - use TranslationRepositoryEmbedded instead" }
        coroutineScopeIo.launch { initialize() }
    }
}
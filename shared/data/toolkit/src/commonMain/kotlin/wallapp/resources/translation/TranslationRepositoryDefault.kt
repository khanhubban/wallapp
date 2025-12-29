package wallapp.resources.translation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.language.isEnglishLanguage
import wallapp.log.Logger
import wallapp.resources.translation.TranslationLanguage.Companion.fromIso

class TranslationRepositoryDefault(
    private val config: TranslationRepositoryConfig,
    private val translationRepositoryEnglish: TranslationRepositorySingle,
    private val coroutineScopeIo: CoroutineScope,
) : TranslationRepository {

    companion object {
        val Log = Logger("TranslationRepository")
    }

    private val availableTranslationLanguages: List<TranslationLanguage>
        get() = config.availableTranslationLanguages
    private val systemLanguageIsos: MutableStateFlow<List<String>> =
        MutableStateFlow(config.languageIsos ?: listOf(TranslationLanguage.English.iso))

    private val translationRepositories: StateFlow<List<TranslationRepositorySingle>> =
        systemLanguageIsos.map { isos ->
            isos.mapNotNull { iso ->
                mapIsoToTranslationRepository(iso)
            }
        }
        .stateIn(coroutineScopeIo, started = SharingStarted.Eagerly, initialValue = listOf(translationRepositoryEnglish))

    override fun getString(stringKey: String): String {
        val repos = translationRepositories.value
        repos.forEach { repo ->
            if (repo.isReady.value) {
                val string = repo.getString(stringKey)
                if (string != null) {
                    return string
                }
            }
        }
        throw IllegalStateException("No translation repository is ready")
    }

    private fun mapIsoToTranslationRepository(iso: String): TranslationRepositorySingle? {
        return if (iso.isEnglishLanguage()) {
            translationRepositoryEnglish
        } else {
            availableTranslationLanguages.fromIso(iso)?.let { language ->
                TranslationRepositorySingle.from(
                    language = language,
                    coroutineScopeIo = coroutineScopeIo,
                )
            }
        }
    }

    init {
        Log.d("System language isos: $systemLanguageIsos")
    }
}
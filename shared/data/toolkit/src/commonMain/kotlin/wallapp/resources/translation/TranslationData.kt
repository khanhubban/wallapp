package wallapp.resources.translation

import kotlinx.serialization.json.Json
import wallapp.resources.readString

data class TranslationData(
    private val jsonString: String,
) {
    private val translations: Map<String, String> = Json.decodeFromString(jsonString)

    fun getString(stringKey: String): String? {
        return translations[stringKey]
    }

    fun getFormattedStringKeys(): List<String>? {
        return translations.filter { (key, value) ->
            value.contains("%")
        }
            .keys
            .toList()
            .ifEmpty { null }
    }

    fun getAllKeys(): Set<String> {
        return translations.keys
    }

    companion object {
        fun fromResourceEmbedded(resource: TranslationResource.Embedded): TranslationData {
            return TranslationData(resource.string)
        }

        suspend fun fromResource(resource: TranslationResource): TranslationData {
            return when (resource) {
                is TranslationResource.Resource -> resource.resource.readString()!!
                is TranslationResource.Embedded -> resource.string
            }.let { jsonString ->
                TranslationData(jsonString)
            }
        }
    }
}
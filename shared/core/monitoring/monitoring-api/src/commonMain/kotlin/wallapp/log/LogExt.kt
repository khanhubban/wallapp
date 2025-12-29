package wallapp.log

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private fun formatString(format: String, args: Array<out Any?>): String {
    val json = Json { encodeDefaults = true }
    val placeholders = args.mapIndexed { index, value ->
        "%$index" to JsonPrimitive(value.toString())
    }.toMap()

    val jsonObject = JsonObject(placeholders)
    val jsonString = json.encodeToString(JsonObject.serializer(), jsonObject)

    return format.replace(Regex("%\\d")) { matchResult ->
        val key = matchResult.value
        jsonString.substringBefore(key).substringAfterLast("\"") + jsonString.substringAfter("$key\":\"").substringBefore("\"")
    }
}

fun formatLogMessage(message: String?, vararg args: Any?): String? {
    return message?.let { formatString(it, args) }
}
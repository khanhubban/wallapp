package wallapp.json

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull

fun JsonObject.getKeyValues(): List<Pair<String, Any>>? {
    val entries = entries
    return mutableMapOf<String, Any>().apply {
        entries.forEach { (key, value) ->
            when (value) {
                is JsonArray -> {
                    val stringSet = value.mapNotNull { (it as? JsonPrimitive)?.content }.toSet()
                    put(key, stringSet)
                }

                is JsonPrimitive -> {
                    when {
                        value.isString -> put(key, value.content)
                        value.booleanOrNull != null -> put(key, value.boolean)
                        value.intOrNull != null -> put(key, value.int)
                        value.longOrNull != null -> put(key, value.long)
                        value.floatOrNull != null -> put(key, value.float)
                        value.doubleOrNull != null -> put(key, value.double)
                    }
                }

                is JsonObject -> TODO()
            }
        }
    }.toList().let {
        it.ifEmpty { null }
    }
}


fun <T : Any> JsonObjectBuilder.putValue(key: String, value: T) {
    when (value) {
        is String -> put(key, JsonPrimitive(value))
        is Boolean -> put(key, JsonPrimitive(value))
        is Int -> put(key, JsonPrimitive(value))
        is Long -> put(key, JsonPrimitive(value))
        is Float -> put(key, JsonPrimitive(value))
        is Double -> put(key, JsonPrimitive(value))
        is Set<*> -> {
            value.forEach {
                require(it is String)
            }
            @Suppress("UNCHECKED_CAST")
            put(key, JsonArray((value as Set<String>).map { JsonPrimitive(it) }))
        }
        else -> {
            throw UnsupportedOperationException("Type ${value::class.simpleName} is unsupported")
        }
    }
}

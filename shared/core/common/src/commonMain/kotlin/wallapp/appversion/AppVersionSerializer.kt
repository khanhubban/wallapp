package wallapp.appversion

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object AppVersionSerializer : KSerializer<AppVersion> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AppVersion") {
        element<String>("type")
        element<JsonObject>("data")
    }

    override fun serialize(encoder: Encoder, value: AppVersion) {
        val jsonEncoder = encoder as? kotlinx.serialization.json.JsonEncoder
            ?: throw SerializationException("This class can be serialized only by JSON")

        val jsonObject = when (value) {
            is AppVersion.AppVersionAndroid -> JsonObject(
                mapOf(
                    "type" to JsonPrimitive("AppVersionAndroid"),
                    "data" to jsonEncoder.json.encodeToJsonElement(value)
                )
            )
            is AppVersion.AppVersionIos -> JsonObject(
                mapOf(
                    "type" to JsonPrimitive("AppVersionIos"),
                    "data" to jsonEncoder.json.encodeToJsonElement(value)
                )
            )
        }
        jsonEncoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): AppVersion {
        val jsonDecoder = decoder as? kotlinx.serialization.json.JsonDecoder
            ?: throw SerializationException("This class can be deserialized only by JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject
        val type = jsonObject["type"]?.jsonPrimitive?.content
            ?: throw SerializationException("Missing type discriminator")
        val data = jsonObject["data"]?.jsonObject
            ?: throw SerializationException("Missing data element")

        return when (type) {
            "AppVersionAndroid" -> jsonDecoder.json.decodeFromJsonElement<AppVersion.AppVersionAndroid>(data)
            "AppVersionIos" -> jsonDecoder.json.decodeFromJsonElement<AppVersion.AppVersionIos>(data)
            else -> throw SerializationException("Unknown type: $type")
        }
    }
}
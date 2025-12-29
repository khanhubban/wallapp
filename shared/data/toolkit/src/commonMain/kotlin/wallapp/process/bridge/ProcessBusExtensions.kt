package wallapp.process.bridge

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import wallapp.json.putValue


fun <T> getProcessBusCommand(key: String, value: T): String {
    val jsonObject = buildJsonObject {
        putValue(key, value as Any)
    }
    return Json.encodeToString(JsonObject.serializer(), jsonObject)
}
package wallapp.prefs

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


/**
 * [actionType]: A value that matches an [ActionType.type]
 */

fun createPreferenceDefaultsTriggerDefaultString(actionType: String): String {
    val jsonObject = buildJsonObject {
        put("trigger_mode", "default")
        put("intent", actionType)
    }
    return Json.encodeToString(JsonObject.serializer(), jsonObject)
}


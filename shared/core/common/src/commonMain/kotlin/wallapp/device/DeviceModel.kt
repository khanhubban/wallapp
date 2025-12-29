package wallapp.device

import kotlinx.serialization.Serializable
import wallapp.string.stripWhitespace

@Serializable
data class DeviceModel(
    val manufacturer: String,
    val modelName: String,
) {

    fun getLabel(stripWhitespace: Boolean = true): String =
        "${manufacturer}_${modelName}".let {
            if (stripWhitespace) it.stripWhitespace() else it
        }
}

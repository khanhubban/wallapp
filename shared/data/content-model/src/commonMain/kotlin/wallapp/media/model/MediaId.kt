package wallapp.media.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaId(val id: Long) {

    companion object {
        val Preset = MediaId(-1)
    }
}
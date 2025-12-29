package wallapp.media.model

import wallapp.resource.ImageHash

data class MediaHolder(
    val mediaId: MediaId,
    val contentDescription: String? = null,
    val imageHash: ImageHash? = null,
) {
    companion object {
        val Preset by lazy { MediaHolder(MediaId.Preset) }
    }
}

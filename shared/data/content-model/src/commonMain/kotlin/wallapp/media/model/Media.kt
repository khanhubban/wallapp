package wallapp.media.model

import wallapp.image.ImageSize
import wallapp.resource.ImageHash
import wallapp.type.MediaType


data class Media(
    val mediaId: MediaId,
    val type: MediaType,
    val width: Int?,
    val height: Int?,
    val imageHash: ImageHash?,
) {

    val aspectRatio: Float?
        get() {
            val width = width
            val height = height
            return if (width != null && height != null) {
                width.toFloat() / height.toFloat()
            } else {
                null
            }
        }

    val imageSize: ImageSize?
        get() {
            val width = width
            val height = height
            return if (width != null && height != null) {
                ImageSize(width, height)
            } else {
                null
            }
        }
}
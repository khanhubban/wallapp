package wallapp.image

data class ImageVideoPlaybackError(
    val message: String,
    val code: Int,
    val codeAdditional: Int? = null,
)
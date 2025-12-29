package wallapp.image.host

interface ImageHostUrlMapper {

    fun canMapDynamicImageUrl(imageUrl: String): Boolean

    /**
     * [alignment] must be [androidx.compose.ui.Alignment] or [wallapp.pixel.view.ViewAlignment].
     */
    fun mapDynamicImageUrl(
        imageUrl: String,
        options: ImageHostOptions,
        alignment: Any?,
    ): ImageHostDynamicUrlResult
}

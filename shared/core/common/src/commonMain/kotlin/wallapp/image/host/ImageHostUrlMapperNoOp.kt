package wallapp.image.host

class ImageHostUrlMapperNoOp : ImageHostUrlMapper  {

    override fun canMapDynamicImageUrl(imageUrl: String): Boolean = false

    override fun mapDynamicImageUrl(
        imageUrl: String,
        options: ImageHostOptions,
        alignment: Any?,
    ): ImageHostDynamicUrlResult = ImageHostDynamicUrlResult(imageUrl, imageSize = null)
}

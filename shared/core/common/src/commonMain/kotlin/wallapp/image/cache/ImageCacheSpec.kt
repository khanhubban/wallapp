package wallapp.image.cache

data class ImageCacheSpec(
    val memoryCachePolicy: ImageCachePolicy,
    val diskCachePolicy: ImageCachePolicy,
) {

    val debugString: String
        get() = if (this == ImageCacheSpec.ImageCacheSpecWarm) {
            "Warm (memory)"
        } else if (this == ImageCacheSpec.ImageCacheSpecCool) {
            "Cool (disc)"
        } else {
            "ImageCacheSpec(memoryCachePolicy=$memoryCachePolicy, diskCachePolicy=$diskCachePolicy)"
        }

    companion object {
        val ImageCacheSpecWarm = ImageCacheSpec(
            memoryCachePolicy = ImageCachePolicy.Enabled,
            diskCachePolicy = ImageCachePolicy.Enabled,
        )
        val ImageCacheSpecCool = ImageCacheSpec(
            memoryCachePolicy = ImageCachePolicy.Disabled,
            diskCachePolicy = ImageCachePolicy.WriteOnly,
        )
    }
}

package wallapp.image.host

class ImageHostOptions {
    var imageHostFormat: ImageHostFormat? = null

    var width: Int? = null
    var height: Int? = null
    /**
     * [crop] must be true to get a non-square image. Crop also attempts to center any crop around
     * a face that may or may not be found in the image.
     */
    var crop: Boolean = false
}

fun ImageHostOptions(init: ImageHostOptions.() -> Unit): ImageHostOptions {
    return ImageHostOptions().apply {
        init()
    }
}

fun ImageHostOptionsDefault(
    imageHostFormat: ImageHostFormat,
): ImageHostOptions {
    return ImageHostOptions(
        init = {
            this.imageHostFormat = imageHostFormat
        }
    )
}

fun ImageHostOptionsDefault(
    imageHostFormat: ImageHostFormat,
    init: ImageHostOptions.() -> Unit,
): ImageHostOptions {
    return ImageHostOptions(
        init = {
            this.imageHostFormat = imageHostFormat
            init()
        }
    )
}

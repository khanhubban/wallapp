package wallapp.image

import wallapp.theme.ColorToken

class ImageOptions {
    var tintColorToken: ColorToken? = null
    var imageSize: ImageSize? = null
}

fun ImageOptions(init: ImageOptions.() -> Unit): ImageOptions {
    return ImageOptions().apply {
        init()
    }
}
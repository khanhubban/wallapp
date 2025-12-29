package wallapp.image

import wallapp.graphics.Color

expect val Image.isUrlImage: Boolean
expect val Image.imageUrl: String?

expect val Image.isDrawableImage: Boolean

expect val Image.isVectorImage: Boolean

expect val Image.isAnimatedImage: Boolean

expect val Image.isColorImage: Boolean
expect val Image.imageColor: Color?

expect val Image.isRawImage: Boolean

expect val Image.isFileUriImage: Boolean

val Image.loadingImage: Image?
    get() = when (this) {
        is Image.ImageStates -> this.loading
        else -> null
    }
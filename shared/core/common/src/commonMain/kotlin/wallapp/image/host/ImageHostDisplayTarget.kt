package wallapp.image.host

sealed class ImageHostDisplayTarget {

    data object InAppCompose : ImageHostDisplayTarget()

    /**
     * Used for iOS UIKit rendering.
     */
    data object InAppNative : ImageHostDisplayTarget()

    data object SystemGallery : ImageHostDisplayTarget()
}
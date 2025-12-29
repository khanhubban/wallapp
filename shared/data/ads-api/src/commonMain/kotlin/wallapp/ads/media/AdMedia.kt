package wallapp.ads.media

import wallapp.image.Image

data class AdMedia(
    val image: Image,
    /**
     * scale returned by AdMob, "which denotes the ratio of pixels to dp."
     */
    val scale: Double?,
) {

    constructor(image: Image) : this(image, null)
}

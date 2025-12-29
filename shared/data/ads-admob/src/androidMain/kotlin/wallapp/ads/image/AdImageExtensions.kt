package wallapp.ads.image

import wallapp.ads.inline.native.AdImageAdMobNative
import wallapp.ads.media.AdMedia

val AdImage?.adMedia: AdMedia?
    get() {
        val image = this ?: return null
        require(image is AdImageAdMobNative)
        return AdMedia(image.nativeAdImage)
    }
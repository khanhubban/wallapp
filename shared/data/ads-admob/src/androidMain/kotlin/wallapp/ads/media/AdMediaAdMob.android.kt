package wallapp.ads.media

import wallapp.image.Image
import com.google.android.gms.ads.nativead.NativeAd.Image as NativeAdImage

fun Image(nativeAdImage: NativeAdImage): Image? {
    val uri = nativeAdImage.uri
    if (uri != null) {
        return Image.from(
            model = uri.toString(),
            contentDescription = null,
        )
    }

    val drawable = nativeAdImage.drawable
    if (drawable != null) {
        return Image.from(
            model = drawable,
            contentDescription = null,
        )
    }

    return null
}

fun AdMedia(nativeAdImage: NativeAdImage?): AdMedia? {
    if (nativeAdImage == null) return null
    val image = Image(nativeAdImage) ?: return null
    return AdMedia(image, scale = nativeAdImage.scale)
}
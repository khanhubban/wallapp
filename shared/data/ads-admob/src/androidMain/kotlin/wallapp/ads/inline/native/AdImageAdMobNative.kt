package wallapp.ads.inline.native

import android.graphics.drawable.Drawable
import android.net.Uri
import com.google.android.gms.ads.nativead.NativeAd
import wallapp.ads.image.AdImage

internal class AdImageAdMobNative(
    val nativeAdImage: NativeAd.Image,
) : AdImage {
    override val uri: Uri?
        get() = nativeAdImage.uri
    override val drawable: Drawable?
        get() = nativeAdImage.drawable
    override val resourceId: Int?
        get() = null
    override val scale: Double
        get() = nativeAdImage.scale
}
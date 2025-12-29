package wallapp.ads.inline.promo

import android.view.View
import androidx.annotation.DrawableRes

object PromoImageUtils {

    internal const val REQUIRED_IMAGE_WIDTH = 1800
    internal const val REQUIRED_IMAGE_HEIGHT = 1300
    internal const val REQUIRED_ASPECT_RATIO = REQUIRED_IMAGE_WIDTH / REQUIRED_IMAGE_HEIGHT.toFloat()

    fun getDesiredBitmapSize(viewWidth: Int, viewHeight: Int) =
            getDesiredBitmapSize(viewWidth.toFloat(), viewHeight.toFloat())

    fun getDesiredBitmapSize(view: View) = getDesiredBitmapSize(view.width, view.height)

    fun getDesiredBitmapSize(viewWidth: Float, viewHeight: Float): Pair<Int, Int> {
        return if (viewWidth > viewHeight) {
            (viewHeight * REQUIRED_ASPECT_RATIO).toInt() to viewHeight.toInt()
        } else {
            viewWidth.toInt() to viewHeight.toInt()
        }
    }

    internal const val DRAWABLE_PREFIX = "drawable://"

    fun getPromoDrawableUri(@DrawableRes drawableRes: Int): String {
        return "$DRAWABLE_PREFIX$drawableRes"
    }

    @DrawableRes fun getPromoDrawableId(uri: String): Int? {
        if (uri.startsWith(DRAWABLE_PREFIX)) {
            return uri.replace(DRAWABLE_PREFIX, "").toInt()
        }
        return null
    }
}
package wallapp.ads.image

import android.widget.ImageView
import coil.load
import coil.request.Disposable
import coil.transform.Transformation

class AdImageLoaderCoil : AdImageLoader {

    override fun showImageInView(image: AdImage, view: ImageView) {
        val drawable = image.drawable
        when {
            drawable != null -> {
                view.setImageDrawable(drawable)
            }
            else -> {
                loadImage(image, view)
            }
        }
    }

    private fun loadImage(image: AdImage, view: ImageView, transformation: Transformation? = null): Disposable {
        val request = view.load(image.uri) {

            val lp = view.layoutParams
            if (lp.width > 0 && lp.height > 0) {
                size(lp.width, lp.height)
            }

            if (transformation != null) {
                transformations(transformation)
            }
        }

        return request
    }
}
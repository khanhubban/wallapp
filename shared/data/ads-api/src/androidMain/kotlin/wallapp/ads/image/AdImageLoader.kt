package wallapp.ads.image

import android.widget.ImageView


interface AdImageLoader {

    fun showImageInView(image: AdImage, view: ImageView)

}
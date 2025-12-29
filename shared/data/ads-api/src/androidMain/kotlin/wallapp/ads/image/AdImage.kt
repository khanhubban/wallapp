package wallapp.ads.image

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes


interface AdImage {

    val uri: Uri?

    val drawable: Drawable?

    @get:DrawableRes val resourceId: Int?

    val scale: Double

}
package wallapp.ads.inline.internal

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes
import wallapp.ads.image.AdImage

class AdImageInternal : AdImage {
    override val uri: Uri
    override val drawable: Drawable?

    @DrawableRes
    override val resourceId: Int?

    constructor(drawable: Drawable) {
        this.drawable = drawable
        resourceId = null
        uri = Uri.parse("<specified_drawable>")
    }

    constructor(uri: Uri) {
        this.uri = uri
        drawable = null
        resourceId = null
    }

    constructor(@DrawableRes drawableRes: Int) {
        resourceId = drawableRes
        drawable = null
        uri = Uri.parse("<specified_drawable_res>")
    }

    override val scale: Double
        get() = 1.0
}
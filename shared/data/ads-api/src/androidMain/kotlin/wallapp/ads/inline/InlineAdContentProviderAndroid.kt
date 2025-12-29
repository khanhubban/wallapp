package wallapp.ads.inline

import android.graphics.drawable.BitmapDrawable
import android.text.TextUtils
import wallapp.ads.image.AdImage

/**
 *
 */
abstract class InlineAdContentProviderAndroid : InlineAdContentProvider {
    abstract val callToActionIcon: AdImage?
    abstract val images: List<AdImage?>?
    abstract val icon: AdImage?
    open val mediaContent: Any? get() = false

    override fun toString(): String {
        return ("contentState: $contentState"
                + "icon: " + asLoggable(icon)
                + "images: " + asLoggable(images))
    }

    fun asLoggable(string: CharSequence?): CharSequence {
        return """
               ${(if (TextUtils.isEmpty(string)) "<none>" else string).toString()}
               
               """.trimIndent()
    }

    fun asLoggable(images: List<AdImage?>?): CharSequence {
        if (images == null || images.size == 0) return "<none>"
        var result = ""
        for (i in images.indices) {
            result += "image_" + i + ": " + asLoggable(images[i])
        }
        return result
    }

    fun asLoggable(image: AdImage?): CharSequence {
        if (image == null) return "<none>"
        val uri = image.uri
        var result: String = uri?.toString() ?: ""
        val drawable = image.drawable
        if (drawable != null && drawable is BitmapDrawable) {
            result += ", " + drawable.bitmap.width + "x" + drawable.bitmap.height
        }
        return asLoggable(result)
    }
}
package wallapp.ads.inline

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import wallapp.ads.image.AdImage
import wallapp.ads.inline.internal.AdImageInternal
import wallapp.ads.inline.style.AdStyle
import wallapp.annotation.ColorInt
import wallapp.resources.R
import wallapp.resources.isXmlResource


/**
 *
 */
class InlineAdConfigAndroid internal constructor(
    override val adSource: InlineAdSource,
    val adDescriptor: InlineAdDescriptor,
    val adInitDescriptor: InlineAdInitDescriptor,
    var adStyle: AdStyle,
    val label: String,
    val onCloseClickListener: View.OnClickListener?,
    val onPlaceholderCloseClickListener: View.OnClickListener?,
    val headline: String?,
    val body: CharSequence?,
    val callToAction: String?,
    val callToActionIcon: AdImage?,
    val icon: AdImage?,
    val iconTint: ColorStateList?,
    val image: AdImage?,
    val imageTint: ColorStateList?,
    val onClickListener: View.OnClickListener?,
    val onPlaceholderClickListener: View.OnClickListener?,
    val onActionClickListener: View.OnClickListener?,
    val fallbackAdConfig: InlineAdConfig?
) : InlineAdConfig {
    /**
     *
     */
    class Builder(
        val adSource: InlineAdSource,
        val adDescriptor: InlineAdDescriptor,
        val adInitDescriptor: InlineAdInitDescriptor,
        val style: AdStyle,
    ) {
        var headline: String? = null
        var body: CharSequence? = null
        var callToAction: String? = null
        var callToActionIcon: AdImage? = null
        var icon: AdImage? = null
        var iconTint: ColorStateList? = null
        var image: AdImage? = null
        var imageTint: ColorStateList? = null
        var fallbackAdConfig: InlineAdConfig? = null
        private var onClickListener: View.OnClickListener? = null
        private var onActionClickListener: View.OnClickListener? = null
        private var onCloseClickListener: View.OnClickListener? = null
        private var onPlaceholderClickListener: View.OnClickListener? = null
        private var onPlaceholderCloseClickListener: View.OnClickListener? = null

        fun create(
            resources: Resources
        ): InlineAdConfigAndroid {
            validate(resources)
            return InlineAdConfigAndroid(
                adSource,
                adDescriptor,
                adInitDescriptor,
                adStyle = style,
                label = "${adInitDescriptor::class.simpleName}_${style.label}",
                onCloseClickListener = onCloseClickListener,
                onPlaceholderCloseClickListener = onPlaceholderCloseClickListener,
                headline = headline,
                body = body,
                callToAction = callToAction,
                callToActionIcon = callToActionIcon,
                icon = icon,
                iconTint = iconTint,
                image = image,
                imageTint = imageTint,
                onClickListener = onClickListener,
                onPlaceholderClickListener = onPlaceholderClickListener,
                onActionClickListener = onActionClickListener,
                fallbackAdConfig = fallbackAdConfig,
            )
        }

        private fun validate(res: Resources) {
            // Note: Because of a Picasso limitation (https://github.com/square/picasso/issues/1109). This is fixed
            // in Picasso 3.x, but that has yet to be released.
            val icon = icon
            if (icon?.resourceId != null) {
                require(!res.isXmlResource(icon.resourceId!!)) { "Icon is set via resource, which won't load via Picasso 2.x. Pass as an inflated Drawable instead." }
            }

            val image = image
            if (image?.resourceId != null) {
                require(!res.isXmlResource(image.resourceId!!)) { "Image is set via resource, which won't load via Picasso 2.x. Pass as an inflated Drawable instead." }
            }

            val callToActionIcon = callToActionIcon
            if (callToActionIcon?.resourceId != null) {
                require(!res.isXmlResource(callToActionIcon.resourceId!!)) { "Icon is set via resource, which won't load via Picasso 2.x. Pass as an inflated Drawable instead." }
            }
        }

        fun onClickListener(onClickListener: View.OnClickListener?): Builder {
            this.onClickListener = onClickListener
            return this
        }

        fun onCloseClickListener(onCloseClickListener: View.OnClickListener?): Builder {
            this.onCloseClickListener = onCloseClickListener
            return this
        }

        fun onPlaceholderClickListener(onCloseClickListener: View.OnClickListener?): Builder {
            onPlaceholderClickListener = onCloseClickListener
            return this
        }

        fun onPlaceholderCloseClickListener(onCloseClickListener: View.OnClickListener?): Builder {
            onPlaceholderCloseClickListener = onCloseClickListener
            return this
        }

        fun headline(headline: String?): Builder {
            this.headline = headline
            return this
        }

        fun body(body: CharSequence?): Builder {
            this.body = body
            return this
        }

        fun callToAction(callToAction: String?): Builder {
            this.callToAction = callToAction
            return this
        }

        fun callToActionClickListener(onClickListener: View.OnClickListener?): Builder {
            onActionClickListener = onClickListener
            return this
        }

        fun callToActionIcon(drawable: Drawable?): Builder {
            callToActionIcon = drawable?.let { AdImageInternal(it) }
            return this
        }

        fun icon(uri: Uri?): Builder {
            icon = AdImageInternal(uri!!)
            return this
        }

        fun icon(drawable: Drawable?): Builder {
            icon = drawable?.let { AdImageInternal(it) }
            return this
        }

        fun icon(@DrawableRes resourceId: Int): Builder {
            icon = AdImageInternal(resourceId)
            return this
        }

        fun iconTint(@ColorInt iconTint: Int): Builder {
            this.iconTint = ColorStateList.valueOf(iconTint)
            return this
        }

        fun iconTint(iconTint: ColorStateList?): Builder {
            this.iconTint = iconTint
            return this
        }

        fun image(uri: Uri): Builder {
            image = AdImageInternal(uri)
            return this
        }

        fun image(drawable: Drawable): Builder {
            image = AdImageInternal(drawable)
            return this
        }

        fun image(@DrawableRes resourceId: Int): Builder {
            image = AdImageInternal(resourceId)
            return this
        }

        fun imageTint(@ColorInt imageTint: Int): Builder {
            this.imageTint = ColorStateList.valueOf(imageTint)
            return this
        }

        fun imageTint(imageTint: ColorStateList?): Builder {
            this.imageTint = imageTint
            return this
        }

        fun fallbackAdConfig(fallbackAdConfig: InlineAdConfig?): Builder {
            this.fallbackAdConfig = fallbackAdConfig
            return this
        }
    }

    @get:LayoutRes val rootLayoutId: Int
        get() = adInitDescriptor.rootLayoutId

    fun inflateAd(context: Context, root: ViewGroup?): View {
        val result = LayoutInflater.from(context).inflate(rootLayoutId, root) as ViewGroup
        val adLayout = LayoutInflater.from(context).inflate(adStyle.layoutId, null)
        result.addView(adLayout, result.childCount)
        var closeButton = adLayout.findViewById<View>(R.id.ad_close)
        if (closeButton == null) {
            closeButton = result.findViewById(R.id.ad_close)
        }
        if (onCloseClickListener != null) {
            closeButton?.visibility = View.VISIBLE
            closeButton?.setOnClickListener(onCloseClickListener)
            closeButton?.bringToFront()
        } else {
            closeButton?.visibility = View.GONE
        }
        return result
    }
}
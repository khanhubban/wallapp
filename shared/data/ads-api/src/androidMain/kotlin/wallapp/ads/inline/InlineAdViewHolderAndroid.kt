package wallapp.ads.inline

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import wallapp.ads.inline.style.AdStyle
import wallapp.ads.widget.shimmer.Shimmer.ColorHighlightBuilder
import wallapp.ads.widget.shimmer.ShimmerFrameLayout
import wallapp.log.Log
import wallapp.resources.R
import wallapp.utils.getColor


class InlineAdViewHolderAndroid(context: Context) : FrameLayout(context), InlineAdViewHolder {
    private var placeholder: View? = null
    private var currentStyle: AdStyle? = null
    private var content: InlineAdViewAndroid? = null
    private var isDestroyed = false

    fun applyStyle(style: AdStyle) {
        if (style != currentStyle) {
            style.applyAdBackgroundStyleTo(this)
            if (layoutParams == null) {
                layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, style.height)
            } else {
                val lp = layoutParams
                lp.height = style.height
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            }
            content?.also { style.applyTo(it) }
            currentStyle = style
        }
    }

    fun showAd(adView: InlineAdViewAndroid) {
        if (isDestroyed) {
            adView.destroy()
            return
        }
        ensureContentNotDisplaying()
        content = adView
        if (currentStyle != null) {
            currentStyle!!.applyTo(adView)
        }
        val animDuration = resources.getInteger(R.integer.ad_crossfade_duration)
        placeholder?.also {
            it.animate()
                .alpha(0f).setDuration(animDuration.toLong())
                .withEndAction { removeView(it) }
                .start()
            this.placeholder = null
        }
        addView(content!!.adView)
        adView.adView.alpha = 0f
        adView.adView.animate()
            .alpha(1f).setDuration(animDuration.toLong())
            .start()
    }

    fun showPlaceholderFor(adConfig: InlineAdConfigAndroid) {
        ensureContentNotDisplaying()
        if (placeholder == null) {
            placeholder = createAdPlaceholderFor(adConfig)
            addView(placeholder)
        }
    }

    private fun createAdPlaceholderFor(config: InlineAdConfigAndroid): View {
        val inflater = LayoutInflater.from(context)
        val placeHolderContent = inflater.inflate(config.adStyle.layoutPlaceholderId, this, false)
        val closeView = placeHolderContent.findViewById<View>(R.id.ad_close)
        if (config.onPlaceholderCloseClickListener != null && closeView != null) {
            closeView.setOnClickListener(config.onPlaceholderCloseClickListener)
        }
        val shimmer = ColorHighlightBuilder()
            .setHighlightColor(getColor(R.color.ad_shimmer_highlight_color))
            .setBaseColor(getColor(R.color.ad_shimmer_base_color))
            .setDuration(1000)
            .build()
        val placeHolder: View
        var shimmerLayout: ShimmerFrameLayout? = placeHolderContent.findViewById(R.id.ad_shimmer)
        if (shimmerLayout == null) {
            shimmerLayout = ShimmerFrameLayout(context)
            shimmerLayout.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            shimmerLayout.addView(placeHolderContent)
            placeHolder = shimmerLayout
        } else {
            placeHolder = placeHolderContent
        }
        shimmerLayout.setShimmer(shimmer)
        placeHolder.setOnClickListener(config.onPlaceholderClickListener)
        return placeHolder
    }

    fun destroy() {
        Log.d("[AdDebug] InlineAdViewHolder.destroy()")
        isDestroyed = true
        content?.destroy()
        content = null
    }

    private fun ensureContentNotDisplaying() {
        check(content == null) { "forbidden to call this method after a call to showAd" }
    }
}
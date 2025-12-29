@file:Suppress("unused")

package wallapp.ads.inline.style

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.TextView
import androidx.annotation.LayoutRes
import wallapp.ads.inline.InlineAdViewAndroid
import wallapp.ads.inline.style.AdStylePresets.callToActionDefaultBgStyleBuilder


class AdStyle private constructor(
    val label: String,
    @LayoutRes val layoutId: Int,
    @LayoutRes val layoutPlaceholderId: Int,
    val height: Int,
    private val backgroundHelper: AdBackgroundHelper,
    private val verticalIndicatorOffset: Int,
    val horizontalIndicatorOffset: Int,
    val adTextStyle: AdTextStyle?,
    private val callToActionTextStyle: AdTextStyle?,
) {
    /***
     * [height] the height, either LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, or a
     * fixed size in pixels.
     */
    class Builder(
        adStyleConfig: AdStyleConfig,
        res: Resources,
        private val label: String,
        @LayoutRes private val contentLayoutId: Int,
        @LayoutRes private val contentLayoutPlaceholderId: Int,
        private val height: Int,
    ) {
        private var adTextStyle: AdTextStyle?
        private var adBackgroundStyle: AdBackgroundStyle
        private var callToActionTextStyle: AdTextStyle?
        private var callToActionBackgroundStyle: AdBackgroundStyle?

        fun backgroundStyle(adBackgroundStyle: AdBackgroundStyle): Builder {
            this.adBackgroundStyle = adBackgroundStyle
            return this
        }

        /**
         * Overrides the default text style
         * @param adTextStyle text style of ad, use `null` to apply style defined in layout
         * and not override it.
         */
        fun textStyle(adTextStyle: AdTextStyle?): Builder {
            this.adTextStyle = adTextStyle
            return this
        }

        fun callToActionBackgroundStyle(adBackgroundStyle: AdBackgroundStyle?): Builder {
            callToActionBackgroundStyle = adBackgroundStyle
            return this
        }

        fun callToActionTextStyle(adTextStyle: AdTextStyle?): Builder {
            callToActionTextStyle = adTextStyle
            return this
        }

        fun noTextAndActionStyle(): Builder {
            adTextStyle = null
            callToActionBackgroundStyle = null
            callToActionTextStyle = null
            return this
        }

        fun create(): AdStyle {
            return AdStyle(
                label, contentLayoutId,
                contentLayoutPlaceholderId, height,
                AdBackgroundHelper(adBackgroundStyle, callToActionBackgroundStyle),
                adBackgroundStyle.strokeWidth, adBackgroundStyle.strokeWidth,
                adTextStyle, callToActionTextStyle
            )
        }

        init {
            adTextStyle = AdTextStyle.from(adStyleConfig.accentColor)
            adBackgroundStyle = AdBackgroundStyle.DEFAULT
            callToActionTextStyle = AdTextStyle.DEFAULT
            callToActionBackgroundStyle = callToActionDefaultBgStyleBuilder(adStyleConfig, res)
                .build()
        }
    }

    fun applyTo(adView: InlineAdViewAndroid) {
        applyTextStyleTo(adView.headlineView)
        applyTextStyleTo(adView.bodyView)
        applyTextStyleTo(adView.priceView)
        applyTextStyleTo(adView.storeView)
        applyTextStyleTo(adView.advertiserView)
        val callToAction = adView.callToActionView
        if (callToAction is Button && callToAction.getVisibility() == View.VISIBLE) {
            applyCallToActionStyleTo(callToAction)
        }
        applyIndicatorOffsetsTo(adView)
    }

    fun applyAdBackgroundStyleTo(bgView: View?) {
        bgView?.also {
            backgroundHelper.applyStyleTo(bgView)
        }
    }

    private fun applyTextStyleTo(text: View?) {
        if (adTextStyle != null
            && text != null
            && text is TextView
            && text.getVisibility() == View.VISIBLE) {
            adTextStyle.applyTo((text as TextView?)!!)
        }
    }

    private fun applyIndicatorOffsetsTo(adView: InlineAdViewAndroid) {
        applyIndicatorOffsetsTo(adView.closeButtonView)
        applyIndicatorOffsetsTo(adView.adAttributionView)
        val adChoices = adView.adChoicesView
        adChoices?.observeForever { view: View? -> this.applyIndicatorOffsetsTo(view) }
    }

    private fun applyCallToActionStyleTo(callToAction: Button) {
        callToActionTextStyle?.applyTo(callToAction)
        backgroundHelper.applyStyleToCallToAction(callToAction)
    }

    private fun applyIndicatorOffsetsTo(view: View?) {
        if (view != null) {
            val mlp = view.layoutParams as MarginLayoutParams
            if (mlp.topMargin != verticalIndicatorOffset || mlp.bottomMargin != verticalIndicatorOffset || mlp.leftMargin != horizontalIndicatorOffset || mlp.rightMargin != horizontalIndicatorOffset) {
                mlp.bottomMargin = verticalIndicatorOffset
                mlp.topMargin = mlp.bottomMargin
                mlp.leftMargin = horizontalIndicatorOffset
                mlp.rightMargin = mlp.leftMargin
                view.post(Runnable { view.requestLayout() })
            }
        }
    }
}
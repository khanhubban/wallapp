package wallapp.ads.inline.internal

import android.view.View
import androidx.lifecycle.LiveData
import wallapp.ads.inline.InlineAdViewAndroid

class InlineAdViewInternal(override val adView: View) : InlineAdViewAndroid {
    override var iconView: View? = null
    override var headlineView: View? = null
    override var bodyView: View? = null
    override var callToActionView: View? = null
    override var advertiserView: View? = null
    override var storeView: View? = null
    override var priceView: View? = null
    override var imageView: View? = null
    override var starRatingView: View? = null
    
    override fun findViewById(id: Int): View? {
        return adView.findViewById(id)
    }

    override val adAttributionView: View?
        get() = null
    override val closeButtonView: View?
        get() = null
    override val adChoicesView: LiveData<View>?
        get() = null

    override fun setMediaView(view: Any?) {}
    override fun destroy() {}
}
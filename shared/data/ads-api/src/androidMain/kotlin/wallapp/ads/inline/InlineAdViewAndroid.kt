package wallapp.ads.inline

import android.view.View
import androidx.lifecycle.LiveData


interface InlineAdViewAndroid : InlineAdView {
    val adView: View
    var headlineView: View?
    var callToActionView: View?
    var iconView: View?
    var bodyView: View?
    var storeView: View?
    var priceView: View?
    var advertiserView: View?
    var imageView: View?
    var starRatingView: View?
    val adAttributionView: View?
    val closeButtonView: View?
    val adChoicesView: LiveData<View>?

    fun setMediaView(view: Any?)
    fun findViewById(id: Int): View?
    fun destroy()
}
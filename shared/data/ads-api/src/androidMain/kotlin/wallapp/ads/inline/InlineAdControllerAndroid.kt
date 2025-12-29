package wallapp.ads.inline

import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.text.TextUtils
import android.view.TouchDelegate
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import wallapp.ads.image.AdImage
import wallapp.ads.image.AdImageLoader
import wallapp.coroutine.collectIn
import wallapp.log.Log
import wallapp.prefs.PreferenceStorage
import wallapp.resources.R
import wallapp.theme.ThemeType
import wallapp.time.createOperationTimerForDebug
import wallapp.util.WeakReference

abstract class InlineAdControllerAndroid protected constructor(
    protected val adConfig: InlineAdConfigAndroid,
    val imageLoader: AdImageLoader,
    private val coroutineScopeMain: CoroutineScope,
    private val preferenceStorage: PreferenceStorage,
) : InlineAdController {
    protected var adContent: InlineAdContentProviderAndroid? = null

    private val timer = createOperationTimerForDebug()

    private var themeJob: Job? = null

    private var headlineView: TextView? = null
    private var bodyView: TextView? = null


    private val componentCallbacks = object : ComponentCallbacks {
        override fun onConfigurationChanged(newConfig: Configuration) {
            // Check if the theme changed
            val isNightMode = (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            headlineView?.let {
                handleThemeConfiguration(it.context, preferenceStorage.themeType.value, isNightMode)
            }
        }

        override fun onLowMemory() {
            // Handle low memory situation if needed
        }
    }

    fun destroy(context: Context) {
        Log.d("[AdDebug] InlineAdController.destroy()")
        adContent?.destroy()
        adContent = null

        themeJob?.cancel()
        themeJob = null
        headlineView = null
        bodyView = null
        context.applicationContext.unregisterComponentCallbacks(componentCallbacks)
    }

    fun canPopulate(): Boolean {
        return adContent != null
    }

    abstract fun setAd(nativeAd: Any?)

    protected abstract fun inflateAdView(context: Context): InlineAdViewAndroid

    fun configure(adView: InlineAdViewAndroid): InlineAdContentState {
        return adContent!!.let {
            it.configure(adView)
            it.contentState
        }
    }

    /**
     * Populates a [InlineAdViewAndroid] object with data from a given
     * [InlineAdContentProviderAndroid].
     *
     */
    fun createAdView(context: Context): InlineAdViewAndroid {
        val timestamp = timer.operationStart()
        val adView = inflateAdView(context)

        val adContent = adContent!!
        val adContentState = adContent.contentState

//    if (DEBUG) {
//      Log.d(TAG, "[" + adConfig.label + "] " + adContent.toString());
//    }

//    // Get the video controller for the ad. One will always be provided, even if the ad doesn't
//    // have a video asset.
//    VideoController vc = nativeAd.getVideoController();
//
//    // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
//    // VideoController will call methods on this object when events occur in the video
//    // lifecycle.
//    vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
//      public void onVideoEnd() {
//        // Publishers should allow native ads to complete video playback before refreshing
//        // or replacing them with another ad in the same UI location.
//        super.onVideoEnd();
//      }
//    });

        val mediaView = adView.findViewById(R.id.ad_media)
        val composeViewAdCallToAction = adView.findViewById(R.id.compose_view_ad_call_to_action) as ComposeView
        configureMediaView(adView, mediaView)
        val composeViewAdIconView = adView.findViewById(R.id.compose_view_ad_app_icon) as? ComposeView

        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = composeViewAdCallToAction
        adView.iconView = composeViewAdIconView ?: adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // Some assets are guaranteed to be in every ad.
        headlineView = adView.headlineView as TextView?
        headlineView?.text = adContentState.headline

        // These assets aren't guaranteed to be in every ad, so it's important to
        // check before trying to display them.
        /*val callToActionView = adView.callToActionView as Button?
        if (callToActionView != null) {
            if (adContentState.callToAction?.isEmpty() == true) {
                callToActionView.visibility = View.GONE
            } else {
                callToActionView.visibility = View.VISIBLE
                callToActionView.text = adContentState.callToAction
                val icon = adContent.callToActionIcon?.drawable
                if (icon != null && callToActionView is MaterialButton) {
                    callToActionView.icon = icon
                }
            }
        }*/
        configureComposeView(adView, composeViewAdCallToAction, adContentState.callToAction)
        bodyView = adView.bodyView as TextView?
        bodyView?.let {
            if (TextUtils.isEmpty(adContentState.body)) {
                it.visibility = View.GONE
            } else {
                it.text = adContentState.body
                it.visibility = View.VISIBLE
            }
        }

        themeJob?.cancel()
        themeJob = preferenceStorage.themeType.collectIn(coroutineScopeMain) {
            handleThemeConfiguration(context, themeType = it, isNightMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
        }

        context.applicationContext.registerComponentCallbacks(componentCallbacks)

        if (composeViewAdIconView != null) {
            configureComposeView(adView, composeViewAdIconView, adContent.icon)
        } else {
            val iconView = adView.iconView as ImageView?
            if (iconView != null) {
                val icon = adContent.icon
                if (icon == null) {
                    iconView.visibility = View.GONE
                } else {
                    iconView.visibility = View.VISIBLE
                    imageLoader.showImageInView(icon, iconView)
                }
            }
        }

        val priceView = adView.priceView as TextView?
        if (priceView != null) {
            val price = adContentState.price
            if (price == null) {
                priceView.visibility = View.GONE
            } else {
                priceView.visibility = View.VISIBLE
                priceView.text = price
            }
        }
        val storeView = adView.storeView as TextView?
        if (storeView != null) {
            val store = adContentState.store
            if (store == null) {
                storeView.visibility = View.GONE
            } else {
                storeView.visibility = View.VISIBLE
                storeView.text = store
            }
        }
        val startRatingView = adView.starRatingView as RatingBar?
        if (startRatingView != null) {
            val starRating = adContentState.starRating
            if (starRating == null) {
                startRatingView.visibility = View.GONE
            } else {
                startRatingView.rating = starRating.toFloat()
                startRatingView.visibility = View.VISIBLE
            }
        }
        val advertiserView = adView.advertiserView as TextView?
        if (advertiserView != null) {
            val advertiser = adContentState.advertiser
            if (advertiser == null) {
                advertiserView.visibility = View.GONE
            } else {
                advertiserView.text = advertiser
                advertiserView.visibility = View.VISIBLE
            }
        }
        val adViewRef = WeakReference(adView)
        val mappedBounds = Rect()
        val viewTreeObserver = adView.adView.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val inlineAdView = adViewRef.get()
                if (inlineAdView == null) {
                    if (viewTreeObserver.isAlive) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                } else {
                    mapAdViewClicksToHeadline(mappedBounds, inlineAdView)
                }
            }
        })
        timer.logOperationTime(
            timestamp, "populateAdView()",
            Thread.currentThread().name
        )
        return adView
    }

    open fun configureMediaView(inlineAdView: InlineAdViewAndroid, mediaView: View?) {
        TODO("Must be overridden by a subclass")
    }

    open fun configureComposeView(inlineAdView: InlineAdViewAndroid, composeView: ComposeView?, title: String?) {
        TODO("Must be overridden by a subclass")
    }

    open fun configureComposeView(inlineAdView: InlineAdViewAndroid, composeView: ComposeView?, image: AdImage?) {
        TODO("Must be overridden by a subclass")
    }

    private fun handleThemeConfiguration(
        context: Context,
        themeType: ThemeType,
        isNightMode: Boolean,
    ) {
        val headlineColor: Int
        val bodyColor: Int

        when (themeType) {
            ThemeType.Light -> {
                headlineColor = ContextCompat.getColor(context, android.R.color.black)
                bodyColor = ContextCompat.getColor(context, android.R.color.darker_gray)
            }

            ThemeType.Dark -> {
                headlineColor = ContextCompat.getColor(context, android.R.color.white)
                bodyColor = ContextCompat.getColor(context, android.R.color.white)
            }

            else -> {
                headlineColor = ContextCompat.getColor(context, if (isNightMode) android.R.color.white else android.R.color.black)
                bodyColor = ContextCompat.getColor(context, if (isNightMode) android.R.color.white else android.R.color.darker_gray)
            }
        }

        headlineView?.setTextColor(headlineColor)
        bodyView?.setTextColor(bodyColor)
    }

    private fun mapAdViewClicksToHeadline(currentBounds: Rect, adView: InlineAdViewAndroid?) {
        if (adView == null) return
        val view = adView.adView
        if (currentBounds.width() != view.width || currentBounds.height() != view.height) {
            val headlineView = adView.headlineView
            if (headlineView != null) {
                val newBounds = Rect(0, 0, view.width, view.height)
                view.touchDelegate = TouchDelegate(newBounds, headlineView)
                currentBounds.set(newBounds)
            }
        }
    }
}
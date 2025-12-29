package wallapp.ads.inline

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.core.util.Pair
import wallapp.activity.ActivityLifecycleListener
import wallapp.ads.image.AdImageLoader
import wallapp.log.Log
import wallapp.system.ui.controller.UiController
import wallapp.system.ui.controller.activity
import wallapp.view.DelayedViewManager.delayInitialization

/**
 * Usage:
 * 1. Call [prepareAd] to pre-fetch content for the ad. Call once per ad.
 * 2. Call [showAd] to display the ad.
 *
 * Note: this class handles destroying any ads created by the [Context] used
 */
class InlineAdCreatorDefault(
    private val inlineAdInitFactory: InlineAdInitFactory,
    context: Context,
    val imageLoader: AdImageLoader,
): InlineAdCreator {
    private val cachedReusableAdHandles: MutableMap<InlineAdDescriptor, Pair<Activity, InlineAdHandleAndroid>> = mutableMapOf()
    
    private val activityBoundAdHandles: MutableList<Pair<Activity, InlineAdHandleAndroid>> = ArrayList()

    private fun getCachedReusableAdHandle(activity: Activity, adDescriptor: InlineAdDescriptor): InlineAdHandleAndroid? {
        return cachedReusableAdHandles[adDescriptor]?.let {
            if (it.first === activity) {
                Log.d("[AdDebug] Using cached InlineAdHandle for $adDescriptor")
                it.second
            } else {
                null
            }
        }
    }

    private fun cacheActivityBoundAdHandle(activity: Activity, adHandle: InlineAdHandleAndroid) {
        activityBoundAdHandles.add(Pair.create(activity, adHandle)).also {
            Log.d("[AdDebug] cache adHandle %s to Activity %s",
                adHandle, activity::class.simpleName)
        }
    }

    private fun cacheReusableAdHandle(
        activity: Activity,
        adDescriptor: InlineAdDescriptor,
        adHandle: InlineAdHandleAndroid,
    ) {
        require(cachedReusableAdHandles[adDescriptor] == null)
        cachedReusableAdHandles[adDescriptor] = Pair.create(activity, adHandle)
        Log.d("[AdDebug] cache reusable adHandle %s for Activity %s",
            adHandle, activity::class.simpleName)
    }

    override fun prepareAd(
        uiController: UiController,
        adConfig: InlineAdConfig,
    ): InlineAdHandleAndroid {
        val activity = uiController.activity
        require(adConfig is InlineAdConfigAndroid)
        val adDescriptor = adConfig.adDescriptor

        var adHandle = if (adDescriptor.reuseAdHandle) {
            getCachedReusableAdHandle(activity, adDescriptor)
        } else {
            null
        }

        if (adHandle == null) {
            adHandle = inlineAdInitFactory.createInlineAdHandle(adConfig) as InlineAdHandleAndroid
            adHandle.prepareNativeAd()

            cacheActivityBoundAdHandle(activity, adHandle)
            if (adDescriptor.reuseAdHandle) {
                cacheReusableAdHandle(activity, adDescriptor, adHandle)
            }
        }

        return adHandle
    }

    private fun validateAdHandle(adHandle: InlineAdHandleAndroid?): InlineAdHandleAndroid? {
        if (adHandle != null) {
            for (entry in activityBoundAdHandles) {
                if (entry.second === adHandle) {
                    return adHandle
                }
            }
            throw IllegalArgumentException("Unable to locate adHandle")
        }
        return null
    }

    override fun showAd(
        uiController: UiController,
        adHandle: InlineAdHandle,
        useDelayedViewInitializer: Boolean,
        showPlaceholder: Boolean
    ): InlineAdViewHolderAndroid {
        require(adHandle is InlineAdHandleAndroid)
        val activity = uiController.activity
        val holder = InlineAdViewHolderAndroid(activity)
        if (useDelayedViewInitializer) {
            showAd(holder, adHandle, populate = false, showPlaceholder)
            delayInitialization(holder) {
                showAd(holder, adHandle, populate = true, showPlaceholder)
            }
        } else {
            showAd(holder, adHandle, populate = true, showPlaceholder)
        }
        return holder
    }

    private fun showAd(
        parentView: InlineAdViewHolderAndroid,
        nativeAdHandle: InlineAdHandleAndroid,
        populate: Boolean,
        showPlaceholder: Boolean = true
    ) {
        val adHandle = validateAdHandle(nativeAdHandle)
            ?: throw IllegalArgumentException("Must call prepareAd() first")
        adHandle.configureAdView(parentView, populate, showPlaceholder)
    }

    fun destroyAllForActivity(activity: Activity) {
        Log.i("[AdDebug] destroyAllForActivity(), cachedReusableAdHandles.size: %d, contextBoundAdHandles.size: %d",
            cachedReusableAdHandles.size, activityBoundAdHandles.size)

        val adHandleIterator = activityBoundAdHandles.iterator()
        while (adHandleIterator.hasNext()) {
            val entry = adHandleIterator.next()
            if (entry.first === activity) {
                entry.second?.destroy()
                adHandleIterator.remove()
                Log.d("[AdDebug] Removed adHandle entry ${entry.second}, size: ${activityBoundAdHandles.size}")
            }
        }

        val adUnitIterator: MutableIterator<Map.Entry<InlineAdDescriptor, Pair<Activity, InlineAdHandleAndroid>>> =
            cachedReusableAdHandles.entries.iterator()
        while (adUnitIterator.hasNext()) {
            val entry = adUnitIterator.next()
            if (entry.value.first === activity) {
                adUnitIterator.remove()
                Log.d("[AdDebug] Removed cached reusable entry ${entry.value.second}, size: ${cachedReusableAdHandles.size}")
            }
        }
    }

    private val activityLifecycleCallbacks = object : ActivityLifecycleListener.Callbacks() {

        override fun onActivityDestroyed(activity: Activity) {
            destroyAllForActivity(activity)
        }
    }

    init {
        ActivityLifecycleListener(context as Application, activityLifecycleCallbacks)
    }

    companion object {
        const val TAG = "[AdDebug]"
    }
}
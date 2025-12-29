package wallapp.ads.inline

import android.content.Context
import android.os.Looper
import androidx.annotation.UiThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import wallapp.ads.inline.InlineAdHandleState.AD_STATE_DESTROYED
import wallapp.ads.inline.InlineAdHandleState.AD_STATE_ERROR
import wallapp.ads.inline.InlineAdHandleState.AD_STATE_LOADED
import wallapp.ads.inline.InlineAdHandleState.AD_STATE_LOADING
import wallapp.ads.inline.InlineAdHandleState.AD_STATE_UNINITIALIZED
import wallapp.log.Log
import wallapp.time.OperationTimer
import wallapp.time.createOperationTimerForDebug
import wallapp.util.WeakReference
import java.util.WeakHashMap

abstract class InlineAdHandleAndroid(
    protected val context: Context,
    private val inlineAdInitFactory: InlineAdInitFactory,
    private val adConfig: InlineAdConfigAndroid,
    protected val adController: InlineAdControllerAndroid,
    private val coroutineScopeMain: CoroutineScope,
    private val coroutineScopeIo: CoroutineScope,
) : InlineAdHandle {
    fun interface OnAdLoadedListener {
        fun onAdLoaded(nativeAd: Any?)
    }

    override val viewStateFlow: MutableStateFlow<InlineAdViewState> =
        MutableStateFlow(InlineAdViewState.NoOp)

    private var adViewHolder: WeakReference<InlineAdViewHolderAndroid>? = null

    private var state: InlineAdHandleState = AD_STATE_UNINITIALIZED
    private val pendingConfigureCalls: WeakHashMap<InlineAdViewHolderAndroid, Any> = WeakHashMap()
    private var fallbackAdHandle: InlineAdHandleAndroid? = null

    val timer: OperationTimer = createOperationTimerForDebug()

    /***
     * @return Ad item height, height the height, either
     * [android.view.ViewGroup.LayoutParams.WRAP_CONTENT],
     * [android.view.ViewGroup.LayoutParams.MATCH_PARENT], or a fixed size in pixels.
     */
    val adHeight: Int
        get() = adConfig.adStyle.height

    init {
        setState(AD_STATE_UNINITIALIZED)
    }

    fun prepareNativeAd() {
        ensureOnMainThread()
        ensureNotDestroyed()
        if (state == AD_STATE_UNINITIALIZED) {
            setState(AD_STATE_LOADING)
            coroutineScopeIo.launch {
                loadNativeAd()
            }
        }
    }

    fun configureAdView(
        holder: InlineAdViewHolderAndroid,
        populateView: Boolean,
        showInitialPlaceholder: Boolean = true
    ) {
        ensureOnMainThread()
        ensureNotDestroyed()

        if (fallbackAdHandle != null) {
            fallbackAdHandle!!.configureAdView(holder, populateView)
            return
        }

        holder.applyStyle(adConfig.adStyle)

        when (state) {
            AD_STATE_UNINITIALIZED, AD_STATE_LOADING -> {
                if (showInitialPlaceholder) {
                    holder.configurePlaceholderView()
                }
                if (populateView) {
                    addToPendingCalls(holder)
                }
                prepareNativeAd()
            }
            AD_STATE_LOADED -> if (populateView) {
                holder.configureAdView()
            } else {
                holder.configurePlaceholderView()
            }
            else -> holder.configurePlaceholderView()
        }
    }

    private fun updateViewState(viewState: InlineAdViewState) {
        Log.d("[AdDebug] updateViewState(): viewState=$viewState")
        viewStateFlow.value = viewState
    }

    private fun InlineAdViewHolderAndroid.configurePlaceholderView() {
        showPlaceholderFor(adConfig)
        updateViewState(InlineAdViewState.Loading)
    }

    private fun InlineAdViewHolderAndroid.configureAdView() {
        Log.d("doConfigureAdView()")
        coroutineScopeIo.launch {
            val inlineAdView = adController.createAdView(context)
            coroutineScopeMain.launch {
                onAdViewCreated(this@configureAdView, inlineAdView)
            }
        }
    }

    private fun onAdViewCreated(adViewHolder: InlineAdViewHolderAndroid, adView: InlineAdViewAndroid) {
        ensureOnMainThread()
        if (state == AD_STATE_DESTROYED) return

        this.adViewHolder = WeakReference(adViewHolder)

        val timestamp = timer.operationStart()

        val inlineAdContentState = adController.configure(adView)
        adViewHolder.showAd(adView)
        updateViewState(InlineAdViewState.Data(inlineAdContentState))

        timer.logOperationTime(
            timestamp, "AdHandle.configureAdView()",
            Thread.currentThread().name
        )
    }

    @UiThread
    fun destroy() {
        ensureOnMainThread()
        ensureNotDestroyed()
        fallbackAdHandle?.destroy()
        adController.destroy(context)
        adViewHolder?.get()?.also {
            it.destroy()
        }
        adViewHolder = null
        setState(AD_STATE_DESTROYED)
    }

    private fun addToPendingCalls(parent: InlineAdViewHolderAndroid) {
        pendingConfigureCalls[parent] = NULL_VALUE
    }

    private fun executePendingConfigureCalls() {
        if (isLoaded) {
            for (parent in pendingConfigureCalls.keys) {
                configureAdView(parent, true)
            }
            pendingConfigureCalls.clear()
        }
    }

    private fun tryRecoverFromError() {
        if (adConfig.fallbackAdConfig != null) {
            fallbackAdHandle = (inlineAdInitFactory.createInlineAdHandle(adConfig.fallbackAdConfig) as InlineAdHandleAndroid)
                .also {
                    it.prepareNativeAd()
                    for (pendingHolder in pendingConfigureCalls.keys) {
                        it.configureAdView(pendingHolder, true)
                    }
                }
        }
    }

    protected abstract fun loadNativeAd()

    protected fun setState(state: InlineAdHandleState) {
        if (isOnMainThread) {
            //AdHandle::destroy might have been called on the main thread before async loading finished
            if (this.state == AD_STATE_DESTROYED) return
            Log.d("setState(): %s -> %s", this.state, state)
            this.state = state
            if (state == AD_STATE_ERROR) {
                tryRecoverFromError()
            } else if (state == AD_STATE_LOADED) {
                executePendingConfigureCalls()
            }
        } else {
            coroutineScopeMain.launch { setState(state) }
        }
    }

    protected fun setNativeAd(nativeAd: Any?) {
        adController.setAd(nativeAd)
    }

    private val isLoaded: Boolean
        get() = state == AD_STATE_LOADED && adController.canPopulate()

    private fun ensureOnMainThread() {
        check(isOnMainThread) { "this method can be called only on the main thread" }
    }

    private fun ensureNotDestroyed() {
        check(state != AD_STATE_DESTROYED) { "attempted to call a method on destroyed AdHandle" }
    }

    private val isOnMainThread: Boolean
        get() = Looper.getMainLooper() == Looper.myLooper()

    companion object {
        private val NULL_VALUE = Any()
    }

}
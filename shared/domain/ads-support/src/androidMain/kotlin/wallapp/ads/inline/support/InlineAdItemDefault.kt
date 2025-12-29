package wallapp.ads.inline.support

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import wallapp.ads.inline.InlineAdConfig
import wallapp.ads.inline.InlineAdCreator
import wallapp.ads.inline.InlineAdHandle
import wallapp.ads.inline.InlineAdSource
import wallapp.ads.inline.InlineAdViewHolderAndroid
import wallapp.ads.inline.InlineAdViewState
import wallapp.log.Log
import wallapp.resources.R
import wallapp.system.ui.controller.UiController
import wallapp.system.ui.controller.UiControllerAndroid
import wallapp.system.ui.controller.activity
import wallapp.view.DelayedViewManager

@OptIn(ExperimentalCoroutinesApi::class)
class InlineAdItemDefault(
    private val adConfig: InlineAdConfig,
    private val adCreator: InlineAdCreator,
    private val coroutineScopeMain: CoroutineScope,
) : InlineAdItem {

    val inlineAdSource: InlineAdSource
        get() = adConfig.adSource

    var adElevation: Float? = null

    private var adViewHolder: InlineAdViewHolderAndroid? = null

    private val inlineAdHandle: MutableStateFlow<InlineAdHandle?> = MutableStateFlow(null)

    override val viewStateFlow: StateFlow<InlineAdViewState> = inlineAdHandle
        .flatMapLatest { handle ->
            handle?.viewStateFlow ?: MutableStateFlow(InlineAdViewState.NoOp)
        }
        .onEach { viewState ->
            Log.i("[AdDebug] emit viewStateFlow: newState=$viewState, inlineAdHandle=$inlineAdHandle")
        }
        .stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = InlineAdViewState.NoOp
        )

    fun requireAdViewHolder(activity: Activity): InlineAdViewHolderAndroid? =
        adViewHolder ?: prepareAndShowAd(activity)

//    fun requireAdConfig(activity: Activity): InlineAdConfigAndroid =
//        adConfig ?: configure(activity).second

    @Suppress("UNCHECKED_CAST")
    private fun prepareAndShowAd(activity: Activity): InlineAdViewHolderAndroid? {
        val uiController = UiControllerAndroid(activity)
        return adCreator.prepareAd(uiController, adConfig)?.let { inlineAdHandle: InlineAdHandle ->
            Log.w("[AdDebug] prepareAndShowAd(): inlineAdHandle=$inlineAdHandle")
            this.inlineAdHandle.value = inlineAdHandle
            adCreator.showAd(uiController, inlineAdHandle, false)
                .let { inlineAdViewHolder ->
                    (inlineAdViewHolder as? InlineAdViewHolderAndroid).also {
                        adViewHolder = it
                    }
                }
        }
    }

    fun applyAdStyle(adView: View) {
        adView.elevation = adElevation ?: adView.resources.getDimension(R.dimen.ad_elevation)
    }

    override fun bindAd(parent: Any /*ViewGroup*/, delayInit: Boolean) {
        require(parent is ViewGroup) { "Parent must be ViewGroup" }
        val activity = requireNotNull(parent.context as Activity)
        val adViewHolder: InlineAdViewHolderAndroid = requireAdViewHolder(activity) ?: return

        parent.removeAllViews()
        (adViewHolder.parent as? ViewGroup)?.removeView(adViewHolder)

        parent.addView(adViewHolder)
        applyAdStyle(parent)
        if (!delayInit) {
            adViewHolder.post{
                DelayedViewManager.executeDelayedInitializer(adViewHolder)
            }
        }
    }

    override fun bind(uiController: UiController, delayInit: Boolean) {
        val activity = requireNotNull(uiController.activity)
        val adViewHolder: InlineAdViewHolderAndroid = requireAdViewHolder(activity) ?: return
        if (!delayInit) {
            adViewHolder.post{
                DelayedViewManager.executeDelayedInitializer(adViewHolder)
            }
        }
    }
}

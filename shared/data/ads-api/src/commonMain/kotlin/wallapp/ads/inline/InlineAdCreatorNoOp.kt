package wallapp.ads.inline

import wallapp.system.ui.controller.UiController

object InlineAdCreatorNoOp : InlineAdCreator {
    override fun prepareAd(
        uiController: UiController,
        adConfig: InlineAdConfig,
    ): InlineAdHandle? = null

    override fun showAd(
        uiController: UiController,
        adHandle: InlineAdHandle,
        useDelayedViewInitializer: Boolean,
        showPlaceholder: Boolean
    ): InlineAdViewHolder? = null
}
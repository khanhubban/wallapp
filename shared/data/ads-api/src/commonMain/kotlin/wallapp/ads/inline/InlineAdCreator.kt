package wallapp.ads.inline

import wallapp.system.ui.controller.UiController


interface InlineAdCreator {

    fun prepareAd(
        uiController: UiController,
        adConfig: InlineAdConfig,
    ): InlineAdHandle?

    fun showAd(
        uiController: UiController,
        adHandle: InlineAdHandle,
        useDelayedViewInitializer: Boolean,
        showPlaceholder: Boolean = true,
    ): InlineAdViewHolder?
}

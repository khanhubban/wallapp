package wallapp.ads.inline.support

import wallapp.ads.AdType


class GlobalFullScreenAdShowCallbacksDefault(
    val listener: FullScreenAdShowListenerDefault,
) : GlobalFullScreenAdShowCallbacks() {

    override fun onAdShowed(adType: AdType) {
        super.onAdShowed(adType)
        listener.adShowed.value = adType
    }

    override fun onAdDismissed(adType: AdType) {
        super.onAdDismissed(adType)
        listener.adDismissed.value = adType
    }
}
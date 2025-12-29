package wallapp.ads


abstract class FullScreenAdShowCallbacks {

    /**
     * The ad showed the full screen content
     */
    open fun onAdShowed(adType: AdType) { }

    /**
     * An impression is recorded for an ad.
     */
    open fun onAdImpression(adType: AdType) { }

    /**
     * The ad dismissed full screen content
     */
    open fun onAdDismissed(adType: AdType) { }

    /**
     * The ad failed to show full screen content
     */
    open fun onAdFailedToShow(adType: AdType, adError: AdError?) { }
}
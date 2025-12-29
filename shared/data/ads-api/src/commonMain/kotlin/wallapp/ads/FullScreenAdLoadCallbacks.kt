package wallapp.ads


interface FullScreenAdLoadCallbacks {

    fun onAdLoaded()

    fun onAdFailedToLoad(adError: AdError)
}
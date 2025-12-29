package wallapp.ads.initializerstate



class AdInitializerStateNoAds : AdInitializerState {

    override val initializeAds: Boolean
        get() = false
}
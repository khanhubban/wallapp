package wallapp.ads.interstitial


data class InterstitialAdUnitId(val id: String) {

    init {
        require(id.isNotEmpty())
    }

}
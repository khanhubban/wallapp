package wallapp.ads.reward


data class RewardInterstitialAdUnitId(val id: String) {

    init {
        require(id.isNotEmpty())
    }

}
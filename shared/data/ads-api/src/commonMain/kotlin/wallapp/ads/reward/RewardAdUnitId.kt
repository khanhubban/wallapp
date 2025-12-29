package wallapp.ads.reward


data class RewardAdUnitId(val id: String) {

    init {
        require(id.isNotEmpty())
    }

}
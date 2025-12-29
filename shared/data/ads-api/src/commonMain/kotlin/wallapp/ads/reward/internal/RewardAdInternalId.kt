package wallapp.ads.reward.internal

data class RewardAdInternalId(
    val name: String,
    val brand: RewardAdInternalBrand,
) {
    init {
        require(name.isNotBlank()) { "RewardAdInternalId value must not be blank" }
        require(name.startsWith("ad~")) { "RewardAdInternalId value must start with 'ad~'" }
    }

    companion object {
        val Preset = RewardAdInternalId("ad~demo~01", RewardAdInternalBrand.Demo)
    }
}
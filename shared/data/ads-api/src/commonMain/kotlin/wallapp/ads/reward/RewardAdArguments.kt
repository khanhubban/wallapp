package wallapp.ads.reward


data class RewardAdArguments(
    val rewardAdHandle: Int,
) {
    constructor(
        rewardAdHandle: RewardAdHandle,
    ) : this(rewardAdHandle.handle)

}

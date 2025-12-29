package wallapp.ads.reward

interface RewardAdCoordinator {
    fun register(rewardAdStateCallback: RewardAdStateCallback)
    fun unregister(rewardAdStateCallback: RewardAdStateCallback)
    fun loadRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks?)
    fun showRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks)
}

typealias RewardAdStateCallback = (RewardAdState) -> Unit
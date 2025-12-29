package wallapp.ads.reward

import wallapp.ads.AdSourceInitializer


class RewardAdHandleManager(
    val adSourceInitializer: AdSourceInitializer,
) {
    val canServeAds: Boolean
        get() = adSourceInitializer.state == AdSourceInitializer.State.Initialized

    private val handles = mutableListOf<RewardAdHandle>()

    fun createRewardAdHandle(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks?): RewardAdHandle {
        return RewardAdHandle.createRewardAdHandle(rewardAdPlaybackCallbacks).apply {
            handles.add(this)
        }
    }

    fun updateRewardHandleCallbacks(
        rewardAdHandle: RewardAdHandle,
        rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks?,
    ): RewardAdHandle {
        val existing = handles.find { it.handle == rewardAdHandle.handle }
            ?: throw IllegalStateException("RewardAdHandle $rewardAdHandle not found")
        handles.remove(existing)
        return RewardAdHandle(rewardAdHandle.handle, rewardAdPlaybackCallbacks).apply {
            handles.add(this)
        }
    }

    fun destroyRewardAdHandle(rewardAdHandle: RewardAdHandle) {
        handles.remove(rewardAdHandle)
    }

    fun findRewardAdHandle(handle: Int): RewardAdHandle? {
        return handles.find { it.handle == handle }
    }
}

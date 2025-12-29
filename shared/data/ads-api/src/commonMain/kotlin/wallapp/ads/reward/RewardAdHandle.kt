package wallapp.ads.reward

data class RewardAdHandle(
    val handle: Int,
    /**
     * Will be null in the event an ad has been preloaded, but not yet shown.
     */
    val playbackCallbacks: RewardAdPlaybackCallbacks?,
) {

    companion object {
        var handle: Int = 1

        fun createRewardAdHandle(callbacks: RewardAdPlaybackCallbacks?): RewardAdHandle {
            handle++
            return RewardAdHandle(handle, callbacks)
        }
    }

}

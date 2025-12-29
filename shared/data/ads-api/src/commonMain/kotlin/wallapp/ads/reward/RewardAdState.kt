package wallapp.ads.reward

import co.touchlab.skie.configuration.annotations.SealedInterop


@SealedInterop.Enabled
sealed class RewardAdState {

    data object NoOp : RewardAdState()

    data object Unloaded: RewardAdState()

    data object Loading: RewardAdState()

    data object Loaded : RewardAdState()

    /**
     * The ad is currently being shown. Will be true if the ad has finished but the user has not
     * yet closed it.
     */
    data object Showing : RewardAdState()

    sealed class Error : RewardAdState() {

        data object ErrorFailedToLoad: Error()

        data object ErrorFailedToShow: Error()
    }
}

val RewardAdState.canLoad: Boolean
    get() = this is RewardAdState.Unloaded
            || this is RewardAdState.Error
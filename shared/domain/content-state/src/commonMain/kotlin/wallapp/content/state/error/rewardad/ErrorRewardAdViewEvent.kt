package wallapp.content.state.error.rewardad

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewEvent


@Immutable
sealed interface ErrorRewardAdViewEvent : ViewEvent {

    @Immutable
    data object OpenNetworkSettings : ErrorRewardAdViewEvent

    @Immutable
    data object OpenPrivacySettings : ErrorRewardAdViewEvent
}
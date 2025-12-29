package wallapp.content.state.rewardadinternal

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewEvent


@Immutable
sealed interface RewardAdInternalViewEvent : ViewEvent {

    @Immutable
    data object CheckToClose : RewardAdInternalViewEvent

    @Immutable
    data object ForceClose : RewardAdInternalViewEvent

    @Immutable
    data object CallToAction : RewardAdInternalViewEvent
}

typealias RewardAdInternalViewEventSink = (RewardAdInternalViewEvent) -> Unit
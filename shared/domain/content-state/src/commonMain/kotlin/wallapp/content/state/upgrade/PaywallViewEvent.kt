package wallapp.content.state.upgrade

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewEvent

sealed class PaywallViewEvent : ViewEvent {
    @Immutable
    data class PlusPlanViewEvent(
        val plusPlan: PlusPlan
    ): PaywallViewEvent()

    @Immutable
    data class SubscriptionPlanViewEvent(
        val subscriptionPlan: SubscriptionPlan,
    ): PaywallViewEvent()

    @Immutable
    data object ActionButtonViewEvent: PaywallViewEvent()
}

typealias PaywallViewEventSink = (PaywallViewEvent) -> Unit
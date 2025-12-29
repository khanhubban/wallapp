package wallapp.content.state.upgrade.plus.paywall

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.content.state.upgrade.plus.plans.SubscriptionPlanViewState
import wallapp.pixel.view.ViewState

@Immutable
data class PaywallPlanViewState(
    val subscriptionFeatures: List<PaywallFeatureViewState>,
    val plusPlans: List<SubscriptionPlanViewState>,
    val topPadding: Dp,
) : ViewState

package wallapp.ui.content.paywall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.content.state.upgrade.plus.paywall.PaywallPlanViewState
import wallapp.pixel.render.Render
import wallapp.ui.content.upgrade.plus.plans.PlusPlansColumn

@Composable
fun PaywallPlan(
    render: Render,
    viewState: PaywallPlanViewState,
    modifier: Modifier = Modifier,
) {
    val paddingDefault = render.defaultViewSpec.paddingDefault
    val plusPlans = viewState.plusPlans
    val subscriptionFeatures = viewState.subscriptionFeatures
    val topPadding = viewState.topPadding

    Column(
        modifier = modifier
            .padding(top = topPadding)
            .padding(horizontal = paddingDefault),
        verticalArrangement = Arrangement.Top,
    ) {
        subscriptionFeatures.forEach {
            PaywallFeature(render, it)
        }

        Spacer(modifier = Modifier.weight(1f))

        PlusPlansColumn(
            render,
            plusPlans,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(paddingDefault))
    }
}
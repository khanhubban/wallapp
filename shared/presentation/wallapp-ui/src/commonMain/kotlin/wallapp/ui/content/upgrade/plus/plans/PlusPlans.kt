package wallapp.ui.content.upgrade.plus.plans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wallapp.content.state.upgrade.plus.plans.SubscriptionPlanViewState
import wallapp.pixel.render.Render

@Composable
fun PlusPlansColumn(
    render: Render,
    plusPlans: List<SubscriptionPlanViewState>,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    )  {
        plusPlans.forEach { plusPlan ->
            PurchasePlanOutline(
                render,
                plusPlan,
            )
        }
    }
}
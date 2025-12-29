package wallapp.content.state.upgrade.plus.plans

import androidx.compose.runtime.Immutable
import wallapp.content.state.upgrade.PaywallViewEvent
import wallapp.content.state.upgrade.PaywallViewEventSink
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewState
import wallapp.theme.Theme

@Immutable
data class SubscriptionPlanViewState(
    val theme: Theme,
    val title: MenuItem,
    val highlight: Text?,
    val price: Text,
    val isSelected: Boolean,
    val shapeSpec: ShapeSpec,
    val eventSink: PaywallViewEventSink,
    val event: PaywallViewEvent.PlusPlanViewEvent,
) : ViewState
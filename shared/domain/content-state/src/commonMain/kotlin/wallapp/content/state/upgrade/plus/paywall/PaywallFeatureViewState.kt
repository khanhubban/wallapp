package wallapp.content.state.upgrade.plus.paywall

import androidx.compose.runtime.Immutable
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewState

@Immutable
data class PaywallFeatureViewState(
    val icon: MenuItem,
    val label: Text,
) : ViewState

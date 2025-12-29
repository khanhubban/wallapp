package wallapp.content.state.upgrade.plus.promo

import androidx.compose.runtime.Immutable
import wallapp.image.Image
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.view.ViewState
import wallapp.theme.Theme

@Immutable
data class UpgradePromoViewState(
    val theme: Theme,
    val heroImage: Image,
    val title: MenuItem,
    val actionButton: MenuItem,
) : ViewState

package wallapp.content.state.social

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.view.ViewState

@Immutable
data class SocialLinksViewState(
    val socialLinks: List<MenuItem>,
    val height: Dp,
) : ViewState {

    companion object {
        val Preset = SocialLinksViewState(
            socialLinks = emptyList(),
            height = 56.dp,
        )
    }
}
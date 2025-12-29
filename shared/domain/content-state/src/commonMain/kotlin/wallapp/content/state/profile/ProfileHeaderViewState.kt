package wallapp.content.state.profile

import androidx.compose.runtime.Immutable
import wallapp.graphics.Color
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.message.MessageBarViewState
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewState

@Immutable
data class ProfileHeaderViewState(
    val viewSpec: ProfileHeaderViewSpec,
    val messageBar: MessageBarViewState?,
    val title: Text,
    val summary: Text?,
    val upgradeButton: MenuItem?,
    val containerColor: Color,
) : ViewState

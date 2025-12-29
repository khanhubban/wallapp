package wallapp.content.state.wallpaper

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState

@Immutable
@SealedInterop.Enabled
sealed class WallpaperArtistViewState : ViewState {

    data class Loading(
        val height: Dp,
    ) : WallpaperArtistViewState()

    data class Data(
        val name: Text,
        val nameEventHandler: ViewEventHandler,
        val profileImage: ProfileImageViewState,
        val actionButtons: List<MenuItem>?,
    ) : WallpaperArtistViewState()

    companion object {
        val Preset = WallpaperArtistViewState.Data(
            name = Text.createPreset("Preset"),
            nameEventHandler = ViewEventHandler.NoOp,
            profileImage = ProfileImageViewState.Preset,
            actionButtons = null,
        )
    }
}

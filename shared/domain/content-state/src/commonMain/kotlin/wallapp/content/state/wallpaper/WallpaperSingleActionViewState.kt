package wallapp.content.state.wallpaper

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.toolbar.ToolbarViewState

@Immutable
@SealedInterop.Enabled
sealed class WallpaperSingleActionViewState : ScreenViewState {

    @Immutable
    data object Loading : WallpaperSingleActionViewState()

    @Immutable
    data class Success(
        val viewSpec: WallpaperSingleActionViewSpec,
        val toolbarViewState: ToolbarViewState,
        val actionButton1: MenuItem,
        val actionButton2: MenuItem,
        val actionButton3: MenuItem?,
    ) : WallpaperSingleActionViewState()
}
package wallapp.content.state.collection

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.toolbar.ToolbarViewState

@Immutable
@SealedInterop.Enabled
sealed class CollectionActionViewState : ScreenViewState {

    @Immutable
    data object Loading : CollectionActionViewState()

    @Immutable
    data class Success(
        val viewSpec: CollectionActionViewSpec,
        val toolbarViewState: ToolbarViewState,
        val actionButton1: MenuItem,
        val actionButton2: MenuItem? = null,
        val infoMessage: MenuItem? = null,
    ) : CollectionActionViewState()
}
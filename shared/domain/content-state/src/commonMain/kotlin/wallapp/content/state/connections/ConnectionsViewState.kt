package wallapp.content.state.connections

import androidx.compose.runtime.Immutable
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.tab.TabsViewState
import wallapp.pixel.toolbar.ToolbarViewState

@Immutable
sealed interface ConnectionsViewState : ScreenViewState {

    @Immutable
    data object Loading : ConnectionsViewState

    @Immutable
    data class Success(
        val toolbarViewState: ToolbarViewState,
        val tabs: TabsViewState,
    ): ConnectionsViewState
}
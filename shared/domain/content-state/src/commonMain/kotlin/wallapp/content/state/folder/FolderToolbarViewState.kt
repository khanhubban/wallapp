package wallapp.content.state.folder

import androidx.compose.runtime.Immutable
import wallapp.pixel.text.Text
import wallapp.pixel.toolbar.ToolbarViewState
import wallapp.pixel.view.ViewState
import wallapp.theme.Theme

@Immutable
data class FolderToolbarViewState(
    val theme: Theme,
    val toolbarViewState: ToolbarViewState,
    val name: Text,
) : ViewState

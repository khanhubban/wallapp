package wallapp.content.state.folder

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.pixel.view.ViewSpec


@Immutable
data class FolderViewSpec(
    val paddingDefault: Dp = 16.dp,
    val toolbarViewSpec: FolderToolbarViewSpec,
) : ViewSpec
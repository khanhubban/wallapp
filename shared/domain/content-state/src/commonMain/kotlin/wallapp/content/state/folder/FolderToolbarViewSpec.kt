package wallapp.content.state.folder

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import wallapp.pixel.view.ViewSpec

@Immutable
data class FolderToolbarViewSpec(
    val minToolbarHeight: Dp,
    val maxToolbarHeight: Dp,
    val paddingDefault: Dp,
    val titleTop: Dp,
    val titleHeight: Dp,
) : ViewSpec

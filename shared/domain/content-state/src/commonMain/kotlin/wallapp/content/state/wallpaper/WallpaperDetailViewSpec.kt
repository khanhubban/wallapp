package wallapp.content.state.wallpaper

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class WallpaperDetailViewSpec(
    val height: Dp,
    val itemHeight: Dp,
    val itemImageSize: Dp,
    val itemVerticalSpacerHeight: Dp,
) {
    companion object {
        val Preset = WallpaperDetailViewSpec(
            height = 100.dp,
            itemHeight = 28.dp,
            itemImageSize = 24.dp,
            itemVerticalSpacerHeight = 8.dp,
        )
    }
}

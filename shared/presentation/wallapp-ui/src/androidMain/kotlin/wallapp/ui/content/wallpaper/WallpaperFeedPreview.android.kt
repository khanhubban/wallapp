package wallapp.ui.content.wallpaper

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import wallapp.content.state.wallpaper.WallpaperPreviewViewState
import wallapp.pixel.render.Render

@Preview
@Composable
fun WallpaperFeedPreviewPreview() {
    WallpaperFeedPreview(
        render = Render.Preset,
        wallpaperPreview = WallpaperPreviewViewState.Preset,
    )
}

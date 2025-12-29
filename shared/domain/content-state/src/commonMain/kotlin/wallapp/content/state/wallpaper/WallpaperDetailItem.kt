package wallapp.content.state.wallpaper

import wallapp.image.Image
import wallapp.pixel.text.Text

data class WallpaperDetailItem(
    val image: Image,
    val label: Text,
)
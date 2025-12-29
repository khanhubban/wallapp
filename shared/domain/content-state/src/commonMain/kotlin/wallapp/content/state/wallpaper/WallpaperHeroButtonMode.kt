package wallapp.content.state.wallpaper

import androidx.compose.runtime.Immutable

@Immutable
sealed interface WallpaperHeroButtonMode {

    @Immutable
    data class GetWallpaper(val label: String) : WallpaperHeroButtonMode

    @Immutable
    data class GetCollection(val label: String) : WallpaperHeroButtonMode
}
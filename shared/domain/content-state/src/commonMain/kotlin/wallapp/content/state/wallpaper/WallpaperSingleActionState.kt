package wallapp.content.state.wallpaper

sealed class WallpaperSingleActionState {

    data object Locked : WallpaperSingleActionState()
    data object Unlocked : WallpaperSingleActionState()
}
package wallapp.wallpaper.actionbutton

import wallapp.content.model.Id.RemixId
import wallapp.pixel.menu.MenuItem

sealed class WallpaperActionButtonState {

    abstract val wallpaperId: RemixId
    abstract val buttonViewState: MenuItem

    data class None(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperActionButtonState()

    data class WatchRewardAd(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperActionButtonState()

    data class RewardAdLoading(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperActionButtonState()

    data class Get(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperActionButtonState()

    data class Downloading(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperActionButtonState()

    data class SetAsWallpaper(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperActionButtonState()

    data class DownloadedToPhotos(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperActionButtonState()

    data class Current(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperActionButtonState()

    data class Applying(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperActionButtonState()

    data class CheckingStatus(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperActionButtonState()

    data class Plus(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperActionButtonState()

    data class PermissionDenied(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperActionButtonState()

    data class OpenInPhotos(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperActionButtonState()
}
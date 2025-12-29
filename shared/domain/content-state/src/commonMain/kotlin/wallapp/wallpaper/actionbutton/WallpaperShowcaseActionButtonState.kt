package wallapp.wallpaper.actionbutton

import wallapp.content.model.Id.RemixId
import wallapp.pixel.menu.MenuItem

sealed class WallpaperShowcaseActionButtonState {

    abstract val wallpaperId: RemixId
    abstract val buttonViewState: MenuItem

    data class None(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperShowcaseActionButtonState()

    data class Get(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperShowcaseActionButtonState()

    data class Downloading(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperShowcaseActionButtonState()

    data class Current(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperShowcaseActionButtonState()

    data class Applying(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperShowcaseActionButtonState()

    data class PermissionDenied(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : WallpaperShowcaseActionButtonState()
}
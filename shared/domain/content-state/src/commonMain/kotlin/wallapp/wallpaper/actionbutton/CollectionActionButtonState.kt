package wallapp.wallpaper.actionbutton

import wallapp.content.model.Id.RemixId
import wallapp.pixel.menu.MenuItem

sealed class CollectionActionButtonState {

    abstract val wallpaperId: RemixId
    abstract val buttonViewState: MenuItem

    data class None(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : CollectionActionButtonState()

    data class DownloadSelected(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : CollectionActionButtonState()

    data class SetAsWallpaper(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : CollectionActionButtonState()

    data class Current(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : CollectionActionButtonState()

    data class CheckingStatus(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : CollectionActionButtonState()

    data class Applying(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : CollectionActionButtonState()

    data class PermissionDenied(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : CollectionActionButtonState()

    data class DownloadedToPhotos(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : CollectionActionButtonState()

    data class Downloading(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : CollectionActionButtonState()

    data class OpenInPhotos(
        override val wallpaperId: RemixId,
        override val buttonViewState: MenuItem,
    ) : CollectionActionButtonState()

}
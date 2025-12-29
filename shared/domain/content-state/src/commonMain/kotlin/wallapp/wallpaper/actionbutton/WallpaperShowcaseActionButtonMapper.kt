package wallapp.wallpaper.actionbutton

import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperId
import wallapp.content.model.WallpaperRemix
import wallapp.content.state.error.permissionSystemMediaDenied
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler
import wallapp.resources.image.ImageRepository
import wallapp.resources.string.StringRepository
import wallapp.text.TextButtonLabel
import wallapp.theme.ColorToken
import wallapp.view.ViewEventFactory
import wallapp.view.menu.MenuItemFactory
import wallapp.wallpaper.download.WallpaperDownloadState
import wallapp.wallpaper.download.normalizedProgress

class WallpaperShowcaseActionButtonMapper(
    private val viewEventFactory: ViewEventFactory,
    private val menuItemFactory: MenuItemFactory,
    private val imageRepository: ImageRepository,
    private val strings: StringRepository,
) {

    fun mapGetButtonState(
        wallpaperId: RemixId,
        wallpaper: WallpaperRemix?,
        appearAsPhotosButton: Boolean = false,
        onGetClick: (WallpaperId) -> Unit,
    ): WallpaperShowcaseActionButtonState {
        return if (wallpaper != null) {
            val eventHandler = ViewEventHandler.createOnClick {
                onGetClick(wallpaper.id)
            }

            val button = if (appearAsPhotosButton) {
                menuItemFactory.createActionButtonDownloadedToPhotos(
                    staticWallpaperSize = null,
                    eventHandler = eventHandler,
                )
            } else {
                menuItemFactory.createActionButton(
                    image = imageRepository.wallpaperGet,
                    text = TextButtonLabel(strings.getWallpaper),
                    eventHandler = eventHandler,
                )
            }

            WallpaperShowcaseActionButtonState.Get(wallpaperId, button)
        } else {
            Log.d("mapGetButtonState(): wallpaper is null, returning None state.")
            mapNoneButtonState(wallpaperId)
        }
    }

    private fun mapNoneButtonState(wallpaperId: RemixId): WallpaperShowcaseActionButtonState {
        return WallpaperShowcaseActionButtonState.None(
            wallpaperId = wallpaperId,
            buttonViewState = menuItemFactory.createActionButton(
                image = null,
                text = Text.createPreset(""),
                eventHandler = ViewEventHandler.NoOp,
            )
        )
    }

    fun mapDownloadingButtonState(
        wallpaperId: RemixId,
        wallpaperDownloadState: WallpaperDownloadState,
    ): WallpaperShowcaseActionButtonState {

        val buttonViewState = menuItemFactory.createActionProgressButton(
            progress = wallpaperDownloadState.normalizedProgress,
            text = TextButtonLabel(
                strings.downloading,
                colorToken = ColorToken.ThemeOnTertiary
            ),
        )

        return WallpaperShowcaseActionButtonState.Downloading(wallpaperId, buttonViewState)
    }

    fun mapCurrentButtonState(
        wallpaperId: RemixId,
        onCurrentClick: ((WallpaperId) -> Unit),
    ): WallpaperShowcaseActionButtonState {
        val eventHandler = ViewEventHandler.createOnClick {
            onCurrentClick(wallpaperId)
        }

        return WallpaperShowcaseActionButtonState.Current(
            wallpaperId,
            menuItemFactory.createActionButton(
                image = imageRepository.checkCircle,
                text = TextButtonLabel(strings.currentWallpaper),
                eventHandler = eventHandler,
            ),
        )
    }

    fun mapApplyingButtonState(wallpaperId: RemixId): WallpaperShowcaseActionButtonState {
        return WallpaperShowcaseActionButtonState.Applying(
            wallpaperId = wallpaperId,
            buttonViewState = menuItemFactory.createActionButton(
                image = null,
                text = TextButtonLabel(strings.applying),
                eventHandler = ViewEventHandler.NoOp,
            )
        )
    }

    fun mapPermissionDeniedState(
        wallpaperId: RemixId,
        staticWallpaperSize: StaticWallpaperSize,
    ): WallpaperShowcaseActionButtonState {
        return WallpaperShowcaseActionButtonState.PermissionDenied(
            wallpaperId,
            menuItemFactory.createActionButton(
                image = imageRepository.download,
                text = TextButtonLabel(strings.download(staticWallpaperSize)),
                eventHandler = viewEventFactory.createNavigateToError(permissionSystemMediaDenied()),
            ),
        )
    }
}
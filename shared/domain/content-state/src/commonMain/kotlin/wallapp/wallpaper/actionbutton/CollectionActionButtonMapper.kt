package wallapp.wallpaper.actionbutton

import wallapp.content.model.Id
import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperRemix
import wallapp.content.state.error.permissionSystemMediaDenied
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler
import wallapp.resources.image.ImageRepository
import wallapp.resources.string.StringRepository
import wallapp.system.photo.SystemPhotoId
import wallapp.text.TextButtonLabel
import wallapp.theme.ColorToken
import wallapp.view.ViewEventFactory
import wallapp.view.menu.MenuItemFactory
import wallapp.wallpaper.download.WallpaperDownloadState
import wallapp.wallpaper.download.normalizedProgress

class CollectionActionButtonMapper(
    private val viewEventFactory: ViewEventFactory,
    private val menuItemFactory: MenuItemFactory,
    private val imageRepository: ImageRepository,
    private val strings: StringRepository,
) {
    fun mapSetWallpaperButtonState(
        wallpaperId: RemixId,
        wallpaperItem: WallpaperRemix?,
        staticWallpaperSize: StaticWallpaperSize,
        onSetClick: (wallpaperRemix: WallpaperRemix, staticWallpaperSize: StaticWallpaperSize) -> Unit,
    ): CollectionActionButtonState {
        return CollectionActionButtonState.SetAsWallpaper(
            wallpaperId,
            menuItemFactory.createActionButton(
                image = imageRepository.wallpaperSet,
                text = TextButtonLabel(strings.setWallpaperFullRes),
                eventHandler = ViewEventHandler.createOnClick {
                    wallpaperItem?.let { wallpaper ->
                        onSetClick.invoke(wallpaper, staticWallpaperSize)
                    }
                },
            ),
        ).also {
            Log.d("mapSetWallpaperButtonState(): Set state created - $it")
        }
    }

    fun mapSetWallpaperButtonStateWithWallpaperDownloadState(
        wallpaperId: RemixId,
        wallpaperItem: WallpaperRemix?,
        wallpaperDownloadState: WallpaperDownloadState.Success,
        staticWallpaperSize: StaticWallpaperSize,
        onSetClick: (WallpaperDownloadState.Success, wallpaperRemix: WallpaperRemix) -> Unit,
    ): CollectionActionButtonState {
        return CollectionActionButtonState.SetAsWallpaper(
            wallpaperId,
            menuItemFactory.createActionButton(
                image = imageRepository.wallpaperSet,
                text = TextButtonLabel(strings.setWallpaper(staticWallpaperSize)),
                eventHandler = ViewEventHandler.createOnClick {
                    wallpaperItem?.let { wallpaper ->
                        onSetClick.invoke(wallpaperDownloadState, wallpaper)
                    }
                },
            ),
        ).also {
            Log.d("mapSetWallpaperButtonState(): Set state created - $it")
        }
    }

    fun mapDownloadSelectedState(
        wallpaperId: RemixId,
        wallpaper: WallpaperRemix?,
        staticWallpaperSize: StaticWallpaperSize,
        onGetClick: (WallpaperRemix, StaticWallpaperSize) -> Unit,
    ): CollectionActionButtonState {
        return if (wallpaper != null) {
            CollectionActionButtonState.DownloadSelected(
                wallpaperId,
                menuItemFactory.createActionButton(
                    image = imageRepository.download,
                    text = TextButtonLabel(strings.downloadSelected),
                    eventHandler = ViewEventHandler.createOnClick {
                        onGetClick(wallpaper, staticWallpaperSize)
                    },
                )
            )
        } else {
            Log.d("mapGetButtonState(): wallpaper is null, returning None state.")
            mapNoneButtonState()
        }
    }


    fun mapNoneButtonState(): CollectionActionButtonState {
        return CollectionActionButtonState.None(
            wallpaperId = Id.RemixId(""),
            buttonViewState = menuItemFactory.createActionButton(
                image = null,
                text = Text.createPreset(""),
                eventHandler = ViewEventHandler.NoOp,
            )
        )
    }

    fun mapCurrentButtonState(
        wallpaperId: RemixId,
    ) = CollectionActionButtonState.Current(
        wallpaperId,
        menuItemFactory.createActionButton(
            image = imageRepository.checkCircle,
            text = TextButtonLabel(strings.currentWallpaper),
            eventHandler = ViewEventHandler.NoOp,
        ),
    )

    fun mapCheckingStatusButtonState(wallpaperId: RemixId): CollectionActionButtonState {
        return CollectionActionButtonState.CheckingStatus(
            wallpaperId = wallpaperId,
            buttonViewState = menuItemFactory.createActionButton(
                image = null,
                text = TextButtonLabel(strings.checkingStatus),
                eventHandler = ViewEventHandler.NoOp,
            )
        )
    }

    fun mapApplyingButtonState(wallpaperId: RemixId): CollectionActionButtonState {
        return CollectionActionButtonState.Applying(
            wallpaperId = wallpaperId,
            buttonViewState = menuItemFactory.createActionButton(
                image = null,
                text = TextButtonLabel(strings.applying),
                eventHandler = ViewEventHandler.NoOp,
            )
        )
    }

    fun mapPermissionDeniedState(wallpaperId: RemixId): CollectionActionButtonState {
        return CollectionActionButtonState.PermissionDenied(
            wallpaperId,
            menuItemFactory.createActionButton(
                image = imageRepository.download,
                text = TextButtonLabel(strings.download(StaticWallpaperSize.FullResolution)),
                eventHandler = viewEventFactory.createNavigateToError(permissionSystemMediaDenied()),
            ),
        )
    }

    fun mapDownloadedToPhotosState(wallpaperId: RemixId): CollectionActionButtonState {
        return CollectionActionButtonState.DownloadedToPhotos(
            wallpaperId,
            menuItemFactory.createActionButtonDownloadedToPhotos(StaticWallpaperSize.FullResolution),
        )
    }

    fun mapDownloadingButtonState(
        wallpaperId: RemixId,
        wallpaperDownloadState: WallpaperDownloadState,
    ): CollectionActionButtonState {
        val buttonViewState = menuItemFactory.createActionProgressButton(
            progress = wallpaperDownloadState.normalizedProgress,
            text = TextButtonLabel(
                strings.downloading,
                colorToken = ColorToken.ThemeOnTertiary
            ),
            onClick = ViewEventHandler.NoOp,
        )

        return CollectionActionButtonState.Downloading(
            wallpaperId,
            buttonViewState,
        )
    }

    fun mapErrorButtonState(
        wallpaperId: RemixId,
        wallpaper: WallpaperRemix?,
        onGetClick: () -> Unit,
    ) = CollectionActionButtonState.DownloadSelected(
        wallpaperId = wallpaperId,
        buttonViewState = menuItemFactory.createActionButton(
            image = null,
            text = TextButtonLabel(strings.retry),
            eventHandler = ViewEventHandler.createOnClick {
                wallpaper?.let {
                    onGetClick()
                }
            },
        )
    )

    fun mapOpenInPhotosButtonState(
        wallpaperId: RemixId,
        staticWallpaperSize: StaticWallpaperSize,
        systemPhotoId: SystemPhotoId,
        onOpenInPhotosClick: (systemPhotoId: SystemPhotoId) -> Unit
    ): CollectionActionButtonState {
        return CollectionActionButtonState.OpenInPhotos(
            wallpaperId = wallpaperId,
            buttonViewState = menuItemFactory.createActionButton(
                image = imageRepository.photosAppIcon,
                imageIsIcon = false,
                text = TextButtonLabel(strings.openInPhotos(staticWallpaperSize)),
                eventHandler = ViewEventHandler.createOnClick {
                    onOpenInPhotosClick.invoke(systemPhotoId)
                },
            )
        )
    }
}

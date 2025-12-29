package wallapp.wallpaper.actionbutton

import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperRemix
import wallapp.content.state.error.permissionSystemMediaDenied
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.pixel.alert.AlertViewState
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


class WallpaperActionButtonMapper(
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
    ): WallpaperActionButtonState {
        return WallpaperActionButtonState.SetAsWallpaper(
            wallpaperId,
            menuItemFactory.createActionButton(
                image = imageRepository.wallpaperSet,
                text = TextButtonLabel(strings.setWallpaper(staticWallpaperSize)),
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
    ): WallpaperActionButtonState {
        return WallpaperActionButtonState.SetAsWallpaper(
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

    fun mapGetButtonState(
        wallpaperId: RemixId,
        wallpaper: WallpaperRemix?,
        staticWallpaperSize: StaticWallpaperSize,
        onGetClick: (WallpaperRemix, StaticWallpaperSize) -> Unit,
    ): WallpaperActionButtonState {
        return if (wallpaper != null) {
            val label = strings.download(staticWallpaperSize)

            WallpaperActionButtonState.Get(
                wallpaperId,
                menuItemFactory.createActionButton(
                    image = imageRepository.download,
                    text = TextButtonLabel(label),
                    eventHandler = ViewEventHandler.createOnClick {
                        Log.d("mapGetButtonState(): onGetClick() - $wallpaperId")
                        onGetClick(wallpaper, staticWallpaperSize)
                    },
                )
            ).also {
                Log.d("mapGetButtonState(): Get state created - $it")
            }
        } else {
            Log.d("mapGetButtonState(): wallpaper is null, returning None state.")
            mapNoneButtonState(wallpaperId)
        }
    }

    private fun mapNoneButtonState(wallpaperId: RemixId) = WallpaperActionButtonState.None(
        wallpaperId = wallpaperId,
        buttonViewState = menuItemFactory.createActionButton(
            image = null,
            text = Text.createPreset(""),
            eventHandler = ViewEventHandler.NoOp,
        )
    )

    fun mapDownloadingButtonState(
        wallpaperId: RemixId,
        wallpaperDownloadState: WallpaperDownloadState,
        staticWallpaperSize: StaticWallpaperSize,
        wallpaper: WallpaperRemix?,
        onCancelClick: (WallpaperRemix, StaticWallpaperSize) -> Unit,
    ): WallpaperActionButtonState {
        val buttonViewState = menuItemFactory.createActionProgressButton(
            progress = wallpaperDownloadState.normalizedProgress,
            text = TextButtonLabel(
                strings.downloading,
                colorToken = ColorToken.ThemeOnTertiary
            ),
            onClick = viewEventFactory.createShowAlert(
                AlertViewState(
                    title = strings.cancelDownload,
                    message = strings.cancelWallpaperDownloadMessage,
                    buttonPrimary = strings.yes,
                    buttonSecondary = strings.no,
                    buttonPrimaryOnClick = ViewEventHandler.createOnClick {
                        if (wallpaper != null) {
                            onCancelClick(wallpaper, staticWallpaperSize)
                        }
                    },
                    buttonSecondaryOnClick = ViewEventHandler.NoOp,
                )
            ),
        )

        return WallpaperActionButtonState.Downloading(wallpaperId, buttonViewState)
    }

    fun mapRewardAdLoadingButtonState(wallpaperId: RemixId): WallpaperActionButtonState =
        WallpaperActionButtonState.RewardAdLoading(
            wallpaperId,
            menuItemFactory.createActionButton(
                image = imageRepository.rewardAd,
                text = TextButtonLabel(strings.preparingPlayback),
                eventHandler = ViewEventHandler.NoOp,
            ),
        )

    fun mapWatchRewardAdButtonState(
        wallpaperId: RemixId,
        remainingAds: Int,
        showRewardAd: () -> Unit,
    ) = WallpaperActionButtonState.WatchRewardAd(
        wallpaperId,
        menuItemFactory.createActionButton(
            image = imageRepository.rewardAd,
            text = TextButtonLabel(strings.watchAdForHd),
            eventHandler = ViewEventHandler.createOnClick { showRewardAd() },
        ),
    )

    fun mapCurrentButtonState(
        wallpaperId: RemixId,
    ) = WallpaperActionButtonState.Current(
        wallpaperId,
        menuItemFactory.createActionButton(
            image = imageRepository.checkCircle,
            text = TextButtonLabel(strings.currentWallpaper),
            eventHandler = ViewEventHandler.NoOp,
        ),
    )

    fun mapErrorButtonState(
        wallpaperId: RemixId,
        wallpaper: WallpaperRemix?,
        onGetClick: () -> Unit,
    ) = WallpaperActionButtonState.Get(
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

    fun mapCheckingStatusButtonState(wallpaperId: RemixId): WallpaperActionButtonState {
        return WallpaperActionButtonState.CheckingStatus(
            wallpaperId = wallpaperId,
            buttonViewState = menuItemFactory.createActionButton(
                image = null,
                text = TextButtonLabel(strings.checkingStatus),
                eventHandler = ViewEventHandler.NoOp,
            )
        )
    }

    fun mapApplyingButtonState(wallpaperId: RemixId): WallpaperActionButtonState {
        return WallpaperActionButtonState.Applying(
            wallpaperId = wallpaperId,
            buttonViewState = menuItemFactory.createActionButton(
                image = null,
                text = TextButtonLabel(strings.applying),
                eventHandler = ViewEventHandler.NoOp,
            )
        )
    }

    fun mapPlusButtonState(wallpaperId: RemixId): WallpaperActionButtonState {
        return WallpaperActionButtonState.Plus(
            wallpaperId = wallpaperId,
            buttonViewState = menuItemFactory.createPlusButton()
        )
    }

    fun mapPermissionDeniedState(
        wallpaperId: RemixId,
        staticWallpaperSize: StaticWallpaperSize,
    ): WallpaperActionButtonState {
        return WallpaperActionButtonState.PermissionDenied(
            wallpaperId,
            menuItemFactory.createActionButton(
                image = imageRepository.download,
                text = TextButtonLabel(strings.download(staticWallpaperSize)),
                eventHandler = viewEventFactory.createNavigateToError(permissionSystemMediaDenied()),
            ),
        )
    }

    fun mapDownloadedToPhotosState(
        wallpaperId: RemixId,
        staticWallpaperSize: StaticWallpaperSize,
    ): WallpaperActionButtonState {
        return WallpaperActionButtonState.DownloadedToPhotos(
            wallpaperId,
            menuItemFactory.createActionButtonDownloadedToPhotos(staticWallpaperSize),
        )
    }

    fun mapOpenInPhotosButtonState(
        wallpaperId: RemixId,
        staticWallpaperSize: StaticWallpaperSize,
        systemPhotoId: SystemPhotoId,
        onOpenInPhotosClick: (systemPhotoId: SystemPhotoId) -> Unit
    ): WallpaperActionButtonState {
        return WallpaperActionButtonState.OpenInPhotos(
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
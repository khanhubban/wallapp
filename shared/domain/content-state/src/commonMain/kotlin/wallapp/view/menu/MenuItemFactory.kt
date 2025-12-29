package wallapp.view.menu

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.content.model.Id.ArtistId
import wallapp.content.state.downloadstatus.DownloadStatusViewState
import wallapp.content.state.favorite.FavoriteViewSpec
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.data.following.FollowState
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.font.TextStyle
import wallapp.graphics.Color
import wallapp.graphics.Colors
import wallapp.image.Image
import wallapp.pixel.button.ButtonAppearance
import wallapp.pixel.button.ButtonViewState
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.menu.MenuItem.MenuItemButton
import wallapp.pixel.menu.MenuItem.MenuItemGroupHorizontal
import wallapp.pixel.menu.MenuItem.MenuItemGroupVertical
import wallapp.pixel.menu.MenuItem.MenuItemIcon
import wallapp.pixel.menu.MenuItem.MenuItemImage
import wallapp.pixel.menu.MenuItem.MenuItemLabel
import wallapp.pixel.menu.MenuItem.MenuItemSpacer
import wallapp.pixel.shape.ShapeSize
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.shape.ShapeStyle
import wallapp.pixel.text.Text
import wallapp.pixel.text.TextStyleBody
import wallapp.pixel.text.TextStyleCaption
import wallapp.pixel.util.DpOptional
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewEventHandler.Companion.createOnClick
import wallapp.resources.ArrowBack
import wallapp.resources.Icon
import wallapp.resources.image.ImageRepository
import wallapp.resources.string.StringRepository
import wallapp.string.splitIntoLinesAlt
import wallapp.text.TextAlign
import wallapp.text.TextButtonLabel
import wallapp.theme.ColorToken
import wallapp.unit.Alignment
import wallapp.unit.Height
import wallapp.unit.Width
import wallapp.unit.height
import wallapp.unit.width
import wallapp.view.ViewEventFactory
import wallapp.view.ViewSpecArbitrator
import wallapp.view.ViewStateMapper
import wallapp.view.shape.ShapeSpecFactory

class MenuItemFactory(
    private val viewSpecArbitrator: ViewSpecArbitrator,
    private val viewEventFactory: ViewEventFactory,
    private val viewStateMapper: ViewStateMapper,
    private val imageRepository: ImageRepository,
    private val shapeSpecFactory: ShapeSpecFactory,
    private val strings: StringRepository,
) {
    fun createImage(
        image: Image,
        size: Dp,
        onClick: ViewEventHandler? = null,
    ) = MenuItemImage(
        imageViewState = ImageViewState(
            image,
            viewSpec = ImageViewSpec(
                width = size,
                height = size,
                shapeSpec = null,
            ),
            // Keep an eye on this - it's possible that we should use [size] here.
            imageSize = null,
        ),
        onClick = onClick,
    )

    val back: MenuItemIcon
        get() = MenuItemIcon(
            icon = Image.from(Icon.ArrowBack, contentDescription = "Back"),
            onClick = createOnClick(viewEventFactory.createOnClickNavigateBack()),
        )

    fun createSearchIcon() =
        MenuItemIcon(
            icon = imageRepository.search,
            onClick = createOnClick(viewEventFactory.createOnClickNavigateToSearch()),
        )

    fun createCloseButton(
        tintColor: ColorToken? = MenuItemIcon.DefaultTintColor,
        onClick: ViewEventHandler? = createOnClick(viewEventFactory.createOnClickNavigateBack()),
    ) = MenuItemIcon(
            icon = imageRepository.closeInset,
            tintColor = tintColor,
            onClick = onClick,
        )

    fun createIcon(
        icon: Image,
        tintColor: ColorToken? = null,
        size: Dp? = null,
        onClick: ViewEventHandler? = null,
    ) = createIcon(
        icon = icon,
        tintColor = tintColor,
        width = size,
        height = size,
        onClick = onClick,
    )

    fun createIcon(
        icon: Image,
        tintColor: ColorToken? = null,
        width: Dp? = null,
        height: Dp? = null,
        onClick: ViewEventHandler? = null,
    ) = MenuItemIcon(
        icon = icon,
        tintColor = tintColor,
        width = width?.let { DpOptional(it) },
        height = height?.let { DpOptional(it) },
        onClick = onClick,
    )

    fun createButtonIcon(
        icon: Image,
        tintColor: ColorToken? = ColorToken.ThemeOnPrimary,
    ): MenuItemIcon = createIcon(
        icon = icon,
        tintColor = tintColor,
        size = viewSpecArbitrator.toolbarHeroButtonIconSize,
        onClick = null,
    )

    fun createSpacer(width: Dp? = null, height: Dp? = null) =
        MenuItemSpacer(width?.let { DpOptional(it) }, height?.let { DpOptional(it) })

    fun createSpacerWidth(width: Dp? = 16.dp) =
        MenuItemSpacer(width?.let { DpOptional(it) }, height = null)

    fun createSpacerHeight(height: Dp? = 16.dp) =
        MenuItemSpacer(width = null, height?.let { DpOptional(it) })

    fun centeredText(
        text: Text,
        width: Width? = 200.dp.width,
        height: Height? = 56.dp.height,
    ): MenuItemLabel {
        return createLabel(
            text = text,
            contentAlignment = Alignment.Center,
            width = width,
            height = height,
        )
    }

    fun createLabel(
        text: Text,
        contentAlignment: Alignment,
        width: Width? = 200.dp.width,
        height: Height? = 56.dp.height,
        onClick: ViewEventHandler? = null,
    ) = MenuItemLabel(
        text = text,
        contentAlignment = contentAlignment,
        width = width,
        height = height,
        onClick = onClick,
    )

    fun createLabel(
        text: Text,
        minTextStyle: TextStyle? = null,
        contentAlignment: Alignment = Alignment.CenterStart,
        width: Width? = null,
        height: Height? = null,
        autoResize: Boolean = false,
        onClick: ViewEventHandler? = null,
    ): MenuItemLabel {
        return MenuItemLabel(
            text = text,
            contentAlignment = contentAlignment,
            width = width,
            height = height,
            minTextStyle = minTextStyle,
            autoResize = autoResize,
            onClick = onClick,
        )
    }

    fun createVerticalGroup(
        items: List<MenuItem>,
        height: Dp = 56.dp,
        width: Dp? = null,
    ) = MenuItemGroupVertical(
        menuItems = items,
        width = width?.let { DpOptional(it) },
        height = DpOptional(height),
    )

    fun createFollowButton(
        artistId: ArtistId,
        followState: FollowState?,
        colorToken: ColorToken? = null,
        showLabel: Boolean = true,
        shortLabel: Boolean = true,
    ): MenuItemButton {
        val eventHandler = viewEventFactory.createFollowing(artistId, followState)
        val isFollowing = followState?.isFollowing ?: false

        val label = if (showLabel) {
            val label = if (isFollowing) {
                strings.unfollow
            } else {
                strings.followArbitrated(shortLabel)
            }
            createButtonLabel(TextButtonLabel(label, colorToken = colorToken))
        } else {
            null
        }

        val (items, width) = listOf(label!!) to 68.dp
        val height = 48.dp

        return createHorizontalButton(
            items = items,
            height = height,
            width = width,
            eventHandler = eventHandler,
        )
    }

    private fun createButtonLabel(
        text: Text,
        onClick: ViewEventHandler? = null,
    ) = MenuItemLabel(
        text = text,
        onClick = onClick,
    )

    fun createHorizontalButton(
        items: List<MenuItem>,
        width: Dp? = null,
        height: Dp = 56.dp,
        buttonAppearance: ButtonAppearance = ButtonAppearance.Default,
        shapeSpec: ShapeSpec? = null,
        containerColorToken: ColorToken? = null,
        eventHandler: ViewEventHandler?,
    ): MenuItemButton = MenuItemButton(
        button = viewStateMapper.mapButtonViewState(
            createHorizontalGroup(
                items = items,
                height = height,
            ),
            buttonAppearance = buttonAppearance,
            shapeSpec = shapeSpec,
            containerColorToken = containerColorToken,
            eventHandler = eventHandler,
        ),
        width = width?.let { DpOptional(it) },
        height = DpOptional(height),
    )

    fun createHorizontalGroup(
        items: List<MenuItem>,
        height: Dp = 56.dp,
        width: Dp? = null,
    ): MenuItemGroupHorizontal = MenuItemGroupHorizontal(
        menuItems = items,
        height = height,
        width = width?.let { DpOptional(it) },
    )

    fun createActionButton(
        image: Image?,
        text: Text,
        buttonAppearance: ButtonAppearance = ButtonAppearance.Default,
        width: Dp? = null,
        height: Dp = 56.dp,
        horizontalPadding: Dp = 0.dp,
        contentColorToken: ColorToken? = ColorToken.ThemeOnPrimary,
        containerColorToken: ColorToken? = ColorToken.ThemePrimary,
        eventHandler: ViewEventHandler,
        imageIsIcon: Boolean = true,
    ): MenuItem {
        return createHorizontalButton(
            items = mutableListOf<MenuItem>().apply {
                if (horizontalPadding > 0.dp) {
                    add(createSpacerWidth(width = horizontalPadding))
                }
                if (image != null) {
                    val item = if (imageIsIcon) {
                        createIcon(image, contentColorToken, size = viewSpecArbitrator.iconSize)
                    } else {
                        createImage(image, size = viewSpecArbitrator.iconSize)
                    }
                    add(item)
                    add(createSpacerWidth(width = 12.dp))
                }
                add(createLabel(text))
                if (horizontalPadding > 0.dp) {
                    add(createSpacerWidth(width = horizontalPadding))
                }
            },
            buttonAppearance = buttonAppearance,
            containerColorToken = containerColorToken,
            width = width,
            height = height,
            eventHandler = eventHandler,
        )
    }

    fun createBuyCollectionButton(
        priceLabel: String,
        eventHandler: ViewEventHandler,
    ): MenuItem {
        return createHorizontalButton(
            items = listOfNotNull(
                createButtonIcon(imageRepository.collection),
                createSpacerWidth(viewSpecArbitrator.paddingDefault),
                createLabel(TextButtonLabel(strings.buyWallpaperCollection, maxLines = 1, useMarquee = true, animateMarquee = false)),
                createSpacerWidth(viewSpecArbitrator.paddingDefault),
                createLabel(TextButtonLabel(priceLabel)),
            ),
            eventHandler = eventHandler,
        )
    }

    fun createGetButton(
        label: String,
        eventHandlerGet: ViewEventHandler,
        downloadStatus: DownloadStatusViewState? = null,
        width: Dp? = null,
        height: Dp = 56.dp,
    ): MenuItem {
        var items: MenuItem = createHorizontalGroup(
            items = listOf(
                createButtonIcon(imageRepository.wallpaperGet),
                createSpacerWidth(),
                createLabel(TextButtonLabel(label)),
            ),
        )

        if (downloadStatus != null) {
            items = createActionProgressButton(
                progress = downloadStatus.progress,
                text = TextButtonLabel(
                    strings.downloadStatusCombined(
                        downloadStatus.title.string,
                        downloadStatus.summary.string,
                    ),
                    colorToken = ColorToken.ThemeOnTertiary,
                ),
            )
        }

        val eventHandler = if (downloadStatus != null) {
            ViewEventHandler.NoOp
        } else {
            eventHandlerGet
        }

        return MenuItemButton(
            button = ButtonViewState(
                menuItem = items,
                shapeSpec = shapeSpecFactory.buttonShapeSpecDefault,
                eventHandler = eventHandler,
            ),
            width = width?.let { DpOptional(it) },
            height = DpOptional(height),
        )
    }

    fun createPlusHeroButton(
        label: String = strings.joinPlus,
        eventHandler: ViewEventHandler = viewEventFactory.createNavigateToPaywall(subscriptionPlan = SubscriptionPlan.PlusUnlimited),
    ): MenuItem {
        return createPlusButton(
            width = viewSpecArbitrator.heroButtonWidth,
            label = label,
            eventHandler = eventHandler,
        )
    }

    fun createPlusButton(
        width: Dp? = viewSpecArbitrator.plusButtonWidth,
        height: Dp = viewSpecArbitrator.plusButtonHeight,
        label: String = strings.plus,
        eventHandler: ViewEventHandler = viewEventFactory.createNavigateToPaywall(subscriptionPlan = SubscriptionPlan.PlusUnlimited),
    ): MenuItem {
        val image = imageRepository.plusIndicator
        val text = TextButtonLabel(label, colorToken = ColorToken.Custom(Color.White))

        val horizontalPadding: Dp = 0.dp

        return createHorizontalButton(
            items = mutableListOf<MenuItem>().apply {
                if (horizontalPadding > 0.dp) {
                    add(createSpacerWidth(width = horizontalPadding))
                }
                add(createIcon(image, tintColor = null, size = viewSpecArbitrator.iconSizeLarge))
                add(createSpacerWidth(width = viewSpecArbitrator.paddingDefault))
                add(createLabel(text))
                if (horizontalPadding > 0.dp) {
                    add(createSpacerWidth(width = horizontalPadding))
                }
            },
            buttonAppearance = ButtonAppearance.Highlight,
            containerColorToken = ColorToken.ThemeSecondary,
            width = width,
            height = height,
            eventHandler = eventHandler,
        )
    }

    fun createPlusAdFreeButton(
        label: String = strings.goAdFree,
        width: Dp? = viewSpecArbitrator.plusButtonWidth,
        height: Dp = viewSpecArbitrator.plusButtonHeight,
        eventHandler: ViewEventHandler = viewEventFactory.createNavigateToPaywall(subscriptionPlan = SubscriptionPlan.PlusBasic),
    ): MenuItem {
        val image = imageRepository.adFree
        val contentColorToken = ColorToken.Custom(Color.Black)
        val text = TextButtonLabel(label, colorToken = contentColorToken)
        val backgroundColor = Colors.AdFree

        val horizontalPadding: Dp = 0.dp

        return createHorizontalButton(
            items = mutableListOf<MenuItem>().apply {
                if (horizontalPadding > 0.dp) {
                    add(createSpacerWidth(width = horizontalPadding))
                }
                add(createIcon(image, tintColor = contentColorToken, size = viewSpecArbitrator.iconSizeLarge))
                add(createSpacerWidth(width = viewSpecArbitrator.paddingDefault))
                add(createLabel(text))
                if (horizontalPadding > 0.dp) {
                    add(createSpacerWidth(width = horizontalPadding))
                }
            },
            buttonAppearance = ButtonAppearance.Highlight,
            containerColorToken = ColorToken.Custom(backgroundColor),
            width = width,
            height = height,
            eventHandler = eventHandler,
        )
    }


    fun createActionProgressButton(
        progress: Float,
        text: Text,
        width: Dp? = null,
        height: Dp = 56.dp,
        onClick: ViewEventHandler? = null,
    ): MenuItem {
        return MenuItem.MenuItemProgressButton(
            text,
            progress,
            height = DpOptional(height),
            width = width?.let { DpOptional(it) },
            onClick = onClick,
            shapeSpec = shapeSpecFactory.buttonShapeSpecDefault,
        )
    }

    fun createCircularButton(
        menuItem: MenuItem,
        eventHandler: ViewEventHandler?,
        containerColor: ColorToken = ColorToken.ThemeOnPrimary,
        buttonSize: Dp = viewSpecArbitrator.iconSizeMediumLarge,
    ): MenuItemButton {
        return MenuItemButton(
            ButtonViewState(
                menuItem = menuItem,
                shapeSpec = ShapeSpec(
                    shapeStyle = ShapeStyle.Circle,
                    shapeSize = ShapeSize.Small
                ),
                buttonAppearance = ButtonAppearance.Highlight,
                containerColorToken = containerColor,
                eventHandler = eventHandler,
            ),
            width = DpOptional(buttonSize),
            height = DpOptional(buttonSize),
        )
    }

    fun createCircularCloseButton(
        buttonSize: Dp = viewSpecArbitrator.iconSizeMediumLarge,
        tintColor: ColorToken = ColorToken.ThemePrimary,
        containerColor: ColorToken = ColorToken.ThemeOnPrimary,
        eventHandler: ViewEventHandler? = createOnClick(viewEventFactory.createOnClickNavigateBack()),
    ): MenuItemButton {
        val closeIcon = createIcon(
            icon = imageRepository.closeInset,
            size = buttonSize,
            tintColor = tintColor,
        )
        return createCircularButton(
            menuItem = closeIcon,
            eventHandler = eventHandler,
            containerColor = containerColor,
            buttonSize = buttonSize
        )
    }

    fun createCircularShareButton(
        eventHandler: ViewEventHandler,
        tintColor: ColorToken = ColorToken.ThemePrimary,
        containerColor: ColorToken = ColorToken.ThemeOnPrimary,
    ): MenuItemButton {
        val shareIcon = createIcon(
            icon = imageRepository.shareInset,
            tintColor = tintColor,
        )
        return createCircularButton(shareIcon, eventHandler, containerColor = containerColor)
    }

    fun createCircularFavoriteButton(
        favoriteId: String,
        isFavorite: Boolean,
        eventHandler: ViewEventHandler,
        tintColor: ColorToken = ColorToken.ThemePrimary,
        containerColor: ColorToken = ColorToken.ThemeOnPrimary,
    ): MenuItemButton {
        val size = viewSpecArbitrator.iconSizeLarge
        val favoriteViewState = viewStateMapper.mapFavoriteViewState(
            favoriteId = favoriteId,
            isFavorite = isFavorite,
            title = "",
            viewSpec = FavoriteViewSpec(
                size = size,
            ),
            onClick = eventHandler,
            tintColor = tintColor,
        )
        val favorite = MenuItem.MenuItemViewStateWrapper(
            viewState = favoriteViewState,
            width = DpOptional(size),
            height = DpOptional(size),
            tintColor = tintColor,
        )
        return createCircularButton(favorite, eventHandler, containerColor = containerColor)
    }

    fun createFolderLinkButton(
        label: String,
        icon: Image,
        eventHandler: ViewEventHandler,
        tintColor: ColorToken = ColorToken.ThemePrimary,
        containerColor: ColorToken = ColorToken.ThemeOnPrimary,
        width: Dp? = null,
        height: Dp = 56.dp,
    ): MenuItemButton {
        val text = TextStyleBody(label, colorToken = tintColor)
        val folderLinkIcon = createIcon(
            icon = icon,
            tintColor = tintColor,
            size = viewSpecArbitrator.iconSizeMediumLarge,
        )
        val horizontalPadding = 4.dp
        return createHorizontalButton(
            items = mutableListOf<MenuItem>().apply {
                add(createSpacerWidth(width = horizontalPadding))
                add(createLabel(text))
//                add(createSpacerWidth(width = horizontalPadding))
                add(folderLinkIcon)
//                add(createSpacerWidth(width = horizontalPadding))
            },
            width = width,
            height = height,
            buttonAppearance = ButtonAppearance.Highlight,
            shapeSpec = shapeSpecFactory.buttonShapeSpecCircular,
            containerColorToken = containerColor,
            eventHandler = eventHandler,
        )
    }

    fun createCircularButton(
        icon: Image,
        iconColor: ColorToken,
        containerColor: ColorToken,
        buttonSize: Dp = viewSpecArbitrator.iconSizeLarge,
        eventHandler: ViewEventHandler? = null,
    ): MenuItemButton {
        return createCircularButton(
            menuItem = createIcon(
                icon = icon,
                tintColor = iconColor,
            ),
            eventHandler = eventHandler,
            containerColor = containerColor,
            buttonSize = buttonSize,
        )
    }

    fun createDownloadAllButton(
        downloadCollectionViewEventHandler: ViewEventHandler,
        activeDownloadStatus: DownloadStatusViewState? = null,
    ): MenuItem {
        if (activeDownloadStatus != null) {
            if (activeDownloadStatus.totalDownloadCount > 1) {
                return createActionProgressButton(
                    progress = activeDownloadStatus.progress,
                    text = TextButtonLabel(
                        strings.downloadStatusCombined(
                            activeDownloadStatus.title.string,
                            activeDownloadStatus.summary.string,
                        ),
                        colorToken = ColorToken.ThemeOnTertiary,
                    ),
                )
            } else {
                return createDownloadButton(
                    image = imageRepository.collection,
                    label = strings.downloadAll,
                    downloadCollectionViewEventHandler = downloadCollectionViewEventHandler,
                    disabled = true,
                )
            }
        }

        return createDownloadButton(
            image = imageRepository.collection,
            label = strings.downloadAll,
            downloadCollectionViewEventHandler,
        )
    }

    fun createDownloadSelectedButton(
        downloadCollectionViewEventHandler: ViewEventHandler,
    ): MenuItem {
        return createDownloadButton(
            image = imageRepository.download,
            label = strings.downloadSelected,
            downloadCollectionViewEventHandler,
        )
    }

    private fun createDownloadButton(
        image: Image,
        label: String,
        downloadCollectionViewEventHandler: ViewEventHandler,
        width: Dp? = null,
        height: Dp = 56.dp,
        disabled: Boolean = false,
    ): MenuItemButton {
        val items: MenuItem = createHorizontalGroup(
            items = listOf(
                createButtonIcon(image),
                createSpacerWidth(),
                createLabel(TextButtonLabel(label)),
            ),
        )

        val eventHandler = if (disabled) {
            ViewEventHandler.NoOp
        } else {
            downloadCollectionViewEventHandler
        }
        return MenuItemButton(
            button = ButtonViewState(
                menuItem = items,
                shapeSpec = shapeSpecFactory.buttonShapeSpecDefault,
                eventHandler = eventHandler,
            ),
            width = width?.let { DpOptional(it) },
            height = DpOptional(height),
        )
    }

    fun createFlair(): MenuItem {
        return createHorizontalGroup(
            items = listOf(
                createIcon(
                    icon = imageRepository.info,
                    width = 60.dp,
                    height = 32.dp,
                ),
            )
        )
    }

    fun createActionButtonDownloadedToPhotos(
        staticWallpaperSize: StaticWallpaperSize?,
        eventHandler: ViewEventHandler = ViewEventHandler.NoOp,
    ): MenuItem {
        val contentColor = ColorToken.ThemeOnSurface
        return createActionButton(
            image = imageRepository.photosAppIcon,
            imageIsIcon = false,
            text = TextButtonLabel(strings.downloadedSize(staticWallpaperSize), colorToken = contentColor),
            eventHandler = eventHandler,
            buttonAppearance = ButtonAppearance.DisabledWithClick,
            containerColorToken = ColorToken.ThemeTertiary,
            contentColorToken = contentColor,
        )
    }

    fun createAdFreeCollectionLockedInfo(
        manageSubscriptionEventHandler: ViewEventHandler = viewEventFactory.createNavigateToPaywall(subscriptionPlan = SubscriptionPlan.PlusUnlimited),
    ): MenuItem {
        val message2Split = strings.adFreeCollectionLockedMessage2.splitIntoLinesAlt(2)
        val message = strings.adFreeCollectionLockedMessage1 + "\n" + message2Split

        return createVerticalGroup(
            items = listOf(
                createLabel(
                    TextStyleBody(message, textAlign = TextAlign.Center),
                    contentAlignment = Alignment.Center,
                    width = Width.WidthFillMax(),
                ),
                createHorizontalGroup(
                    items = listOf(
                        createSpacer(),
                        createLabel(TextStyleCaption(strings.manageSubscription), contentAlignment = Alignment.Center, onClick = manageSubscriptionEventHandler),
                        createSpacer(),
                    ),
                    width = viewSpecArbitrator.adFreeCollectionLockedInfoWidth,
                )

            ),
            width = viewSpecArbitrator.adFreeCollectionLockedInfoWidth,
            height = viewSpecArbitrator.adFreeCollectionLockedInfoHeight,
        )
    }
}

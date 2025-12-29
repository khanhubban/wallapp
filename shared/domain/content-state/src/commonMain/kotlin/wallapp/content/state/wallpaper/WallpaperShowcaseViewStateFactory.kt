package wallapp.content.state.wallpaper

import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wallapp.app.AppStateManager
import wallapp.appconfig.AppConfig
import wallapp.content.model.Id
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.RemixId
import wallapp.content.model.Wallpaper
import wallapp.content.model.WallpaperScreenTheme
import wallapp.content.state.favorite.FavoriteViewState
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.data.artist.Artist
import wallapp.data.collection.CollectionState
import wallapp.data.content.ContentResult.WallpaperContentResult
import wallapp.data.entitlement.EntitlementState
import wallapp.data.entitlement.isUnlockedCollection
import wallapp.data.following.FollowState
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.graphics.Color
import wallapp.image.ImageViewSpecFactory
import wallapp.image.sized.SizedImage
import wallapp.log.Log
import wallapp.pixel.alert.AlertViewStateOk
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.menu.MenuItem.MenuItemButton
import wallapp.pixel.screen.ScreenViewState
import wallapp.pixel.text.Text
import wallapp.pixel.text.TextStyleBody
import wallapp.pixel.text.TextStyleSubheading
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewEventHandler
import wallapp.resources.image.ImageRepository
import wallapp.resources.string.StringRepository
import wallapp.screen.ScreenArgument
import wallapp.search.SearchCategorySpecFactory
import wallapp.string.quote
import wallapp.system.platform.PlatformFeature
import wallapp.text.TextAlign
import wallapp.text.TextToolbarTitle
import wallapp.theme.ColorToken
import wallapp.unit.Alignment
import wallapp.unit.height
import wallapp.unit.width
import wallapp.view.ViewEventFactory
import wallapp.view.ViewFactory
import wallapp.view.ViewSpecArbitrator
import wallapp.view.ViewSpecFactory
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateMapper
import wallapp.view.menu.MenuItemFactory


class WallpaperShowcaseViewStateFactory(
    private val viewStateFactory: ViewStateFactory,
    private val viewStateMapper: ViewStateMapper,
    private val viewEventFactory: ViewEventFactory,
    private val viewSpecFactory: ViewSpecFactory,
    private val viewSpecArbitrator: ViewSpecArbitrator,
    private val viewFactory: ViewFactory,
    private val imageViewSpecFactory: ImageViewSpecFactory,
    private val appStateManager: AppStateManager,
    private val appConfig: AppConfig,
    private val menuItemFactory: MenuItemFactory,
    private val strings: StringRepository,
    private val imageRepository: ImageRepository,
    private val searchCategorySpecFactory: SearchCategorySpecFactory,
    private val coroutineScopeMain: CoroutineScope,
) {
    private val displaysAsBottomSheet: Boolean
        get() = appConfig.spotlightBottomSheet.value || PlatformFeature.SupportModalSheetBehaviour

    private val spotlightTheme: WallpaperScreenTheme
        get() = appConfig.spotlightTheme.value
    private val showCollection: Boolean
        get() = appConfig.spotlightShowsCollection.value
    private val showAdditionalCollections: Boolean
        get() = appConfig.spotlightShowsOtherCollections.value

    fun createPlaceholderActionButton(): MenuItem = viewStateFactory.createPlaceholderActionButton()

    private fun createArtist(
        artist: Artist,
        imageViewSpec: ImageViewSpec,
        artistFollowState: FollowState?,
        toggleFollowEventHandler: ViewEventHandler,
        navigateToArtistEventHandler: ViewEventHandler,
        favoriteViewState: FavoriteViewState,
        animateFollowIndicator: Boolean,
        followAnimStartedEventHandler: ViewEventHandler,
    ): WallpaperArtistViewState {
        return viewStateMapper.mapWallpaperArtist(
            artist,
            sizedImage = SizedImage.ArtistSmall,
            imageViewSpec = imageViewSpec,
            actionButtons = null,//listOf(followButton),
            favorite = favoriteViewState,
            showFollowIndicator = true,
            followState = artistFollowState,
            toggleFollowEventHandler = toggleFollowEventHandler,
            navigateToArtistEventHandler = navigateToArtistEventHandler,
            animateFollowIndicator = animateFollowIndicator,
            followAnimStartedEventHandler = followAnimStartedEventHandler,
        )
    }

    private fun createWallpaperArtistViewState(
        wallpaperPreview: WallpaperPreviewViewState,
        artist: Artist?,
        followState: FollowState?,
        toggleFollowEventHandler: ViewEventHandler,
        navigateToArtistEventHandler: ViewEventHandler,
        animateFollowIndicator: Boolean,
        followAnimStartedEventHandler: ViewEventHandler,
    ): WallpaperArtistViewState {
        val imageViewSpec = imageViewSpecFactory.wallpaperArtistProfileImageViewSpec
        if (artist == null) {
            return WallpaperArtistViewState.Loading(height = imageViewSpec.height)
        }

        return createArtist(
            artist,
            imageViewSpec,
            followState,
            toggleFollowEventHandler,
            navigateToArtistEventHandler,
            wallpaperPreview.favorite,
            animateFollowIndicator,
            followAnimStartedEventHandler,
        )
    }

    private fun navigateToSubScreen(onClick: () -> Unit) {
        if (displaysAsBottomSheet) {
            appStateManager.dismissBottomSheet()
            // Transition appearance is improved with minor delay here
            coroutineScopeMain.launch {
                delay(100)
                onClick.invoke()
            }
        } else {
            onClick.invoke()
        }
    }

    private fun createTitleAndSubTitleMenuItem(
        titleLabel: Text,
        subtitleLabel: Text?,
        collectionId: CollectionId?,
        isAiEnhanced: Boolean,
    ): MenuItem {
        val subtitle = if (collectionId != null) {
            val eventHandler = viewEventFactory.createNavigateToScreen(
                ScreenArgument.CollectionIdScreenArgument(
                    collectionId = collectionId,
                    firstWallpaperId = null,
                ),
            )
            subtitleLabel?.let {
                menuItemFactory.createLabel(
                    it,
                    height = 26.dp.height,
                    contentAlignment = Alignment.Center,
                    onClick = eventHandler,
                    width = viewSpecArbitrator.wallpaperToolbarTitleWidth.width,
                )
            }
        } else {
            subtitleLabel?.let {
                menuItemFactory.centeredText(
                    it,
                    height = 26.dp.height,
                )
            }
        }

        val titleHeight = if (subtitle != null) 30.dp else 56.dp
        val titleLabelItem = menuItemFactory.createLabel(titleLabel)

        val titleItems = if (isAiEnhanced) {
            val aiEnhancedOnClick = viewEventFactory.createShowAlert(
                AlertViewStateOk(
                    title = strings.aiEnhancedTitle,
                    message = strings.aiEnhancedMessage
                )
            )

            listOf(
                menuItemFactory.createSpacer(width = 8.dp),
                menuItemFactory.createIcon(
                    imageRepository.aiEnhanced,
                    size = viewSpecArbitrator.iconSize,
                    onClick = aiEnhancedOnClick,
                ),
                titleLabelItem,
                menuItemFactory.createSpacer(width = 8.dp)
            )
        } else {
            listOf(titleLabelItem)
        }

        val title = menuItemFactory.createHorizontalGroup(
            items = titleItems,
            height = titleHeight,
            width = viewSpecArbitrator.wallpaperToolbarTitleWidth
        )

        return if (subtitle != null) {
            menuItemFactory.createVerticalGroup(listOf(title, subtitle))
        } else {
            title
        }
    }

    private fun List<Wallpaper>?.map(
        wallpaperPreview: WallpaperPreviewViewState,
    ): List<WallpaperPreviewViewState>? = this?.map { it.map(wallpaperPreview) }

    private fun Wallpaper.map(
        wallpaperPreview: WallpaperPreviewViewState,
    ): WallpaperPreviewViewState {
        return viewStateMapper.mapWallpaperPreviewViewState(
            wallpaper = this,
            viewSpec = wallpaperPreview.viewSpec,
            onClick = wallpaperPreview.onClick,
            onClickFavorite = wallpaperPreview.favorite.onClick,
            onClickPlus = null,
        ).let {
            val title = wallpaperPreview.title
            it.copy(
                title = title,
                subtitle = if (it.title != title) it.title else null,
                favorite = wallpaperPreview.favorite.copy(
                    isFavorite = it.favorite.isFavorite,
                ),
            )
        }
    }

    private fun createWallpaperDetail(
        id: Id,
        detailCategories: String?,
        showAiEnhanced: Boolean,
        sdResolution: String?,
        hdResolution: String,
        showCopyright: Boolean = !viewSpecArbitrator.windowIsCompact || sdResolution == null,
    ): WallpaperDetailViewState {
        val categories = if (showAiEnhanced) {
            WallpaperDetailItem(
                imageRepository.aiEnhancedCircleOutline,
                TextStyleBody(strings.aiEnhancedTitle),
            )
        } else if (detailCategories != null) {
            WallpaperDetailItem(
                imageRepository.filter,
                TextStyleBody(detailCategories),
            )
        } else {
            null
        }

        val detailItems = WallpaperDetailViewState.from(
            categories = categories,
            copyright = if (showCopyright) {
                WallpaperDetailItem(
                    imageRepository.copyright,
                    TextStyleBody(strings.copyrightFullWithYear),
                )
            } else {
                null
            },
            dimensionsSd = sdResolution?.let {
                WallpaperDetailItem(imageRepository.info, TextStyleBody(it))
            },
            dimensionsHd = WallpaperDetailItem(
                image = imageRepository.info,
                label = TextStyleBody(hdResolution)
            ),
        )
        val detailCount = detailItems?.size ?: -1
        require(detailCount > 0) {
            "At least one detail item must be provided, id: ${id.name.quote()}"
        }

        return WallpaperDetailViewState(
            viewSpec = viewSpecFactory.wallpaperDetailViewSpec(itemDetailCount = detailCount),
            detailItems = detailItems,
        )
    }

    private fun WallpaperContentResult.mapLabels(selectedIndex: Int): Pair<String?, String?> {
        val wallpaper = this.wallpaper ?: return null to null
        return if (wallpaper.isSingle) {
            wallpaper.label to null
        } else {
            val selectedWallpaper = collectionState?.wallpapers?.getOrNull(selectedIndex)
            selectedWallpaper?.label to collectionState?.label
        }
    }

    fun createViewState(
        dataMap: Map<RemixId, WallpaperContentResult>,
        entitlementState: EntitlementState?,
        selectedIndex: Int,
        selectedRemixId: RemixId,
        actionButton: MenuItem,
        onPageChangedWallpaperPreview: (Int) -> Unit,
        toggleFollowEventHandler: ViewEventHandler,
        navigateToArtistEventHandler: ViewEventHandler,
        shareEventHandler: ViewEventHandler,
        overlayScreen: ScreenViewState?,
        animateFollowIndicator: Boolean,
        followAnimStartedEventHandler: ViewEventHandler,
        controlButtonBackgroundColor: Color,
        controlButtonOnBackgroundColor: Color,
    ): WallpaperShowcaseViewState {
        val data = dataMap[selectedRemixId] ?: return WallpaperShowcaseViewState.Loading
        val wallpaper = data.wallpaper ?: return WallpaperShowcaseViewState.Loading
        val wallpaperState = data.wallpaperState ?: return WallpaperShowcaseViewState.Loading
        val isAiEnhanced = wallpaper.isAiEnhanced
//        val isSingle = wallpaper.isSingle
//        val isUnlocked = entitlementState?.isUnlocked

        val collection = data.collectionState
        require(wallpaper.isSingle || (wallpaper.isInCollection && collection != null)) {
            "Collection must be provided if Wallpaper is not a Single (${wallpaper.id})"
        }
        val collectionWallpapers = collection?.wallpapers
        val artist = data.artist
        val artistFollowState = data.artistFollowState
        val additionalCollections = data.additionalCollectionStates
        val currentPreviewIndex = if (selectedIndex == -1) {
            0
        } else {
            selectedIndex
        }

        Log.d("createViewState(): selectedIndex: $selectedIndex, currentPreviewIndex: $currentPreviewIndex")

        val wallpaperPreviewViewSpec = viewSpecFactory.wallpaperPreviewViewSpecWallpaperShowcase

        // Clicking the preview will perform the same action as clicking the action button
        val wallpaperPreviewOnClick = if (actionButton is MenuItemButton) {
            actionButton.button.eventHandler ?: ViewEventHandler.NoOp
        } else {
            ViewEventHandler.NoOp
        }

        val wallpaperPreview = viewStateMapper.mapWallpaperPreviewViewState(
            wallpaper = wallpaper,
            viewSpec = wallpaperPreviewViewSpec,
            onClick = wallpaperPreviewOnClick,
            onClickFavorite = viewEventFactory.createOnClickFavorite(
                collectionWallpapers?.get(currentPreviewIndex)?.id
                    ?: wallpaper.id,
            ),
            onClickPlus = null,
        )
        val wallpaperPreviews: List<WallpaperPreviewViewState> =
            collectionWallpapers?.map(wallpaperPreview)?.ifEmpty { null }
                ?: listOf(wallpaperPreview)

        val views = mutableListOf<View>()
        if (showCollection && collection != null) {
            views.add(
                viewFactory.createCollectionPreview(
                    collectionState = collection,
                    collectionPreviewViewSpec = viewSpecFactory.collectionPreviewFullViewSpec,
                )
            )
        }
        if (showAdditionalCollections && additionalCollections != null && artist != null) {
            views.addAll(
                viewFactory.createMoreCollectionsFromArtist(
                    artist = artist,
                    collectionStates = additionalCollections,
                ),
            )
        }

        val detailCategories = if (isAiEnhanced) {
            null
        } else {
            data.searchCategories
                ?.takeIf { it.isNotEmpty() }
                ?.mapNotNull { searchCategorySpecFactory.find(it) }
                ?.map { it.label }
                ?.sorted()
                ?.joinToString(", ") ?: "(to be classified)"
        }

        val hdImageSize = wallpaperState.wallpaper.downloadMedia.hdImageSize
        val hdResolution = strings.resolutionFullRes(hdImageSize.width, hdImageSize.height)

        val sdResolution = if (wallpaper.isSingle) {
            strings.resolutionHd(StaticWallpaperSize.StandardResolution.width, StaticWallpaperSize.StandardResolution.height)
        } else {
            null
        }
        val detail = createWallpaperDetail(
            id = wallpaper.id,
            detailCategories = detailCategories,
            showAiEnhanced = isAiEnhanced,
            sdResolution = sdResolution,
            hdResolution = hdResolution,
        )

        val currentPreview = when {
            selectedIndex > -1 && selectedIndex < wallpaperPreviews.size -> {
                wallpaperPreviews[selectedIndex]
            }

            else -> {
                wallpaperPreviews.first()
            }
        }

        val (title, subtitle) = data.mapLabels(selectedIndex).let { labels ->
            val titleLabel = labels.first ?: ""
            TextToolbarTitle(titleLabel) to
                    labels.second?.let {
                        TextStyleSubheading(
                            it,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                    }
        }

        val titleAndSubTitle = createTitleAndSubTitleMenuItem(
            titleLabel = title,
            subtitleLabel = subtitle,
            collectionId = if (subtitle != null) { collection?.id } else { null },
            isAiEnhanced = isAiEnhanced,
        )
        val onSwipeToDismiss = viewStateFactory.createOnSwipeToDismiss()

        val wallpaperViewSpec = viewSpecFactory.wallpaperShowcaseViewSpec

        val controlButtonEdgePadding = viewSpecArbitrator.wallpaperShowcaseControlButtonEdgePadding
        val controlButtonSpacing = viewSpecArbitrator.wallpaperShowcaseControlButtonSpacing
        val actionItems = menuItemFactory.createHorizontalGroup(
            listOfNotNull(
                menuItemFactory.createCircularFavoriteButton(
                    favoriteId = currentPreview.favorite.id,
                    currentPreview.favorite.isFavorite,
                    currentPreview.favorite.onClick,
                    containerColor = ColorToken.Custom(controlButtonBackgroundColor),
                    tintColor = ColorToken.Custom(controlButtonOnBackgroundColor),
                ),
                menuItemFactory.createSpacer(width = controlButtonSpacing),
                menuItemFactory.createCircularShareButton(
                    shareEventHandler,
                    containerColor = ColorToken.Custom(controlButtonBackgroundColor),
                    tintColor = ColorToken.Custom(controlButtonOnBackgroundColor),
                ),
                menuItemFactory.createSpacer(width = controlButtonEdgePadding)
            )
        )

        val close = menuItemFactory.createHorizontalGroup(
            listOf(
                menuItemFactory.createSpacer(width = controlButtonEdgePadding),
                menuItemFactory.createCircularCloseButton(
                    containerColor = ColorToken.Custom(controlButtonBackgroundColor),
                    tintColor = ColorToken.Custom(controlButtonOnBackgroundColor),
                ),
            )
        )

        val previewActionButton = if (collection != null && entitlementState?.isUnlockedCollection != true) {
            menuItemFactory.createImage(
                image = imageRepository.plusIndicatorOnBackground,
                size = 40.dp,
                onClick = viewEventFactory.createNavigateToPaywall(subscriptionPlan = SubscriptionPlan.PlusUnlimited),
            )
        } else {
            null
        }

        val wallpaperArtist = createWallpaperArtistViewState(
            wallpaperPreview = currentPreview,
            artist = artist,
            followState = artistFollowState,
            toggleFollowEventHandler = toggleFollowEventHandler,
            navigateToArtistEventHandler = navigateToArtistEventHandler,
            animateFollowIndicator = animateFollowIndicator,
            followAnimStartedEventHandler = followAnimStartedEventHandler
        )

        return WallpaperShowcaseViewState.Success(
            wallpaperViewSpec,
            shadowStatusBar =  createShadowStatusBarImageViewState(),
            overlayScreen = overlayScreen,
            wallpaperPreviews = wallpaperPreviews,
            currentPreviewIndex = currentPreviewIndex,
            onPageChangedWallpaperPreview = onPageChangedWallpaperPreview,
            wallpaperArtist = wallpaperArtist,
            detail = detail,
            actionButton = actionButton,
            title = titleAndSubTitle,
            close = close,
            actionItems = actionItems,
            previewActionButton = previewActionButton,
            themeColors = null,
            useBackgroundGradient = false,
            indicatorPillBackgroundColor = controlButtonBackgroundColor,
            indicatorColor = controlButtonOnBackgroundColor,
            onSwipeToDismiss = onSwipeToDismiss,
            offsetForStatusBar = !displaysAsBottomSheet,
            previousClickContentDescription = strings.previous,
            nextClickContentDescription = strings.next,
        )
    }

    private fun createShadowStatusBarImageViewState(): ImageViewState? {
        return if (!displaysAsBottomSheet) {
            ImageViewState(
                image = imageRepository.statusBarShadow,
                viewSpec = imageViewSpecFactory.shadowStatusBarImageViewSpec,
                imageSize = null
            )
        } else {
            null
        }
    }

    fun createCollectionWallpaperDownloadButton(
        collectionState: CollectionState,
        wallpaper: Wallpaper,
        label: String,
        entitlementState: EntitlementState?,
    ): MenuItem {
        var modalSheetHeight = viewSpecArbitrator.bottomSheetHeightTwoOptions.value
        if (collectionState.showAdFreeCollectionLockedInfo) {
            modalSheetHeight += viewSpecArbitrator.adFreeCollectionLockedInfoHeight.value
        }
        val eventHandler = viewEventFactory.createNavigateToScreen(
            ScreenArgument.CollectionActionScreenArgument(
                collectionId = collectionState.id,
                singleWallpaperId = wallpaper.id,
                modalSheetHeight = modalSheetHeight,
            ),
        )

        // TODO: This should have only one source of truth instead of current 2
        return if (collectionState.connectionState?.isUnlocked == true || entitlementState?.isUnlockedCollection == true) {
            menuItemFactory.createGetButton(
                label,
                eventHandler,
            )
        } else {
            menuItemFactory.createBuyCollectionButton(
                collectionState.priceLabelNonNull,
                eventHandler,
            )
        }
    }
}
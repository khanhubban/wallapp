package wallapp.view

import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.StateFlow
import wallapp.ads.inline.support.InlineAdItem
import wallapp.appconfig.AppConfig
import wallapp.content.model.Id
import wallapp.content.model.Wallpaper
import wallapp.content.model.WallpaperRemix
import wallapp.content.state.ad.AdViewState
import wallapp.content.state.ad.FeedAdViewState
import wallapp.content.state.artist.ArtistPreviewViewState
import wallapp.content.state.carousel.CarouselPageViewState
import wallapp.content.state.carousel.CarouselViewState
import wallapp.content.state.collection.CollectionPreviewFooterViewState
import wallapp.content.state.collection.CollectionPreviewLayerViewState
import wallapp.content.state.collection.CollectionPreviewViewSpec
import wallapp.content.state.collection.CollectionPreviewViewState
import wallapp.content.state.exhibit.ExhibitViewState
import wallapp.content.state.favorite.FavoriteViewSpec
import wallapp.content.state.favorite.FavoriteViewState
import wallapp.content.state.follow.FollowIndicatorViewState
import wallapp.content.state.profile.ProfileCuratorViewState
import wallapp.content.state.profile.ProfileImageIndicatorViewState
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.content.state.social.SocialLinkViewEvent
import wallapp.content.state.social.SocialLinksViewState
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.content.state.upgrade.plus.indicator.PlusIndicatorViewState
import wallapp.content.state.wallpaper.WallpaperArtistViewState
import wallapp.content.state.wallpaper.WallpaperPreviewViewSpec
import wallapp.content.state.wallpaper.WallpaperPreviewViewState
import wallapp.data.artist.Artist
import wallapp.data.artist.ArtistState
import wallapp.data.collection.CollectionState
import wallapp.data.content.media.ContentMediaRepository
import wallapp.data.curator.Curator
import wallapp.data.favorite.FavoriteItemsRepository
import wallapp.data.folder.FolderState
import wallapp.data.following.FollowState
import wallapp.data.highlight.Highlight
import wallapp.data.highlight.Highlight.ArtistHighlight
import wallapp.data.highlight.Highlight.CollectionHighlight
import wallapp.data.highlight.Highlight.FolderHighlight
import wallapp.data.highlight.Highlight.PlusHighlight
import wallapp.data.highlight.Highlight.SignInHighlight
import wallapp.data.highlight.Highlight.WallpaperHighlight
import wallapp.data.highlight.Highlights
import wallapp.data.social.SocialLinks
import wallapp.di.resolveDependency
import wallapp.image.Image
import wallapp.image.ImageViewSpecFactory
import wallapp.image.sized.SizedImage
import wallapp.image.updateAnimatedSpecWith
import wallapp.media.model.MediaHolder
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.button.ButtonAppearance
import wallapp.pixel.button.ButtonViewState
import wallapp.pixel.feed.MaxFeedWidthNoPaddingViewSpec
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.menu.MenuItem.MenuItemContainer
import wallapp.pixel.menu.MenuItem.MenuItemIcon
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.text.Text
import wallapp.pixel.text.TextStyleCallToAction
import wallapp.pixel.text.TextStyleSubheadingActive
import wallapp.pixel.util.DpOptional
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewContentScale
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewEventSink
import wallapp.pixel.view.ViewId
import wallapp.resources.image.ImageRepository
import wallapp.resources.string.StringRepository
import wallapp.screen.ScreenArgument
import wallapp.system.platform.PlatformFeature
import wallapp.text.TextAlign
import wallapp.text.TextBodySmallResizable
import wallapp.text.TextCollectionFooter
import wallapp.text.TextExhibit
import wallapp.theme.ColorToken
import wallapp.unit.Alignment
import wallapp.view.shape.ShapeSpecFactory

class ViewStateMapperDefault(
    private val viewEventFactory: ViewEventFactory,
    private val viewSpecFactory: ViewSpecFactory,
    private val viewSpecArbitrator: ViewSpecArbitrator,
    private val viewIdMapper: ViewIdMapper,
    private val imageViewSpecFactory: ImageViewSpecFactory,
    private val shapeSpecFactory: ShapeSpecFactory,
    private val strings: StringRepository,
    private val imageRepository: ImageRepository,
    private val contentMediaRepository: ContentMediaRepository,
    private val favoriteItemsRepository: FavoriteItemsRepository,
) : ViewStateMapper {

    companion object {
        const val CollectionPreviewMaxItems = 3
    }

    private val isReady: StateFlow<Boolean>
        get() = viewSpecArbitrator.isReady

    private fun requireIsReady() {
        require(isReady.value) { "ViewStateMapper is not ready" }
    }

    private val String.nonEmptyString: String?
        get() = if (isNullOrEmpty()) {
            null
        } else {
            this
        }

    private fun String.resizableText(maxLines: Int = 1): Text =
        TextBodySmallResizable(
            string = this,
            maxLines = maxLines,
            textAlign = TextAlign.Start,
        )

    override fun mapButtonViewState(
        menuItem: MenuItem,
        animatedViewSpec: AnimatedViewSpec?,
        buttonAppearance: ButtonAppearance,
        shapeSpec: ShapeSpec?,
        containerColorToken: ColorToken?,
        eventHandler: ViewEventHandler?,
    ): ButtonViewState {
        if (animatedViewSpec != null) {
            require(shapeSpec == null) { "shapeSpec must be null when animatedViewSpec is provided" }
        }

        return ButtonViewState(
            menuItem = menuItem,
            animatedViewSpec = animatedViewSpec,
            shapeSpec = if (animatedViewSpec == null) {
                shapeSpec ?: shapeSpecFactory.buttonShapeSpecDefault
            } else {
                null
            },
            buttonAppearance = buttonAppearance,
            containerColorToken = containerColorToken,
            eventHandler = eventHandler,
        )
    }

    override fun mapWallpaperImage(
        wallpaper: Wallpaper,
        viewSpec: WallpaperPreviewViewSpec,
    ): ImageViewState {
        requireIsReady()

        return contentMediaRepository.getImageViewState(
            mediaHolder = wallpaper.previewImages.mediaHolder,
            sizedImage = viewSpec.sizedImage,
            imageViewSpec = viewSpec.imageViewSpec,
        )
    }

    val previewFooterBackground: Image
        get() = imageRepository.contentFooterOverlay

    override fun mapFavoriteViewState(
        favoriteId: String,
        isFavorite: Boolean,
        title: String,
        viewSpec: FavoriteViewSpec?,
        onClick: ViewEventHandler,
        tintColor: ColorToken?,
    ): FavoriteViewState {
        return FavoriteViewState(
            id = favoriteId,
            viewSpec = viewSpec ?: viewSpecFactory.favoriteViewSpec,
            isFavorite = isFavorite,
            selectedImage = imageRepository.favoriteOn,
            unselectedImage = imageRepository.favoriteOff,
            onClick = onClick,
            favoriteContentDescription = strings.favoriteContentDescription(title, isFavorite),
            selectedImageAnimated = imageRepository.favoriteOnAnimated,
            unselectedImageAnimated = imageRepository.favoriteOffAnimated,
            tintColor = tintColor,
        )
    }

    override fun mapWallpaperPreviewViewState(
        wallpaper: Wallpaper,
        viewSpec: WallpaperPreviewViewSpec,
        onClick: ViewEventHandler,
        onClickFavorite: ViewEventHandler,
        onClickPlus: ViewEventHandler?,
        useCollectionLabel: Boolean,
    ): WallpaperPreviewViewState {
        requireIsReady()

        val title = if (useCollectionLabel) {
            wallpaper.collectionLabel
        }  else {
            wallpaper.label
        }
        val subtitle = null

        val favorite = mapFavoriteViewState(
            favoriteId = wallpaper.id.name,
            isFavorite = favoriteItemsRepository.isFavorite(wallpaper.id),
            title = title,
            viewSpec = null,
            onClick = onClickFavorite,
        )

        val plusIndicator = if (onClickPlus != null) {
            mapPlusIndicator(indicatorOnBackground = true, viewEventHandler = onClickPlus)
        } else {
            null
        }

        return WallpaperPreviewViewState(
            viewSpec = viewSpec,
            viewId = viewIdMapper.mapWallpaperPreviewViewId(wallpaper.id),
            imageViewState = mapWallpaperImage(wallpaper, viewSpec),
            title = title.resizableText(),
            subtitle = subtitle?.resizableText(),
            scrimImage = previewFooterBackground,
            onClick = onClick,
            isSingle = wallpaper.isSingle,
            plusIndicator = plusIndicator,
            favorite = favorite,
        )
    }

    override fun mapArtistPreview(
        artist: Artist,
        sizedImage: SizedImage,
        imageViewSpec: ImageViewSpec,
        onClick: ViewEventHandler,
    ) = ArtistPreviewViewState(
        name = artist.name.resizableText(maxLines = 2),
        backgroundImages = emptyList(),
        profileShadowImage = null,
        containerShapeSpec = null,
        profileImage = mapProfileImage(
            profileImageMediaHolder = artist.profileImageMediaHolder,
            sizedImage = sizedImage,
            imageViewSpec = imageViewSpec,
            eventHandler = null,
        ),
        eventHandler = onClick,
        followButton = null,
    )

    override fun mapArtistPreviewOnboarding(
        artistState: ArtistState,
        followToggleExtraAction: ((Id.ArtistId) -> Unit)?,
    ): ArtistPreviewViewState {
//        val colorToken = ColorToken.LocalContent
        val imageViewSpec = imageViewSpecFactory.artistPreviewProfileImageViewSpecOnboarding
//        val viewSpec = viewSpecFactory.artistPreviewViewSpecOnboarding
        val containerShapeSpec = shapeSpecFactory.artistOnboardingShapeSpec

        val eventHandler = viewEventFactory.createFollowing(
            artistState.id,
            artistState.followState,
            actionBlock = followToggleExtraAction
        )
        val followStateIndicator = mapFollowIndicator(visible = artistState.followState?.isFollowing == true)

        val backgroundImages = mapArtistPreviewBackgroundImages(artistState)

        return mapArtistPreview(
            artistState,
            sizedImage = SizedImage.ArtistMedium,
            imageViewSpec = imageViewSpec,
            backgroundImages = backgroundImages,
            onClick = eventHandler,
            onClickFavorite = viewEventFactory.createOnClickFavorite(artistState.id),
            profileImageEventHandler = viewEventFactory.createArtistProfile(artistState),
            containerShapeSpec = containerShapeSpec,
            followButton = null,
            followIndicator = followStateIndicator,
            showArtistProfileBorder = true,
            followIndicatorOutlineColorToken = null,
        )
    }

    override fun mapArtistPreview(
        artistState: ArtistState,
        sizedImage: SizedImage,
        imageViewSpec: ImageViewSpec,
        backgroundImages: List<ImageViewState>?,
        onClick: ViewEventHandler,
        onClickFavorite: ViewEventHandler,
        profileImageEventHandler: ViewEventHandler?,
        containerShapeSpec: ShapeSpec?,
        followButton: MenuItem?,
        followIndicator: FollowIndicatorViewState?,
        followIndicatorOutlineColorToken: ColorToken?,
        showArtistProfileBorder: Boolean,
    ): ArtistPreviewViewState {
        val profileImage = mapProfileImage(
            profileImageMediaHolder = artistState.artist.profileImageMediaHolder,
            sizedImage,
            imageViewSpec,
            profileImageEventHandler,
            showFollowIndicator = false,
            followIndicatorOutlineColorToken = followIndicatorOutlineColorToken,
            artistState.followState,
            showArtistProfileBorder,
        )

        val profileShadowImage = if (showArtistProfileBorder) {
            imageRepository.profileShadow
        } else {
            null
        }

        return ArtistPreviewViewState(
            name = artistState.artist.name.resizableText(maxLines = 2),
            profileImage = profileImage,
            profileShadowImage = profileShadowImage,
            backgroundImages = backgroundImages ?: emptyList(),
            containerShapeSpec = containerShapeSpec,
            eventHandler = onClick,
            followButton = followButton,
            followIndicator = followIndicator,
        )
    }

    override fun mapArtistPreviewBackgroundImages(
        artistState: ArtistState,
    ): List<ImageViewState>? {
        return artistState.previewWallpapers
            ?.map { wallpaper: WallpaperRemix ->
                val previewViewSpec = if (wallpaper.isSingle) {
                    viewSpecFactory.wallpaperPreviewViewSpecArtistSelectionBackgroundSingle
                } else {
                    viewSpecFactory.wallpaperPreviewViewSpecArtistSelectionBackgroundTrack
                }
                mapWallpaperImage(wallpaper, previewViewSpec)
            }
    }

    private fun mapPlusIndicator(
        collectionState: CollectionState,
    ): PlusIndicatorViewState? {
        val connectionState = collectionState.connectionState ?: return null
        if (connectionState.isUnlocked) {
            return null
        }

        return mapPlusIndicator(
            indicatorOnBackground = true,
            viewEventHandler = viewEventFactory.createNavigateToPaywall(subscriptionPlan = SubscriptionPlan.PlusUnlimited),
        )
    }

    private fun mapCollectionPreviewFooter(
        collectionState: CollectionState,
    ): CollectionPreviewFooterViewState {
        val isUnlocked = collectionState.connectionState?.isUnlocked == true

//        val price: Text? = if (!isUnlocked) {
//            collection.connectionState?.priceLabel?.let { TextCollectionFooter(it) }
//        } else {
//            null
//        }
        val price: Text? = null
        val onClick = null
        return CollectionPreviewFooterViewState(
            title = TextCollectionFooter(collectionState.label),
            price = price,
            scrimImage = previewFooterBackground,
            shapeSpec = shapeSpecFactory.collectionWideFooterShapeSpec,
            onClick = onClick,
        )
    }

    private fun mapCollectionPreviewLayerSmall(
        imageViewState: ImageViewState,
        index: Int,
        eventHandler: ViewEventHandler,
    ): CollectionPreviewLayerViewState {
        val shadow = when (index) {
            0 -> null
            1 -> imageRepository.collectionStackShadowMiddle
            else -> imageRepository.collectionStackShadowBottom
        }

        return CollectionPreviewLayerViewState(
            imageViewState = ImageViewState(
                image = imageViewState.image,
                viewSpec = imageViewSpecFactory.collectionPreviewSmallImageViewSpec(index = index),
                imageSize = imageViewState.imageSize,
            ),
            shadow = shadow,
            eventHandler = eventHandler,
        )
    }

    private fun mapCollectionPreviewLayerFull(
        imageViewState: ImageViewState,
        index: Int,
        eventHandler: ViewEventHandler,
    ): CollectionPreviewLayerViewState {
        val shadow = when (index) {
            0 -> null
            1 -> imageRepository.collectionStackShadowMiddle
            else -> imageRepository.collectionStackShadowBottom
        }

        return CollectionPreviewLayerViewState(
            imageViewState = ImageViewState(
                image = imageViewState.image,
                viewSpec = imageViewSpecFactory.collectionPreviewFullImageViewSpec(index = index),
                imageSize = imageViewState.imageSize,
            ),
            shadow = shadow,
            eventHandler = eventHandler,
        )
    }

    private fun mapCollectionPreviewLayer(
        imageViewState: ImageViewState,
        index: Int,
        useSmall: Boolean,
        eventHandler: ViewEventHandler,
    ): CollectionPreviewLayerViewState {
        return if (useSmall) {
            mapCollectionPreviewLayerSmall(imageViewState, index, eventHandler)
        } else {
            mapCollectionPreviewLayerFull(imageViewState, index, eventHandler)
        }
    }

    private fun wallpaperPreviewViewSpecCollectionPreview(index: Int, useSmall: Boolean):
            WallpaperPreviewViewSpec {
        return if (useSmall) {
            viewSpecFactory.wallpaperPreviewViewSpecCollectionPreviewSmall(index)
        } else {
            viewSpecFactory.wallpaperPreviewViewSpecCollectionPreviewFull(index)
        }
    }

    override fun mapCollectionPreview(
        collectionState: CollectionState,
        collectionPreviewViewSpec: CollectionPreviewViewSpec,
        showFooter: Boolean,
        eventHandlerOverride: ViewEventHandler?,
    ): CollectionPreviewViewState {
        requireIsReady()

        val useSmallSize = collectionPreviewViewSpec != viewSpecFactory.collectionPreviewFullViewSpec

        val previewItems = collectionState.wallpapers.take(CollectionPreviewMaxItems)
        val layers = previewItems
            .mapIndexed { index, wallpaper ->
                val wallpaperPreviewViewSpec = wallpaperPreviewViewSpecCollectionPreview(index, useSmallSize)
                val wallpaperPreview = mapWallpaperPreviewViewState(
                    wallpaper,
                    viewSpec = wallpaperPreviewViewSpec,
                    onClick = ViewEventHandler.NoOp,
                    onClickFavorite = ViewEventHandler.NoOp,
                    onClickPlus = null,
                )

                val eventHandler = eventHandlerOverride
                    ?: viewEventFactory.createNavigateToScreen(
                        ScreenArgument.CollectionIdScreenArgument(
                            collectionId = collectionState.id,
                            firstWallpaperId = wallpaper.id,
                        )
                    )

                mapCollectionPreviewLayer(wallpaperPreview.imageViewState, index, useSmallSize, eventHandler)
            }

        val footer = if (showFooter) {
            mapCollectionPreviewFooter(collectionState)
        } else {
            null
        }
        val plusIndicator = mapPlusIndicator(collectionState)

        return CollectionPreviewViewState(
            viewSpec = collectionPreviewViewSpec,
            viewId = viewIdMapper.mapCollectionPreviewViewId(collectionState.id, secondaryId = collectionState.secondaryId),
            layers = layers,
            title = collectionState.label,
            plusIndicator = plusIndicator,
            footer = footer,
        )
    }

    private fun arbitrateProfileImageIndicatorImage(
        animateFollowIndicator: Boolean,
        followState: FollowState?,
        showFollowIndicator: Boolean,
        followAnimStartedEventHandler: ViewEventHandler?,
    ): Image? {
        val useAnimatedIndicatorImage = if (PlatformFeature.IsIos) {
            // This code will be very short-lived, so we can live with this resolveDependency for now.
            val appConfig = resolveDependency<AppConfig>()
            appConfig.enableNativeUiRendering.value
        } else {
            PlatformFeature.AnimatedImagesSupported
        }

        return if (useAnimatedIndicatorImage) {
            val animationStarted = {
                followAnimStartedEventHandler?.invoke() ?: Unit
            }

            when {
                !showFollowIndicator -> null
                followState?.isFollowing == true -> imageRepository.followOn.updateAnimatedSpecWith(
                    animateFollowIndicator,
                    animationStarted
                )

                else -> imageRepository.followOff.updateAnimatedSpecWith(
                    animateFollowIndicator,
                    animationStarted
                )
            }
        } else {
            when {
                !showFollowIndicator -> null
                followState?.isFollowing == true -> imageRepository.followingProfile
                else -> imageRepository.followProfile
            }
        }
    }

    override fun mapProfileImage(
        profileImageMediaHolder: MediaHolder,
        sizedImage: SizedImage,
        imageViewSpec: ImageViewSpec,
        eventHandler: ViewEventHandler?,
        showFollowIndicator: Boolean,
        followIndicatorOutlineColorToken: ColorToken?,
        followState: FollowState?,
        showArtistProfileBorder: Boolean,
        animateFollowIndicator: Boolean,
        followAnimStartedEventHandler: ViewEventHandler?,
    ): ProfileImageViewState {
//        Log.d("[LottieFixes] mapProfileImage, animateFollowIndicator: $animateFollowIndicator")
        requireIsReady()

        val indicator = arbitrateProfileImageIndicatorImage(
            animateFollowIndicator,
            followState,
            showFollowIndicator,
            followAnimStartedEventHandler,
        ).let {
            if (it != null) {
                requireNotNull(followIndicatorOutlineColorToken)
                ProfileImageIndicatorViewState(
                    viewSpec = viewSpecFactory.profileImageIndicatorViewSpec,
                    image = it,
                    colorToken = followIndicatorOutlineColorToken,
                )
            } else {
                null
            }
        }

        val imageViewState = contentMediaRepository.getImageViewState(
            mediaHolder = profileImageMediaHolder,
            sizedImage = sizedImage,
            imageViewSpec = imageViewSpec,
        )

        return ProfileImageViewState(
            imageViewState = imageViewState,
            showBorder = showArtistProfileBorder,
            indicator = indicator,
            eventHandler = eventHandler,
        )
    }

    override fun mapWallpaperArtist(
        artist: Artist,
        sizedImage: SizedImage,
        imageViewSpec: ImageViewSpec,
        actionButtons: List<MenuItem>?,
        favorite: FavoriteViewState,
        showFollowIndicator: Boolean,
        followState: FollowState?,
        toggleFollowEventHandler: ViewEventHandler,
        navigateToArtistEventHandler: ViewEventHandler,
        animateFollowIndicator: Boolean,
        followAnimStartedEventHandler: ViewEventHandler,
    ) = WallpaperArtistViewState.Data(
        name = TextStyleSubheadingActive(artist.name, textAlign = TextAlign.Start, maxLines = 2),
        nameEventHandler = navigateToArtistEventHandler,
        profileImage = mapProfileImage(
            profileImageMediaHolder = artist.profileImageMediaHolder,
            sizedImage = sizedImage,
            imageViewSpec = imageViewSpec,
            eventHandler = toggleFollowEventHandler,
            showFollowIndicator = true,
            followIndicatorOutlineColorToken = ColorToken.ThemeBackground,
            followState = followState,
            animateFollowIndicator = animateFollowIndicator,
            followAnimStartedEventHandler = followAnimStartedEventHandler,
        ),
        actionButtons = actionButtons,
    )

    fun mapProfileCurator(
        viewId: ViewId,
        profileImageMediaHolder: MediaHolder,
        titleTwoLines: String,
        viewEventHandler: ViewEventHandler,
    ): ProfileCuratorViewState {
        return ProfileCuratorViewState(
            viewSpec = viewSpecFactory.curatorViewSpec,
            viewId = viewId,
            backgroundColor = ColorToken.ThemeSurface,
            name = TextStyleCallToAction(
                titleTwoLines,
                textAlign = TextAlign.Center,
            ),
            profileImage = mapProfileImage(
                profileImageMediaHolder = profileImageMediaHolder,
                sizedImage = SizedImage.ArtistMedium,
                imageViewSpec = imageViewSpecFactory.profileCuratorImageViewSpec,
                eventHandler = viewEventHandler,
                showFollowIndicator = false,
                followIndicatorOutlineColorToken = null,
                followState = null,
                showArtistProfileBorder = true,
                animateFollowIndicator = false,
                followAnimStartedEventHandler = null,
            ),
            profileShadowImage = imageRepository.profileCuratorShadow,
            eventHandler = viewEventHandler,
        )
    }

    override fun mapProfileCurator(
        curator: Curator,
        viewEventHandler: ViewEventHandler,
    ): ProfileCuratorViewState {
        return mapProfileCurator(
            viewId = viewIdMapper.mapProfileCuratorViewId(curator.id),
            profileImageMediaHolder = curator.profileImageMediaHolder,
            titleTwoLines = curator.titleTwoLines,
            viewEventHandler = viewEventHandler,
        )
    }

    override fun mapProfileCurator(
        artistState: ArtistState,
        viewEventHandler: ViewEventHandler,
    ): ProfileCuratorViewState {
        return mapProfileCurator(
            curator = artistState.artist,
            viewEventHandler = viewEventHandler,
        )
    }

    override fun mapProfileCurator(
        folderState: FolderState,
        viewEventHandler: ViewEventHandler,
    ): ProfileCuratorViewState {
        return mapProfileCurator(
            curator = folderState.folder,
            viewEventHandler = viewEventHandler,
        )
    }

    fun mapSocialLink(
        url: String?,
        image: Image,
        eventSink: ViewEventSink,
    ): MenuItem? =
        if (url == null) {
            null
        } else {
            val eventHandler = ViewEventHandler.Event(
                eventSink = eventSink,
                event = SocialLinkViewEvent(url),
            )

            val size = viewSpecArbitrator.socialLinkButtonItemSize
            val menuItem = MenuItemIcon(
                icon = image,
                height = DpOptional(size),
                width = DpOptional(size),
                onClick = null,
            )

            MenuItemContainer(
                menuItem = menuItem,
                menuItemAlignment = Alignment.Center,
                width = DpOptional(viewSpecArbitrator.socialLinkButtonItemSize),
                height = DpOptional(viewSpecArbitrator.socialLinkButtonItemSize),
                padding = viewSpecArbitrator.socialLinkButtonPadding,
                onClick = eventHandler,
            )
        }

    fun mapSocialInstagram(url: String?, eventSink: ViewEventSink): MenuItem? =
        mapSocialLink(url, imageRepository.socialInstagram, eventSink)
    fun mapSocialTwitter(url: String?, eventSink: ViewEventSink): MenuItem? =
        mapSocialLink(url, imageRepository.socialTwitter, eventSink)

    override fun mapSocialLinks(
        eventSink: ViewEventSink,
        socialLinks: SocialLinks,
    ): SocialLinksViewState? {
        return listOfNotNull(
            mapSocialInstagram(socialLinks.instagram, eventSink),
            mapSocialTwitter(socialLinks.twitter, eventSink),
//            mapSocialYouTube(socialLinks.youTube, eventSink),
//            mapSocialTiktok(socialLinks.tikTok, eventSink),
//            mapSocialSnapchat(socialLinks.snapchat, eventSink),
//            mapSocialTwitch(socialLinks.twitch, eventSink),
//            mapSocialFacebook(socialLinks.facebook, eventSink),
//            mapSocialDiscord(socialLinks.discord, eventSink),
//            mapSocialLinks(socialLinks.links, eventSink),
//            mapSocialWebsite(socialLinks.website, eventSink),
//            mapSocialShop(socialLinks.shop, eventSink),
        ).let {
            if (it.isEmpty()) {
                null
            } else {
                SocialLinksViewState(
                    socialLinks = it,
                    height = viewSpecArbitrator.socialLinkButtonItemSize,
                )
            }
        }
    }

    override fun mapFeedAd(inlineAdItem: InlineAdItem, fallbackAdViewState: AdViewState): FeedAdViewState {
        return FeedAdViewState(inlineAdItem, fallbackAdViewState)
    }

    override fun mapPlusIndicator(
        indicatorSize: Dp,
        indicatorOnBackground: Boolean,
        viewEventHandler: ViewEventHandler?,
    ) = PlusIndicatorViewState(
        image = if (indicatorOnBackground) {
            imageRepository.plusIndicatorOnBackground
        } else {
            imageRepository.plusIndicator
        },
        width = indicatorSize,
        height = indicatorSize,
        viewEventHandler = viewEventHandler,
    )

    private fun mapExhibit(
        id: Id?,
        imageViewState: ImageViewState,
        label: String?,
        additionalLabel: String? = null,
    ) = ExhibitViewState(
        imageViewState = imageViewState,
        viewId = id?.let { viewIdMapper.mapExhibitViewId(it) },
        shapeSpec = shapeSpecFactory.wallpaperPreviewShapeSpecCarouselHighlight,
        label = label?.let { TextExhibit(it) },
        additionalLabel = additionalLabel?.let { TextStyleSubheadingActive(it, textAlign = TextAlign.Center) },
        bottomShadowImage = ImageViewState(
            image = imageRepository.waterfallGradientBlack,
            viewSpec = ImageViewSpec(
                width = viewSpecArbitrator.windowWidth,
                height = viewSpecArbitrator.carouselHeight * 2/3,
                shapeSpec = null,
                contentScale = ViewContentScale.FillBounds,
            ),
            imageSize = null,
        )
    )

    private fun mapCarouselPage(
        highlight: CollectionHighlight,
    ): CarouselPageViewState {
        val featureBannerImageMediaHolder = highlight.category.featureBannerImageMediaHolder
        val imageViewState = if (featureBannerImageMediaHolder != null) {
            contentMediaRepository.getImageViewState(
                mediaHolder = featureBannerImageMediaHolder,
                sizedImage = SizedImage.Exhibit,
                imageViewSpec = imageViewSpecFactory.carouselHighlightImageViewSpec,
            )
        } else {
            mapWallpaperImage(
                highlight.previewWallpaper,
                viewSpec = viewSpecFactory.wallpaperPreviewViewSpecCarouselHighlight,
            )
        }

        return CarouselPageViewState(
            view = View(
                viewState = mapExhibit(highlight.collectionId, imageViewState, highlight.label),
                viewSpec = MaxFeedWidthNoPaddingViewSpec
            ),
            onClick = viewEventFactory.createNavigateToScreen(highlight.collectionId),
        )
    }

    private fun mapCarouselPage(
        highlight: WallpaperHighlight,
    ): CarouselPageViewState {
        val wallpaper = highlight.wallpaper
        val wallpaperPreview = mapWallpaperImage(
            wallpaper,
            viewSpec = viewSpecFactory.wallpaperPreviewViewSpecCarouselHighlight,
        )
        return CarouselPageViewState(
            view = View(
                viewState = mapExhibit(highlight.wallpaper.id, wallpaperPreview, highlight.label),
                viewSpec = MaxFeedWidthNoPaddingViewSpec
            ),
            onClick = viewEventFactory.createNavigateToScreen(highlight.wallpaper.id),
        )
    }

    private fun mapCarouselPage(
        highlight: FolderHighlight,
    ): CarouselPageViewState {
        val imageViewState = contentMediaRepository.getImageViewState(
            mediaHolder = highlight.folder.featureBannerImageMediaHolder,
            sizedImage = SizedImage.Exhibit,
            imageViewSpec = imageViewSpecFactory.carouselHighlightImageViewSpec,
        )

        return CarouselPageViewState(
            view = View(
                viewState = mapExhibit(highlight.folder.id, imageViewState, highlight.label),
                viewSpec = MaxFeedWidthNoPaddingViewSpec
            ),
            onClick = viewEventFactory.createNavigateToScreen(highlight.folder.id),
        )
    }

    private fun mapCarouselPage(
        highlight: ArtistHighlight,
    ): CarouselPageViewState {
        val artist = highlight.artist
        val featureBannerImageMediaHolder = artist.featureBannerImageMediaHolder
        val (mediaHolder, sizedImage) = if (featureBannerImageMediaHolder != null) {
            featureBannerImageMediaHolder to SizedImage.Exhibit
        } else {
            artist.profileImageMediaHolder to SizedImage.ArtistMedium
        }

        val imageViewState = contentMediaRepository.getImageViewState(
            mediaHolder = mediaHolder,
            sizedImage = sizedImage,
            imageViewSpec = imageViewSpecFactory.carouselHighlightImageViewSpec,
        )

        return CarouselPageViewState(
            view = View(
                viewState = mapExhibit(artist.id, imageViewState, highlight.label, additionalLabel = artist.name),
                viewSpec = MaxFeedWidthNoPaddingViewSpec
            ),
            onClick = viewEventFactory.createNavigateToScreen(highlight.artist.id),
        )
    }

    private fun mapCarouselPage(
        highlight: PlusHighlight,
    ): CarouselPageViewState {

        val imageViewState = ImageViewState(
            image = highlight.upgradePlusImage,
            imageSize = null,
            viewSpec = imageViewSpecFactory.carouselHighlightImageViewSpec,
        )

        return CarouselPageViewState(
            view = View(
                viewState = mapExhibit(id = null, imageViewState, highlight.label),
                viewSpec = MaxFeedWidthNoPaddingViewSpec
            ),
            onClick = viewEventFactory.createNavigateToPaywall(subscriptionPlan = SubscriptionPlan.PlusUnlimited),
        )
    }

    override fun mapCarouselPage(
        highlight: Highlight,
    ): CarouselPageViewState {
        return when (highlight) {
            is ArtistHighlight -> mapCarouselPage(highlight)
            is CollectionHighlight -> mapCarouselPage(highlight)
            is FolderHighlight -> mapCarouselPage(highlight)
            is PlusHighlight -> mapCarouselPage(highlight)
            SignInHighlight -> TODO()
            is WallpaperHighlight -> mapCarouselPage(highlight)
        }
    }

    override fun mapCarousel(
        highlights: Highlights,
    ): CarouselViewState {
        requireIsReady()

        val pages = highlights.highlights.map {
            mapCarouselPage(it)
        }
        return CarouselViewState(
            pages = pages,
            initialPage = highlights.startIndex,
        )
    }

    override fun mapFirstRunCarouselBackgrounds(wallpapers: List<Wallpaper>): List<ImageViewState> {
        requireIsReady()

        return wallpapers.map {
            mapWallpaperImage(
                it,
                // This is the correct value, but was changed to work with the few images available
                // in the open source release.
                // viewSpec = viewSpecFactory.wallpaperPreviewViewSpecFullScreenBackground,
                viewSpec = viewSpecFactory.wallpaperPreviewViewSpecFeedSingle,
            )
        }
    }

    override fun mapFollowIndicator(visible: Boolean): FollowIndicatorViewState {
        return FollowIndicatorViewState(
            visible = visible,
            icon = imageRepository.onboardingCheck,
            background = imageRepository.onboardingSelection,
        )
    }
}
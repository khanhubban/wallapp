package wallapp.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.content.state.account.AccountOverviewViewSpec
import wallapp.content.state.ad.AdViewSpec
import wallapp.content.state.artist.ArtistPreviewViewSpec
import wallapp.content.state.artist.ArtistPreviewViewSpecOnboarding
import wallapp.content.state.artist.ArtistToolbarViewSpec
import wallapp.content.state.artist.ArtistViewSpec
import wallapp.content.state.carousel.CarouselViewSpec
import wallapp.content.state.collection.CollectionActionButtonViewSpec
import wallapp.content.state.collection.CollectionActionButtonViewSpec.BuyCollectionActionButtonViewSpec
import wallapp.content.state.collection.CollectionActionButtonViewSpec.GetCollectionActionButtonViewSpec
import wallapp.content.state.collection.CollectionActionViewSpec
import wallapp.content.state.collection.CollectionPreviewViewSpec
import wallapp.content.state.collection.CollectionToolbarViewSpec
import wallapp.content.state.error.ErrorViewSpec
import wallapp.content.state.error.rewardad.ErrorRewardAdViewSpec
import wallapp.content.state.explore.ExploreHeaderViewSpec
import wallapp.content.state.favorite.FavoriteViewSpec
import wallapp.content.state.feed.FeedContentPreviewViewSpec
import wallapp.content.state.folder.FolderPreviewViewSpec
import wallapp.content.state.folder.FolderToolbarViewSpec
import wallapp.content.state.folder.FolderViewSpec
import wallapp.content.state.home.HomeHeaderViewSpec
import wallapp.content.state.home.HomeOnboardingHeaderViewSpec
import wallapp.content.state.home.HomeTopBarViewSpec
import wallapp.content.state.profile.ProfileCuratorViewSpec
import wallapp.content.state.profile.ProfileHeaderViewSpec
import wallapp.content.state.profile.ProfileImageIndicatorViewSpec
import wallapp.content.state.search.SearchInputViewSpec
import wallapp.content.state.search.SearchResultsHeaderViewSpec
import wallapp.content.state.settings.SettingViewSpec.SettingItemPreviewRowViewSpec
import wallapp.content.state.settings.SettingViewSpec.SettingItemPreviewViewSpec
import wallapp.content.state.signin.SignInButtonViewSpec
import wallapp.content.state.wallpaper.WallpaperDetailViewSpec
import wallapp.content.state.wallpaper.WallpaperPreviewViewSpec
import wallapp.content.state.wallpaper.WallpaperShowcaseViewSpec
import wallapp.content.state.wallpaper.WallpaperSingleActionViewSpec
import wallapp.image.ImageViewSpecFactory
import wallapp.image.ImageViewSpecFactoryPreset
import wallapp.image.sized.SizedImage
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.bottomsheet.BottomSheetDescriptor
import wallapp.pixel.feed.FeedViewSpec
import wallapp.pixel.feed.FeedViewViewSpec
import wallapp.pixel.feed.MaxFeedWidthViewSpec
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.message.MessageBarViewSpec
import wallapp.pixel.navigationbar.NavigationBarViewSpec
import wallapp.pixel.selection.SelectionGroupViewSpec
import wallapp.pixel.tab.TabsViewSpec
import wallapp.pixel.util.DpOptional
import wallapp.unit.Padding
import wallapp.view.shape.ShapeSpecFactory
import wallapp.view.shape.ShapeSpecFactoryDefault

class ViewSpecFactoryDefault(
    private val viewSpecArbitrator: ViewSpecArbitrator,
    private val viewAlignmentFactory: ViewAlignmentFactory,
    private val imageViewSpecFactory: ImageViewSpecFactory,
    private val shapeSpecFactory: ShapeSpecFactory,
) : ViewSpecFactory {

    private val statusBarHeight: Dp
        get() = viewSpecArbitrator.statusBarHeight
    private val deviceWidth: Dp
        get() = viewSpecArbitrator.windowWidth
    private val deviceHeight: Dp
        get() = viewSpecArbitrator.windowHeight
    private val paddingDefault: Dp
        get() = viewSpecArbitrator.paddingDefault
    private val paddingSmall: Dp
        get() = viewSpecArbitrator.paddingSmall

    private val feedItemWideSpanWidth: Dp
        get() = viewSpecArbitrator.feedItemWideSpanWidth
    private val feedItemWideSpanHeight: Dp
        get() = viewSpecArbitrator.feedItemWideSpanHeight

    private val canUseStaggeredGrid: Boolean
        get() = true

    override val feedGridViewSpec: FeedViewSpec
        get() = FeedViewSpec(
            columns = viewSpecArbitrator.feedColumns,
            staggeredGrid = false,
            itemWidth = viewSpecArbitrator.feedItemSingleSpanWidth,
        )
    override val feedGridNonStretchedViewSpec: FeedViewSpec
        get() = FeedViewSpec(
            columns = viewSpecArbitrator.feedColumns,
            staggeredGrid = false,
            stretchDanglingNonMaxSpanItems = false,
            itemWidth = viewSpecArbitrator.feedItemSingleSpanWidth,
        )

    override val feedStaggeredViewSpec: FeedViewSpec
        get() = FeedViewSpec(
            columns = viewSpecArbitrator.feedColumns,
            staggeredGrid = canUseStaggeredGrid,
            itemWidth = viewSpecArbitrator.feedItemSingleSpanWidth,
        )
    override val feedViewMaxWidthViewSpec: FeedViewViewSpec
        get() = MaxFeedWidthViewSpec

    override val navigationBarViewSpec: NavigationBarViewSpec
        get() = NavigationBarViewSpec(
            navBarItemsHeight = 72.dp,
            bottomNavBarItemsYOffset = viewSpecArbitrator.bottomNavBarItemsYOffset,
            edgePadding = viewSpecArbitrator.bottomNavBarHorizontalSpacing,
            maxOffset = 120.dp,
        )

    override val feedContentPreviewViewSpecDefault: FeedContentPreviewViewSpec
        get() = FeedContentPreviewViewSpec(
            shapeSpec = shapeSpecFactory.feedContentPreviewColumnShapeSpec,
            width = viewSpecArbitrator.feedItemSingleSpanWidth,
            height = viewSpecArbitrator.feedItemSingleSpanHeight,
        )
    override val feedContentPreviewViewSpecSquare: FeedContentPreviewViewSpec
        get() = FeedContentPreviewViewSpec(
            shapeSpec = shapeSpecFactory.feedContentPreviewColumnShapeSpec,
            width = viewSpecArbitrator.feedItemSingleSpanWidth,
            height = viewSpecArbitrator.feedItemSingleSpanWidth,
        )
    override val feedContentPreviewViewSpecWide: FeedContentPreviewViewSpec
        get() = FeedContentPreviewViewSpec(
            width = feedItemWideSpanWidth,
            height = feedItemWideSpanHeight,
            shapeSpec = shapeSpecFactory.feedContentPreviewWideShapeSpec,
            useMaxItemSpan = true,
        )
    override val wallpaperPreviewViewSpecArtistSelectionBackgroundSingle: WallpaperPreviewViewSpec
        get() = WallpaperPreviewViewSpec.Default(
            imageViewSpec = imageViewSpecFactory.artistSelectionBackgroundImageViewSpec,
            sizedImage = SizedImage.WallpaperFeedSingle,
        )
    override val wallpaperPreviewViewSpecArtistSelectionBackgroundTrack: WallpaperPreviewViewSpec
        get() = WallpaperPreviewViewSpec.Default(
            imageViewSpec = imageViewSpecFactory.artistSelectionBackgroundImageViewSpec,
            sizedImage = SizedImage.WallpaperFeedTrack,
        )

    override val wallpaperPreviewViewSpecFullScreenBackground: WallpaperPreviewViewSpec
        get() = WallpaperPreviewViewSpec.Default(
            imageViewSpec = imageViewSpecFactory.fullScreenImageViewSpec,
            sizedImage = SizedImage.FullScreen,
        )

    override val wallpaperPreviewViewSpecCarouselHighlight: WallpaperPreviewViewSpec
        get() = WallpaperPreviewViewSpec.Default(
            imageViewSpec = imageViewSpecFactory.carouselHighlightImageViewSpec,
            sizedImage = SizedImage.Showcase,
        )

    override val wallpaperPreviewViewSpecFeedSingle: WallpaperPreviewViewSpec
        get() = WallpaperPreviewViewSpec.WithFooter(
            imageViewSpec = imageViewSpecFactory.wallpaperFeedSingleImageViewSpec,
            sizedImage = SizedImage.WallpaperFeedSingle,
            footerHeight = viewSpecArbitrator.contentFooterHeight,
            shapeSpec = shapeSpecFactory.wallpaperPreviewShapeSpecFeedSingle,
        )
    override val wallpaperPreviewViewSpecFeedTrackNoFooter: WallpaperPreviewViewSpec
        get() = WallpaperPreviewViewSpec.Default(
            imageViewSpec = imageViewSpecFactory.wallpaperFeedTrackImageViewSpec,
            sizedImage = SizedImage.WallpaperFeedTrack,
            shapeSpec = shapeSpecFactory.wallpaperPreviewShapeSpecFeedSingle,
        )
    override val wallpaperPreviewViewSpecFeedTrackWithFooter: WallpaperPreviewViewSpec
        get() = WallpaperPreviewViewSpec.WithFooter(
            imageViewSpec = imageViewSpecFactory.wallpaperFeedTrackImageViewSpec,
            sizedImage = SizedImage.WallpaperFeedTrack,
            footerHeight = viewSpecArbitrator.contentFooterHeight,
            shapeSpec = shapeSpecFactory.wallpaperPreviewShapeSpecFeedSingle,
        )

    override val wallpaperPreviewViewSpecWallpaperShowcase: WallpaperPreviewViewSpec
        get() = WallpaperPreviewViewSpec.Default(
            imageViewSpec = imageViewSpecFactory.wallpaperShowcaseImageViewSpec,
            sizedImage = SizedImage.Showcase,
        )

    override fun wallpaperDetailViewSpec(itemDetailCount: Int): WallpaperDetailViewSpec {
        return WallpaperDetailViewSpec(
            height = viewSpecArbitrator.wallpaperDetailViewHeight(itemCount = itemDetailCount),
            itemHeight = viewSpecArbitrator.wallpaperDetailItemHeight,
            itemImageSize = viewSpecArbitrator.wallpaperDetailItemImageSize,
            itemVerticalSpacerHeight = viewSpecArbitrator.wallpaperDetailItemVerticalSpacerHeight,
        )
    }

    override val wallpaperSingleActionViewSpecTwoOptions: WallpaperSingleActionViewSpec
        get() = WallpaperSingleActionViewSpec(
            height = viewSpecArbitrator.bottomSheetHeightTwoOptions,
            horizontalPadding = paddingDefault * 2,
        )
    override val wallpaperSingleActionViewSpecThreeOptions: WallpaperSingleActionViewSpec
        get() = WallpaperSingleActionViewSpec(
            height = viewSpecArbitrator.bottomSheetHeightThreeOptions,
            horizontalPadding = paddingDefault * 2,
        )

    private fun ImageViewSpec.mapToWallpaperPreviewViewSpec(
        sizedImage: SizedImage,
    ): WallpaperPreviewViewSpec =
        WallpaperPreviewViewSpec.Default(
            imageViewSpec = this,
            sizedImage = sizedImage,
        )

    override fun wallpaperPreviewViewSpecCollectionPreviewFull(index: Int): WallpaperPreviewViewSpec {
        val sizedImage = when (index) {
            0 -> SizedImage.WallpaperCollectionLargeLayer0
            1 -> SizedImage.WallpaperCollectionLargeLayer1
            2 -> SizedImage.WallpaperCollectionLargeLayer2
            else -> throw IllegalArgumentException("Invalid index: $index")
        }
        return imageViewSpecFactory.collectionPreviewFullImageViewSpec(index)
            .mapToWallpaperPreviewViewSpec(sizedImage)
    }

    override fun wallpaperPreviewViewSpecCollectionPreviewSmall(index: Int): WallpaperPreviewViewSpec {
        val sizedImage = when (index) {
            0 -> SizedImage.WallpaperCollectionSmallLayer0
            1 -> SizedImage.WallpaperCollectionSmallLayer1
            2 -> SizedImage.WallpaperCollectionSmallLayer2
            else -> throw IllegalArgumentException("Invalid index: $index")
        }
        return imageViewSpecFactory.collectionPreviewSmallImageViewSpec(index)
            .mapToWallpaperPreviewViewSpec(sizedImage)
    }

    override val animatedToolbarHeroButtonViewSpec: AnimatedViewSpec
        get() = AnimatedViewSpec(
            heightMin = DpOptional(viewSpecArbitrator.heroButtonHeight),
            heightMax = DpOptional(viewSpecArbitrator.heroButtonHeight),
            widthMin = DpOptional(viewSpecArbitrator.collectionToolbarButtonWidthMin),
            widthMax = DpOptional(viewSpecArbitrator.collectionToolbarButtonWidthMax),
            paddingMin = Padding(
                top = viewSpecArbitrator.collectionToolbarButtonPaddingTopMin,
                end = viewSpecArbitrator.collectionToolbarButtonPaddingEnd,
                bottom = viewSpecArbitrator.collectionToolbarButtonPaddingBottomMin,
            ),
            paddingMax = Padding(
                top = viewSpecArbitrator.collectionToolbarButtonMaxTop,
                end = viewSpecArbitrator.collectionToolbarButtonPaddingEnd,
            ),
            alignmentWhenCollapsed = Alignment.TopEnd,
            alignmentWhenExpanded = Alignment.TopEnd,
        )

    override val activeDownloadStatusViewSpec: MessageBarViewSpec
        get() = MessageBarViewSpec(maxHeight = 48.dp)

    override val activeDownloadStatusHideViewSpec: MessageBarViewSpec
        get() = MessageBarViewSpec(maxHeight = 0.dp)

    override val collectionActionViewSpecOneOption: CollectionActionViewSpec
        get() = CollectionActionViewSpec(
            height = viewSpecArbitrator.bottomSheetHeightOneOption,
            horizontalPadding = paddingDefault * 2,
        )
    override fun collectionActionViewSpecTwoOptions(
        showInfoNotice: Boolean,
    ): CollectionActionViewSpec {
        val height = if (showInfoNotice) {
            viewSpecArbitrator.bottomSheetHeightTwoOptions + viewSpecArbitrator.adFreeCollectionLockedInfoHeight
        } else {
            viewSpecArbitrator.bottomSheetHeightTwoOptions
        }
        return CollectionActionViewSpec(
            height = height,
            horizontalPadding = paddingDefault * 2,
        )
    }

    override val collectionPreviewFullViewSpec: CollectionPreviewViewSpec
        get() = CollectionPreviewViewSpec(
            width = viewSpecArbitrator.collectionPreviewFullWidth,
            height = viewSpecArbitrator.collectionPreviewFullHeight,
            footerHeight = DpOptional(viewSpecArbitrator.contentFooterHeight),
            useMaxItemSpan = true,
        )

    override val collectionPreviewSmallViewSpec: CollectionPreviewViewSpec
        get() = CollectionPreviewViewSpec(
            width = viewSpecArbitrator.collectionPreviewSmallWidth,
            height = viewSpecArbitrator.collectionPreviewSmallHeight,
            footerHeight = null,
            useMaxItemSpan = false,
        )

    override val artistPreviewViewSpecDefault: FeedViewViewSpec
        get() = ArtistPreviewViewSpec.Default
    override val artistPreviewViewSpecMaxSpan: FeedViewViewSpec
        get() = ArtistPreviewViewSpec.MaxSpan
    override val artistPreviewViewSpec: FeedViewViewSpec
        get() = artistPreviewViewSpecMaxSpan
    override val artistPreviewViewSpecOnboarding: ArtistPreviewViewSpecOnboarding
        get() = ArtistPreviewViewSpecOnboarding(
            width = viewSpecArbitrator.artistOnboardingWidth,
            height = viewSpecArbitrator.artistOnboardingHeight,
            profileShadowImageSize = viewSpecArbitrator.artistOnboardingProfileImageShadowSize,
            followIconSize = viewSpecArbitrator.feedItemSingleSpanWidth / 4.5f,
        )

    private val artistProfileAnimatedViewSpec: AnimatedViewSpec
        get() {
            val sizeMin = viewSpecArbitrator.artistToolbarProfileImageMinSize
            val sizeMax = viewSpecArbitrator.artistToolbarProfileImageMaxSize

            return AnimatedViewSpec(
                heightMin = DpOptional(sizeMin),
                heightMax = DpOptional(sizeMax),
                widthMin = DpOptional(sizeMin),
                widthMax = DpOptional(sizeMax),
                paddingMin = Padding(
                    start = 0.dp,
                    top = viewSpecArbitrator.artistToolbarProfileImageMinTopPadding,
                    bottom = viewSpecArbitrator.artistToolbarProfileImageMinBottomPadding,
                ),
                paddingMax = Padding(
                    start = 0.dp,
                    top = viewSpecArbitrator.artistToolbarProfileImageMaxTop,
                ),
                alignmentWhenCollapsed = Alignment.TopCenter,
                alignmentWhenExpanded = Alignment.TopCenter,
            )
        }

    private val artistToolbarSeparatorViewSpec: AnimatedViewSpec
        get() = AnimatedViewSpec(
            heightMin = DpOptional(viewSpecArbitrator.artistToolbarMinHeight),
            heightMax = DpOptional(viewSpecArbitrator.artistToolbarMaxHeight),
        )

    private val artistToolbarViewSpec: ArtistToolbarViewSpec
        get() = ArtistToolbarViewSpec(
            paddingDefault = paddingDefault,
            minToolbarHeight = viewSpecArbitrator.artistToolbarMinHeight,
            maxToolbarHeight = viewSpecArbitrator.artistToolbarMaxHeight,
            profileAnimatedViewSpec = artistProfileAnimatedViewSpec,
            separatorViewSpec = artistToolbarSeparatorViewSpec,
            titleTop = viewSpecArbitrator.artistToolbarTitleTop,
            titleHeight = viewSpecArbitrator.artistToolbarTitleHeight,
            socialLinksTop = viewSpecArbitrator.artistToolbarSocialLinksTop,
            socialLinksHeight = viewSpecArbitrator.artistToolbarSocialLinksHeight,
            separatorHeight = viewSpecArbitrator.artistToolbarSeparatorHeight,
        )

    private val collectionLockedProfileAnimatedViewSpec: AnimatedViewSpec
        get() = collectionProfileAnimatedViewSpec

    private val collectionProfileAnimatedViewSpec: AnimatedViewSpec
        get() {
            val sizeMin = viewSpecArbitrator.collectionToolbarProfileImageMinSize
            val sizeMax = viewSpecArbitrator.collectionToolbarProfileImageMaxSize

            return AnimatedViewSpec(
                heightMin = DpOptional(sizeMin),
                heightMax = DpOptional(sizeMax),
                widthMin = DpOptional(sizeMin),
                widthMax = DpOptional(sizeMax),
                paddingMin = Padding(
                    start = viewSpecArbitrator.toolbarXlHeight - (paddingDefault * 1.5f),
                    top = viewSpecArbitrator.collectionToolbarProfileImageMinTopPadding,
                    bottom = viewSpecArbitrator.collectionToolbarProfileImageMinBottomPadding,
                ),
                paddingMax = Padding(
                    start = 0.dp,
                    top = viewSpecArbitrator.collectionToolbarProfileImageMaxTop,
                ),
                alignmentWhenCollapsed = Alignment.TopStart,
                alignmentWhenExpanded = Alignment.TopCenter,
            )
        }

    override fun collectionToolbarLockedViewSpec(showCollectionLockedInfo: Boolean): CollectionToolbarViewSpec.Locked {
        return CollectionToolbarViewSpec.Locked(
            minToolbarHeight = viewSpecArbitrator.collectionToolbarLockedMinHeight,
            maxToolbarHeight = if (showCollectionLockedInfo) {
                viewSpecArbitrator.collectionToolbarLockedWithInfoMaxHeight
            } else {
                viewSpecArbitrator.collectionToolbarLockedMaxHeight
            },
            paddingDefault = paddingDefault,
            profileAnimatedViewSpec = collectionLockedProfileAnimatedViewSpec,
            artistNamePaddingTop = viewSpecArbitrator.collectionToolbarArtistTitleTop,
            artistNameHeight = viewSpecArbitrator.collectionToolbarArtistTitleHeight,
            collectionNamePaddingTop = viewSpecArbitrator.collectionToolbarCollectionTitleTop,
            collectionNameHeight = viewSpecArbitrator.collectionToolbarCollectionTitleHeight,
            adFreeCollectionLockedInfoTop = viewSpecArbitrator.collectionToolbarAdFreeCollectionLockedInfoTop,
            adFreeCollectionLockedInfoHeight = viewSpecArbitrator.collectionToolbarAdFreeCollectionLockedInfoHeight,
        )
    }

    override val collectionToolbarUnlockedViewSpec: CollectionToolbarViewSpec.Unlocked
        get() = CollectionToolbarViewSpec.Unlocked(
            minToolbarHeight = viewSpecArbitrator.collectionToolbarUnlockedMinHeight,
            maxToolbarHeight = viewSpecArbitrator.collectionToolbarUnlockedMaxHeight,
            paddingDefault = paddingDefault,
            profileAnimatedViewSpec = collectionProfileAnimatedViewSpec,
            artistNamePaddingTop = viewSpecArbitrator.collectionToolbarArtistTitleTop,
            artistNameHeight = viewSpecArbitrator.collectionToolbarArtistTitleHeight,
            collectionNamePaddingTop = viewSpecArbitrator.collectionToolbarCollectionTitleTop,
            collectionNameHeight = viewSpecArbitrator.collectionToolbarCollectionTitleHeight,
        )
    override val collectionActionButtonViewSpecBuyCollection: BuyCollectionActionButtonViewSpec
        get() = BuyCollectionActionButtonViewSpec(
            animatedViewSpec = animatedToolbarHeroButtonViewSpec,
            item1PaddingStartMin = viewSpecArbitrator.collectionToolbarBuyButtonItem1PaddingStartMin,
            item1PaddingStartMax = viewSpecArbitrator.collectionToolbarBuyButtonItem1PaddingStartMax,
            item3PaddingEnd = viewSpecArbitrator.collectionToolbarBuyButtonItem3PaddingEnd,
        )
    override val collectionActionButtonViewSpecDownloadProgress: CollectionActionButtonViewSpec.DownloadProgressButtonViewSpec
        get() = CollectionActionButtonViewSpec.DownloadProgressButtonViewSpec(
            animatedViewSpec = animatedToolbarHeroButtonViewSpec,
            minIconPaddingStart = viewSpecArbitrator.collectionToolbarGetButtonIconPaddingStartMin,
            maxLabelPaddingStart = viewSpecArbitrator.collectionDownloadProgressButtonLabelPaddingHorizontal,
            countLabelPaddingEndMin = viewSpecArbitrator.collectionToolbarGetButtonIconPaddingStartMin,
            countLabelPaddingEndMax = viewSpecArbitrator.collectionDownloadProgressButtonLabelPaddingHorizontal,
        )
    override val collectionActionButtonViewSpecGetCollection: GetCollectionActionButtonViewSpec
        get() = GetCollectionActionButtonViewSpec(
            animatedToolbarHeroButtonViewSpec,
            iconPaddingStartMin = viewSpecArbitrator.collectionToolbarGetButtonIconPaddingStartMin,
            iconPaddingStartMax = viewSpecArbitrator.collectionToolbarGetButtonIconPaddingStartMax,
            labelPaddingStartMin = viewSpecArbitrator.collectionToolbarGetButtonLabelPaddingStartMin,
            labelPaddingStartMax = viewSpecArbitrator.collectionToolbarGetButtonLabelPaddingStartMax,
        )

    override fun collectionFeedViewSpec(
        toolbarExpanded: Boolean,
        showAdFreeCollectionLockedInfo: Boolean,
    ): FeedViewSpec {
        return this.feedGridNonStretchedViewSpec.copy(
            offsetYAnimated = if (toolbarExpanded) {
                val offset = if (showAdFreeCollectionLockedInfo) {
                    viewSpecArbitrator.collectionToolbarLockedWithInfoMaxHeight
                } else {
                    viewSpecArbitrator.collectionToolbarLockedMaxHeight
                }
                viewSpecArbitrator.statusBarHeight + offset
            } else {
                viewSpecArbitrator.statusBarHeight + viewSpecArbitrator.collectionToolbarLockedMinHeight
            },
        )
    }

    override val artistViewSpec: ArtistViewSpec
        get() = ArtistViewSpec(
            paddingDefault = paddingDefault,
            toolbarViewSpec = artistToolbarViewSpec,
        )

    override val homeOnboardingHeaderViewSpec: HomeOnboardingHeaderViewSpec
        get() = HomeOnboardingHeaderViewSpec(
            height = 180.dp,
            verticalPadding = paddingDefault,
            itemSpacing = paddingDefault / 2,
        )

    override val homeTopBarViewSpec: HomeTopBarViewSpec
        get() = HomeTopBarViewSpec(
            height = viewSpecArbitrator.homeTopBarHeight,
            tabContainerHeight = viewSpecArbitrator.indicatorTabContainerHeight,
            profileVerticalPadding = viewSpecArbitrator.homeProfileVerticalPadding,
        )

    override val homeHeaderViewSpec: HomeHeaderViewSpec
        get() = HomeHeaderViewSpec(
            height = viewSpecArbitrator.homeHeaderHeight,
            profileVerticalPadding = viewSpecArbitrator.homeProfileVerticalPadding,
            separatorHeight = viewSpecArbitrator.separatorHeight,
            separatorPadding = viewSpecArbitrator.separatorPadding,
            filterHeight = viewSpecArbitrator.homeFilterHeight,
        )

    override val homeFilterViewSpec: SelectionGroupViewSpec
        get() = SelectionGroupViewSpec.Grid(
            itemWidth = viewSpecArbitrator.homeFilterWidth,
            itemHeight = viewSpecArbitrator.homeFilterHeight,
            horizontalItemSpacing = viewSpecArbitrator.paddingDefault,
        )

    override val wallpaperShowcaseViewSpec: WallpaperShowcaseViewSpec
        get() = WallpaperShowcaseViewSpec(
            topControlButtonsVerticalOffset = viewSpecArbitrator.wallpaperShowcaseTopControlButtonsVerticalOffset,
            wallpaperPreviewShapeSpec = shapeSpecFactory.wallpaperPreviewShapeSpecWallpaperShowcase,
            bottomPadding = viewSpecArbitrator.navigationBarHeight,
            indicatorContainerHeight = viewSpecArbitrator.pageIndicatorContainerHeight,
        )

    override val favoriteViewSpec: FavoriteViewSpec
        get() = FavoriteViewSpec(size = 30.dp)

    override val highlightCarouselViewSpec: CarouselViewSpec
        get() = CarouselViewSpec(
            height = viewSpecArbitrator.carouselHeight,
            extraHeightForScroll = viewSpecArbitrator.carouselExtraHeightForScroll,
            pageWidth = viewSpecArbitrator.carouselPageWidth,
            pageSpacing = viewSpecArbitrator.carouselPageSpacing,
            edgeButtonWidth = viewSpecArbitrator.carouselEdgeButtonWidth,
            centerPage = true,
            transformEdgeItems = true,
            applyInfiniteScroll = true,
            indicatorContainerHeight = viewSpecArbitrator.pageIndicatorContainerHeight,
            padding = Padding(bottom = viewSpecArbitrator.carouselVerticalPadding),
        )

    override val searchInputViewSpec: SearchInputViewSpec
        get() = SearchInputViewSpec(
            topPadding = viewSpecArbitrator.searchInputTopPadding,
            searchBarHeight = viewSpecArbitrator.searchBarHeight,
            searchFiltersTopPadding = viewSpecArbitrator.searchFiltersTopPadding,
            searchBarHorizontalPadding = viewSpecArbitrator.paddingDefault + viewSpecArbitrator.paddingSmall ,
            filterToggleHeight = viewSpecArbitrator.searchFilterToggleHeight,
        )

    override val searchSelectionGroupViewSpec: SelectionGroupViewSpec
        get() = SelectionGroupViewSpec.Grid(
            itemWidth = 132.dp,
            itemHeight = viewSpecArbitrator.selectionRowHeight,
            horizontalItemSpacing = viewSpecArbitrator.paddingDefault,
        )

    override val searchSelectionButtonGroupViewSpec: SelectionGroupViewSpec
        get() = SelectionGroupViewSpec.Grid(
            itemWidth = 132.dp,
            itemHeight = viewSpecArbitrator.selectionRowHeight,
            containerPaddingValues = PaddingValues(viewSpecArbitrator.paddingDefault / 2),
            horizontalItemSpacing = viewSpecArbitrator.paddingDefault,
            verticalItemSpacing = viewSpecArbitrator.paddingDefault,
        )

    override val searchResultsHeaderViewSpec: SearchResultsHeaderViewSpec
        get() = SearchResultsHeaderViewSpec(
            barTopPadding = viewSpecArbitrator.searchInputTopPadding,
            headerHeight = viewSpecArbitrator.searchResultsHeaderHeight,
            searchBarExternalHorizontalPadding = viewSpecArbitrator.paddingDefault + viewSpecArbitrator.paddingSmall,
            searchBarInternalHorizontalPadding = viewSpecArbitrator.paddingLarge,
            searchBarHeight = viewSpecArbitrator.searchResultsBarHeight,
            searchBarShapeSpec = shapeSpecFactory.searchResultsHeaderShapeSpec,
        )

    override val bottomSheetSpec: BottomSheetDescriptor
        get() = BottomSheetDescriptor(
            peekHeight = 0.dp,
            peekOffset = 0.dp,
            halfExpandedHeight = null,
            expandedOffset = viewSpecArbitrator.statusBarHeight,
        )

    override val exploreHeaderViewSpec: ExploreHeaderViewSpec
        get() = ExploreHeaderViewSpec(
            height = viewSpecArbitrator.toolbarHeight,
            offsetDueToHighlightCarousel = viewSpecArbitrator.carouselHeight,
        )

    override val exploreFeedViewSpec: FeedViewSpec
        get() = feedStaggeredViewSpec.copy(
            shapeSpec = shapeSpecFactory.exploreToolbarShapeSpec,
        )

    override val profileHeaderViewSpec: ProfileHeaderViewSpec
        get() = ProfileHeaderViewSpec(
            padding = Padding(
                start = viewSpecArbitrator.settingsStartPadding,
                end = viewSpecArbitrator.settingsStartPadding,
                top = paddingDefault,
                bottom = paddingDefault,
            ),
            statusBarHeight = statusBarHeight,
            height = viewSpecArbitrator.profileHeaderHeight,
            titleHeight = viewSpecArbitrator.profileHeaderTitleHeight,
            subtitleTopPadding = viewSpecArbitrator.profileHeaderSubtitleTopPadding,
            subtitleHeight = viewSpecArbitrator.profileHeaderSubtitleHeight,
        )

    override val accountOverviewViewSpec: AccountOverviewViewSpec
        get() = AccountOverviewViewSpec(
            padding = Padding(
                horizontal = paddingDefault,
                vertical = paddingDefault * 2,
            ),
            paddingDefault = paddingDefault,
            loadingSize = 80.dp,
            backgroundShapeSpec = shapeSpecFactory.buttonShapeSpecDefault,
        )

    override val signInButtonViewSpec: SignInButtonViewSpec
        get() = SignInButtonViewSpec(
            width = viewSpecArbitrator.signInButtonWidth,
            height = viewSpecArbitrator.signInButtonHeight,
        )

    override val themeSettingViewSpec: SettingItemPreviewViewSpec
        get() = SettingItemPreviewViewSpec(
            width = 96.dp,
            height = 56.dp,
        )

    override val appIconSettingRowViewSpec: SettingItemPreviewRowViewSpec
        get() = SettingItemPreviewRowViewSpec(
            itemSpacing = DpOptional(paddingSmall),
            horizontalPadding = viewSpecArbitrator.settingItemPreviewRowHorizontalPadding,
            verticalPadding = viewSpecArbitrator.settingItemPreviewRowVerticalPadding,
        )
    override val appIconSettingViewSpec: SettingItemPreviewViewSpec
        get() = SettingItemPreviewViewSpec(
            width = viewSpecArbitrator.appIconSettingItemWidth,
            height = viewSpecArbitrator.appIconSettingItemHeight,
        )

    override val errorViewSpec: ErrorViewSpec
        get() = ErrorViewSpec(
            imageSize = 132.dp,
        )

    override fun tabsIndicatorViewSpec(width: Dp?): TabsViewSpec.Indicator =
        TabsViewSpec.Indicator(
            tabHeight = viewSpecArbitrator.indicatorTabHeight,
            tabIndicatorHeight = viewSpecArbitrator.indicatorTabIndicatorHeight,
            tabContainerHeight = viewSpecArbitrator.indicatorTabContainerHeight,
            tabWidth = width?.let { DpOptional(it) },
        )

    override fun tabsPillViewSpec(width: Dp?): TabsViewSpec.Pill =
        TabsViewSpec.Pill(
            containerHeight = viewSpecArbitrator.pillTabContainerHeight,
            tabWidth = width?.let { DpOptional(it) },
            tabHeight = viewSpecArbitrator.pillTabHeight,
        )

    override val errorRewardAdViewSpec: ErrorRewardAdViewSpec
        get() = ErrorRewardAdViewSpec(
            unlockContainerSize = viewSpecArbitrator.unlockWallpaperUnlockContainerSize,
            upgradeContainerSize = viewSpecArbitrator.unlockWallpaperUpgradeContainerSize,
            unlockWallpaperContentPadding = viewSpecArbitrator.paddingLarge,
            spaceAboveUpgradeToUnlock = viewSpecArbitrator.upgradeHighlightHighResHeight.minus(70.dp),
            spaceBetweenUpgradeContent = viewSpecArbitrator.paddingSmall,
            paddingBottom = viewSpecArbitrator.paddingDefault,
        )

    override val presetAdViewSpec: AdViewSpec
        get() = AdViewSpec(
            closeButtonPadding = Padding(
                top = viewSpecArbitrator.adCloseButtonPaddingTop,
                end = viewSpecArbitrator.adCloseButtonPaddingEnd,
            )
        )

    private val folderToolbarViewSpec: FolderToolbarViewSpec
        get() = FolderToolbarViewSpec(
            paddingDefault = paddingDefault,
            minToolbarHeight = viewSpecArbitrator.folderToolbarMinHeight,
            maxToolbarHeight = viewSpecArbitrator.folderToolbarMaxHeight,
            titleTop = viewSpecArbitrator.folderToolbarTitleTop,
            titleHeight = viewSpecArbitrator.folderToolbarTitleHeight,
        )

    override val folderViewSpec: FolderViewSpec
        get() = FolderViewSpec(
            paddingDefault = paddingDefault,
            toolbarViewSpec = folderToolbarViewSpec,
        )

    override val profileImageIndicatorViewSpec: ProfileImageIndicatorViewSpec
        get() = ProfileImageIndicatorViewSpec(
            size = 30.dp,
            borderSize = 4.dp,
        )

    override val curatorViewSpec: ProfileCuratorViewSpec
        get() = ProfileCuratorViewSpec(
            width = viewSpecArbitrator.feedItemSingleSpanWidth,
            height = viewSpecArbitrator.feedItemSingleSpanWidth,
            profileShadowImageSize = viewSpecArbitrator.profileCuratorImageShadowSize,
            containerShapeSpec = shapeSpecFactory.artistOnboardingShapeSpec,
        )

    override val folderPreviewViewSpec: FolderPreviewViewSpec
        get() = FolderPreviewViewSpec(
            width = viewSpecArbitrator.folderPreviewWidth,
            height = viewSpecArbitrator.folderPreviewHeight,
            footerHeight = null,
            useMaxItemSpan = true,
        )

    override val wallpaperPreviewViewSpecFolderPreviewFeedSingle: WallpaperPreviewViewSpec
        get() = WallpaperPreviewViewSpec.Default(
            imageViewSpec = imageViewSpecFactory.folderPreviewWallpaperFeedImageViewSpec,
            sizedImage = SizedImage.WallpaperFeedSingle,
        )
    override val wallpaperPreviewViewSpecFolderPreviewFeedTrack: WallpaperPreviewViewSpec
        get() = WallpaperPreviewViewSpec.Default(
            imageViewSpec = imageViewSpecFactory.folderPreviewWallpaperFeedImageViewSpec,
            sizedImage = SizedImage.WallpaperFeedTrack,
        )
}

fun ViewSpecFactoryDefaultMock(
    viewSpecArbitrator: ViewSpecArbitrator,
    viewAlignmentFactory: ViewAlignmentFactory = ViewAlignmentFactoryDefault(),
    shapeSpecFactory: ShapeSpecFactory = ShapeSpecFactoryDefault(),
    imageViewSpecFactory: ImageViewSpecFactory = ImageViewSpecFactoryPreset(viewSpecArbitrator, viewAlignmentFactory, shapeSpecFactory),
): ViewSpecFactoryDefault = ViewSpecFactoryDefault(
    viewSpecArbitrator = viewSpecArbitrator,
    viewAlignmentFactory = viewAlignmentFactory,
    imageViewSpecFactory = imageViewSpecFactory,
    shapeSpecFactory = shapeSpecFactory,
)
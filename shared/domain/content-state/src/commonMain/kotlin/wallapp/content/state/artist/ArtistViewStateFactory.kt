package wallapp.content.state.artist

import wallapp.content.model.Wallpaper
import wallapp.content.prefetch.ContentPrefetcher
import wallapp.content.state.toolbar.CollapsingToolbarStateWrapper
import wallapp.data.content.ContentResult.ArtistScreenContentResult
import wallapp.image.ImageViewSpecFactory
import wallapp.image.sized.SizedImage
import wallapp.pixel.alert.AlertViewStateOkCancel
import wallapp.pixel.feed.FeedScrollStateWrapper
import wallapp.pixel.feed.FeedState
import wallapp.pixel.feed.FeedViewState
import wallapp.pixel.message.MessageBarViewState
import wallapp.pixel.pager.PagerPersistableStateWrapper
import wallapp.pixel.tab.TabViewState
import wallapp.pixel.tab.TabsViewState
import wallapp.pixel.text.TextStyleSubheadingActive
import wallapp.pixel.toolbar.ToolbarViewState
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewEventSink
import wallapp.resources.string.StringRepository
import wallapp.text.TextTabSelected
import wallapp.text.TextTabUnselected
import wallapp.theme.ColorToken
import wallapp.view.ViewEventFactory
import wallapp.view.ViewFactory
import wallapp.view.ViewSpecArbitrator
import wallapp.view.ViewSpecFactory
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateMapper
import wallapp.view.feed.FeedFormatter
import wallapp.view.menu.MenuItemFactory

class ArtistViewStateFactory(
    private val viewStateFactory: ViewStateFactory,
    private val viewStateMapper: ViewStateMapper,
    private val viewSpecArbitrator: ViewSpecArbitrator,
    private val viewFactory: ViewFactory,
    private val viewSpecFactory: ViewSpecFactory,
    private val imageViewSpecFactory: ImageViewSpecFactory,
    private val feedFormatter: FeedFormatter,
    private val menuItemFactory: MenuItemFactory,
    private val strings: StringRepository,
    private val viewEventFactory: ViewEventFactory,
) {
    private fun List<Wallpaper>.mapWallpaperToView(
        contentPrefetcher: ContentPrefetcher,
        staggeredGrid: Boolean,
        scrollStateWrapper: FeedScrollStateWrapper,
    ): View =
        map {
            viewFactory.createWallpaperFeedPreview(
                wallpaper = it,
                showPlusButton = false,
            )
        }
        .mapToFeedView(contentPrefetcher, staggeredGrid, scrollStateWrapper)

    private fun List<View>.mapToFeedView(
        contentPrefetcher: ContentPrefetcher,
        staggeredRandomGrid: Boolean,
        scrollStateWrapper: FeedScrollStateWrapper,
    ): View {
        val formattedViews = feedFormatter.format(
            views = this,
            spacerStart = viewFactory.feedSpacerVerticalTabbed,
            canExpandItemWidth = false,
        )

        contentPrefetcher.onDataUpdated(formattedViews.map { it.viewState })

        return View(
            viewState = FeedViewState(
                views = formattedViews,
                feedViewSpec = viewSpecFactory.feedGridNonStretchedViewSpec,
                viewsVisibleListener = contentPrefetcher.viewsVisibleListener,
                feedState = FeedState(
                    initialFeedScrollState = scrollStateWrapper.lastScrollState,
                    lastScrollStateUpdateSink = scrollStateWrapper.lastScrollStateUpdateSink,
                ),
            ),
        )
    }

    fun createArtistViewState(
        data: ArtistScreenContentResult?,
        messageBar: MessageBarViewState?,
        topBarContainerColor: ColorToken,
        socialLinkEventSink: ViewEventSink,
        contentPrefetchers: List<ContentPrefetcher>,
        scrollStateWrapper: FeedScrollStateWrapper,
        collapsingToolbarStateWrapper: CollapsingToolbarStateWrapper,
        pagerPersistableStateWrapper: PagerPersistableStateWrapper,
        artistFollowEventHandler: ViewEventHandler,
        animateFollowIndicator: Boolean,
        followAnimStartedEventHandler: ViewEventHandler? = null,
    ): ArtistViewState {
        val artistState = data?.artistState
        val artist = artistState?.artist
        val collections = data?.collectionStates
//        val currentWallpaper = artistDetail?.currentWallpaper

        if (artistState == null || artist == null) return ArtistViewState.Loading

        val artistName = TextStyleSubheadingActive(string = artist.name, maxLines = 2)

        val profileImageOnClick = if (artistState.followState?.isFollowing == true) {
            viewEventFactory.createShowAlert(
                AlertViewStateOkCancel(
                    title = strings.unfollowArtistTitle(artist.name),
                    message = "",
                    okOnClick = artistFollowEventHandler,
                    cancelOnClick = ViewEventHandler.NoOp,
                ),
            )
        } else {
            artistFollowEventHandler
        }
        //            viewEventFactory.createOnClickFollowOnly(artist.id, artistDetail.followState)

        val socialLinks = artistState.artist.socialLinks?.let {
            viewStateMapper.mapSocialLinks(socialLinkEventSink, it)
        }

        val collectionViewSpec = viewSpecFactory.collectionPreviewFullViewSpec
        val selectedContentColorToken = ColorToken.ThemeOnBackground
        val indicatorColorToken = ColorToken.ThemeSecondary
        val containerColorToken = ColorToken.ThemeSurfaceVariant

        val tab0 = TabViewState(
            headerSelected = menuItemFactory.centeredText(TextTabSelected(strings.singles)),
            headerUnselected = menuItemFactory.centeredText(TextTabUnselected(strings.singles)),
            view = artistState.feedItems?.mapWallpaperToView(
                contentPrefetcher = contentPrefetchers[0],
                staggeredGrid = false,
                scrollStateWrapper = scrollStateWrapper,
            ),
            selectedContentColorToken = selectedContentColorToken,
        )
        val tab1 = TabViewState(
            headerSelected = menuItemFactory.centeredText(TextTabSelected(strings.collections)),
            headerUnselected = menuItemFactory.centeredText(TextTabUnselected(strings.collections)),
            view = if (collections.isNullOrEmpty()) {
                listOf(
                    viewFactory.createSpacer(height = viewSpecArbitrator.paddingLarge * 3),
                    viewFactory.createFullSpanView(viewStateFactory.createNoCollectionsViewState())
                )
            } else {
                collections.map {
                    viewFactory.createCollectionPreview(
                        collectionState = it,
                        collectionPreviewViewSpec = collectionViewSpec,
                    )
                }
            }.mapToFeedView(
                contentPrefetcher = contentPrefetchers[1],
                staggeredRandomGrid = false,
                scrollStateWrapper = scrollStateWrapper,
            ),
            selectedContentColorToken = selectedContentColorToken,
        )

        val tabs = TabsViewState.Indicator(
            viewSpec = viewSpecFactory.tabsIndicatorViewSpec(),
            tabs = listOf(tab0, tab1),
            indicatorColorToken = indicatorColorToken,
            containerColorToken = containerColorToken,
            initialIndex = pagerPersistableStateWrapper.lastPagerState?.initialPage ?: 0,
        )

        val artistViewSpec = viewSpecFactory.artistViewSpec

        val artistToolbarViewState = ArtistToolbarViewState(
            messageBar = messageBar,
            toolbarViewState = ToolbarViewState(
                navigationIcon = menuItemFactory.back,
                height = artistViewSpec.toolbarViewSpec.minToolbarHeight,
                containerColorOverride = ColorToken.Transparent, // toolbar color is applied once, via containerColorOverride below (#413).
            ),
            containerColorOverride = topBarContainerColor,
            name = artistName,
            profileImage = viewStateMapper.mapProfileImage(
                profileImageMediaHolder = artist.profileImageMediaHolder,
                sizedImage = SizedImage.ArtistMedium,
                imageViewSpec = imageViewSpecFactory.artistToolbarProfileImageViewSpec,
                eventHandler = profileImageOnClick,
                showFollowIndicator = true,
                followIndicatorOutlineColorToken = ColorToken.ThemeSurface,
                followState = artistState.followState,
                animateFollowIndicator = animateFollowIndicator,
                followAnimStartedEventHandler = followAnimStartedEventHandler,
            ),
            socialLinks = socialLinks,
            collapsingToolbarStateWrapper = collapsingToolbarStateWrapper,
        )

        return ArtistViewState.Success(
            viewSpec = artistViewSpec,
            artistToolbar = artistToolbarViewState,
            tabs = tabs,
            onSwipeToDismiss = viewStateFactory.createOnSwipeToDismiss(),
            lastPagerStateUpdateSink = pagerPersistableStateWrapper.lastPagerStateUpdateSink,
        )
    }
}
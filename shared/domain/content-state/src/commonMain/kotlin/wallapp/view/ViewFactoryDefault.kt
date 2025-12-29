package wallapp.view

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.ads.inline.support.InlineAdItem
import wallapp.content.model.Wallpaper
import wallapp.content.model.WallpaperId
import wallapp.content.state.ContentState
import wallapp.content.state.ad.AdViewState
import wallapp.content.state.collection.CollectionPreviewViewSpec
import wallapp.content.state.folder.FolderPreviewViewSpec
import wallapp.content.state.folder.FolderPreviewViewState
import wallapp.content.state.widget.HorizontalScrollRowViewState
import wallapp.content.state.widget.WidgetViewState
import wallapp.data.artist.Artist
import wallapp.data.artist.ArtistState
import wallapp.data.collection.CollectionGroup
import wallapp.data.collection.CollectionState
import wallapp.data.highlight.Highlights
import wallapp.image.sized.SizedImage
import wallapp.pixel.feed.MaxFeedWidthNoPaddingViewSpec
import wallapp.pixel.feed.MaxFeedWidthViewSpec
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.separator.SeparatorViewState
import wallapp.pixel.spacer.SpacerViewState
import wallapp.pixel.util.DpOptional
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewSpec
import wallapp.pixel.view.ViewState
import wallapp.screen.createWallpaperScreenArgument
import wallapp.text.TextFeedTitle
import wallapp.theme.ColorToken
import wallapp.unit.Alignment
import wallapp.unit.Width.WidthFillMax
import wallapp.unit.height
import wallapp.view.menu.MenuItemFactory

class ViewFactoryDefault(
    private val viewStateMapper: ViewStateMapper,
    private val viewSpecFactory: ViewSpecFactory,
    private val viewSpecArbitrator: ViewSpecArbitrator,
    private val viewEventFactory: ViewEventFactory,
    private val menuItemFactory: MenuItemFactory,
) : ViewFactory {

    private val toolbarHeight: Dp
        get() = viewSpecArbitrator.toolbarHeight
    private val statusBarHeight: Dp
        get() = viewSpecArbitrator.statusBarHeight
    private val paddingSmall: Dp
        get() = viewSpecArbitrator.paddingSmall
    private val paddingDefault: Dp
        get() = viewSpecArbitrator.paddingDefault

    private val feedBottomSpacerHeight = 40.dp

    override fun createExploreContentStateView(contentState: ContentState): View {
        return when (contentState) {
            is ContentState.Wallpaper -> {
                createWallpaperFeedPreview(
                    contentState.wallpaper,
                    showPlusButton = false,
                )
            }

            is ContentState.Collection -> {
                createCollectionPreview(
                    collectionState = contentState.collectionState,
                    collectionPreviewViewSpec = contentState.viewSpec,
                )
            }

            is ContentState.Highlights -> {
                createHighlightCarousel(
                    highlights = contentState.highlights,
                )
            }
        }
    }

    override fun createWallpaperFeedPreview(
        wallpaper: Wallpaper,
        showPlusButton: Boolean,
        showFooter: Boolean,
        firstWallpaperId: WallpaperId?,
    ): View {
        val isSingle = wallpaper.isSingle
        val wallpaperPreviewViewSpec = if (isSingle) {
            viewSpecFactory.wallpaperPreviewViewSpecFeedSingle
        } else {
            if (showFooter) {
                viewSpecFactory.wallpaperPreviewViewSpecFeedTrackWithFooter
            } else {
                viewSpecFactory.wallpaperPreviewViewSpecFeedTrackNoFooter
            }
        }
        val onClickPlus = if (showPlusButton) {
            viewEventFactory.createNavigateToPaywall()
        } else {
            null
        }

        val viewState = viewStateMapper.mapWallpaperPreviewViewState(
            wallpaper,
            wallpaperPreviewViewSpec,
            viewEventFactory.createNavigateToScreen(
                wallpaper.createWallpaperScreenArgument(firstWallpaperId = firstWallpaperId),
            ),
            viewEventFactory.createOnClickFavorite(wallpaper.id),
            onClickPlus,
            useCollectionLabel = false,
        )
        val viewSpec = if (wallpaper.isSingle) {
            viewSpecFactory.feedContentPreviewViewSpecDefault
        } else {
            viewSpecFactory.feedContentPreviewViewSpecSquare
        }

        return View(
            viewState,
            viewSpec,
            applyScrollParallax = true,
        )
    }

    override fun createCollectionPreview(
        collectionState: CollectionState,
        collectionPreviewViewSpec: CollectionPreviewViewSpec,
    ) = View(
        viewState = viewStateMapper.mapCollectionPreview(
            collectionState,
            collectionPreviewViewSpec,
            showFooter = collectionPreviewViewSpec.useMaxItemSpan == true,
        ),
        viewSpec = collectionPreviewViewSpec,
    )

    override fun createCollectionGroup(
        collectionGroup: CollectionGroup,
    ): List<View> {
        val collectionPreviews = collectionGroup.collectionStates.map { collection ->
            createCollectionPreview(
                collectionState = collection,
                collectionPreviewViewSpec = viewSpecFactory.collectionPreviewFullViewSpec,
            )
        }

        val views = mutableListOf<View>()

        views += createWidget(
            menuItemFactory.createLabel(
                text = TextFeedTitle(collectionGroup.label),
                width = WidthFillMax(),
                contentAlignment = Alignment.Center,
            ),
        )

        views += createHorizontalScrollRow(
            views = collectionPreviews,
        )

        views += createWidget(
            menuItemFactory.createSpacer(height = 8.dp),
        )

        return views
    }

    override fun createFolderPreview(
        folderPreviewViewState: FolderPreviewViewState,
        folderPreviewViewSpec: FolderPreviewViewSpec,
    ): View {
        return View(
            viewState = folderPreviewViewState,
            viewSpec = folderPreviewViewSpec
        )
    }

    override fun createArtistPreview(
        artist: Artist,
        sizedImage: SizedImage,
        imageViewSpec: ImageViewSpec,
    ) = View(
        viewStateMapper.mapArtistPreview(
            artist,
            sizedImage,
            imageViewSpec,
            onClick = viewEventFactory.createNavigateToScreen(artist.id),
        ),
        viewSpecFactory.artistPreviewViewSpec,
    )

    override fun createArtistPreview(
        artistState: ArtistState,
        sizedImage: SizedImage,
        imageViewSpec: ImageViewSpec,
        forceMaxSpan: Boolean,
    ): View {
        val colorToken = ColorToken.LocalContent

        val followButton = menuItemFactory.createFollowButton(
            artistId = artistState.id,
            followState = artistState.followState,
            colorToken = colorToken,
            showLabel = true,
        )

        val viewSpec = if (forceMaxSpan) {
            viewSpecFactory.artistPreviewViewSpecMaxSpan
        } else {
            viewSpecFactory.artistPreviewViewSpec
        }

        val eventHandler = viewEventFactory.createNavigateToScreen(artistState.id)

        val backgroundImages = viewStateMapper.mapArtistPreviewBackgroundImages(
            artistState)

        return View(
            viewStateMapper.mapArtistPreview(
                artistState,
                sizedImage,
                imageViewSpec = imageViewSpec,
                backgroundImages = backgroundImages,
                onClick = eventHandler,
                onClickFavorite = viewEventFactory.createOnClickFavorite(artistState.id),
                profileImageEventHandler = viewEventFactory.createArtistProfile(artistState),
                containerShapeSpec = null,
                followButton = followButton,
                followIndicator = null,
                showArtistProfileBorder = false,
                followIndicatorOutlineColorToken = null,
            ),
            viewSpec,
        )
    }

    override fun createHighlightCarousel(
        highlights: Highlights,
    ) = View(
        viewStateMapper.mapCarousel(highlights),
        viewSpecFactory.highlightCarouselViewSpec,
    )

    override fun createInlineAd(adItem: InlineAdItem, fullWidth: Boolean, fallbackAdViewState: AdViewState): View {
        return View(
            viewStateMapper.mapFeedAd(adItem, fallbackAdViewState),
            if (fullWidth) MaxFeedWidthViewSpec else viewSpecFactory.feedContentPreviewViewSpecDefault
        )
    }

    override fun createHorizontalScrollRow(views: List<View>): View {
        return View(
            HorizontalScrollRowViewState(views),
            MaxFeedWidthNoPaddingViewSpec,
        )
    }

    override fun createSpacer(height: Dp?, width: Dp?): View {
        return View(
            viewState = SpacerViewState(height = height?.let { DpOptional(it) }, width = width?.let { DpOptional(it) }),
            viewSpec = MaxFeedWidthViewSpec,
        )
    }

    override fun createWidget(menuItem: MenuItem, viewSpec: ViewSpec): View {
        return View(
            WidgetViewState(menuItem),
            viewSpec,
        )
    }

    override fun createMoreCollectionsFromArtist(
        artist: Artist,
        collectionStates: List<CollectionState>,
    ): List<View> {
        if (collectionStates.isEmpty()) return emptyList()

        val spacer = createWidget(menuItemFactory.createSpacer(height = 72.dp))
        val label = createWidget(
            menuItemFactory.createLabel(
                text = TextFeedTitle("More from ${artist.name}", maxLines = 1, autoResize = true),
                contentAlignment = Alignment.Center,
                height = 56.dp.height,
                width = WidthFillMax(),
            ),
        )
        val collectionViews = collectionStates.map {
            createCollectionPreview(
                collectionState = it,
                collectionPreviewViewSpec = viewSpecFactory.collectionPreviewFullViewSpec,
            )
        }

        return listOf(spacer, label) + collectionViews
    }

    override fun createSeparator() = View(
        viewState = SeparatorViewState(),
        viewSpec = MaxFeedWidthViewSpec,
    )

    override fun createFullSpanView(viewState: ViewState): View {
        return View(
            viewState = viewState,
            viewSpec = viewSpecFactory.feedViewMaxWidthViewSpec,
        )
    }

    private val feedViewMaxWidthViewSpec
        get() = viewSpecFactory.feedViewMaxWidthViewSpec

    override val feedSpacerTop: View
        get() = View(
            viewState = SpacerViewState(height = DpOptional(statusBarHeight + paddingDefault)),
            viewSpec = feedViewMaxWidthViewSpec,
        )
    override val feedSpacerTopWithToolbar: View
        get() = View(
            viewState = SpacerViewState(height = DpOptional(toolbarHeight + statusBarHeight)),
            viewSpec = feedViewMaxWidthViewSpec,
        )

    override val feedSpacerTopWithToolbarOnly: View
        get() = View(
            viewState = SpacerViewState(height = DpOptional(toolbarHeight)),
            viewSpec = feedViewMaxWidthViewSpec,
        )

    override val feedSpacerBottom: View
        get() = View(
            viewState = SpacerViewState(height = DpOptional(feedBottomSpacerHeight)),
            viewSpec = feedViewMaxWidthViewSpec,
        )
    override val feedSpacerBottomWithNavBar: View
        get() = View(
            viewState = SpacerViewState(height = DpOptional(100.dp)),
            viewSpec = feedViewMaxWidthViewSpec,
        )

    override val feedSpacerHorizontalDefault: View
        get() = View(
            viewState = SpacerViewState(width = DpOptional(16.dp)),
            viewSpec = feedViewMaxWidthViewSpec,
        )

    override val feedSpacerVerticalSmall: View
        get() = View(
            viewState = SpacerViewState(height = DpOptional(paddingSmall)),
            viewSpec = feedViewMaxWidthViewSpec,
        )
    override val feedSpacerVerticalNone: View
        get() = View(
            viewState = SpacerViewState(height = DpOptional(0.dp)),
            viewSpec = feedViewMaxWidthViewSpec,
        )

    override val feedSpacerVerticalDefault: View
        get() = View(
            viewState = SpacerViewState(height = DpOptional(paddingDefault)),
            viewSpec = feedViewMaxWidthViewSpec,
        )

    override val feedSpacerVerticalTabbed: View
        get() = View(
            viewState = SpacerViewState(height = DpOptional(viewSpecArbitrator.feedTabbedSpacerStartHeight)),
            viewSpec = feedViewMaxWidthViewSpec,
        )

    override val feedSpacerSkipRendering: View
        get() = View(
            viewState = SpacerViewState(skipRendering = true),
            viewSpec = feedViewMaxWidthViewSpec,
        )

    override fun feedSpacerVertical(height: Dp) = View(
        viewState = SpacerViewState(height = DpOptional(height)),
        viewSpec = feedViewMaxWidthViewSpec,
    )

    override fun collectionFeedSpacerBottom(feedItemsHeight: Dp): View {
        val toolbarHeight = viewSpecArbitrator.collectionToolbarUnlockedMaxHeight
        val statusBarHeight = viewSpecArbitrator.statusBarHeight
        val defaultSpacerHeight = feedBottomSpacerHeight + toolbarHeight + statusBarHeight
        val screenHeight = viewSpecArbitrator.windowHeight
        val contentHeight = statusBarHeight + toolbarHeight + feedItemsHeight
        val bufferHeight = if (contentHeight < screenHeight) screenHeight - contentHeight else 0.dp
        return View(
            viewState = SpacerViewState(height = DpOptional(defaultSpacerHeight + bufferHeight)),
            viewSpec = feedViewMaxWidthViewSpec,
        )
    }
}
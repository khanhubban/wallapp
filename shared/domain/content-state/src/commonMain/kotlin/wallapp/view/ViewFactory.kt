package wallapp.view

import androidx.compose.ui.unit.Dp
import wallapp.ads.inline.support.InlineAdItem
import wallapp.content.model.Wallpaper
import wallapp.content.model.WallpaperId
import wallapp.content.state.ContentState
import wallapp.content.state.ad.AdViewState
import wallapp.content.state.collection.CollectionPreviewViewSpec
import wallapp.content.state.folder.FolderPreviewViewSpec
import wallapp.content.state.folder.FolderPreviewViewState
import wallapp.data.artist.Artist
import wallapp.data.artist.ArtistState
import wallapp.data.collection.CollectionGroup
import wallapp.data.collection.CollectionState
import wallapp.data.highlight.Highlights
import wallapp.image.sized.SizedImage
import wallapp.pixel.feed.MaxFeedWidthViewSpec
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewSpec
import wallapp.pixel.view.ViewState

interface ViewFactory {

    fun createExploreContentStateView(contentState: ContentState): View

    fun createArtistPreview(
        artist: Artist,
        sizedImage: SizedImage,
        imageViewSpec: ImageViewSpec,
    ) : View

    fun createArtistPreview(
        artistState: ArtistState,
        sizedImage: SizedImage,
        imageViewSpec: ImageViewSpec,
        forceMaxSpan: Boolean = false,
    ) : View

    fun createWallpaperFeedPreview(
        wallpaper: Wallpaper,
        showPlusButton: Boolean,
        showFooter: Boolean = true,
        firstWallpaperId: WallpaperId? = null,
    ): View

    fun createCollectionPreview(
        collectionState: CollectionState,
        collectionPreviewViewSpec: CollectionPreviewViewSpec,
    ): View

    fun createCollectionGroup(
        collectionGroup: CollectionGroup,
    ): List<View>

    fun createFolderPreview(
        folderPreviewViewState: FolderPreviewViewState,
        folderPreviewViewSpec: FolderPreviewViewSpec,
    ): View

    fun createHighlightCarousel(highlights: Highlights) : View

    fun createWidget(menuItem: MenuItem, viewSpec: ViewSpec = MaxFeedWidthViewSpec): View

    fun createMoreCollectionsFromArtist(
        artist: Artist,
        collectionStates: List<CollectionState>,
    ): List<View>

    fun createInlineAd(adItem: InlineAdItem, fullWidth: Boolean, fallbackAdViewState: AdViewState): View

    fun createHorizontalScrollRow(views: List<View>): View

    fun createSpacer(height: Dp? = null, width: Dp? = null): View

    fun createSeparator(): View

    fun createFullSpanView(viewState: ViewState): View

    val feedSpacerTop: View
    val feedSpacerTopWithToolbar: View
    val feedSpacerTopWithToolbarOnly: View
    val feedSpacerBottom: View
    val feedSpacerBottomWithNavBar: View
    val feedSpacerHorizontalDefault: View
    val feedSpacerVerticalSmall: View
    val feedSpacerVerticalNone: View
    val feedSpacerVerticalDefault: View
    val feedSpacerVerticalTabbed: View
    val feedSpacerSkipRendering: View
    fun feedSpacerVertical(height: Dp): View
    fun collectionFeedSpacerBottom(feedItemsHeight: Dp): View
}

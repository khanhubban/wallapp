package wallapp.view

import androidx.compose.ui.unit.Dp
import wallapp.ads.inline.support.InlineAdItem
import wallapp.content.model.Id
import wallapp.content.model.Wallpaper
import wallapp.content.state.ad.AdViewState
import wallapp.content.state.ad.FeedAdViewState
import wallapp.content.state.artist.ArtistPreviewViewState
import wallapp.content.state.carousel.CarouselPageViewState
import wallapp.content.state.carousel.CarouselViewState
import wallapp.content.state.collection.CollectionPreviewViewSpec
import wallapp.content.state.collection.CollectionPreviewViewState
import wallapp.content.state.favorite.FavoriteViewSpec
import wallapp.content.state.favorite.FavoriteViewState
import wallapp.content.state.follow.FollowIndicatorViewState
import wallapp.content.state.profile.ProfileCuratorViewState
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.content.state.social.SocialLinksViewState
import wallapp.content.state.upgrade.plus.indicator.PlusIndicatorViewState
import wallapp.content.state.wallpaper.WallpaperArtistViewState
import wallapp.content.state.wallpaper.WallpaperPreviewViewSpec
import wallapp.content.state.wallpaper.WallpaperPreviewViewState
import wallapp.data.artist.Artist
import wallapp.data.artist.ArtistState
import wallapp.data.collection.CollectionState
import wallapp.data.curator.Curator
import wallapp.data.folder.FolderState
import wallapp.data.following.FollowState
import wallapp.data.highlight.Highlight
import wallapp.data.highlight.Highlights
import wallapp.data.social.SocialLinks
import wallapp.image.sized.SizedImage
import wallapp.media.model.MediaHolder
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.button.ButtonAppearance
import wallapp.pixel.button.ButtonViewState
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewEventSink
import wallapp.theme.ColorToken

interface ViewStateMapper {

    fun mapButtonViewState(
        menuItem: MenuItem,
        animatedViewSpec: AnimatedViewSpec? = null,
        buttonAppearance: ButtonAppearance = ButtonAppearance.Default,
        shapeSpec: ShapeSpec? = null,
        containerColorToken: ColorToken? = null,
        eventHandler: ViewEventHandler?,
    ): ButtonViewState

    fun mapFavoriteViewState(
        favoriteId: String,
        isFavorite: Boolean,
        title: String,
        viewSpec: FavoriteViewSpec?,
        onClick: ViewEventHandler,
        tintColor: ColorToken? = null,
    ): FavoriteViewState

    fun mapWallpaperImage(
        wallpaper: Wallpaper,
        viewSpec: WallpaperPreviewViewSpec,
    ): ImageViewState

    fun mapWallpaperPreviewViewState(
        wallpaper: Wallpaper,
        viewSpec: WallpaperPreviewViewSpec,
        onClick: ViewEventHandler,
        onClickFavorite: ViewEventHandler,
        // If null, the Plus indicator will not be shown:
        onClickPlus: ViewEventHandler?,
        useCollectionLabel: Boolean = true,
    ): WallpaperPreviewViewState

    fun mapArtistPreview(
        artist: Artist,
        sizedImage: SizedImage,
        imageViewSpec: ImageViewSpec,
        onClick: ViewEventHandler,
    ): ArtistPreviewViewState

    fun mapArtistPreviewOnboarding(
        artistState: ArtistState,
        followToggleExtraAction: ((Id.ArtistId) -> Unit)? = null,
    ): ArtistPreviewViewState

    fun mapArtistPreview(
        artistState: ArtistState,
        sizedImage: SizedImage,
        imageViewSpec: ImageViewSpec,
        backgroundImages: List<ImageViewState>?,
        onClick: ViewEventHandler,
        onClickFavorite: ViewEventHandler,
        profileImageEventHandler: ViewEventHandler?,
        containerShapeSpec: ShapeSpec?,
        followButton: MenuItem?,
        followIndicator: FollowIndicatorViewState? = null,
        followIndicatorOutlineColorToken: ColorToken?,
        showArtistProfileBorder: Boolean,
    ): ArtistPreviewViewState

    fun mapArtistPreviewBackgroundImages(
        artistState: ArtistState,
    ): List<ImageViewState>?

    fun mapCollectionPreview(
        collectionState: CollectionState,
        collectionPreviewViewSpec: CollectionPreviewViewSpec,
        showFooter: Boolean,
        // Ideally will be non-null, in which case an event handler will be created based on the
        // preview image. Specify non-null if you wish to override this behavior.
        eventHandlerOverride: ViewEventHandler? = null,
    ): CollectionPreviewViewState

    fun mapWallpaperArtist(
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
    ): WallpaperArtistViewState

    fun mapProfileImage(
        profileImageMediaHolder: MediaHolder,
        sizedImage: SizedImage,
        imageViewSpec: ImageViewSpec,
        eventHandler: ViewEventHandler?,
        showFollowIndicator: Boolean = false,
        followIndicatorOutlineColorToken: ColorToken? = null,
        followState: FollowState? = null,
        showArtistProfileBorder: Boolean = false,
        animateFollowIndicator: Boolean = true,
        followAnimStartedEventHandler: ViewEventHandler? = null,
    ): ProfileImageViewState

    fun mapProfileCurator(
        curator: Curator,
        viewEventHandler: ViewEventHandler,
    ): ProfileCuratorViewState

    fun mapProfileCurator(
        artistState: ArtistState,
        viewEventHandler: ViewEventHandler,
    ): ProfileCuratorViewState

    fun mapProfileCurator(
        folderState: FolderState,
        viewEventHandler: ViewEventHandler,
    ): ProfileCuratorViewState

    fun mapSocialLinks(
        eventSink: ViewEventSink,
        socialLinks: SocialLinks,
    ): SocialLinksViewState?

    fun mapFeedAd(inlineAdItem: InlineAdItem, fallbackAdViewState: AdViewState): FeedAdViewState

    fun mapPlusIndicator(
        indicatorSize: Dp = PlusIndicatorViewState.DefaultSize,
        indicatorOnBackground: Boolean,
        viewEventHandler: ViewEventHandler?,
    ): PlusIndicatorViewState

    fun mapCarouselPage(
        highlight: Highlight,
    ): CarouselPageViewState

    fun mapCarousel(
        highlights: Highlights,
    ): CarouselViewState

    fun mapFirstRunCarouselBackgrounds(wallpapers: List<Wallpaper>): List<ImageViewState>

    fun mapFollowIndicator(visible: Boolean): FollowIndicatorViewState
}
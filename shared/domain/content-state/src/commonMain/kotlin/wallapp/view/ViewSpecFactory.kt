package wallapp.view

import androidx.compose.ui.unit.Dp
import wallapp.content.state.account.AccountOverviewViewSpec
import wallapp.content.state.ad.AdViewSpec
import wallapp.content.state.artist.ArtistPreviewViewSpecOnboarding
import wallapp.content.state.artist.ArtistViewSpec
import wallapp.content.state.carousel.CarouselViewSpec
import wallapp.content.state.collection.CollectionActionButtonViewSpec
import wallapp.content.state.collection.CollectionActionViewSpec
import wallapp.content.state.collection.CollectionPreviewViewSpec
import wallapp.content.state.collection.CollectionToolbarViewSpec
import wallapp.content.state.error.ErrorViewSpec
import wallapp.content.state.error.rewardad.ErrorRewardAdViewSpec
import wallapp.content.state.explore.ExploreHeaderViewSpec
import wallapp.content.state.favorite.FavoriteViewSpec
import wallapp.content.state.feed.FeedContentPreviewViewSpec
import wallapp.content.state.folder.FolderPreviewViewSpec
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
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.bottomsheet.BottomSheetDescriptor
import wallapp.pixel.feed.FeedViewSpec
import wallapp.pixel.feed.FeedViewViewSpec
import wallapp.pixel.message.MessageBarViewSpec
import wallapp.pixel.navigationbar.NavigationBarViewSpec
import wallapp.pixel.selection.SelectionGroupViewSpec
import wallapp.pixel.tab.TabsViewSpec

interface ViewSpecFactory {

    val feedGridViewSpec: FeedViewSpec
    val feedGridNonStretchedViewSpec: FeedViewSpec
    val feedStaggeredViewSpec: FeedViewSpec

    val feedViewMaxWidthViewSpec: FeedViewViewSpec

    val navigationBarViewSpec: NavigationBarViewSpec

    val animatedToolbarHeroButtonViewSpec: AnimatedViewSpec

    val activeDownloadStatusViewSpec: MessageBarViewSpec
    val activeDownloadStatusHideViewSpec: MessageBarViewSpec

    val feedContentPreviewViewSpecDefault: FeedContentPreviewViewSpec
    val feedContentPreviewViewSpecSquare: FeedContentPreviewViewSpec
    val feedContentPreviewViewSpecWide: FeedContentPreviewViewSpec

    val wallpaperPreviewViewSpecArtistSelectionBackgroundSingle: WallpaperPreviewViewSpec
    val wallpaperPreviewViewSpecArtistSelectionBackgroundTrack: WallpaperPreviewViewSpec
    val wallpaperPreviewViewSpecFullScreenBackground: WallpaperPreviewViewSpec
    val wallpaperPreviewViewSpecCarouselHighlight: WallpaperPreviewViewSpec
    val wallpaperPreviewViewSpecFeedSingle: WallpaperPreviewViewSpec
    val wallpaperPreviewViewSpecFeedTrackNoFooter: WallpaperPreviewViewSpec
    val wallpaperPreviewViewSpecFeedTrackWithFooter: WallpaperPreviewViewSpec
    val wallpaperPreviewViewSpecWallpaperShowcase: WallpaperPreviewViewSpec
    fun wallpaperPreviewViewSpecCollectionPreviewFull(index: Int): WallpaperPreviewViewSpec
    fun wallpaperPreviewViewSpecCollectionPreviewSmall(index: Int): WallpaperPreviewViewSpec

    fun wallpaperDetailViewSpec(itemDetailCount: Int): WallpaperDetailViewSpec

    val wallpaperSingleActionViewSpecTwoOptions: WallpaperSingleActionViewSpec
    val wallpaperSingleActionViewSpecThreeOptions: WallpaperSingleActionViewSpec

    val collectionActionViewSpecOneOption: CollectionActionViewSpec
    fun collectionActionViewSpecTwoOptions(showInfoNotice: Boolean): CollectionActionViewSpec

    val collectionPreviewFullViewSpec: CollectionPreviewViewSpec
    val collectionPreviewSmallViewSpec: CollectionPreviewViewSpec
    fun collectionToolbarLockedViewSpec(showCollectionLockedInfo: Boolean): CollectionToolbarViewSpec.Locked
    val collectionToolbarUnlockedViewSpec: CollectionToolbarViewSpec.Unlocked
    val collectionActionButtonViewSpecBuyCollection: CollectionActionButtonViewSpec.BuyCollectionActionButtonViewSpec
    val collectionActionButtonViewSpecDownloadProgress: CollectionActionButtonViewSpec.DownloadProgressButtonViewSpec
    val collectionActionButtonViewSpecGetCollection: CollectionActionButtonViewSpec.GetCollectionActionButtonViewSpec
    fun collectionFeedViewSpec(
        toolbarExpanded: Boolean,
        showAdFreeCollectionLockedInfo: Boolean,
    ): FeedViewSpec

    val artistPreviewViewSpec: FeedViewViewSpec
    val artistPreviewViewSpecDefault: FeedViewViewSpec
    val artistPreviewViewSpecMaxSpan: FeedViewViewSpec
    val artistPreviewViewSpecOnboarding: ArtistPreviewViewSpecOnboarding

    val artistViewSpec: ArtistViewSpec

    val highlightCarouselViewSpec: CarouselViewSpec

    val homeOnboardingHeaderViewSpec: HomeOnboardingHeaderViewSpec

    val homeTopBarViewSpec: HomeTopBarViewSpec
    val homeHeaderViewSpec: HomeHeaderViewSpec
    val homeFilterViewSpec: SelectionGroupViewSpec

    val wallpaperShowcaseViewSpec: WallpaperShowcaseViewSpec

    val favoriteViewSpec: FavoriteViewSpec

    val searchInputViewSpec: SearchInputViewSpec
    val searchSelectionGroupViewSpec: SelectionGroupViewSpec
    val searchSelectionButtonGroupViewSpec: SelectionGroupViewSpec
    val searchResultsHeaderViewSpec: SearchResultsHeaderViewSpec

    val bottomSheetSpec: BottomSheetDescriptor

    val exploreHeaderViewSpec: ExploreHeaderViewSpec
    val exploreFeedViewSpec: FeedViewSpec

    val profileHeaderViewSpec: ProfileHeaderViewSpec

    val accountOverviewViewSpec: AccountOverviewViewSpec

    val signInButtonViewSpec: SignInButtonViewSpec

    val appIconSettingRowViewSpec: SettingItemPreviewRowViewSpec
    val appIconSettingViewSpec: SettingItemPreviewViewSpec
    val themeSettingViewSpec: SettingItemPreviewViewSpec

    val errorViewSpec: ErrorViewSpec

    fun tabsIndicatorViewSpec(width: Dp? = null): TabsViewSpec.Indicator
    fun tabsPillViewSpec(width: Dp? = null): TabsViewSpec.Pill

    val errorRewardAdViewSpec: ErrorRewardAdViewSpec

    val presetAdViewSpec: AdViewSpec
    val folderViewSpec: FolderViewSpec

    val profileImageIndicatorViewSpec: ProfileImageIndicatorViewSpec

    val curatorViewSpec: ProfileCuratorViewSpec

    val folderPreviewViewSpec: FolderPreviewViewSpec
    val wallpaperPreviewViewSpecFolderPreviewFeedSingle: WallpaperPreviewViewSpec
    val wallpaperPreviewViewSpecFolderPreviewFeedTrack: WallpaperPreviewViewSpec
}

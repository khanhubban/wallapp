package wallapp.view

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import kotlinx.coroutines.flow.StateFlow
import wallapp.unit.Padding


interface ViewSpecArbitrator {

    val isReady: StateFlow<Boolean>

    val windowWidth: Dp
    val windowHeight: Dp
    val windowMaxSize: Dp
        get() = windowWidth.coerceAtLeast(windowHeight)
    val windowIsCompact: Boolean

    val statusBarHeight: Dp
    val navigationBarHeight: Dp

    val bottomSheetPresentationOffsetTop: Dp

    val screenScrimHeight: Dp

    val paddingSmall: Dp
    val paddingDefault: Dp
    val paddingLarge: Dp

    val defaultIconSize: Dp

    val indicatorTabHeight: Dp
    val indicatorTabIndicatorHeight: Dp // the actual indicator height
    val indicatorTabContainerHeight: Dp
    val pillTabsWidth: Dp
    val pillTabContainerHeight: Dp
    val pillTabHeight: Dp

    val feedTabbedSpacerStartHeight: Dp

    val feedColumns: Int
    val feedItemSingleSpanWidth: Dp
    val feedItemSingleSpanHeight: Dp
    val feedItemWideSpanWidth: Dp
    val feedItemWideSpanHeight: Dp

    val bottomNavBarItemHeight: Dp
    val bottomNavBarHorizontalSpacing: Dp

    val bottomNavBarItemsYOffset: Dp

    val plusButtonWidth: Dp
    val plusButtonHeight: Dp

    val socialLinkButtonImageSize: Dp
    val socialLinkButtonItemSize: Dp
    val socialLinkButtonPadding: Padding

    val toolbarHeight: Dp
    val toolbarXlHeight: Dp

    val heroButtonHeight: Dp
    val heroButtonHeightXl: Dp
    val heroButtonWidth: Dp
    val toolbarHeroButtonWidth: Dp
    val toolbarHeroButtonHeight: Dp
    val toolbarHeroButtonIconSize: Dp

    val carouselHighlightImageWidth: Dp
    val carouselHighlightImageHeight: Dp

    val carouselEdgeButtonWidth: Dp
    val carouselPageSpacing: Dp
    val carouselPageWidth: Dp
    val carouselHeight: Dp
    val carouselExtraHeightForScroll: Dp
    val carouselVerticalPadding: Dp

    val artistProfileImageMediumSize: Dp

    val artistOnboardingWidth: Dp
    val artistOnboardingHeight: Dp
    val artistOnboardingWallpaperBackgroundWidth: Dp
    val artistOnboardingWallpaperBackgroundHeight: Dp
    val artistOnboardingProfileImageSize: Dp
    val artistOnboardingProfileImageShadowSize: Dp

    val artistToolbarMinHeight: Dp
    val artistToolbarMaxHeight: Dp
    val artistToolbarProfileImageMaxTop: Dp
    val artistToolbarProfileImageMaxSize: Dp
    val artistToolbarProfileImageMinSize: Dp
    val artistToolbarProfileImageMinTopPadding: Dp
    val artistToolbarProfileImageMinBottomPadding: Dp
    val artistToolbarTitleTop: Dp
    val artistToolbarTitleHeight: Dp
    val artistToolbarSocialLinksTop: Dp
    val artistToolbarSocialLinksHeight: Dp
    val artistToolbarSeparatorHeight: Dp

    val contentFooterHeight: Dp

    val collectionPreviewSmallWidth: Dp
    val collectionPreviewSmallHeight: Dp
    val collectionPreviewSmallLayer0Width: Dp
    val collectionPreviewSmallLayer0Height: Dp
    val collectionPreviewSmallLayer1Width: Dp
    val collectionPreviewSmallLayer1Height: Dp
    val collectionPreviewSmallLayer2Width: Dp
    val collectionPreviewSmallLayer2Height: Dp
    val collectionPreviewFullWidth: Dp
    val collectionPreviewFullHeight: Dp
    val collectionPreviewFullLayer0Width: Dp
    val collectionPreviewFullLayer0Height: Dp
    val collectionPreviewFullLayer1Width: Dp
    val collectionPreviewFullLayer1Height: Dp
    val collectionPreviewFullLayer2Width: Dp
    val collectionPreviewFullLayer2Height: Dp

    val collectionToolbarLockedMinHeight: Dp
    val collectionToolbarLockedMaxHeight: Dp
    val collectionToolbarLockedWithInfoMaxHeight: Dp
    val collectionToolbarUnlockedMinHeight: Dp
    val collectionToolbarUnlockedMaxHeight: Dp
    val collectionToolbarProfileImageMaxTop: Dp
    val collectionToolbarProfileImageMaxSize: Dp
    val collectionToolbarProfileImageMinSize: Dp
    val collectionToolbarProfileImageMinTopPadding: Dp
    val collectionToolbarProfileImageMinBottomPadding: Dp
    val collectionToolbarButtonWidthMin: Dp
    val collectionToolbarButtonWidthMax: Dp
    val collectionToolbarButtonMinTop: Dp
    val collectionToolbarButtonMaxTop: Dp
    val collectionToolbarButtonMaxHeight: Dp
    val collectionToolbarButtonMaxBottom: Dp
    val collectionToolbarButtonPaddingTopMin: Dp
    val collectionToolbarButtonPaddingBottomMin: Dp
    val collectionToolbarButtonPaddingEnd: Dp
    val collectionToolbarBuyButtonItem1PaddingStartMin: Dp
    val collectionToolbarBuyButtonItem1PaddingStartMax: Dp
    val collectionToolbarBuyButtonItem3PaddingEnd: Dp
    val collectionDownloadProgressButtonLabelPaddingHorizontal: Dp
    val collectionToolbarGetButtonIconPaddingStartMin: Dp
    val collectionToolbarGetButtonIconPaddingStartMax: Dp
    val collectionToolbarGetButtonLabelPaddingStartMin: Dp
    val collectionToolbarGetButtonLabelPaddingStartMax: Dp
    val collectionToolbarCollectionTitleTop: Dp
    val collectionToolbarCollectionTitleHeight: Dp
    val collectionToolbarArtistTitleTop: Dp?
    val collectionToolbarArtistTitleHeight: Dp?
    val collectionToolbarAdFreeCollectionLockedInfoTop: Dp
    val collectionToolbarAdFreeCollectionLockedInfoHeight: Dp

    val onboardingFollowButtonSize: Dp

    val homeTopBarHeight: Dp
    val homeHeaderHeight: Dp
    val homeProfileVerticalPadding: Dp
    val homeFilterWidth: Dp
    val homeFilterHeight: Dp

    val profileCuratorImageSize: Dp
    val profileCuratorImageShadowSize: Dp
    val profileHeaderHeight: Dp
    val profileHeaderTitleHeight: Dp
    val profileHeaderSubtitleTopPadding: Dp
    val profileHeaderSubtitleHeight: Dp

    val profileImageHeightToolbar: Dp

    val separatorHeight: Dp
    val separatorPadding: Padding

    val settingsStartPadding: Dp

    val wallpaperToolbarTitleWidth: Dp
    val toolbarTitleWidthAdjustedForNavigationIcon: Dp

    val downloadContentDialogWidth: Dp

    val searchBarHeight: Dp
    val searchBarColorItemSize: Dp
    val searchBarColorInsideItemSize: Dp
    val searchBarFilterHorizontalPadding: Dp
    val searchRecipeBinColorItemSize: Dp
    val searchRecipeBinColorInsideItemSize: Dp
    val searchFilterToggleHeight: Dp
    val searchFiltersTopPadding: Dp
    val searchResultsBarHeight: Dp
    val searchResultsHeaderHeight: Dp
    val searchResultFeedTopPadding: Dp
    val searchInputTopPadding: Dp

    val selectionRowHeight: Dp

    val signInButtonWidth: Dp
    val signInButtonHeight: Dp

    val iconButtonLayerSize: Dp
    val iconSizeSmall: Dp
    val iconSize: Dp
    val iconSizeLarge: Dp
    val iconSizeMediumLarge: Dp
    val iconSizeMediumLarge2: Dp
    val iconSizeExtraLarge: Dp

    val wallpaperPreviewWidth: Dp
    val wallpaperPreviewWidthMax: Dp
    val wallpaperPreviewHeightWallpaperShowcase: Dp

    val wallpaperShowcaseTopControlButtonsVerticalOffset: Dp
    val wallpaperShowcaseControlButtonEdgePadding: Dp
    val wallpaperShowcaseControlButtonSpacing: Dp
    val wallpaperShowcaseControlButtonHeight: Dp
    val wallpaperShowcaseControlButtonWidth: Dp

    fun wallpaperDetailViewHeight(itemCount: Int): Dp
    val wallpaperDetailItemHeight: Dp
    val wallpaperDetailItemImageSize: Dp
    val wallpaperDetailItemVerticalSpacerHeight: Dp

    val unlockWallpaperUnlockContainerSize: DpSize
    val unlockWallpaperUpgradeContainerSize: DpSize
    val unlockWallpaperPreviewWidth: Dp
    val unlockWallpaperPreviewHeight: Dp
    val unlockWallpaperMenuItemHeight: Dp
    val unlockWallpaperSdToHdIconBetweenSpace: Dp

    val upgradeHighlightPreviewsHeight: Dp
    val upgradeHighlightLowResWidth: Dp
    val upgradeHighlightLowResHeight: Dp
    val upgradeHighlightHighResWidth: Dp
    val upgradeHighlightHighResHeight: Dp
    val upgradeHighlightPhoneFrameWidth: Dp
    val upgradeHighlightPhoneFrameHeight: Dp

    val plusHeroImageWidth: Dp
    val plusHeroImageHeight: Dp

    val paywallFeatureIconSize: Dp
    val paywallFeatureItemHeight: Dp

    val bottomSheetHeightOneOption: Dp
    val bottomSheetHeightTwoOptions: Dp
    val bottomSheetHeightThreeOptions: Dp

    val folderToolbarMinHeight: Dp
    val folderToolbarMaxHeight: Dp
    val folderToolbarTitleTop: Dp
    val folderToolbarTitleHeight: Dp

    val settingItemPreviewRowHorizontalPadding: Dp
    val settingItemPreviewRowVerticalPadding: Dp
    val appIconSettingItemWidth: Dp
    val appIconSettingItemHeight: Dp

    val pageIndicatorContainerHeight: Dp

    val adCloseButtonPaddingTop: Dp
    val adCloseButtonPaddingEnd: Dp

    val adFreeCollectionLockedInfoWidth: Dp
    val adFreeCollectionLockedInfoHeight: Dp

    val folderPreviewWidth: Dp
    val folderPreviewHeight: Dp
    val folderPreviewWallpaperFeedItemWidth: Dp
    val folderPreviewWallpaperFeedItemHeight: Dp
}
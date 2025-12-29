package wallapp.view

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.state.index.IndexTab
import wallapp.system.platform.PlatformFeature
import wallapp.unit.Padding

class ViewSpecArbitratorDefault(
    private val config: ViewSpecArbitratorConfig,
) : ViewSpecArbitrator {

    override val isReady: StateFlow<Boolean>
        get() = config.isReady

    private val nativeBottomSheetUiSupported: Boolean
        get() = PlatformFeature.NativeBottomSheetUiSupported

    override val windowWidth: Dp
        get() = config.windowWidth
    override val windowHeight: Dp
        get() = config.windowHeight
    override val windowIsCompact: Boolean
        get() = config.windowIsCompact

    override val statusBarHeight: Dp
        get() = config.statusBarHeight
    override val navigationBarHeight: Dp
        get() = config.navigationBarHeight

    override val feedColumns: Int
        get() = config.feedColumns

    /**
     * The offset from the top of the screen to the top of a naturally expanded bottom sheet on iOS.
     * The 10.dp was determined from on a iPhone 15.
     */
    override val bottomSheetPresentationOffsetTop: Dp
        get() = if (nativeBottomSheetUiSupported) {
            statusBarHeight + 10.dp
        } else {
            0.dp
        }

    override val screenScrimHeight: Dp
        get() = navigationBarHeight + 60.dp

    val bottomNavBarTabWidth get() = 84.dp
    val bottomNavBarTabCount get() = IndexTab.entries.size

    override val paddingSmall: Dp
        get() = 8.dp
    override val paddingDefault: Dp
        get() = 16.dp
    override val paddingLarge: Dp
        get() = paddingDefault * 2

    override val defaultIconSize: Dp
        get() = 36.dp

    override val indicatorTabIndicatorHeight: Dp
        get() = 6.dp
    override val indicatorTabHeight: Dp
        get() = toolbarHeight - indicatorTabIndicatorHeight
    override val indicatorTabContainerHeight: Dp
        get() = indicatorTabHeight + indicatorTabIndicatorHeight
    override val pillTabsWidth: Dp
        get() = 280.dp
    override val pillTabHeight: Dp
        get() = 36.dp
    override val pillTabContainerHeight: Dp
        get() = pillTabHeight + paddingSmall

    override val feedTabbedSpacerStartHeight: Dp
        get() = indicatorTabContainerHeight.minus(indicatorTabIndicatorHeight)

    override val feedItemSingleSpanWidth: Dp
        get() = (windowWidth - (paddingDefault * 2) - (paddingDefault * (feedColumns - 1))) / feedColumns

    override val feedItemSingleSpanHeight: Dp
        get() = feedItemSingleSpanWidth * 1.5f

    override val feedItemWideSpanWidth: Dp
        get() = feedItemSingleSpanWidth * 2 + paddingDefault

    override val feedItemWideSpanHeight: Dp
        get() = feedItemWideSpanWidth

    override val bottomNavBarItemHeight: Dp
        get() = 72.dp

    override val bottomNavBarHorizontalSpacing: Dp
        get() = (windowWidth - (bottomNavBarTabWidth * bottomNavBarTabCount)) / (bottomNavBarTabCount + 1)
//        get() = (deviceWidth - (bottomNavBarTabWidth * bottomNavBarTabCount)) / 2

    // TODO: replace this with a proper check
    private val usingLegacySystemNavigationBar: Boolean
        get() = navigationBarHeight > 36.dp

    override val bottomNavBarItemsYOffset: Dp
        get() = if (usingLegacySystemNavigationBar) {
            navigationBarHeight
        } else {
            navigationBarHeight / 2
        }

    override val plusButtonWidth: Dp
        get() = 144.dp
    override val plusButtonHeight: Dp
        get() = 56.dp

    override val socialLinkButtonImageSize: Dp
        get() = 20.dp
    override val socialLinkButtonItemSize: Dp
        get() = socialLinkButtonImageSize.plus(4.dp)
    override val socialLinkButtonPadding: Padding
        get() = Padding(all = ((socialLinkButtonItemSize - socialLinkButtonImageSize) / 2))

    override val toolbarHeight: Dp
        get() = 56.dp
    override val toolbarXlHeight: Dp
        get() = 72.dp

    override val heroButtonHeight: Dp
        get() = 52.dp
    override val heroButtonHeightXl: Dp
        get() = 60.dp
    override val heroButtonWidth: Dp
        get() = min(windowWidth - 60.dp, 600.dp)

    override val toolbarHeroButtonWidth: Dp
        get() = 160.dp
    override val toolbarHeroButtonHeight: Dp
        get() = artistToolbarMinHeight - (paddingDefault * 1.5f)
    override val toolbarHeroButtonIconSize: Dp
        get() = defaultIconSize * .67f

    override val carouselHighlightImageWidth: Dp
        get() = windowWidth
    override val carouselHighlightImageHeight: Dp
        get() = carouselHighlightImageWidth * 1.25f // 4:5 aspect ratio

    override val carouselEdgeButtonWidth: Dp
        get() = 36.dp
    override val carouselPageSpacing: Dp
        get() = paddingDefault
    override val carouselPageWidth: Dp
        get() = windowWidth - (carouselEdgeButtonWidth * 2) - (carouselPageSpacing * 2)
    override val carouselHeight: Dp
        get() = (windowMaxSize * .4f) + (carouselVerticalPadding * 2)
    override val carouselExtraHeightForScroll: Dp
        get() = carouselHeight * 0.4f
    override val carouselVerticalPadding: Dp
        get() = paddingDefault

    override val artistProfileImageMediumSize: Dp
        get() = 82.dp

    override val artistOnboardingWidth: Dp
        get() = feedItemSingleSpanWidth
    override val artistOnboardingHeight: Dp
        get() = artistOnboardingWidth * 1.1f
    override val artistOnboardingWallpaperBackgroundWidth: Dp
        get() = artistOnboardingWidth / 2
    override val artistOnboardingWallpaperBackgroundHeight: Dp
        get() = artistOnboardingHeight
    override val artistOnboardingProfileImageSize: Dp
        get() = artistProfileImageMediumSize
    override val artistOnboardingProfileImageShadowSize: Dp
        get() = artistOnboardingProfileImageSize * 1.775f

    override val artistToolbarMinHeight: Dp
        get() = toolbarXlHeight
    override val artistToolbarMaxHeight: Dp
        get() = artistToolbarSocialLinksTop +
                artistToolbarSocialLinksHeight +
                artistToolbarSeparatorHeight
    override val artistToolbarProfileImageMaxSize: Dp
        get() = 76.dp
    override val artistToolbarProfileImageMaxTop: Dp
        get() = 0.dp
    override val artistToolbarProfileImageMinSize: Dp
        get() = artistToolbarMinHeight -
                artistToolbarProfileImageMinTopPadding -
                artistToolbarProfileImageMinBottomPadding
    override val artistToolbarProfileImageMinTopPadding: Dp
        get() = paddingSmall
    override val artistToolbarProfileImageMinBottomPadding: Dp
        get() = paddingSmall
    override val artistToolbarTitleTop: Dp
        get() = artistToolbarProfileImageMaxSize +
                artistToolbarProfileImageMaxTop +
                paddingSmall
    override val artistToolbarTitleHeight: Dp
        get() = 36.dp
    override val artistToolbarSocialLinksTop: Dp
        get() = artistToolbarTitleTop +
                artistToolbarTitleHeight +
                paddingSmall
    override val artistToolbarSocialLinksHeight: Dp
        get() = socialLinkButtonItemSize
    override val artistToolbarSeparatorHeight: Dp
        get() = 0.dp//separatorToolbarHeight

    override val contentFooterHeight: Dp
        get() = 44.dp

    override val collectionPreviewSmallWidth: Dp
        get() = feedItemSingleSpanWidth
    override val collectionPreviewSmallHeight: Dp
        get() = collectionPreviewSmallWidth * .75f
    override val collectionPreviewSmallLayer0Width: Dp
        get() = collectionPreviewSmallWidth
    override val collectionPreviewSmallLayer0Height: Dp
        get() = collectionPreviewSmallHeight - collectionPreviewSmallLayer1Height - collectionPreviewSmallLayer2Height
    override val collectionPreviewSmallLayer1Width: Dp
        get() = collectionPreviewSmallLayer0Width - (collectionPreviewSmallHorizontalInsetStep * 2)
    override val collectionPreviewSmallLayer1Height: Dp
        get() = paddingDefault
    override val collectionPreviewSmallLayer2Width: Dp
        get() = collectionPreviewSmallLayer1Width - (collectionPreviewSmallHorizontalInsetStep * 2)
    override val collectionPreviewSmallLayer2Height: Dp
        get() = paddingDefault * .8f
    private val collectionPreviewSmallHorizontalInsetStep: Dp
        get() = 10.dp
    
    override val collectionPreviewFullWidth: Dp
        get() = feedItemWideSpanWidth
    override val collectionPreviewFullHeight: Dp
        get() = 238.dp
    override val collectionPreviewFullLayer0Width: Dp
        get() = collectionPreviewFullWidth
    override val collectionPreviewFullLayer0Height: Dp
        get() = collectionPreviewFullHeight - collectionPreviewFullLayer1Height - collectionPreviewFullLayer2Height
    override val collectionPreviewFullLayer1Width: Dp
        get() = collectionPreviewFullLayer0Width - (collectionPreviewFullHorizontalInsetStep * 2)
    override val collectionPreviewFullLayer1Height: Dp
        get() = 24.dp
    override val collectionPreviewFullLayer2Width: Dp
        get() = collectionPreviewFullLayer1Width - ((collectionPreviewFullHorizontalInsetStep + 2.dp) * 2)
    override val collectionPreviewFullLayer2Height: Dp
        get() = 16.dp
    private val collectionPreviewFullHorizontalInsetStep: Dp
        get() = 26.dp

    override val collectionToolbarLockedMinHeight: Dp
        get() = toolbarXlHeight
    override val collectionToolbarLockedMaxHeight: Dp
        get() = collectionToolbarButtonMaxBottom +
                paddingDefault
    override val collectionToolbarLockedWithInfoMaxHeight: Dp
        get() = collectionToolbarLockedMaxHeight + adFreeCollectionLockedInfoHeight
    override val collectionToolbarUnlockedMinHeight: Dp
        get() = toolbarXlHeight
    override val collectionToolbarUnlockedMaxHeight: Dp
        get() = collectionToolbarLockedMaxHeight
    override val collectionToolbarProfileImageMaxSize: Dp
        get() = artistToolbarProfileImageMaxSize
    override val collectionToolbarProfileImageMinSize: Dp
        get() = artistToolbarProfileImageMinSize
    override val collectionToolbarProfileImageMaxTop: Dp
        get() = artistToolbarProfileImageMaxTop
    override val collectionToolbarProfileImageMinTopPadding: Dp
        get() = artistToolbarProfileImageMinTopPadding
    override val collectionToolbarProfileImageMinBottomPadding: Dp
        get() = artistToolbarProfileImageMinBottomPadding

    override val collectionToolbarButtonWidthMin: Dp
        get() = (windowWidth / 2) - collectionToolbarButtonPaddingEnd - (paddingDefault / 2)
    override val collectionToolbarButtonWidthMax: Dp
        get() = windowWidth - (collectionToolbarButtonPaddingEnd * 2)
    override val collectionToolbarButtonMinTop: Dp
        get() = 4.dp
    override val collectionToolbarButtonMaxTop: Dp
        get() = collectionToolbarCollectionTitleBottom + paddingDefault
    override val collectionToolbarButtonMaxHeight: Dp
        get() = heroButtonHeight
    override val collectionToolbarButtonMaxBottom: Dp
        get() = collectionToolbarButtonMaxTop + collectionToolbarButtonMaxHeight
    override val collectionToolbarButtonPaddingTopMin: Dp
        get() = (toolbarXlHeight - heroButtonHeight) / 2
    override val collectionToolbarButtonPaddingBottomMin: Dp
        get() = collectionToolbarButtonPaddingTopMin
    override val collectionToolbarButtonPaddingEnd: Dp
        get() = 32.dp
    override val collectionToolbarBuyButtonItem1PaddingStartMin: Dp
        get() = 26.dp
    override val collectionToolbarBuyButtonItem1PaddingStartMax: Dp
        get() = 52.dp
    override val collectionToolbarBuyButtonItem3PaddingEnd: Dp
        get() = collectionToolbarButtonPaddingEnd
    override val collectionDownloadProgressButtonLabelPaddingHorizontal: Dp
        get() = 68.dp
    override val collectionToolbarGetButtonIconPaddingStartMin: Dp
        get() = collectionToolbarBuyButtonItem1PaddingStartMin
    override val collectionToolbarGetButtonIconPaddingStartMax: Dp
        get() = collectionToolbarGetButtonLabelPaddingStartMax - paddingLarge
    override val collectionToolbarGetButtonLabelPaddingStartMin: Dp
        get() = collectionToolbarButtonWidthMin * .5f
    override val collectionToolbarGetButtonLabelPaddingStartMax: Dp
        get() = (collectionToolbarButtonWidthMax * .5f) - (paddingDefault * 3)

    override val collectionToolbarArtistTitleTop: Dp
        get() = collectionToolbarProfileImageMaxSize +
                (paddingDefault / 2)
    override val collectionToolbarArtistTitleHeight: Dp?
//        get() = 28.dp
        get() = null // set to null to hide artist title
    val collectionToolbarArtistTitleBottom: Dp
        get() {
            val top = collectionToolbarArtistTitleTop
            val height = collectionToolbarArtistTitleHeight
            return if (top != null && height != null) {
                top + height
            } else {
                collectionToolbarProfileImageMaxSize
            }
        }
    override val collectionToolbarCollectionTitleTop: Dp
        get() = collectionToolbarArtistTitleBottom +
                (paddingSmall)
    override val collectionToolbarCollectionTitleHeight: Dp
        get() = 56.dp
    val collectionToolbarCollectionTitleBottom: Dp
        get() = collectionToolbarCollectionTitleTop +
                collectionToolbarCollectionTitleHeight

    override val collectionToolbarAdFreeCollectionLockedInfoTop: Dp
        get() = collectionToolbarCollectionTitleTop +
                collectionToolbarCollectionTitleHeight +
                heroButtonHeight +
                (paddingDefault * 2)
    override val collectionToolbarAdFreeCollectionLockedInfoHeight: Dp
        get() = adFreeCollectionLockedInfoHeight

    override val onboardingFollowButtonSize: Dp
        get() = 40.dp

    override val homeTopBarHeight: Dp
        get() = homeProfileVerticalPadding +
                profileImageHeightToolbar +
                homeProfileVerticalPadding +
                indicatorTabContainerHeight

    override val homeHeaderHeight: Dp
        get() = homeProfileVerticalPadding +
                profileImageHeightToolbar +
                homeProfileVerticalPadding +
                homeFilterHeight

    override val homeProfileVerticalPadding: Dp
        get() = 8.dp

    override val homeFilterWidth: Dp
        get() = (windowWidth - paddingLarge) / 3

    override val homeFilterHeight: Dp
        get() = 64.dp

    override val profileCuratorImageSize: Dp
        get() = artistProfileImageMediumSize
    override val profileCuratorImageShadowSize: Dp
        get() = profileCuratorImageSize * 1.4f

    override val profileHeaderHeight: Dp
        get() = statusBarHeight +
                paddingDefault +
                profileHeaderTitleHeight +
                profileHeaderSubtitleTopPadding +
                profileHeaderSubtitleHeight +
                paddingDefault

    override val profileHeaderTitleHeight: Dp
        get() = if (PlatformFeature.IsIos) {
            28.dp
        } else {
            36.dp
        }
    override val profileHeaderSubtitleTopPadding: Dp
        get() = paddingSmall
    override val profileHeaderSubtitleHeight: Dp
        get() = 16.dp

    override val profileImageHeightToolbar: Dp
        get() = 64.dp

    override val separatorHeight: Dp
        get() = 1.dp
    val separatorToolbarHeight: Dp
        get() = 2.dp
    override val separatorPadding: Padding
        get() = Padding(
            start = paddingDefault,
            end = paddingDefault,
        )

    override val settingsStartPadding: Dp
        get() = 32.dp

    override val wallpaperToolbarTitleWidth: Dp
        get() = windowWidth - (paddingLarge * 2) //(toolbarIconSize * 3)// + (paddingDefault * 4)
    override val toolbarTitleWidthAdjustedForNavigationIcon: Dp
        get() = wallpaperToolbarTitleWidth - iconSize

    override val downloadContentDialogWidth: Dp
        get() = min(480.dp, (windowWidth - (paddingDefault * 4f)))

    override val searchBarHeight: Dp
        get() = toolbarHeight * 1.35f
//    override val searchBarColorItemSize: Dp
//        get() = ((deviceWidth - (searchBarFilterHorizontalPadding * 2)) / SearchColor.All.size) *.8f
    override val searchBarColorItemSize: Dp
        get() = 36.dp
    override val searchBarColorInsideItemSize: Dp
        get() = searchBarColorItemSize.minus(8.dp)

    override val searchRecipeBinColorItemSize: Dp
        get() = 28.dp
    override val searchRecipeBinColorInsideItemSize: Dp
        get() = searchRecipeBinColorItemSize.minus(6.dp)

    override val searchInputTopPadding: Dp
        get() = paddingSmall
    override val searchBarFilterHorizontalPadding: Dp
        get() = paddingDefault
    override val searchFiltersTopPadding: Dp
        get() = statusBarHeight + searchBarHeight + (paddingSmall * 2)
    override val searchFilterToggleHeight: Dp
        get() = 48.dp
    override val searchResultsBarHeight: Dp
        get() = searchBarHeight
    override val searchResultsHeaderHeight: Dp
        get() = searchResultsBarHeight + statusBarHeight + (paddingSmall * 3)
    override val searchResultFeedTopPadding: Dp
        get() = searchResultsHeaderHeight + paddingSmall

    override val selectionRowHeight: Dp
        get() = 56.dp

    override val signInButtonWidth: Dp
        get() = 300.dp
    override val signInButtonHeight: Dp
        get() = 56.dp

    /** As defined in [androidx.compose.material3.tokens.StateLayerSize] **/
    override val iconButtonLayerSize: Dp
        get() = 40.dp

    override val iconSizeSmall: Dp
        get() = 14.dp
    override val iconSize: Dp
        get() = 24.dp
    override val iconSizeLarge: Dp
        get() = 28.dp
    override val iconSizeMediumLarge: Dp
        get() = 34.dp
    override val iconSizeMediumLarge2: Dp
        get() = 42.dp
    override val iconSizeExtraLarge: Dp
        get() = iconSizeLarge * 2

    override val wallpaperPreviewWidth: Dp
        get() = min(windowWidth - (paddingDefault * 2), 400.dp)
    override val wallpaperPreviewWidthMax: Dp
        get() = windowWidth

    private val wallpaperPreviewChromeHeight: Dp
        get() = statusBarHeight +
                toolbarHeight +
                (toolbarHeight + paddingDefault * 2) +   // The profile row
                (toolbarHeight + paddingDefault * 2) +   // The button row
                navigationBarHeight

    override val wallpaperPreviewHeightWallpaperShowcase: Dp
        get() = (windowHeight * .58f) -
                (if (nativeBottomSheetUiSupported) { statusBarHeight - 8.dp } else { 0.dp })

    override val wallpaperShowcaseTopControlButtonsVerticalOffset: Dp
        get() = if (nativeBottomSheetUiSupported) { 0.dp } else { statusBarHeight }

    override val wallpaperShowcaseControlButtonEdgePadding: Dp
        get() = if (nativeBottomSheetUiSupported) { paddingSmall } else { paddingDefault }
    override val wallpaperShowcaseControlButtonSpacing: Dp
        get() = paddingSmall

    override val wallpaperShowcaseControlButtonWidth: Dp
        get() = 108.dp
    override val wallpaperShowcaseControlButtonHeight: Dp
        get() = iconSizeMediumLarge

    override fun wallpaperDetailViewHeight(itemCount: Int): Dp =
        (wallpaperDetailItemHeight * itemCount) + (wallpaperDetailItemVerticalSpacerHeight * (itemCount - 1))
    override val wallpaperDetailItemHeight: Dp
        get() = wallpaperDetailItemImageSize
    override val wallpaperDetailItemImageSize: Dp
        get() = 20.dp
    override val wallpaperDetailItemVerticalSpacerHeight: Dp
        get() = 6.dp

    override val unlockWallpaperUnlockContainerSize: DpSize
        get() = DpSize(
            width = unlockWallpaperUpgradeContainerSize.width,
            height = (windowHeight - statusBarHeight) - unlockWallpaperUpgradeContainerSize.height,
        )
    override val unlockWallpaperUpgradeContainerSize: DpSize
        get() = DpSize(
            width = windowWidth,
            height = (windowHeight - statusBarHeight) * (if (windowIsCompact) { .4f } else { .5f } ),
        )

    override val unlockWallpaperPreviewWidth: Dp
        get() = ((windowHeight - statusBarHeight) / 2) - toolbarHeight - heroButtonHeightXl - paddingDefault * 4
    override val unlockWallpaperPreviewHeight: Dp
        get() = unlockWallpaperPreviewWidth
    override val unlockWallpaperMenuItemHeight: Dp
        get() = iconSize.plus(6.dp)
    override val unlockWallpaperSdToHdIconBetweenSpace: Dp
        get() = ((upgradeHighlightPhoneFrameWidth / 2) - iconSizeLarge)

    private val upgradeHighlightLowResImageScale: Float by lazy { 1 / 10f }
    override val upgradeHighlightPreviewsHeight: Dp
        get() = 200.dp
    override val upgradeHighlightLowResWidth: Dp
        get() = upgradeHighlightHighResWidth * upgradeHighlightLowResImageScale
    override val upgradeHighlightLowResHeight: Dp
        get() =  upgradeHighlightHighResHeight * upgradeHighlightLowResImageScale
    override val upgradeHighlightHighResWidth: Dp
        get() = upgradeHighlightPhoneFrameWidth - (upgradeHighlightPhoneFrameBorderSize * 2)
    override val upgradeHighlightHighResHeight: Dp
        get() = upgradeHighlightPhoneFrameHeight - upgradeHighlightPhoneFrameBorderSize
    private val upgradeHighlightPhoneFrameBorderSize: Dp by lazy { 8.dp }
    override val upgradeHighlightPhoneFrameWidth: Dp
        // 0.73058254 is the aspect ratio of the Media.PhoneFrameUpper image.
        get() = upgradeHighlightPhoneFrameHeight * 0.73058254f
    override val upgradeHighlightPhoneFrameHeight: Dp
        get() = upgradeHighlightPreviewsHeight
    override val plusHeroImageWidth: Dp
        get() = windowWidth
    override val plusHeroImageHeight: Dp
        get() = if (windowIsCompact) {
            96.dp
        } else {
            160.dp
        }
    override val paywallFeatureIconSize: Dp
        get() = if (windowIsCompact) { 24.dp } else { 26.dp }
    override val paywallFeatureItemHeight: Dp
        get() = paywallFeatureIconSize.plus(12.dp)

    override val bottomSheetHeightOneOption: Dp
        get() = 180.dp
    override val bottomSheetHeightTwoOptions: Dp
        get() = 240.dp
    override val bottomSheetHeightThreeOptions: Dp
        get() = 300.dp

    override val folderToolbarMinHeight: Dp
        get() = toolbarXlHeight

    override val folderToolbarMaxHeight: Dp
        get() = folderToolbarTitleTop +
                folderToolbarTitleHeight +
                paddingLarge

    override val folderToolbarTitleTop: Dp
        get() = paddingDefault

    override val folderToolbarTitleHeight: Dp
        get() = 72.dp

    override val settingItemPreviewRowHorizontalPadding: Dp
        get() = paddingDefault * 2
    override val settingItemPreviewRowVerticalPadding: Dp
        get() = paddingSmall
    override val appIconSettingItemHeight: Dp
        get() = 72.dp
    override val appIconSettingItemWidth: Dp
        get() = 72.dp

    override val pageIndicatorContainerHeight: Dp
        get() = 20.dp

    override val adCloseButtonPaddingTop: Dp
        get() = (paddingDefault + paddingSmall) / 2
    override val adCloseButtonPaddingEnd: Dp
        get() = paddingDefault

    override val adFreeCollectionLockedInfoWidth: Dp
        get() = windowWidth - (paddingLarge * 2)
    override val adFreeCollectionLockedInfoHeight: Dp
        get() = 124.dp
    override val folderPreviewWidth: Dp
        get() = feedItemWideSpanWidth
    override val folderPreviewHeight: Dp
        get() = folderPreviewWallpaperFeedItemWidth * 2f
    override val folderPreviewWallpaperFeedItemWidth: Dp
        get() = folderPreviewWidth / 4
    override val folderPreviewWallpaperFeedItemHeight: Dp
        get() = folderPreviewHeight
}

fun ViewSpecArbitratorDefaultPreset(
    config: ViewSpecArbitratorConfig = ViewSpecArbitratorConfigMock(),
): ViewSpecArbitrator = ViewSpecArbitratorDefault(
    config = config,
)
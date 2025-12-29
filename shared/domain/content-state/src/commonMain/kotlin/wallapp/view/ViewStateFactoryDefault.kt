package wallapp.view

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.account.Account
import wallapp.ads.reward.internal.RewardAdInternalSpec
import wallapp.appconfig.AppConfig
import wallapp.content.model.ContentCategorySpec
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Wallpaper
import wallapp.content.model.WallpaperId
import wallapp.content.prefetch.ContentPrefetcher
import wallapp.content.state.ContentState
import wallapp.content.state.ContentStateFeed
import wallapp.content.state.account.AccountOverviewViewState
import wallapp.content.state.account.AccountViewEvent
import wallapp.content.state.account.AccountViewEventSink
import wallapp.content.state.account.AccountViewState
import wallapp.content.state.artist.ArtistsViewState
import wallapp.content.state.collection.CollectionActionButtonViewState
import wallapp.content.state.collection.CollectionActionViewState
import wallapp.content.state.collection.CollectionToolbarViewState
import wallapp.content.state.collection.CollectionViewState
import wallapp.content.state.connections.ConnectionType
import wallapp.content.state.connections.ConnectionsViewState
import wallapp.content.state.dataconsent.DataConsentViewState
import wallapp.content.state.downloadstatus.DownloadStatusViewState
import wallapp.content.state.error.rewardad.ErrorRewardAdMode
import wallapp.content.state.error.rewardad.ErrorRewardAdViewEvent
import wallapp.content.state.error.rewardad.ErrorRewardAdViewState
import wallapp.content.state.explore.ExploreHeaderViewState
import wallapp.content.state.explore.ExploreViewState
import wallapp.content.state.explore.HighlightCarouselViewState
import wallapp.content.state.explore.LastFeedOffsetUpdateSink
import wallapp.content.state.feed.FeedContentPreviewViewSpec
import wallapp.content.state.firstrun.FirstRunViewState
import wallapp.content.state.folder.FolderPreviewViewState
import wallapp.content.state.folder.FolderToolbarViewState
import wallapp.content.state.folder.FolderViewState
import wallapp.content.state.home.HomeOnboardingHeaderViewState
import wallapp.content.state.home.HomeOnboardingViewState
import wallapp.content.state.paging.ContentStatePagingController
import wallapp.content.state.profile.ProfileConnectionViewState
import wallapp.content.state.profile.ProfileConnectionsViewState
import wallapp.content.state.profile.ProfileHeaderViewState
import wallapp.content.state.profile.ProfileImageIndicatorViewState
import wallapp.content.state.profile.ProfileImageViewState
import wallapp.content.state.profile.ProfileViewState
import wallapp.content.state.rewardadinternal.RewardAdInternalPlaybackState
import wallapp.content.state.rewardadinternal.RewardAdInternalViewEvent
import wallapp.content.state.rewardadinternal.RewardAdInternalViewEventSink
import wallapp.content.state.rewardadinternal.RewardAdInternalViewState
import wallapp.content.state.search.SearchBarViewState
import wallapp.content.state.search.SearchColorViewState
import wallapp.content.state.search.SearchColorsViewState
import wallapp.content.state.search.SearchInputViewState
import wallapp.content.state.search.SearchRecipeBinText
import wallapp.content.state.search.SearchRecipeBinViewSpec
import wallapp.content.state.search.SearchRecipeBinViewState
import wallapp.content.state.search.SearchResultsHeaderViewState
import wallapp.content.state.search.SearchResultsViewState
import wallapp.content.state.search.SearchViewEvent
import wallapp.content.state.search.SearchViewEventSink
import wallapp.content.state.settings.SettingGroupViewState
import wallapp.content.state.settings.SettingViewState
import wallapp.content.state.settings.SettingViewStateFactory
import wallapp.content.state.signin.SignInButtonViewState
import wallapp.content.state.signup.SignUpViewState
import wallapp.content.state.toolbar.CollapsingToolbarStateWrapper
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.content.state.upgrade.plus.button.UpgradeButtonViewState
import wallapp.content.state.upgrade.plus.promo.UpgradePromoViewState
import wallapp.content.state.wallpaper.WallpaperPreviewViewSpec
import wallapp.content.state.wallpaper.WallpaperSingleActionViewState
import wallapp.content.state.wallpaper.arbitrateWallpaperPreviewShowPlusButton
import wallapp.content.state.widget.EdgeFadeViewSpec
import wallapp.content.state.widget.EdgeFadeViewState
import wallapp.content.state.widget.NoDataViewState
import wallapp.data.artist.Artist
import wallapp.data.artist.ArtistState
import wallapp.data.collection.CollectionState
import wallapp.data.content.ContentResult.ArtistsContentResult
import wallapp.data.content.ContentResult.CollectionScreenContentResult
import wallapp.data.content.ContentResult.ConnectionsContentResult
import wallapp.data.content.ContentResult.ConnectionsSummaryContentResult
import wallapp.data.folder.FolderState
import wallapp.data.following.FollowState
import wallapp.data.social.SocialLinks
import wallapp.entitlement.EntitlementRepository
import wallapp.font.TextStyle
import wallapp.graphics.Color
import wallapp.graphics.Colors
import wallapp.image.Image
import wallapp.image.ImageVideoPlaybackCallbacks
import wallapp.image.ImageVideoState
import wallapp.image.ImageViewSpecFactory
import wallapp.image.carousel.ImageCarouselType
import wallapp.image.sized.SizedImage
import wallapp.image.updateAnimatedSpecWith
import wallapp.license.state.isLicensedAny
import wallapp.log.Log
import wallapp.onboarding.OnboardingState
import wallapp.permission.SystemPermissionStatus
import wallapp.pixel.alert.AlertViewState
import wallapp.pixel.alert.AlertViewStateOkCancel
import wallapp.pixel.animation.AnimatedViewSpec
import wallapp.pixel.button.ButtonAppearance
import wallapp.pixel.button.ButtonViewState
import wallapp.pixel.feed.FeedScrollPositionUpdateSink
import wallapp.pixel.feed.FeedScrollStateWrapper
import wallapp.pixel.feed.FeedState
import wallapp.pixel.feed.FeedViewSpec
import wallapp.pixel.feed.FeedViewState
import wallapp.pixel.feed.MaxFeedWidthNoPaddingViewSpec
import wallapp.pixel.feed.MaxFeedWidthViewSpec
import wallapp.pixel.globaloverlay.GlobalOverlayViewState
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.image.carousel.ImageCarouselViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.menu.MenuItemViewState
import wallapp.pixel.message.MessageBarViewState
import wallapp.pixel.navigationbar.NavigationBarItem
import wallapp.pixel.navigationbar.NavigationBarViewState
import wallapp.pixel.paging.PagingViewEventSink
import wallapp.pixel.selection.SelectionGroupViewState
import wallapp.pixel.selection.SelectionViewEventSink
import wallapp.pixel.selection.SelectionViewState
import wallapp.pixel.selection.SelectionViewStyle
import wallapp.pixel.spacer.SpacerViewState
import wallapp.pixel.swipetodismiss.OnSwipeToDismiss
import wallapp.pixel.tab.TabViewState
import wallapp.pixel.tab.TabsViewState
import wallapp.pixel.text.Text
import wallapp.pixel.text.TextStyleBody
import wallapp.pixel.text.TextStyleCallToAction
import wallapp.pixel.text.TextStyleCaption
import wallapp.pixel.text.TextStyleDisplay
import wallapp.pixel.text.TextStyleHeadline
import wallapp.pixel.text.TextStyleSubheading
import wallapp.pixel.text.TextStyleSubheadingActive
import wallapp.pixel.toolbar.ToolbarViewState
import wallapp.pixel.util.ColorOptional
import wallapp.pixel.util.DpOptional
import wallapp.pixel.view.FixedPositionViews
import wallapp.pixel.view.View
import wallapp.pixel.view.ViewContentScale
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewEventSink
import wallapp.pixel.view.ViewState
import wallapp.pixel.view.ViewsVisibleListener
import wallapp.resources.Url
import wallapp.resources.image.ImageRepository
import wallapp.resources.string.StringRepository
import wallapp.screen.ScreenArgument.CollectionActionScreenArgument
import wallapp.screen.ScreenArgument.ConnectionsScreenArgument
import wallapp.search.SearchResult
import wallapp.search.SearchSessionManager
import wallapp.search.model.SearchCategorySpec
import wallapp.search.model.SearchColor
import wallapp.text.TextAlign
import wallapp.text.TextButtonLabel
import wallapp.text.TextCaption
import wallapp.text.TextDisplay
import wallapp.text.TextEmail
import wallapp.text.TextFeedTitle
import wallapp.text.TextPlaceholder
import wallapp.text.TextSelection
import wallapp.text.TextSettingTitle
import wallapp.text.TextSignInButton
import wallapp.text.TextTabSelected
import wallapp.text.TextTabUnselected
import wallapp.text.TextToolbarTitle
import wallapp.theme.ColorToken
import wallapp.theme.Theme
import wallapp.theme.ThemeManager
import wallapp.theme.customColorToken
import wallapp.unit.Alignment
import wallapp.unit.width
import wallapp.view.feed.FeedFormatter
import wallapp.view.menu.MenuItemFactory
import wallapp.view.shape.ShapeSpecFactory
import kotlin.time.Duration

class ViewStateFactoryDefault(
    private val viewFactory: ViewFactory,
    private val viewEventFactory: ViewEventFactory,
    private val viewStateMapper: ViewStateMapper,
    private val viewSpecArbitrator: ViewSpecArbitrator,
    private val viewSpecFactory: ViewSpecFactory,
    private val imageViewSpecFactory: ImageViewSpecFactory,
    private val shapeSpecFactory: ShapeSpecFactory,
    private val settingViewStateFactory: SettingViewStateFactory,
    private val themeManager: ThemeManager,
    private val feedFormatter: FeedFormatter,
    private val menuItemFactory: MenuItemFactory,
    private val strings: StringRepository,
    private val imageRepository: ImageRepository,
    private val appConfig: AppConfig,
    private val entitlementRepository: EntitlementRepository,
) : ViewStateFactory {

    override fun createToolbar(
        centeredText: String,
        showBack: Boolean,
        containerColorOverride: ColorToken,
    ): ToolbarViewState {
        return ToolbarViewState(
            navigationIcon = if (showBack) {
                menuItemFactory.back
            } else {
                null
            },
            title = menuItemFactory.centeredText(TextToolbarTitle(centeredText)),
            centeredTitle = true,
            containerColorOverride = containerColorOverride,
        )
    }

    override fun createMenuItemViewState(
        menuItems: List<MenuItem>,
        itemPadding: Dp,
    ) = MenuItemViewState(
        menuItems = menuItems,
        itemPadding = itemPadding,
    )

    override fun createFollowButton(
        artistId: ArtistId,
        followState: FollowState?,
        showIcon: Boolean,
        animatedViewSpec: AnimatedViewSpec?,
    ): ButtonViewState {
        val shortLabel = true
        val isFollowing = followState?.isFollowing ?: false

        val label = if (isFollowing) {
            strings.unfollow
        } else {
            strings.followArbitrated(shortLabel)
        }
        val (contentColor: ColorToken?, containerColor) = if (isFollowing) {
            ColorToken.LocalContent to ColorToken.ThemeTertiary
        } else {
            ColorToken.LocalContent to ColorToken.ThemePrimary
        }

        val menuItem = menuItemFactory.createHorizontalGroup(
            listOfNotNull(
                menuItemFactory.createLabel(
                    TextButtonLabel(label, colorToken = contentColor),
                ),
            ),
        )

        return viewStateMapper.mapButtonViewState(
            menuItem = menuItem,
            animatedViewSpec = animatedViewSpec,
            containerColorToken = containerColor,
            eventHandler = viewEventFactory.createFollowing(artistId, followState),
        )
    }

    override fun createImageCarouselViewState(
        showcaseWallpapers: List<Wallpaper>,
        wallpaperPreviewViewSpec: WallpaperPreviewViewSpec,
        currentIndex: MutableStateFlow<Int>?,
    ): ImageCarouselViewState {
        val images = showcaseWallpapers.map { viewStateMapper
            .mapWallpaperImage(
                it,
                viewSpec = wallpaperPreviewViewSpec,
            )
        }
        return createImageCarouselViewState(
            images = images,
            currentIndex = currentIndex,
        )
    }

    override fun createImageCarouselViewState(
        images: List<ImageViewState>,
        currentIndex: MutableStateFlow<Int>?,
    ): ImageCarouselViewState {
        return ImageCarouselViewState(
            type = ImageCarouselType.Fade,
            scrim = null,
            imageViewStates = images,
        ) {
            currentIndex?.value = it
        }
    }

    override fun createSignInButtonAppleViewState(
        viewEventHandler: ViewEventHandler,
        shortLabel: Boolean,
    ): SignInButtonViewState.Apple {
        val label = TextSignInButton(
            string = if (shortLabel) {
                strings.signIn
            } else {
                strings.signInWithApple
            },
        )
        return SignInButtonViewState.Apple(
            viewSpec = viewSpecFactory.signInButtonViewSpec,
            imageLight = imageRepository.platformAppleLogoSquareWhite,
            imageDark = imageRepository.platformAppleLogoSquareBlack,
            label = label,
            shapeSpec = shapeSpecFactory.buttonShapeSpecDefault,
            viewEventHandler = viewEventHandler,
        )
    }

    override fun createSignInButtonGoogleViewState(
        viewEventHandler: ViewEventHandler,
        shortLabel: Boolean,
    ): SignInButtonViewState.Google {
        val label = TextSignInButton(
            string = if (shortLabel) {
                strings.signIn
            } else {
                strings.signInWithGoogle
            },
        )
        return SignInButtonViewState.Google(
            viewSpec = viewSpecFactory.signInButtonViewSpec,
            image = imageRepository.platformGoogleGSmall,
            label = label,
            shapeSpec = shapeSpecFactory.buttonShapeSpecDefault,
            viewEventHandler = viewEventHandler,
        )
    }

    fun createSignInButtonViewStates(
        appleSignInOnClick: ViewEventHandler?,
        googleSignInOnClick: ViewEventHandler,
        shortLabel: Boolean,
    ): List<SignInButtonViewState> {
        return mutableListOf<SignInButtonViewState>().apply {
            if (appleSignInOnClick != null) {
                add(createSignInButtonAppleViewState(appleSignInOnClick, shortLabel))
            }
            add(createSignInButtonGoogleViewState(googleSignInOnClick, shortLabel))
        }
    }

    override fun createAccountOverviewLoading() =
        AccountOverviewViewState.Loading(
            viewSpec = viewSpecFactory.accountOverviewViewSpec,
        )

    private fun Account.arbitrateDisplayLabels(): Pair<String?, String?> {
        return if (displayName != null) {
            displayName to null
        } else {
            null to email
        }
    }

    override fun createAccountOverviewSignedIn(
        account: Account,
        profileImage: Image,
        messageBarHeight: Dp,
    ): AccountOverviewViewState.SignedIn {
        val (displayName, email) = account.arbitrateDisplayLabels()

        return AccountOverviewViewState.SignedIn(
            viewSpec = viewSpecFactory.accountOverviewViewSpec,
            profileImage = ProfileImageViewState(
                profileImage = profileImage,
                imageViewSpec = imageViewSpecFactory.accountOverviewProfileImageViewSpec,
            ),
            displayName = displayName?.let { TextSettingTitle(it) },
            email = email?.let { TextEmail(it) },
            onClick = viewEventFactory.createNavigateToAccount(),
            messageBarHeight = messageBarHeight,
            profileImageContentDescription = strings.yourProfileImage,
        )
    }

    override fun createAccountOverviewSignedOut(
        googleSignInOnClick: ViewEventHandler,
        appleSignInOnClick: ViewEventHandler?,
        messageBarHeight: Dp,
    ) = AccountOverviewViewState.SignedOut(
        viewSpec = viewSpecFactory.accountOverviewViewSpec,
        profileImage = null,
        profileEventHandler = null,
        signInMessage = null,
        signInButtons = createSignInButtonViewStates(
            appleSignInOnClick = appleSignInOnClick,
            googleSignInOnClick = googleSignInOnClick,
            shortLabel = true,
        ),
        signInButtonsUseLightTheme = themeManager.theme.value.isLight,
        messageBarHeight = messageBarHeight,
    )

    override fun createAccountViewState(
        account: Account?,
        profileImage: Image,
        subscribedToNewsletter: MutableStateFlow<Boolean>,
        reportUsageStats: MutableStateFlow<Boolean>,
        receiveNotificationsOnChangedEvent: (Boolean) -> Unit,
        receiveNotifications: MutableStateFlow<Boolean>,
        topBarContainerColorToken: ColorToken,
        postNotificationPermissionStatus: SystemPermissionStatus,
        privacySettingsViewEvent: AccountViewEvent?,
        accountViewEventSink: AccountViewEventSink,
    ): AccountViewState {
        val eventSink = accountViewEventSink as ViewEventSink
        val profileImageViewState = if (account != null) {
            createAccountProfileImageViewState(
                profileImage,
                imageViewSpecFactory.accountProfileImageViewSpec,
                eventHandler = viewEventFactory.createNavigateToProfileImagePicker(),
            )
        } else {
            null
        }

        val restorePurchases = settingViewStateFactory.createRestorePurchases(
            ViewEventHandler.Event(
                eventSink = eventSink,
                event = AccountViewEvent.RestorePurchases,
            )
        )

        val privacySettings = privacySettingsViewEvent?.let {
            settingViewStateFactory.createPrivacySettings(
                ViewEventHandler.Event(
                    eventSink = eventSink,
                    event = privacySettingsViewEvent,
                )
            )
        }
        val receiveNotificationsSettings = if (postNotificationPermissionStatus != SystemPermissionStatus.Authorized) {
            settingViewStateFactory.createNotificationsTapToGrantPermission(
                ViewEventHandler.Event(
                    eventSink = eventSink,
                    event = AccountViewEvent.NotificationsTapToGrantPermission,
                )
            )
        } else {
            settingViewStateFactory.createReceiveNotificationsSwitch(
                receiveNotifications = receiveNotifications,
                onChangedEvent = receiveNotificationsOnChangedEvent,
            )
        }


        val settings: MutableList<SettingViewState> = mutableListOf(
            settingViewStateFactory.spacerNormal,
            receiveNotificationsSettings,
            settingViewStateFactory.spacerSection,
            privacySettings,
            settingViewStateFactory.createReportUsageStatsSwitch(reportUsageStats),
            account?.let {
                settingViewStateFactory.createJoinNewsletterSwitch(
                    subscribedToNewsletter = subscribedToNewsletter,
                    onChangedEvent = viewEventFactory.createReceiveNewsletter(),
                )
            },
            settingViewStateFactory.spacerSection,
            restorePurchases,
        )
            .filterNotNull()
            .toMutableList()

        if (entitlementRepository.licenseState.value.isLicensedAny()) {
            val manageSubscription = settingViewStateFactory.createManageSubscription(
                ViewEventHandler.Event(
                    eventSink = eventSink,
                    event = AccountViewEvent.ManageSubscription
                )
            )
            settings.add(manageSubscription)
        }

        if (account != null) {
            val deleteAccount = settingViewStateFactory.createDeleteAccount(
                ViewEventHandler.Event(
                    eventSink = eventSink,
                    event = AccountViewEvent.DeleteAccount,
                )
            )
            val signOutAccount = settingViewStateFactory.createSignOutAccount(
                ViewEventHandler.Event(
                    eventSink = eventSink,
                    event = AccountViewEvent.SignOut,
                )
            )

            val accountSettings = listOf(
                deleteAccount,
                signOutAccount,
            )

            settings.addAll(accountSettings)
        }

        val toolbarViewState = createStandardToolbarViewState(
            strings.account,
            containerColorOverride = topBarContainerColorToken,
        )

        return AccountViewState(
            toolbarViewState = toolbarViewState,
            profileImage = profileImageViewState,
            displayName = account?.displayName?.let { TextSettingTitle(it) },
            email = account?.email?.let { TextEmail(it) },
            settings = settings,
        )
    }

    override fun createAccountProfileImageViewState(
        profileImage: Image,
        imageViewSpec: ImageViewSpec,
        eventHandler: ViewEventHandler?,
    ): ProfileImageViewState {
        return ProfileImageViewState(
            image = profileImage,
            imageViewSpec = imageViewSpec,
            indicator = ProfileImageIndicatorViewState(
                viewSpec = viewSpecFactory.profileImageIndicatorViewSpec,
                image = imageRepository.edit,
                colorToken = ColorToken.ThemeOnBackground
            ),
            eventHandler = eventHandler,
        )
    }

    override fun createArtistsViewState(
        data: ArtistsContentResult?,
        topBarContainerColor: ColorToken,
    ): ArtistsViewState {
        val collectors = data?.artists
        if (data == null || collectors == null) {
            return ArtistsViewState.Loading
        }

        val feedViewSpec = viewSpecFactory.feedGridViewSpec
        val views = feedFormatter.format(
            views = collectors.map {
                viewFactory.createArtistPreview(
                    it,
                    sizedImage = SizedImage.ArtistMedium,
                    imageViewSpec = imageViewSpecFactory.artistsProfileImageViewSpec,
                )
            },
            spacerStart = FeedFormatter.FeedSpacerTopToolbar,
            spacerEnd = FeedFormatter.FeedSpacerBottomNavigationBar,
            canInsertAds = true,
        )

        val toolbar = ToolbarViewState(
            navigationIcon = menuItemFactory.back,
            title = menuItemFactory.centeredText(TextToolbarTitle(strings.allArtists)),
            containerColorOverride = topBarContainerColor,
        )

        Log.d("artistsToolbar: $toolbar")

        return ArtistsViewState.Success(
            feedViewState = FeedViewState(
                feedViewSpec = feedViewSpec,
                views = views,
                toolbar = toolbar,
                viewsVisibleListener = null,
            )
        )
    }

    override fun createSignUpViewState(
        googleSignInButtonViewState: SignInButtonViewState.Google?,
        appleSignInButtonViewState: SignInButtonViewState.Apple?,
        showUpgradeButton: Boolean,
        skipOnClick: () -> Unit,
    ): SignUpViewState {
        // This is a placeholder used only for desktop compatibility.
        val logoStatic = imageRepository.appLogoPreviewCitrus
        val welcomeLabel = TextDisplay(strings.welcomeLabel)
        val skipLabel = TextStyleSubheading(strings.skipForNow)

        val upgradeButton = if (showUpgradeButton) {
            createUpgradeButtonViewState(
                showFullLabel = true,
                heroSize = true,
            )
        } else {
            null
        }

        return SignUpViewState(
            logoStatic = logoStatic,
            welcomeLabel = welcomeLabel,
            appleSignInButtonViewState = appleSignInButtonViewState,
            googleSignButtonInViewState = googleSignInButtonViewState,
            upgradeButton = upgradeButton,
            skipLabel = skipLabel,
            skipOnClick = skipOnClick,
        )
    }

    fun createUpgradeButtonLabel(showFullLabel: Boolean): MenuItem {
        fun createLabel(
            string: String,
            isBold: Boolean, // Ignored for now
            colorToken: ColorToken? = null,
        ): MenuItem {
            return menuItemFactory.createLabel(
                TextButtonLabel(string, colorToken = colorToken),
//                fontWeight = if (isBold) { FontWeight.Bold } else { null },
            )
        }

        return if (showFullLabel) {
            val colorToken = ColorToken.ThemeOnBackground
            val first = createLabel(
                strings.join,
                isBold = false,
                colorToken,
            )
            val second = createLabel(
                strings.plus,
                isBold = true,
                colorToken,
            )
            val spacer = menuItemFactory.createSpacer(width = 4.dp)
            menuItemFactory.createHorizontalGroup(listOf(first, spacer, second))
        } else {
            val spacer = menuItemFactory.createSpacer(width = 4.dp)
            val plus = createLabel(strings.plus, isBold = true)
            menuItemFactory.createHorizontalGroup(listOf(spacer, plus))
        }
    }

    fun createUpgradeButtonViewState(
        showFullLabel: Boolean,
        heroSize: Boolean,
    ) = UpgradeButtonViewState(
        viewStateMapper.mapPlusIndicator(
            indicatorSize = viewSpecArbitrator.iconSizeLarge,
            indicatorOnBackground = false,
            viewEventHandler = null,
        ),
        label = createUpgradeButtonLabel(showFullLabel),
        viewEventHandler = viewEventFactory.createNavigateToPaywall(subscriptionPlan = SubscriptionPlan.PlusUnlimited),
        heroSize = heroSize,
        shapeSpec = shapeSpecFactory.buttonShapeSpecDefault,
    )

    override fun createDataConsentViewState(
        acceptedTerms: MutableStateFlow<Boolean>,
        subscribedToNewsletter: StateFlow<Boolean>,
        reportUsageStats: MutableStateFlow<Boolean>,
        receiveNotifications: MutableStateFlow<Boolean>,
        showAcceptedTermsShimmer: Boolean,
        continueEventHandler: ViewEventHandler,
    ): DataConsentViewState {
        val receiveNotificationsSetting = settingViewStateFactory.createReceiveNotificationsSwitch(
            receiveNotifications = receiveNotifications,
            onChangedEvent = viewEventFactory.createReceiveNotifications(),
        )
        val acceptTermsSetting = settingViewStateFactory
            .createAcceptTermsSwitch(acceptedTerms, showAcceptedTermsShimmer)
        val reportUsageStatsSetting = settingViewStateFactory
            .createReportUsageStatsSwitch(reportUsageStats)
        val subscribedToNewsletterSetting = settingViewStateFactory
            .createJoinNewsletterSwitch(subscribedToNewsletter, viewEventFactory.createReceiveNewsletter())
        val itemSpacer = settingViewStateFactory.spacerNormal

        val continueButtonColorToken: ColorToken? = null
        val buttonEnabled = acceptedTerms.value
        val continueButton = menuItemFactory.createActionButton(
            image = null,
            text = TextButtonLabel(strings.`continue`, colorToken = continueButtonColorToken),
            eventHandler = continueEventHandler,
            buttonAppearance = if (buttonEnabled) {
                ButtonAppearance.Default
            } else {
                ButtonAppearance.DisabledWithClick
            },
            contentColorToken = continueButtonColorToken,
        )

        return DataConsentViewState(
            title = TextDisplay(strings.dataSharingTitle),
            settings = listOf(
                receiveNotificationsSetting,
                itemSpacer,
                reportUsageStatsSetting,
                itemSpacer,
                subscribedToNewsletterSetting,
                itemSpacer,
                acceptTermsSetting,
            ),
            continueButton = continueButton,
            footer = createDataConsentFooter(),
        )
    }

    private fun createDataConsentFooter(): MenuItem {
        return menuItemFactory.createHorizontalGroup(
            items = listOf(
                menuItemFactory.createLabel(
                    TextStyleCaption(strings.privacyPolicy),
                    onClick = viewEventFactory.createNavigateToUrl(Url.PrivacyPolicy),
                ),
                menuItemFactory.createSpacer(viewSpecArbitrator.paddingSmall),
                menuItemFactory.createLabel(
                    TextStyleCaption(strings.dotCharacter),
                ),
                menuItemFactory.createSpacer(viewSpecArbitrator.paddingSmall),
                menuItemFactory.createLabel(
                    TextStyleCaption(strings.termsOfService),
                    onClick = viewEventFactory.createNavigateToUrl(Url.TermsOfService),
                ),
            ),
            height = viewSpecArbitrator.unlockWallpaperMenuItemHeight,
        )
    }

    override fun createFirstRunViewState(
        currentScreenType: OnboardingState,
        showcaseBackgroundImages: List<ImageViewState>,
        currentShowcaseIndex: MutableStateFlow<Int>?,
        googleSignInButtonViewState: SignInButtonViewState.Google,
        appleSignInButtonViewState: SignInButtonViewState.Apple?,
        acceptedTerms: MutableStateFlow<Boolean>,
        subscribedToNewsletter: StateFlow<Boolean>,
        reportUsageStats: MutableStateFlow<Boolean>,
        receiveNotifications: MutableStateFlow<Boolean>,
        showAcceptedTermsShimmer: Boolean,
        continueEventHandler: ViewEventHandler,
        isShowSocialButton: Boolean,
        showUpgradeButton: Boolean,
        skipOnClick: () -> Unit,
    ): FirstRunViewState {
        val imageCarousel = createImageCarouselViewState(
            images = showcaseBackgroundImages,
            currentIndex = currentShowcaseIndex,
//            useGradientScrim = true,
        )

        val screen = if (currentScreenType == OnboardingState.SignUp) {
            createSignUpViewState(
                if (isShowSocialButton) googleSignInButtonViewState else null,
                if (isShowSocialButton) appleSignInButtonViewState else null,
                showUpgradeButton = showUpgradeButton,
                skipOnClick,
            )
        } else {
            createDataConsentViewState(
                acceptedTerms,
                subscribedToNewsletter,
                reportUsageStats,
                receiveNotifications,
                showAcceptedTermsShimmer,
                continueEventHandler,
            )
        }

        return FirstRunViewState.Success(
            theme = themeManager.darkTheme,
            backgroundCarousel = imageCarousel,
            screen = screen,
        )
    }

    override fun createNoFavoritesViewState() = NoDataViewState(
        title = TextStyleSubheading(strings.noFavoritesFoundTitle, textAlign = TextAlign.Center),
        summary = TextStyleBody(strings.noFavoritesFoundSummary, textAlign = TextAlign.Center),
        image = imageRepository.heartLarge,
    )

    override fun createNoPurchasesViewState() = NoDataViewState(
        title = TextStyleSubheading(strings.noPurchasesFoundTitle, textAlign = TextAlign.Center),
        summary = TextStyleBody(strings.noPurchasesFoundSummary, textAlign = TextAlign.Center),
        image = imageRepository.collectionsLarge,
    )

    override fun createNoCollectionsViewState() = NoDataViewState(
        title = TextStyleSubheading(strings.noCollectionsFoundTitle, textAlign = TextAlign.Center),
        summary = null,
        image = imageRepository.collectionsLarge,
    )


    private fun createHomeOnboardingHeaderViewState(
        artists: List<ArtistState>,
        artistFollowOnboardingCount: Int,
        theme: Theme,
    ): HomeOnboardingHeaderViewState {
        val followingCount = artists.filter { it.followState?.isFollowing == true }.size

        val title = TextDisplay(strings.homeOnboardingTitle)
        val summary = TextStyleSubheading(
            string = strings.homeOnboardingSummary(followingCount, artistFollowOnboardingCount),
            textAlign = TextAlign.Center,
            ignoreLargeSystemFontScaling = true,
        )

        return HomeOnboardingHeaderViewState(
            viewSpec = viewSpecFactory.homeOnboardingHeaderViewSpec,
            theme = theme,
            title = title,
            summary = summary,
        )
    }

    /**
     * Inserts spacers to maintain the feed columns. Works around a quirk in the current Compose
     * rendering whereby, in a 2 column feed, if the last row has a single item, that item will be
     * rendered at full screen width. See #1823.
     */
    fun List<View>.insertSpacersToMaintainFeedColumns(): List<View> {
        val blankSpaces = size % viewSpecArbitrator.feedColumns
        if (blankSpaces == 0) return this

        val spacers = List(blankSpaces) {
            val menuItem = menuItemFactory.createSpacer(
                width = viewSpecArbitrator.feedItemSingleSpanWidth
            )
            View(
                viewState = MenuItemViewState(menuItem),
                viewSpec = viewSpecFactory.artistPreviewViewSpecOnboarding
            )
        }

        return this.toMutableList().apply {
            addAll(spacers)
        }.toList()
    }

    override fun createHomeOnboardingViewState(
        artists: List<ArtistState>?,
        artistFollowOnboardingCount: Int,
        theme: Theme,
        scrollStateWrapper: FeedScrollStateWrapper,
        followToggleExtraAction: (ArtistId) -> Unit,
    ): HomeOnboardingViewState {
        return if (artists.isNullOrEmpty()) {
            HomeOnboardingViewState.Loading
        } else {
            val toolbar: HomeOnboardingHeaderViewState =
                createHomeOnboardingHeaderViewState(artists, artistFollowOnboardingCount, theme)

            val artistViews = artists.map {
                View(
                    viewState = viewStateMapper.mapArtistPreviewOnboarding(it, followToggleExtraAction),
                    viewSpec = viewSpecFactory.artistPreviewViewSpecOnboarding,
                )
            }.insertSpacersToMaintainFeedColumns()

            val feedViewSpec = viewSpecFactory.feedGridViewSpec
            val feedViews = feedFormatter.format(
                views = artistViews,
                spacerStart = viewFactory.feedSpacerVertical(toolbar.viewSpec.height),
                spacerEnd = FeedFormatter.FeedSpacerBottomNavigationBar,
                canInsertAds = true,
                distinctByViewId = false,
            )

            HomeOnboardingViewState.Success(
                feedViewState = FeedViewState(
                    feedViewSpec = feedViewSpec,
                    views = feedViews,
                    viewsVisibleListener = null,
                    toolbar = toolbar,
                    applyStatusBarOffsetForToolbar = false,
                    feedState = FeedState(
                        initialFeedScrollState = scrollStateWrapper.lastScrollState,
                        lastScrollStateUpdateSink = scrollStateWrapper.lastScrollStateUpdateSink,
                    ),
                )
            )
        }
    }

    private fun createExploreToolbarViewState(
        containerColor: Color,
    ): ExploreHeaderViewState {
        val searchIcon = menuItemFactory.createIcon(
            icon = imageRepository.search,
            tintColor = ColorToken.ThemeOnBackground,
            onClick = null,
        )
        val searchLabel = menuItemFactory.createLabel(
            text = TextStyleSubheading(strings.search),
        )

        return ExploreHeaderViewState(
            viewSpec = viewSpecFactory.exploreHeaderViewSpec,
            containerColor = containerColor,
            searchIcon = searchIcon,
            searchLabel = searchLabel,
            searchOnClick = viewEventFactory.createOnClickNavigateToSearch(),
            containerShapeSpec = shapeSpecFactory.exploreToolbarShapeSpec,
        )
    }

    override fun createExploreFeedViewState(
        contentStateFeed: ContentStateFeed?,
        pagingViewEventSink: PagingViewEventSink,
        contentPrefetcher: ContentPrefetcher,
        wallpaperViewsPagingController: ContentStatePagingController,
        scrollToTop: MutableSharedFlow<Unit>,
        scrollStateWrapper: FeedScrollStateWrapper,
        statusBarColor: Color,
        feedScrollPositionUpdateSink: FeedScrollPositionUpdateSink,
        lastFeedOffsetUpdateSink: LastFeedOffsetUpdateSink,
        feedOffset: Float?,
        statusBarBackgroundAlphaOnChange: (Float) -> Unit,
        highlightsOnPageChange: (Int) -> Unit,
    ): ExploreViewState {
        val items = contentStateFeed?.feed
        if (items.isNullOrEmpty()) {
            return ExploreViewState.Loading
        }
//        Log.d("[ExploreViewModel] wallpapers: ${items.filterIsInstance<ContentState.WallpaperItem>().size}, collections: ${items.filterIsInstance<ContentState.Collection>().size}, highlights: ${items.filterIsInstance<ContentState.Highlights>().size}")

        val views = wallpaperViewsPagingController.getCurrentContentStates()
            .map { viewFactory.createExploreContentStateView(it) }
        Log.d("[ExploreViewModel] views: ${views.size}")

        val viewsWithHighlights = mutableListOf<View>().apply {
            add(viewFactory.feedSpacerSkipRendering) // To evenly group the items between ads (this was not needed when we had highlights as part of feed)
            addAll(views)
        }

        val highlights = items.filterIsInstance<ContentState.Highlights>().firstOrNull()
        val highlightCarouselViewState = if (highlights != null) {
            createHighlightCarouselViewState(highlights, highlightsOnPageChange)
        } else null

        val feedViewSpec = viewSpecFactory.exploreFeedViewSpec

        val chunkedViews = feedFormatter.groupByChunks(viewsWithHighlights)

        val formattedViews = feedFormatter.format(
            views = chunkedViews,
            spacerStart = FeedFormatter.FeedSpacerTopToolbarOnly,
            spacerEnd = FeedFormatter.FeedSpacerBottomNavigationBar,
            storiesPreview = null,
            canExpandItemWidth = false,
            canInsertAds = true,
        ).filter {
            (it.viewState as? SpacerViewState)?.skipRendering != true
        } // remove the skip rendering spacer

        contentPrefetcher.onDataUpdated(formattedViews.map { it.viewState })

        return ExploreViewState.Success(
            feedViewState = FeedViewState(
                feedViewSpec = feedViewSpec,
                views = formattedViews,
                toolbar = createExploreToolbarViewState(containerColor = statusBarColor),
                statusBarColor = statusBarColor.customColorToken,
                applyStatusBarOffsetForToolbar = false,
                scrollToTop = scrollToTop,
                viewsVisibleListener = contentPrefetcher.viewsVisibleListener,
                feedState = FeedState(
                    initialFeedScrollState = scrollStateWrapper.lastScrollState,
                    lastScrollStateUpdateSink = scrollStateWrapper.lastScrollStateUpdateSink,
                    feedScrollPositionUpdateSink = feedScrollPositionUpdateSink,
                    pagingViewEventSink = pagingViewEventSink,
                    pagingDefaultPageSize = appConfig.feedPagingDefaultPageSize,
                ),
            ),
            highlightCarouselViewState = highlightCarouselViewState,
            lastFeedOffsetUpdateSink = lastFeedOffsetUpdateSink,
            feedOffset = feedOffset,
            statusBarBackgroundAlphaOnChange = statusBarBackgroundAlphaOnChange,
        )
    }

    private fun createHighlightCarouselViewState(
        highlights: ContentState.Highlights,
        onPageChange: (Int) -> Unit,
    ): HighlightCarouselViewState {
        return HighlightCarouselViewState(
            carouselViewSpec = viewSpecFactory.highlightCarouselViewSpec,
            carouselViewState = viewStateMapper.mapCarousel(
                highlights = highlights.highlights,
            ),
            backgroundColor = ColorToken.Custom(Colors.Black),
            autoScrollFirstDelayInSeconds = 12,
            autoScrollDelayInSeconds = 4,
            onPageChange = onPageChange,
        )
    }

    override fun createSearchResultsViewState(
        data: SearchResult,
        searchQuery: String?,
        suggestedCollections: List<CollectionState>,
        contentPrefetcher: ContentPrefetcher,
        scrollToTop: MutableSharedFlow<Unit>,
        resetSearchOnClick: ViewEventHandler,
        feedScrollPositionUpdateSink: FeedScrollPositionUpdateSink,
        searchRecipeBinState: SearchRecipeBinViewState?,
    ): SearchResultsViewState {
        return when (data) {
            is SearchResult.Loading -> SearchResultsViewState.Loading
            is SearchResult.Inactive -> SearchResultsViewState.Inactive
            SearchResult.NoResults -> createSearchResultsViewStateNoResults(
                searchQuery,
                suggestedCollections,
                resetSearchOnClick,
                searchRecipeBinState,
                contentPrefetcher,
                scrollToTop,
                feedScrollPositionUpdateSink,
            )
            is SearchResult.Results -> createSearchResultsViewState(
                data,
                searchQuery,
                suggestedCollections,
                contentPrefetcher,
                scrollToTop,
                resetSearchOnClick,
                feedScrollPositionUpdateSink,
                searchRecipeBinState,
            )
        }
    }

    private fun createSearchResultsViewState(
        data: SearchResult.Results,
        searchQuery: String?,
        suggestedCollections: List<CollectionState>,
        contentPrefetcher: ContentPrefetcher,
        scrollToTop: MutableSharedFlow<Unit>,
        resetSearchOnClick: ViewEventHandler,
        feedScrollPositionUpdateSink: FeedScrollPositionUpdateSink,
        searchRecipeBinState: SearchRecipeBinViewState?,
    ): SearchResultsViewState {
        val wallpapers = data.wallpapers
        val collections = data.collectionStates
        val curators = data.curators

        if (data.isEmpty) {
            return createSearchResultsViewStateNoResults(
                searchQuery,
                suggestedCollections,
                resetSearchOnClick,
                searchRecipeBinState,
                contentPrefetcher,
                scrollToTop,
                feedScrollPositionUpdateSink,
            )
        }

        val wallpaperViews = wallpapers?.map { wallpaper ->
            viewFactory.createWallpaperFeedPreview(
                wallpaper = wallpaper,
                showPlusButton = wallpaper.arbitrateWallpaperPreviewShowPlusButton(),
                showFooter = !wallpaper.arbitrateWallpaperPreviewShowPlusButton(),
            )
        }
        val collectionViewSpec = viewSpecFactory.collectionPreviewSmallViewSpec
        val collectionViews = collections?.map {
            viewFactory.createCollectionPreview(
                collectionState = it,
                collectionPreviewViewSpec = collectionViewSpec,
            )
        }

        val curatorViews = curators?.map {
            View(
                viewState = viewStateMapper.mapProfileCurator(
                    it,
                    viewEventHandler = viewEventFactory.createNavigateToScreen(it.id),
                ),
                viewSpec = viewSpecFactory.artistPreviewViewSpecOnboarding,
            )
        }

        // Prioritize curators, then collections, then wallpapers
        val contentViews = (curatorViews ?: emptyList()) +
                (collectionViews ?: emptyList()) +
                (wallpaperViews ?: emptyList())


        val feedViewSpec = viewSpecFactory.feedStaggeredViewSpec

        val formattedViews = feedFormatter.format(
            views = contentViews,
            spacerStart = viewFactory.feedSpacerVertical(viewSpecArbitrator.searchResultFeedTopPadding),
            spacerEnd = FeedFormatter.FeedSpacerBottomNavigationBar,
            storiesPreview = null,
            canExpandItemWidth = false,
            canInsertAds = false,
        )

        contentPrefetcher.onDataUpdated(formattedViews.map { it.viewState })

        val headerViewState = createSearchResultsHeaderViewState(
            searchQuery,
            resetSearchOnClick,
            searchRecipeBinState,
        )

        return SearchResultsViewState.Data(
            feedViewState = FeedViewState(
                feedViewSpec = feedViewSpec,
                views = formattedViews,
                viewsVisibleListener = contentPrefetcher.viewsVisibleListener,
                scrollToTop = scrollToTop,
                feedState = FeedState(
                    initialFeedScrollState = null,
                    lastScrollStateUpdateSink = null,
                    feedScrollPositionUpdateSink = feedScrollPositionUpdateSink,
                ),
                applyStatusBarOffsetForToolbar = false,
            ),
            headerViewState = headerViewState,
            hasResults = true,
        )
    }

    private fun createSearchResultsViewStateNoResults(
        searchQuery: String?,
        suggestedCollections: List<CollectionState>,
        resetSearchOnClick: ViewEventHandler,
        searchRecipeBinState: SearchRecipeBinViewState?,
        contentPrefetcher: ContentPrefetcher,
        scrollToTop: MutableSharedFlow<Unit>,
        feedScrollPositionUpdateSink: FeedScrollPositionUpdateSink,
    ): SearchResultsViewState {
        val headerViewState = createSearchResultsHeaderViewState(
            searchQuery,
            resetSearchOnClick,
            searchRecipeBinState,
        )

        val collectionViewSpec = viewSpecFactory.collectionPreviewSmallViewSpec

        val suggestedViews = suggestedCollections.map {
            viewFactory.createCollectionPreview(
                it,
                collectionViewSpec,
            )
        }

        val feedViewSpec = viewSpecFactory.feedStaggeredViewSpec

        val formattedViews = feedFormatter.format(
            views = suggestedViews,
            spacerStart = viewFactory.feedSpacerVertical(viewSpecArbitrator.searchResultFeedTopPadding),
            spacerEnd = FeedFormatter.FeedSpacerBottomNavigationBar,
            canInsertAds = true,
        ).let {
            feedFormatter.insertFixedPositionViews(it, createSearchResultFixedPositionViews())
        }

        contentPrefetcher.onDataUpdated(formattedViews.map { it.viewState })

        return SearchResultsViewState.Data(
            headerViewState = headerViewState,
            feedViewState = FeedViewState(
                feedViewSpec = feedViewSpec,
                views = formattedViews,
                viewsVisibleListener = contentPrefetcher.viewsVisibleListener,
                scrollToTop = scrollToTop,
                feedState = FeedState(
                    initialFeedScrollState = null,
                    lastScrollStateUpdateSink = null,
                    feedScrollPositionUpdateSink = feedScrollPositionUpdateSink,
                ),
                applyStatusBarOffsetForToolbar = false,
            ),
            hasResults = false,
        )
    }

    private fun createSearchResultFixedPositionViews(
        startFeedPosition: Int = 1,
    ): FixedPositionViews {
        val noResultsFoundMenuItemViewState = MenuItemViewState(
            menuItems = listOf(
                menuItemFactory.createLabel(TextStyleSubheading(strings.noResultsFound)),
            ),
            centerItems = true,
        )

        val suggestionsForYouMenuItemViewState = MenuItemViewState(
            menuItems = listOf(
                menuItemFactory.createLabel(TextFeedTitle(strings.suggestionsForYou)),
            ),
            centerItems = true,
        )

        return FixedPositionViews(
            views = listOf(
                viewFactory.createSpacer(height = viewSpecArbitrator.paddingLarge),
                viewFactory.createFullSpanView(viewState = noResultsFoundMenuItemViewState),
                viewFactory.createSpacer(height = viewSpecArbitrator.paddingLarge),
                viewFactory.createFullSpanView(viewState = suggestionsForYouMenuItemViewState),
                viewFactory.createSpacer(height = viewSpecArbitrator.paddingDefault),
            ),
            feedPosition = startFeedPosition,
        )
    }

    private fun createSearchResultsHeaderViewState(
        searchQuery: String?,
        clearEventHandler: ViewEventHandler,
        searchRecipeBinState: SearchRecipeBinViewState?
    ): SearchResultsHeaderViewState {
        val title = TextStyleCallToAction(searchQuery ?: strings.results)
        return SearchResultsHeaderViewState(
            viewSpec = viewSpecFactory.searchResultsHeaderViewSpec,
            searchText = title,
            clearIcon = menuItemFactory.createCircularButton(
                icon = imageRepository.closeInset,
                iconColor = ColorToken.ThemeOnBackground,
                containerColor = ColorToken.ThemeBackground,
                buttonSize = viewSpecArbitrator.iconSizeMediumLarge,
                eventHandler = clearEventHandler,
            ),
            topScrim = if (themeManager.theme.value.isLight) {
                imageRepository.waterfallGradientWhite
            } else {
                imageRepository.waterfallGradientBlack
            },
            searchRecipeBinViewState = searchRecipeBinState,
            clickEventHandler = viewEventFactory.createNavigateToSearch(),
        )
    }

    private fun createAccountOverviewViewState(
        account: Account?,
        profileImage: Image,
        isLoading: Boolean,
        appleSignInOnClick: ViewEventHandler?,
        googleSignInOnClick: ViewEventHandler,
        messageBarHeight: Dp,
    ): AccountOverviewViewState {
        return if (isLoading) {
            createAccountOverviewLoading()
        } else if (account != null) {
            createAccountOverviewSignedIn(account, profileImage, messageBarHeight)
        } else {
            createAccountOverviewSignedOut(
                googleSignInOnClick,
                appleSignInOnClick,
                messageBarHeight
            )
        }
    }

    private val profileCopyrightViewStates: List<SettingViewState>
        get() = listOf(
            settingViewStateFactory.spacerNormal,
            settingViewStateFactory.settingsFooter,
            settingViewStateFactory.spacerNormal,
        )
    private val profileSettingsViewStates: List<SettingViewState>
        get() = listOf(
            settingViewStateFactory.spacerSmall,
            settingViewStateFactory.settingsHeading,
            settingViewStateFactory.themeSettingSelector,
            settingViewStateFactory.spacerNormal,
            settingViewStateFactory.appIconSettingSelector,
        )
    private val profileAboutViewStates: List<SettingViewState>
        get() = listOf(
            settingViewStateFactory.spacerSmall,
            settingViewStateFactory.aboutHeading,
            settingViewStateFactory.account,
            settingViewStateFactory.privacyPolicy,
            settingViewStateFactory.termsOfService,
            settingViewStateFactory.openSourceLicenses,
            settingViewStateFactory.appVersion,
        )
    private val profileDebugViewStates: List<SettingViewState>
        get() = listOf(
            settingViewStateFactory.dividerFull,
            settingViewStateFactory.debugSettings,
            settingViewStateFactory.dividerFull,
        )

    private val profileSocialLinks: SocialLinks
        get() = SocialLinks(
            twitter = Url.AppTwitter,
            instagram = Url.AppInstagram,
            discord = null,
            website = Url.AppWebsite,
        )

    private fun createProfileSocialLinksView(socialLinkEventSink: ViewEventSink): View = View(
        viewStateMapper.mapSocialLinks(socialLinkEventSink, profileSocialLinks)!!,
        MaxFeedWidthNoPaddingViewSpec,
    )

    private fun createProfileFeedItems(
        profileHeader: ProfileHeaderViewState,
        accountOverview: AccountOverviewViewState,
        socialLinkEventSink: ViewEventSink,
        showPurchasePlusUi: Boolean,
        showDebugOptions: Boolean,
    ): List<View> {
        fun List<SettingViewState>.asView() =
            View(SettingGroupViewState(this), MaxFeedWidthViewSpec)

        fun List<SettingViewState>.asViewNoPadding() =
            View(SettingGroupViewState(this), MaxFeedWidthNoPaddingViewSpec)

        return listOfNotNull(
            viewFactory.createSpacer(height = profileHeader.viewSpec.height),
            View(accountOverview, MaxFeedWidthViewSpec),
//                profileViewStates.asView,
            profileSettingsViewStates.asViewNoPadding(),
//                spacer,
//            createProfileSocialLinksView(socialLinkEventSink),
//                spacer,
            profileCopyrightViewStates.asView(),
            profileAboutViewStates.asViewNoPadding(),
            if(showDebugOptions) profileDebugViewStates.asViewNoPadding() else null,
            viewFactory.feedSpacerBottomWithNavBar,
        )
    }

    private fun createProfileHeader(
        messageBar: MessageBarViewState?,
        account: Account?,
        showPurchasePlusUi: Boolean,
        containerColor: Color,
    ): ProfileHeaderViewState {
        val summary = if (account != null) {
            strings.memberSince(account.createdAt)
        } else {
            strings.accountSignInInstructions
        }
        val upgradeButton = if (showPurchasePlusUi) {
            menuItemFactory.createPlusButton()
        } else {
            null
        }

        return ProfileHeaderViewState(
            viewSpec = viewSpecFactory.profileHeaderViewSpec,
            messageBar = messageBar,
            title = TextDisplay(strings.appName),
            summary = TextCaption(summary),
            upgradeButton = upgradeButton,
            containerColor = containerColor,
        )
    }

    override fun createProfileViewState(
        account: Account?,
        profileImage: Image,
        messageBar: MessageBarViewState?,
        appleSignInOnClick: ViewEventHandler?,
        googleSignInOnClick: ViewEventHandler,
        showPurchasePlusUi: Boolean,
        profileTopBarContainerColor: Color,
        connectionsSummaryContent: ConnectionsSummaryContentResult?,
        socialLinkEventSink: ViewEventSink,
        scrollToTop: MutableSharedFlow<Unit>,
        scrollStateWrapper: FeedScrollStateWrapper,
        showDebugOptions: Boolean,
    ): ProfileViewState {
        val profileHeader = createProfileHeader(
            messageBar,
            account,
            showPurchasePlusUi,
            containerColor = profileTopBarContainerColor,
        )
        val accountViewState = createAccountOverviewViewState(
            account = account,
            profileImage = profileImage,
            isLoading = false,
            googleSignInOnClick = googleSignInOnClick,
            appleSignInOnClick = appleSignInOnClick,
            messageBarHeight = messageBar?.viewSpec?.maxHeight ?: 0.dp,
        )

        val views = createProfileFeedItems(
            profileHeader = profileHeader,
            accountOverview = accountViewState,
            socialLinkEventSink = socialLinkEventSink,
            showPurchasePlusUi = showPurchasePlusUi,
            showDebugOptions = showDebugOptions,
        )

        return ProfileViewState.Data(
            feedViewState = FeedViewState(
                feedViewSpec = viewSpecFactory.feedGridViewSpec,
                views = views,
                viewsVisibleListener = null,
                applyStatusBarOffsetForToolbar = false,
                toolbar = profileHeader,
                scrollToTop = scrollToTop,
                feedState = FeedState(
                    initialFeedScrollState = scrollStateWrapper.lastScrollState,
                    lastScrollStateUpdateSink = scrollStateWrapper.lastScrollStateUpdateSink,
                ),
            ),
        )
    }

    override fun createIndexNavigationBar(
        items: List<NavigationBarItem>,
        offsetProgress: Float,
        containerColor: Color,
        forceShow: Boolean?,
    ) = NavigationBarViewState(
        viewSpec = viewSpecFactory.navigationBarViewSpec,
        items = items,
        itemsDisplayRipple = false,
        containerColor = ColorOptional(containerColor),
        offsetProgress = offsetProgress,
        forceShow = forceShow,
    )

    private fun createStandardToolbarViewState(
        title: String,
        containerColorOverride: ColorToken,
    ): ToolbarViewState {
        return ToolbarViewState(
            navigationIcon = menuItemFactory.back,
            title = menuItemFactory.centeredText(TextToolbarTitle(title)),
            containerColorOverride = containerColorOverride,
        )
    }

    private fun List<View>.mapToFeedView(
        staggeredGrid: Boolean,
        viewsVisibleListener: ViewsVisibleListener?,
        spacerStart: View? = FeedFormatter.FeedSpacerTopDefault,
        spacerEnd: View? = FeedFormatter.FeedSpacerBottomDefault,
    ): View {
        val feedViewSpec = if (!staggeredGrid) {
            viewSpecFactory.feedGridViewSpec
        } else {
            viewSpecFactory.feedStaggeredViewSpec
        }
        return View(
            viewState = FeedViewState(
                views = feedFormatter.format(
                    views = this,
                    spacerStart = spacerStart,
                    spacerEnd = spacerEnd,
                    canExpandItemWidth = false,
                ),
                feedViewSpec = feedViewSpec,
                viewsVisibleListener = viewsVisibleListener,
            ),
        )
    }

    private fun createProfileConnectionViewState(
        connectionType: ConnectionType,
        title: String,
        count: Int?,
    ) = ProfileConnectionViewState(
        label = title,
        count = count?.toString() ?: strings.dashCharacter,
        onClick = viewEventFactory.createOnClickNavigateToScreen(
            ConnectionsScreenArgument(
                connectionType
            )
        ),
    )

    override fun createProfileConnectionsViewState(
        connectionsSummaryContent: ConnectionsSummaryContentResult?,
    ) =
        ProfileConnectionsViewState(
            connections = listOf(
                createProfileConnectionViewState(
                    ConnectionType.Following,
                    strings.following,
                    connectionsSummaryContent?.artistsCount
                ),
                createProfileConnectionViewState(
                    ConnectionType.Favorites,
                    strings.favorites,
                    connectionsSummaryContent?.favoritesCount
                ),
                createProfileConnectionViewState(
                    ConnectionType.Wallpapers,
                    strings.wallpapers,
                    connectionsSummaryContent?.wallpapersCount
                ),
            ),
        )

    override fun createCollectionViewState(
        data: CollectionScreenContentResult?,
        messageBarViewState: MessageBarViewState?,
        topBarContainerColor: ColorToken,
        scrollStateWrapper: FeedScrollStateWrapper,
        collapsingToolbarStateWrapper: CollapsingToolbarStateWrapper,
        activeDownloadStatus: DownloadStatusViewState?,
        contentPrefetcher: ContentPrefetcher,
        downloadCollectionViewEventHandler: ViewEventHandler,
        toolbarExpanded: Boolean,
        showAdFreeCollectionLockedInfo: Boolean,
        viewsVisibleListener: ViewsVisibleListener,
        firstWallpaperId: WallpaperId?,
    ): CollectionViewState {
        val collection = data?.collectionState
        val artist = data?.artist

        if (data == null || collection == null || artist == null) {
            return CollectionViewState.Loading
        }

        val wallpaperViews = collection.wallpapers.map { wallpaper ->
            // Request that all collection wallpapers be widescreen
            viewFactory.createWallpaperFeedPreview(
                wallpaper = wallpaper,
                showPlusButton = false,
                showFooter = true,
                firstWallpaperId = firstWallpaperId,
            )
        }
//        val allViews = if (showAdditionalCollections && additionalCollections != null) {
//            wallpaperViews + viewFactory.createMoreCollectionsFromArtist(
//                artist,
//                additionalCollections
//            )
//        } else {
//            wallpaperViews
//        }
        val allViews = wallpaperViews

        val toolbarViewState = createCollectionToolbarViewState(
            collectionState = collection,
            artist = artist,
            isUnlocked = data.isUnlocked,
            messageBarViewState = messageBarViewState,
            topBarContainerColor = topBarContainerColor,
            collapsingToolbarStateWrapper = collapsingToolbarStateWrapper,
            artistFollowState = data.artistFollowState,
            activeDownloadStatus = activeDownloadStatus,
            downloadCollectionViewEventHandler = downloadCollectionViewEventHandler,
            showAdFreeCollectionLockedInfo = showAdFreeCollectionLockedInfo,
            toolbarExpanded = toolbarExpanded,
        )

        val feedViewSpec = viewSpecFactory.collectionFeedViewSpec(
            toolbarExpanded = toolbarExpanded,
            showAdFreeCollectionLockedInfo = showAdFreeCollectionLockedInfo,
        )

        val feedItemsHeight = calculateFeedItemsHeight(allViews, feedViewSpec)

        val formattedViews = feedFormatter.format(
            views = allViews,
            spacerStart = FeedFormatter.FeedSpacerVerticalNone,
            spacerEnd = viewFactory.collectionFeedSpacerBottom(feedItemsHeight),
            canInsertAds = true,
        )

        contentPrefetcher.onDataUpdated(formattedViews.map { it.viewState })

        return CollectionViewState.Success(
            toolbarViewState = toolbarViewState,
            feedViewState = FeedViewState(
                feedViewSpec = feedViewSpec,
                views = formattedViews,
                viewsVisibleListener = viewsVisibleListener,
                toolbar = null,
                feedState = FeedState(
                    initialFeedScrollState = scrollStateWrapper.lastScrollState,
                    lastScrollStateUpdateSink = scrollStateWrapper.lastScrollStateUpdateSink,
                ),
                messageBarHeight = if (toolbarViewState is CollectionToolbarViewState.Locked) {
                    messageBarViewState?.viewSpec?.maxHeight ?: 0.dp
                } else {
                    0.dp
                },
            ),
            onSwipeToDismiss = createOnSwipeToDismiss(),
        )
    }

    private fun calculateFeedItemsHeight(views: List<View>, feedViewSpec: FeedViewSpec): Dp {
        val itemsInLongColumn = views.size / 2 + if (views.size % 2 == 1) 1 else 0
        var feedItemsHeight = 0.dp
        repeat(itemsInLongColumn) {
            feedItemsHeight += (views[it].viewSpec as? FeedContentPreviewViewSpec)?.height ?: 0.dp
        }
        feedItemsHeight += feedViewSpec.itemSpacingVertical * itemsInLongColumn
        return feedItemsHeight
    }

//    fun createDownloadCollectionButton(
//        activeDownloadStatus: DownloadStatusViewState?,
//        downloadCollectionViewEventHandler: ViewEventHandler,
//    ): ButtonViewState {
//        var items: MenuItem = menuItemFactory.createHorizontalGroup(
//            items = listOf(
//                menuItemFactory.createButtonIcon(imageRepository.collection),
//                menuItemFactory.createLabel(TextButtonLabel(strings.download)),
//            ),
//        )
//
//        if (activeDownloadStatus != null) {
//            items = menuItemFactory.createActionProgressButton(
//                progress = activeDownloadStatus.progress,
//                text = TextButtonLabel(
//                    strings.downloadStatusCombined(
//                        activeDownloadStatus.title.string,
//                        activeDownloadStatus.summary.string,
//                    ),
//                    colorToken = ColorToken.ThemeOnTertiary,
//                ),
//            )
//        }
//
//        val eventHandler = if (activeDownloadStatus != null) {
//            ViewEventHandler.NoOp
//        } else {
//            downloadCollectionViewEventHandler
//        }
//
//        return ButtonViewState(
//            menuItem = items,
//            animatedViewSpec = viewSpecFactory.animatedToolbarHeroButtonViewSpec
//                .copy(widthMin = if (activeDownloadStatus != null) DpOptional(200.dp) else DpOptional(180.dp)),
//            shapeSpec = shapeSpecFactory.buttonShapeSpecDefault,
//            eventHandler = eventHandler,
//        )
//    }

    fun createDownloadProgressCollectionButton(
        collectionState: CollectionState,
        activeDownloadStatus: DownloadStatusViewState,
    ): CollectionActionButtonViewState.DownloadProgressButton {
        val colorToken = ColorToken.ThemeOnTertiary
        return CollectionActionButtonViewState.DownloadProgressButton(
            viewSpec = viewSpecFactory.collectionActionButtonViewSpecDownloadProgress,
            buttonViewState = createGetCollectionButton(collectionState),
            minIcon = menuItemFactory.createButtonIcon(imageRepository.download, tintColor = colorToken),
            countLabel = TextButtonLabel(activeDownloadStatus.summary.string, colorToken = colorToken),
            maxLabel = TextButtonLabel(activeDownloadStatus.title.string, colorToken = colorToken),
            progress = activeDownloadStatus.progress,
        )
    }

    fun createGetCollectionButton(
        collectionState: CollectionState,
    ): ButtonViewState {
        val collectionId = collectionState.id
        val isUnlocked = collectionState.connectionState?.isUnlocked == true
        val getLabel = strings.get
        val collectionLabel = strings.applySpacePrefix(strings.collection)

        val items: MenuItem = menuItemFactory.createHorizontalGroup(
            items = listOf(
                menuItemFactory.createButtonIcon(imageRepository.wallpaperGet),
                menuItemFactory.createLabel(TextButtonLabel(getLabel)),
                menuItemFactory.createLabel(TextButtonLabel(collectionLabel)),
            ),
        )

        val modalBottomSheetHeight = if (isUnlocked) {
            viewSpecArbitrator.bottomSheetHeightOneOption.value
        } else {
            viewSpecArbitrator.bottomSheetHeightTwoOptions.value
        }

        val eventHandler = viewEventFactory.createNavigateToScreen(
            CollectionActionScreenArgument(
                collectionId = collectionId,
                singleWallpaperId = null,
                modalSheetHeight = modalBottomSheetHeight,
            ),
        )

        return ButtonViewState(
            menuItem = items,
            animatedViewSpec = viewSpecFactory.animatedToolbarHeroButtonViewSpec,
            shapeSpec = shapeSpecFactory.buttonShapeSpecDefault,
            eventHandler = eventHandler,
        )
    }

    /**
     * [priceLabel] must be non-null, because the UI expects 3 elements on the button. Pass an empty
     * string if the price is unavailable.
     */
    fun createBuyCollectionButton(
        collectionId: CollectionId,
        priceLabel: String,
        isThreeButtonHeight: Boolean,
    ): ButtonViewState {
        val eventHandler = viewEventFactory.createNavigateToScreen(
            CollectionActionScreenArgument(
                collectionId = collectionId,
                singleWallpaperId = null,
                modalSheetHeight = if (isThreeButtonHeight) {
                    viewSpecArbitrator.bottomSheetHeightThreeOptions.value
                } else {
                    viewSpecArbitrator.bottomSheetHeightTwoOptions.value
                },
            ),
        )

        val items = menuItemFactory.createBuyCollectionButton(
            priceLabel,
            eventHandler,
        )

        return ButtonViewState(
            menuItem = items,
            animatedViewSpec = viewSpecFactory.animatedToolbarHeroButtonViewSpec,
            shapeSpec = shapeSpecFactory.buttonShapeSpecDefault,
            eventHandler = eventHandler,
        )
    }

    private fun createCollectionToolbarViewState(
        collectionState: CollectionState,
        artist: Artist,
        isUnlocked: Boolean?,
        messageBarViewState: MessageBarViewState?,
        topBarContainerColor: ColorToken,
        collapsingToolbarStateWrapper: CollapsingToolbarStateWrapper,
        artistFollowState: FollowState?,
        activeDownloadStatus: DownloadStatusViewState?,
        showAdFreeCollectionLockedInfo: Boolean,
        downloadCollectionViewEventHandler: ViewEventHandler,
        toolbarExpanded: Boolean,
    ): CollectionToolbarViewState {
        return if (isUnlocked == true) {
            createCollectionToolbarUnlockedViewState(
                collectionState,
                artist,
                topBarContainerColor,
                collapsingToolbarStateWrapper,
                artistFollowState,
                activeDownloadStatus,
                downloadCollectionViewEventHandler,
                toolbarExpanded,
            )
        } else {
            createCollectionToolbarLockedViewState(
                collectionState,
                artist,
                messageBarViewState,
                topBarContainerColor,
                collapsingToolbarStateWrapper,
                artistFollowState,
                showAdFreeCollectionLockedInfo = showAdFreeCollectionLockedInfo,
                toolbarExpanded = toolbarExpanded,
            )
        }
    }

    private fun createCollectionToolbarUnlockedViewState(
        collectionState: CollectionState,
        artist: Artist,
        topBarContainerColor: ColorToken,
        collapsingToolbarStateWrapper: CollapsingToolbarStateWrapper,
        artistFollowState: FollowState?,
        activeDownloadStatus: DownloadStatusViewState?,
        downloadCollectionViewEventHandler: ViewEventHandler,
        toolbarExpanded: Boolean,
    ): CollectionToolbarViewState.Unlocked {
        val toolbarViewSpec = viewSpecFactory.collectionToolbarUnlockedViewSpec

        val toolbar = ToolbarViewState(
            navigationIcon = menuItemFactory.back,
            containerColorOverride = ColorToken.Transparent,
            height = toolbarViewSpec.minToolbarHeight,
        )

        val artistProfileImage = viewStateMapper.mapProfileImage(
            profileImageMediaHolder = artist.profileImageMediaHolder,
            sizedImage = SizedImage.ArtistMedium,
            imageViewSpec = imageViewSpecFactory.collectionToolbarArtistProfileImageViewSpec,
//            eventHandler = viewEventFactory.createFollowOnly(artist.id, artistFollowState),
            eventHandler = viewEventFactory.createNavigateToScreen(artist.id),
            showFollowIndicator = false,
            followState = artistFollowState
        )

        val title = TextDisplay(collectionState.label)

        val collectionActionButtonViewState = if (activeDownloadStatus != null) {
            createDownloadProgressCollectionButton(
                collectionState,
                activeDownloadStatus,
            )
        } else {
            CollectionActionButtonViewState.GetCollectionViewState(
                viewSpec = viewSpecFactory.collectionActionButtonViewSpecGetCollection,
                buttonViewState = createGetCollectionButton(collectionState),
            )
        }

        return CollectionToolbarViewState.Unlocked(
            viewSpec = toolbarViewSpec,
            toolbar = toolbar,
            containerColorOverride = topBarContainerColor,
            title = title,
            artistProfileImage = artistProfileImage,
            artistName = null,//artist.name,
            collectionActionButtonViewState = collectionActionButtonViewState,
            collapsingToolbarStateWrapper = collapsingToolbarStateWrapper,
            isExpanded = toolbarExpanded,
        )
    }

    private fun createCollectionToolbarLockedViewState(
        collectionState: CollectionState,
        artist: Artist,
        messageBarViewState: MessageBarViewState?,
        topBarContainerColor: ColorToken,
        collapsingToolbarStateWrapper: CollapsingToolbarStateWrapper,
        artistFollowState: FollowState?,
        showAdFreeCollectionLockedInfo: Boolean,
        toolbarExpanded: Boolean,
    ): CollectionToolbarViewState.Locked {
        val toolbarViewSpec = viewSpecFactory.collectionToolbarLockedViewSpec(
            showCollectionLockedInfo = showAdFreeCollectionLockedInfo,
        )

        val toolbar = ToolbarViewState(
            navigationIcon = menuItemFactory.back,
            containerColorOverride = ColorToken.Transparent,
        )

        val artistProfileImage = viewStateMapper.mapProfileImage(
            profileImageMediaHolder = artist.profileImageMediaHolder,
            sizedImage = SizedImage.ArtistMedium,
            imageViewSpec = imageViewSpecFactory.collectionToolbarArtistProfileImageViewSpec,
//            eventHandler = viewEventFactory.createFollowOnly(artist.id, artistFollowState),
            eventHandler = viewEventFactory.createNavigateToScreen(artist.id),
            showFollowIndicator = false,
            followState = artistFollowState
        )

        val title = TextDisplay(collectionState.label)

        val heroButton = createBuyCollectionButton(
            collectionState.id,
            collectionState.priceLabelNonNull,
            isThreeButtonHeight = showAdFreeCollectionLockedInfo,
        )
        val collectionActionButtonViewState = CollectionActionButtonViewState.BuyCollectionViewState(
            viewSpec = viewSpecFactory.collectionActionButtonViewSpecBuyCollection,
            buttonViewState = heroButton,
        )

        val adFreeCollectionLockedInfo = if (showAdFreeCollectionLockedInfo) {
            menuItemFactory.createAdFreeCollectionLockedInfo()
        } else {
            null
        }

        return CollectionToolbarViewState.Locked(
            viewSpec = toolbarViewSpec,
            messageBarViewState = messageBarViewState,
            toolbar = toolbar,
            containerColorOverride = topBarContainerColor,
            title = title,
            artistProfileImage = artistProfileImage,
            artistName = null,//artist.name,
            collectionActionButtonViewState = collectionActionButtonViewState,
            adFreeCollectionLockedInfo = adFreeCollectionLockedInfo,
            collapsingToolbarStateWrapper = collapsingToolbarStateWrapper,
            isExpanded = toolbarExpanded,
        )
    }

    override fun createConnectionsViewState(
        connections: ConnectionsContentResult?,
        initialTabIndex: Int,
    ): ConnectionsViewState {
        if (connections == null) {
            return ConnectionsViewState.Loading
        }
        val viewsVisibleListener: ViewsVisibleListener? = null

        val spacerStart =
            viewFactory.feedSpacerVertical(viewSpecFactory.tabsIndicatorViewSpec().tabContainerHeight)

        val followingViews = connections.artists
            ?.map {
                viewFactory.createArtistPreview(
                    artistState = it,
                    sizedImage = SizedImage.ArtistMedium,
                    imageViewSpec = imageViewSpecFactory.connectionsArtistImageViewSpec,
                    forceMaxSpan = true,
                )
            }
            ?.ifEmpty { null }
            ?: listOf(createFallbackMessageView("TODO: Add dedicated \"no followers\" UI"))
        val followingTab = TabViewState(
            headerSelected = menuItemFactory.centeredText(TextTabSelected(strings.following)),
            headerUnselected = menuItemFactory.centeredText(TextTabUnselected(strings.following)),
            view = followingViews.mapToFeedView(
                staggeredGrid = false,
                viewsVisibleListener = viewsVisibleListener,
                spacerStart = spacerStart,
            ),
            selectedContentColorToken = ColorToken.ThemeOnBackground,
        )

        val wallpapersViews = connections.wallpapers
            ?.map { wallpaper ->
                viewFactory.createWallpaperFeedPreview(
                    wallpaper = wallpaper,
                    showPlusButton = false,//wallpaper.arbitrateWallpaperPreviewShowPlusButton(),
                )
            }
            ?.ifEmpty { null }
            ?: listOf(createFallbackMessageView("TODO: Add dedicated \"no unlocked wallpapers\" UI"))
        val wallpapersTab = TabViewState(
            headerSelected = menuItemFactory.centeredText(TextTabSelected(strings.wallpapers)),
            headerUnselected = menuItemFactory.centeredText(TextTabUnselected(strings.wallpapers)),
            view = wallpapersViews.mapToFeedView(
                staggeredGrid = true,
                viewsVisibleListener = viewsVisibleListener,
                spacerStart = spacerStart,
            ),
            selectedContentColorToken = ColorToken.ThemeOnBackground,
        )

        val favoritesView = connections.favorites
            ?.map { wallpaper ->
                viewFactory.createWallpaperFeedPreview(
                    wallpaper = wallpaper,
                    showPlusButton = false,//wallpaper.arbitrateWallpaperPreviewShowPlusButton(),
                )
            }
            ?.ifEmpty { null }
            ?.mapToFeedView(
                staggeredGrid = true,
                viewsVisibleListener = viewsVisibleListener,
                spacerStart = spacerStart,
            )
            ?: View(createNoFavoritesViewState(), MaxFeedWidthViewSpec)
        val favoritesTab = TabViewState(
            headerSelected = menuItemFactory.centeredText(TextTabSelected(strings.favorites)),
            headerUnselected = menuItemFactory.centeredText(TextTabUnselected(strings.favorites)),
            view = favoritesView,
            selectedContentColorToken = ColorToken.ThemeOnBackground,
        )

        val tabs = TabsViewState.Indicator(
            tabs = listOf(
                followingTab,
                favoritesTab,
                wallpapersTab,
            ),
            indicatorColorToken = ColorToken.ThemeOnBackground,
            initialIndex = initialTabIndex,
            viewSpec = viewSpecFactory.tabsIndicatorViewSpec(),
        )

        return ConnectionsViewState.Success(
            toolbarViewState = createStandardToolbarViewState(
                strings.connections,
                containerColorOverride = ColorToken.Transparent,
            ),
            tabs = tabs,
        )
    }

    private fun createFallbackMessageView(message: String): View =
        View(
            viewState = createMenuItemViewState(
                menuItems = listOf(
                    menuItemFactory.centeredText(TextPlaceholder(message)),
                ),
                itemPadding = 0.dp,
            ),
            viewSpec = MaxFeedWidthViewSpec,
        )

    private fun createUnlockSummary(
        image: Image,
        label: String,
    ): MenuItem = menuItemFactory.createHorizontalGroup(
        listOf(
            menuItemFactory.createImage(image, size = viewSpecArbitrator.iconSizeLarge),
            menuItemFactory.createSpacer(width = viewSpecArbitrator.paddingDefault),
            menuItemFactory.centeredText(
                TextStyleSubheading(label, textAlign = TextAlign.Center),
                width = null,
            ),
        )
    )

    override fun createCollectionActionViewState(
        collectionState: CollectionState?,
        actionButton1: MenuItem,
        actionButton2: MenuItem?,
        showAdFreeCollectionLockedInfo: Boolean,
    ): CollectionActionViewState {
        if (collectionState == null) {
            return CollectionActionViewState.Loading
        }

        val toolbarViewState = ToolbarViewState(
            navigationIcon = menuItemFactory.createCircularCloseButton(
                buttonSize = viewSpecArbitrator.iconSizeLarge,
                tintColor = ColorToken.ThemeOnPrimary,
                containerColor = ColorToken.ThemePrimary,
            ),
            title = menuItemFactory.centeredText(TextStyleSubheadingActive(strings.downloadOptions)),
            containerColorOverride = ColorToken.Transparent,
        )

        val viewSpec = if (actionButton2 != null) {
            viewSpecFactory.collectionActionViewSpecTwoOptions(showAdFreeCollectionLockedInfo)
        } else {
            viewSpecFactory.collectionActionViewSpecOneOption
        }
        val infoMessage = if (showAdFreeCollectionLockedInfo) {
            menuItemFactory.createAdFreeCollectionLockedInfo()
        } else {
            null
        }

        return CollectionActionViewState.Success(
            viewSpec = viewSpec,
            toolbarViewState = toolbarViewState,
            actionButton1 = actionButton1,
            actionButton2 = actionButton2,
            infoMessage = infoMessage,
        )
    }

    override fun createFolderViewState(
        data: FolderState?,
        messageBarViewState: MessageBarViewState?,
        topBarContainerColor: ColorToken,
        scrollStateWrapper: FeedScrollStateWrapper,
        contentPrefetcher: ContentPrefetcher,
        theme: Theme,
    ): FolderViewState {
        if (data == null) {
            return FolderViewState.Loading
        }

        val collectionViews = data.collectionStates.map { collectionState ->
            viewFactory.createCollectionPreview(
                collectionState,
                collectionPreviewViewSpec = viewSpecFactory.collectionPreviewSmallViewSpec,
            )
        }

        val wallpaperViews = data.wallpaperStates.map { wallpaperState ->
            viewFactory.createWallpaperFeedPreview(
                wallpaper = wallpaperState.wallpaper,
                showPlusButton = wallpaperState.arbitrateWallpaperPreviewShowPlusButton(),
                showFooter = !wallpaperState.arbitrateWallpaperPreviewShowPlusButton(),
            )
        }

        val folderWallpapers = (data.wallpaperStates.map { it.wallpaper } +
                data.collectionStates.map { it.wallpapers[0] })
            .shuffled()
            .take(4)

        val folderView = viewFactory.createFolderPreview(
            folderPreviewViewState = createFolderPreviewViewState(
                title = data.folder.titleTwoLines,
                wallpapers = folderWallpapers,
            ),
            folderPreviewViewSpec = viewSpecFactory.folderPreviewViewSpec,
        )

        val viewsWithHighlights = mutableListOf<View>().apply {
//            addAll(listOf(folderView) + collectionViews + wallpaperViews)
            addAll(collectionViews + wallpaperViews)
        }
        val groupedViews = feedFormatter.groupByChunks(
            views = viewsWithHighlights,
            forceIncludeDanglingViews = true,
        )

        val formattedViews = feedFormatter.format(
            views = groupedViews,
            spacerStart = FeedFormatter.FeedSpacerVerticalNone,
            spacerEnd = FeedFormatter.FeedSpacerBottomDefault,
            canInsertAds = true,
        )

        contentPrefetcher.onDataUpdated(formattedViews.map { it.viewState })

        val feedViewSpec = viewSpecFactory.feedStaggeredViewSpec
        val feedViewState = FeedViewState(
            feedViewSpec = feedViewSpec,
            views = formattedViews,
            viewsVisibleListener = contentPrefetcher.viewsVisibleListener,
            toolbar = null,
            feedState = FeedState(
                initialFeedScrollState = scrollStateWrapper.lastScrollState,
                lastScrollStateUpdateSink = scrollStateWrapper.lastScrollStateUpdateSink,
            ),
            messageBarHeight = messageBarViewState?.viewSpec?.maxHeight ?: 0.dp,
        )

        val folderViewSpec = viewSpecFactory.folderViewSpec

        val folderName = TextDisplay(string = data.folder.titleTwoLines, maxLines = 2)

        val toolbarViewState = FolderToolbarViewState(
            theme = theme,
            toolbarViewState = ToolbarViewState(
                navigationIcon = menuItemFactory.back,
                containerColorOverride = ColorToken.Transparent,
            ),
            name = folderName,
        )

        return FolderViewState.Success(
            viewSpec = folderViewSpec,
            feedViewState = feedViewState,
            toolbarViewState = toolbarViewState,
            onSwipeToDismiss = createOnSwipeToDismiss(),
        )
    }

    override fun createWallpaperSingleActionViewState(
        wallpaper: Wallpaper?,
        actionButton1: MenuItem,
        actionButton2: MenuItem,
        actionButton3: MenuItem?,
    ): WallpaperSingleActionViewState {
        if (wallpaper == null) return WallpaperSingleActionViewState.Loading

        val toolbarViewState = ToolbarViewState(
            navigationIcon = menuItemFactory.createCircularCloseButton(
                buttonSize = viewSpecArbitrator.iconSizeLarge,
                tintColor = ColorToken.ThemeOnPrimary,
                containerColor = ColorToken.ThemePrimary,
            ),
            title = menuItemFactory.centeredText(TextStyleSubheadingActive(strings.downloadOptions)),
            containerColorOverride = ColorToken.Transparent,
        )

        val viewSpec = if (actionButton3 != null) {
            viewSpecFactory.wallpaperSingleActionViewSpecThreeOptions
        } else {
            viewSpecFactory.wallpaperSingleActionViewSpecTwoOptions
        }

        return WallpaperSingleActionViewState.Success(
            viewSpec = viewSpec,
            toolbarViewState = toolbarViewState,
            actionButton1 = actionButton1,
            actionButton2 = actionButton2,
            actionButton3 = actionButton3,
        )
    }

    override fun createErrorRewardAdViewState(
        errorRewardAdMode: ErrorRewardAdMode,
        eventSink: ViewEventSink,
        errorMessage: String?,
    ): ErrorRewardAdViewState {
        val viewSpec = viewSpecFactory.errorRewardAdViewSpec

        val (title, messageLabels) = when (errorRewardAdMode) {
            ErrorRewardAdMode.NetworkError -> {
                strings.noNetworkConnection to listOf(
                    strings.adPlaybackNetworkErrorMessage1,
                    strings.adPlaybackNetworkErrorMessage2,
                )
            }
            ErrorRewardAdMode.AdConsentDenied -> {
                strings.adConsentRequired to listOf(
                    strings.adConsentRequiredMessage1,
                    strings.adConsentRequiredMessage2,
                )
            }
            ErrorRewardAdMode.Other -> {
                strings.adPlaybackError to listOf(
                    strings.applySpaceSeparator(strings.adPlaybackErrorSummary, strings.applyColonSuffix(strings.possibleReasons)),
                    strings.applyDotPrefix(strings.rewardAdErrorTroubleshootingReason1),
                    strings.applyDotPrefix(strings.rewardAdErrorTroubleshootingReason2),
                    strings.applyDotPrefix(strings.rewardAdErrorTroubleshootingReason3),
                )
            }
        }

        val (viewEvent, label) = when (errorRewardAdMode) {
            ErrorRewardAdMode.AdConsentDenied -> {
                ErrorRewardAdViewEvent.OpenPrivacySettings to strings.openPrivacySettings
            }
            ErrorRewardAdMode.NetworkError, ErrorRewardAdMode.Other -> {
                ErrorRewardAdViewEvent.OpenNetworkSettings to strings.openNetworkSettings
            }
        }

        val eventHandler = ViewEventHandler.Event(eventSink = eventSink, event = viewEvent)

        val actionButton = menuItemFactory.createActionButton(
            image = null,
            text = TextButtonLabel(label),
            eventHandler = eventHandler,
        )

        val toolbarViewState = ToolbarViewState(
            navigationIcon = menuItemFactory.createCloseButton(),
            title = menuItemFactory.centeredText(
                TextToolbarTitle(title, maxLines = 1),
                width = viewSpecArbitrator.toolbarTitleWidthAdjustedForNavigationIcon.width,
            ),
            containerColorOverride = ColorToken.Transparent,
        )

        val messages = messageLabels.map {
            menuItemFactory.createLabel(
                TextStyleBody(it),
                contentAlignment = Alignment.CenterStart,
                height = null,
            )
        }

        val errorMessageText = errorMessage?.let {
            menuItemFactory.createLabel(
                TextStyleCaption(it),
                contentAlignment = Alignment.Center,
                height = null,
            )
        }

        return ErrorRewardAdViewState.Success(
            viewSpec,
            theme = themeManager.lightTheme,
            toolbarViewState,
            messages,
            actionButton,
            errorMessage = errorMessageText,
            upgradePromoViewState = createUpgradePromoViewState(),
        )
    }

    override fun createUpgradePromoViewState(): UpgradePromoViewState {
        val upgradeTitle = menuItemFactory.centeredText(
            TextStyleHeadline(
                strings.unlockAllContentInHd,
                maxLines = 2,
                textAlign = TextAlign.Center,
            ),
            width = null,
            height = null,
        )
        return UpgradePromoViewState(
            theme = themeManager.darkTheme,
            heroImage = imageRepository.plusHero,
            title = upgradeTitle,
            actionButton = menuItemFactory.createPlusHeroButton(),
        )
    }

    override fun createOnSwipeToDismiss(): OnSwipeToDismiss =
        viewEventFactory.createOnClickNavigateBack()

    override fun createSearchBarViewState(
        eventSink: SearchViewEventSink,
        searchSessionManager: SearchSessionManager,
        showSearchReset: Boolean,
        searchRecipeBinState: SearchRecipeBinViewState?,
        searchBarContainerColor: ColorToken,
        searchBarIconContainerColor: ColorToken,
        searchBarTransitionDisabled: Boolean,
    ): SearchBarViewState {
        val query = searchSessionManager.searchQuery.value

        val searchIcon = menuItemFactory.createIcon(
            icon = imageRepository.search,
            tintColor = ColorToken.ThemeOnBackground,
            size = viewSpecArbitrator.iconSizeMediumLarge,
        )
        val clearIcon = menuItemFactory.createCircularButton(
            icon = imageRepository.closeInset,
            iconColor = ColorToken.ThemeOnBackground,
            containerColor = searchBarIconContainerColor,
            buttonSize = viewSpecArbitrator.iconSizeMediumLarge,
        )

        return SearchBarViewState(
            eventSink = eventSink,
            searchIcon = searchIcon,
            searchInputViewShape = shapeSpecFactory.searchInputBarShapeSpec,
            searchHint = if (showSearchReset) TextStyleCallToAction(strings.search) else TextStyleDisplay(strings.search),
            searchFieldTextStyle = TextStyle.CallToAction,
            query = query,
            showClearIcon = showSearchReset,
            clearIcon = clearIcon,
            recipeBinViewState = searchRecipeBinState,
            transitionEnabled = (!query.isNullOrEmpty() || searchRecipeBinState != null) && !searchBarTransitionDisabled,
            containerColor = searchBarContainerColor,
        )
    }

    override fun createSearchInputViewState(
        eventSink: SearchViewEventSink,
        searchSessionManager: SearchSessionManager,
        searchFieldFocused: Boolean,
        searchColors: SearchColorsViewState,
        contentCategoryGroup: SelectionGroupViewState,
        searchFilterGroup: SelectionGroupViewState,
        searchRecipeBinState: SearchRecipeBinViewState?,
        onScrimClick: ViewEventHandler?,
        searchBarContainerColor: ColorToken,
        searchBarIconContainerColor: ColorToken,
        searchBarTransitionDisabled: Boolean,
    ): SearchInputViewState {
        return SearchInputViewState.Data(
            viewSpec = viewSpecFactory.searchInputViewSpec,
            searchBar = createSearchBarViewState(
                eventSink,
                searchSessionManager,
                searchFieldFocused,
                searchRecipeBinState,
                searchBarContainerColor,
                searchBarIconContainerColor,
                searchBarTransitionDisabled,
            ),
            searchColors = searchColors,
            contentCategoryGroup = contentCategoryGroup,
            searchFilterGroup = searchFilterGroup,
            onScrimClick = onScrimClick,
            searchSheetExpanded = !searchFieldFocused,
            sheetDropShadow = ImageViewState(
                image = imageRepository.waterfallGradientBlack,
                viewSpec = imageViewSpecFactory.searchShadowImageViewSpec,
                imageSize = null,
            )
        )
    }

    override fun createSearchColorsViewState(
        eventSink: SearchViewEventSink,
        selectedColors: List<Pair<SearchColor, Boolean>>,
    ): SearchColorsViewState {
        val currentTheme = themeManager.theme.value

        return selectedColors.map { (color, isSelected) ->
//            val useAltSelectionColor = if (color == SearchColor.Black && currentTheme.isLight) {
//                true
//            } else if (color == SearchColor.White && currentTheme.isDark) {
//                true
//            } else {
//                false
//            }
            val canShowBorder = arbitrateCanShowColorBorder(color)

            SearchColorViewState(
                eventSink = eventSink,
                event = SearchViewEvent.SearchColorToggle(color),
                color = color.color,
                isSelected = isSelected,
                canShowBorder = canShowBorder,
            )
        }.let {
            SearchColorsViewState(
                colors = it,
                borderShape = shapeSpecFactory.searchColorBorderShapeSpec,
                backgroundShape = shapeSpecFactory.searchColorBackgroundShapeSpec,
                itemSize = viewSpecArbitrator.searchBarColorItemSize,
                insideItemSize = viewSpecArbitrator.searchBarColorInsideItemSize,
            )
        }
    }

    override fun createSearchContentCategorySelectionGroup(
        eventSink: SelectionViewEventSink,
        data: List<Pair<ContentCategorySpec, Boolean>>,
    ): SelectionGroupViewState {
        val selections = data.map { (category, isSelected) ->
            SelectionViewState(
                eventSink = eventSink,
                key = category,
                selected = isSelected,
                shape = shapeSpecFactory.searchContentCategoryShapeSpec,
                centerHorizontally = true,
                menuItem = menuItemFactory.createLabel(TextSelection(category.label)),
            )
        }

        return SelectionGroupViewState(
            eventSink = eventSink,
            viewSpec = viewSpecFactory.searchSelectionGroupViewSpec,
            items = selections,
            allowMultipleSelections = true,
        )
    }

    override fun createSearchCategoriesSelectionGroup(
        eventSink: SelectionViewEventSink,
        data: List<Pair<SearchCategorySpec, Boolean>>,
    ): SelectionGroupViewState {
        val selections = data.map { (spec, isSelected) ->
            SelectionViewState(
                eventSink = eventSink,
                key = spec,
                selected = isSelected,
                selectionViewStyle = SelectionViewStyle.Button,
                shape = shapeSpecFactory.searchSelectionButtonShapeSpec,
                menuItem = menuItemFactory.createLabel(TextSelection(spec.label, maxLines = 1)),
            )
        }

        return SelectionGroupViewState(
            eventSink = eventSink,
            viewSpec = viewSpecFactory.searchSelectionButtonGroupViewSpec,
            items = selections,
            allowMultipleSelections = false,
        )
    }

    override fun createSearchRecipeBinViewState(
        data: List<Any>,
        screenInput: Boolean,
        eventHandlerCloseSearch: ViewEventHandler.Event,
        eventHandlerQuerySubmit: ViewEventHandler.Event?,
        containerColor: ColorToken,
        iconContainerColor: ColorToken,
        visibleProgress: Float,
    ): SearchRecipeBinViewState? {
        val filters = mutableListOf<Pair<Text, Int>>()
        val colorViewStates = mutableListOf<SearchColorViewState>()
        data.forEach {
            when (it) {
                is SearchColor -> {
                    val canShowBorder = arbitrateCanShowColorBorder(it)
                    colorViewStates.add(
                        SearchColorViewState(
                            eventSink = {  },
                            event = SearchViewEvent.SearchColorToggle(it),
                            color = it.color,
                            isSelected = false,
                            canShowBorder = canShowBorder,
                        )
                    )
                }
                is SearchCategorySpec -> {
                    filters.add(
                        Pair(TextStyleBody(it.label, maxLines = 1), 0)
                    )
                }
                is ContentCategorySpec -> {
                    filters.add(
                        Pair(TextStyleBody(it.label, maxLines = 1), 1)
                    )
                }
                else -> {
                    throw IllegalArgumentException("Unsupported type: $it")
                }
            }
        }
        val colorsViewState = if (colorViewStates.isNotEmpty()) {
            SearchColorsViewState(
                colors = colorViewStates,
                borderShape = shapeSpecFactory.searchColorBorderShapeSpec,
                backgroundShape = shapeSpecFactory.searchColorBackgroundShapeSpec,
                itemSize = viewSpecArbitrator.searchRecipeBinColorItemSize,
                insideItemSize = viewSpecArbitrator.searchRecipeBinColorInsideItemSize,
            )
        } else {
            null
        }
        if (colorsViewState == null && filters.isEmpty()) {
            return null
        }

        val padding = viewSpecArbitrator.paddingLarge
        val halfPadding = padding / 2
        val finalState = mutableListOf<ViewState>()
        finalState.add(SpacerViewState(width = DpOptional(halfPadding)))
        if (colorsViewState != null) {
            finalState.add(colorsViewState)
            finalState.add(SpacerViewState(width = DpOptional(viewSpecArbitrator.paddingSmall)))
        }
        if (filters.isNotEmpty()) {
            val filtersWithCommas = mutableListOf<Text>()
            filters.sortedBy { it.second }.forEachIndexed { index, pair ->
                if (index > 0) {
                    filtersWithCommas.add(TextStyleBody(", ", maxLines = 1))
                }
                filtersWithCommas.add(pair.first)
            }
            finalState.add(
                SearchRecipeBinText(tags = filtersWithCommas)
            )
        }
        finalState.add(SpacerViewState(width = DpOptional(viewSpecArbitrator.paddingSmall)))

        val iconSize = viewSpecArbitrator.iconSizeMediumLarge2
        val clearIcon = menuItemFactory.createCircularButton(
            icon = imageRepository.closeInset,
            iconColor = ColorToken.ThemeOnBackground,
            containerColor = iconContainerColor,
            buttonSize = iconSize,
            eventHandler = eventHandlerCloseSearch,
        )
        val checkIcon = if (screenInput) {
            menuItemFactory.createCircularButton(
                icon = imageRepository.acceptInset,
                iconColor = ColorToken.ThemeOnBackground,
                containerColor = iconContainerColor,
                buttonSize = iconSize,
                eventHandler = eventHandlerQuerySubmit,
            )
        } else {
            null
        }

        val spacing = viewSpecArbitrator.paddingDefault
        val offsetDistance = iconSize + spacing
        val endOffsetX = offsetDistance.times(1 - visibleProgress)
        val recipeEndPadding = (if (checkIcon != null) ((iconSize * 2) + (spacing * 2)) else (iconSize + spacing)) - endOffsetX
        val viewSpec = SearchRecipeBinViewSpec(
            startPadding = halfPadding,
            endPadding = padding,
            recipeEndPadding = recipeEndPadding,
            spacing = spacing,
            actionIconSpacing = spacing,
        )

        val fadeEndPadding = if (checkIcon != null) (iconSize * 2 + spacing) else iconSize
        val edgeFadeViewState = EdgeFadeViewState(
            image = imageRepository.waterfallGradientWhiteVertical,
            viewSpec = EdgeFadeViewSpec(
                startPadding = 0.dp,
                endPadding = fadeEndPadding,
                startWidth = halfPadding,
                endWidth = padding*2,
                tintColorToken = containerColor,
                endOffsetX = -endOffsetX,
            )
        )

        return SearchRecipeBinViewState(
            viewStates = finalState,
            viewSpec = viewSpec,
            containerColorToken = containerColor,
            clearIcon = clearIcon,
            checkIcon = checkIcon,
            shape = shapeSpecFactory.searchInputBarShapeSpec,
            edgeFade = edgeFadeViewState,
            endOffsetX = endOffsetX,
        )
    }

    private fun arbitrateCanShowColorBorder(it: Any) =
        if (it == SearchColor.Dark && themeManager.theme.value.isDark) {
            true
        } else {
            it == SearchColor.Light && themeManager.theme.value.isLight
        }

    override fun createPlaceholderActionButton(): MenuItem {
        return menuItemFactory.createActionButton(
            null,
            Text.createPreset(""),
            eventHandler = ViewEventHandler.NoOp,
        )
    }

    override fun createGlobalOverlay(
        message: String,
        image: Image?,
        imageSize: Dp,
        scrimColor: ColorToken,
    ): GlobalOverlayViewState.Data {
        return GlobalOverlayViewState.Data(
            image = image ?: imageRepository.loading,
            scrimColor = scrimColor,
            imageSize = DpOptional(imageSize),
            message = TextStyleBody(message),
        )
    }

    override fun createCelebrationGlobalOverlay(
        animationCompleted: () -> Unit,
        animationProgressUpdates: (Float) -> Unit,
    ): GlobalOverlayViewState.Data {
        val image = imageRepository.celebration.updateAnimatedSpecWith(
            startAnimation = true,
            animationStarted = {},
            animationCompleted = animationCompleted,
            animationProgressUpdates = animationProgressUpdates,
        )
        return GlobalOverlayViewState.Data(
            image = image,
            scrimColor = ColorToken.Transparent,
            imageSize = null,
            message = null,
        )
    }

    override fun createRewardAdInternal(
        rewardAdInternalSpec: RewardAdInternalSpec,
        rewardAdInternalPlaybackState: RewardAdInternalPlaybackState,
        rewardAdInternalViewEventSink: RewardAdInternalViewEventSink,
        pausePlayback: Boolean,
        startPositionMillis: Int?,
        playbackCallbacks: ImageVideoPlaybackCallbacks?,
        playbackRemainingDuration: Duration?,
        isUnlocked: Boolean,
    ): RewardAdInternalViewState {
        val isPlaybackComplete = rewardAdInternalPlaybackState == RewardAdInternalPlaybackState.Complete
        val showPlaybackBuffering = rewardAdInternalPlaybackState == RewardAdInternalPlaybackState.Buffering
                || rewardAdInternalPlaybackState == RewardAdInternalPlaybackState.None
        val eventSink = rewardAdInternalViewEventSink as ViewEventSink
        val playbackRemainingDurationSeconds = (playbackRemainingDuration?.inWholeSeconds ?: 0)

        val closeEvent = ViewEventHandler.Event(
            eventSink = eventSink,
            event = RewardAdInternalViewEvent.CheckToClose,
        )
        val callToActionEvent = ViewEventHandler.Event(
            eventSink = eventSink,
            event = RewardAdInternalViewEvent.CallToAction,
        )

        val (rewardStatusContent, rewardStatusViewEvent) = when {
            isUnlocked -> strings.rewardGranted to closeEvent
            showPlaybackBuffering -> strings.buffering to ViewEventHandler.NoOp
            playbackRemainingDurationSeconds > 0 ->
                strings.getRewardCountdown(playbackRemainingDurationSeconds) to ViewEventHandler.NoOp
            else -> "" to ViewEventHandler.NoOp
        }

        val rewardStatus = menuItemFactory.createLabel(
            text = TextStyleCallToAction(
                rewardStatusContent,
                colorToken = ColorToken.ThemeBackground,
            ),
            contentAlignment = Alignment.Center,
            width = null,
            height = null,
        )

        val callToAction = menuItemFactory.createActionButton(
            image = null,
            text = TextButtonLabel(rewardAdInternalSpec.callToAction),
            buttonAppearance = ButtonAppearance.Highlight,
            containerColorToken = ColorToken.ThemeSecondary,
            eventHandler = callToActionEvent,
        )

        val closeButton = menuItemFactory.createCircularCloseButton(
            buttonSize = viewSpecArbitrator.iconSizeLarge,
            eventHandler = closeEvent,
            containerColor = ColorToken.ThemePrimary,
            tintColor = ColorToken.ThemeBackground,
        )

        val affiliateDisclaimer = menuItemFactory.createLabel(
            text = TextStyleCaption(
                strings.affiliateLinkDisclaimer,
                colorToken = ColorToken.ThemeBackground,
                textAlign = TextAlign.Center,
            ),
            contentAlignment = Alignment.Center,
            width = null,
            height = null,
        )

        val fallbackMedia = ImageViewState(
            rewardAdInternalSpec.postPlaybackMedia,
            imageViewSpecFactory.fullScreenImageViewSpec,
            imageSize = null,
        )

        val heroMedia = if (!isPlaybackComplete) {
            val videoState = ImageVideoState.Playback(
                isLooping = false,
                useAudio = true,
                pausePlayback = pausePlayback,
                startPositionMillis = startPositionMillis,
                playbackCallbacks = playbackCallbacks,
            )

            ImageViewState(
                Image.from(rewardAdInternalSpec.mediaImageModel),
                imageViewSpecFactory.fullScreenImageViewSpec,
                imageSize = null,
                videoState = videoState,
            )
        } else {
            null
        }

        val onBack: ViewEventHandler = ViewEventHandler.Event(
            eventSink = eventSink,
            event = RewardAdInternalViewEvent.CheckToClose,
        )

        return RewardAdInternalViewState.Data(
            theme = themeManager.lightTheme,
            heroMedia = heroMedia,
            fallbackMedia = fallbackMedia,
            closeButton = closeButton,
            disclaimer = affiliateDisclaimer,
            callToAction = callToAction,
            statusBarShadow = imageRepository.waterfallGradientBlack,
            footerShadow = imageRepository.waterfallGradientBlack,
            rewardStatus = rewardStatus,
            rewardStatusViewEventHandler = rewardStatusViewEvent,
            onClickContent = callToActionEvent,
            onBack = onBack,
        )
    }

    override fun createRewardAdInternalCloseAlert(
        rewardAdInternalViewEventSink: RewardAdInternalViewEventSink,
    ): AlertViewState {
        val eventSink = rewardAdInternalViewEventSink as ViewEventSink

        val closeConfirmEvent = ViewEventHandler.Event(
            eventSink = eventSink,
            event = RewardAdInternalViewEvent.ForceClose,
        )

       return AlertViewStateOkCancel(
            title = strings.closeRewardAdConfirmDialogTitle,
            message = strings.closeRewardAdConfirmDialogMessage,
            okOnClick = closeConfirmEvent,
            okLabel = strings.close,
            cancelOnClick = ViewEventHandler.NoOp,
            cancelLabel = strings.cancel,
        )

    }

    override fun createFolderPreviewViewState(
        title: String,
        wallpapers: List<Wallpaper>
    ): FolderPreviewViewState {
        return FolderPreviewViewState(
            title = TextStyleCallToAction(
                string = title,
                textAlign = TextAlign.Center,
                colorToken = ColorToken.Custom(Color.White),
            ),
            folderPreviewShapeSpec = shapeSpecFactory.folderPreviewShapeSpec,
            wallpapers = wallpapers.map { wallpaper ->
                viewStateMapper.mapWallpaperImage(
                    wallpaper = wallpaper,
                    viewSpec = if (wallpaper.isTrack) {
                        viewSpecFactory.wallpaperPreviewViewSpecFolderPreviewFeedTrack
                    } else {
                        viewSpecFactory.wallpaperPreviewViewSpecFolderPreviewFeedSingle
                    },
                )
            },
            titlePadding = viewSpecArbitrator.paddingDefault,
            bottomShadowImage = ImageViewState(
                image = imageRepository.waterfallGradientBlack,
                viewSpec = ImageViewSpec(
                    width = viewSpecArbitrator.folderPreviewWidth,
                    height = viewSpecArbitrator.folderPreviewHeight / 1.5f,
                    shapeSpec = shapeSpecFactory.folderPreviewShadowShapeSpec,
                    contentScale = ViewContentScale.FillBounds,
                ),
                imageSize = null,
            ),
        )
    }
}
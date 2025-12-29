package wallapp.content.state.settings

import androidx.compose.ui.unit.dp
import io.ktor.http.quote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.app.AppStateManager
import wallapp.appconfig.AppConfig
import wallapp.appicon.AppIcon
import wallapp.appicon.AppIconManager
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.FolderId
import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperId
import wallapp.content.state.appicon.AppIconSpec
import wallapp.content.state.appicon.AppIconSpecFactory
import wallapp.content.state.debug.DebugManager
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.settings.SettingViewState.ItemPreviewRowViewState
import wallapp.content.state.settings.SettingViewState.ItemPreviewViewState
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.content.state.widget.EdgeFadeViewSpec
import wallapp.content.state.widget.EdgeFadeViewState
import wallapp.data.osslicense.OssLicense
import wallapp.entitlement.EntitlementRepository
import wallapp.image.Image
import wallapp.image.bucket.ImageBucketManager
import wallapp.image.bucket.ImageBucketSpec
import wallapp.inappreview.InAppReviewManager
import wallapp.language.LanguageRepository
import wallapp.license.state.LicenseState
import wallapp.license.state.LicenseStateType
import wallapp.math.round
import wallapp.network.NetworkConnectionState
import wallapp.permission.SystemPermissionManager
import wallapp.permission.SystemPermissionStatus
import wallapp.permission.SystemPermissionType
import wallapp.pixel.alert.AlertManager
import wallapp.pixel.alert.AlertViewState
import wallapp.pixel.alert.AlertViewStateOkCancel
import wallapp.pixel.util.DpOptional
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState
import wallapp.preferences.UserPreferences
import wallapp.resources.Url
import wallapp.resources.image.ImageRepository
import wallapp.resources.string.StringRepository
import wallapp.screen.ScreenArgument
import wallapp.screen.ScreenArgument.PaywallScreenArgument
import wallapp.screen.ScreenArgument.ShowcaseAdsScreenArgument
import wallapp.screen.ScreenArgument.ShowcaseTypefaceIosScreenArgument
import wallapp.screen.ScreenArgument.ShowcaseTypefaceScreenArgument
import wallapp.screen.ScreenArgument.WallpaperShowcaseScreenArgument
import wallapp.system.platform.PlatformFeature
import wallapp.system.share.SystemShareManager
import wallapp.text.TextAlign
import wallapp.text.TextCaption
import wallapp.text.TextSettingButton
import wallapp.text.TextSettingHeading
import wallapp.text.TextSettingSubheading
import wallapp.text.TextSettingSubtitle
import wallapp.text.TextSettingTitle
import wallapp.theme.ColorToken
import wallapp.theme.ThemeManager
import wallapp.theme.ThemeType
import wallapp.theme.ThemeTypeSpec
import wallapp.time.TimeZoneRepository
import wallapp.view.ViewEventFactory
import wallapp.view.ViewSpecArbitrator
import wallapp.view.ViewSpecFactory
import wallapp.view.shape.ShapeSpecFactory

class SettingViewStateFactory(
    private val appConfig: AppConfig,
    private val appStateManager: AppStateManager,
    private val userPreferences: UserPreferences,
    private val strings: StringRepository,
    private val viewSpecFactory: ViewSpecFactory,
    private val viewEventFactory: ViewEventFactory,
    private val shapeSpecFactory: ShapeSpecFactory,
    private val entitlementRepository: EntitlementRepository,
    private val alertManager: AlertManager,
    private val themeManager: ThemeManager,
    private val appIconManager: AppIconManager,
    private val appIconSpecFactory: AppIconSpecFactory,
    private val licenseState: LicenseState,
    timeZoneRepository: TimeZoneRepository,
    languageRepository: LanguageRepository,
    private val debugManager: DebugManager,
    private val systemPermissionManager: SystemPermissionManager,
    private val systemShareManager: SystemShareManager,
    private val inAppReviewManager: InAppReviewManager,
    private val imageBucketManager: ImageBucketManager,
    private val imageRepository: ImageRepository,
    private val viewSpecArbitrator: ViewSpecArbitrator,
    private val coroutineScopeMain: CoroutineScope,
) {
    private val canShowChangeIconConfirm: Boolean
        get() = PlatformFeature.IsAndroid

    private fun createUrlOnClick(url: String): ViewEventHandler =
        viewEventFactory.createNavigateToUrl(url)

    fun createViewStateWrapper(viewState: ViewState): SettingViewState.ViewStateWrapper {
        return SettingViewState.ViewStateWrapper(viewState)
    }

    val dividerFull = SettingViewState.Divider()

    fun createHeading(title: String) = SettingViewState.Heading(
        title = TextSettingHeading(title),
    )

    val aboutHeading by lazy { createHeading(strings.about) }
    val settingsHeading by lazy { createHeading(strings.settings) }

    private fun createSettingDetail(
        title: String,
        summary: String? = null,
        onClick: ViewEventHandler?,
    ) = SettingViewState.Detail(
        title = TextSettingTitle(title),
        summary = summary?.let { TextSettingSubtitle(it) },
        onClick = onClick,
    )

    val openSourceLicenses: SettingViewState.Detail by lazy {
        createSettingDetail(
            title = strings.licenses,
            onClick = viewEventFactory.createNavigateToOssLicenses(),
        )
    }
    val account: SettingViewState.Detail by lazy {
        createSettingDetail(
            title = strings.account,
            onClick = viewEventFactory.createNavigateToAccount(),
        )
    }
    val privacyPolicy: SettingViewState.Detail by lazy {
        createSettingDetail(
            title = strings.privacyPolicy,
            onClick = createUrlOnClick(Url.PrivacyPolicy),
        )
    }
    val termsOfService: SettingViewState.Detail by lazy {
        createSettingDetail(
            title = strings.termsOfService,
            onClick = createUrlOnClick(Url.TermsOfService),
        )
    }

    val debugSettings: SettingViewState.Detail by lazy {
        createSettingDetail(
            title = strings.debugSettings,
            onClick = ViewEventHandler.createOnClick { appStateManager.navigateToDebugSettings() },
        )
    }

    val appVersion: SettingViewState.Detail by lazy {
        createSettingDetail(
            title = strings.version,
            summary = strings.versionName,
            onClick = null,
        )
    }

    /**
     * Note: all callbacks here will be recreated on every [SettingViewState.Switch] creation.
     */
    fun createSettingSwitch(
        title: String,
        summary: String? = null,
        mutableStateFlow: MutableStateFlow<Boolean>,
        showShimmer: Boolean = false,
    ): SettingViewState.Switch {
        val onChanged: (Boolean) -> Unit = {
            mutableStateFlow.value = it
        }

        return SettingViewState.Switch(
            title = TextSettingTitle(title),
            summary = summary?.let { TextSettingSubtitle(it, maxLines = 2) },
            icon = null,
            checked = mutableStateFlow.value,
            onCheckedChange = { newValue ->
                mutableStateFlow.value = newValue
                onChanged(newValue)
            },
            showShimmer = showShimmer,
            switchContentDescription = strings.switchContentDescription(
                title,
                mutableStateFlow.value,
            ),
            onClicked = {
                val newValue = !mutableStateFlow.value
                mutableStateFlow.value = newValue
                onChanged(newValue)
            }
        )
    }

    fun createSettingSwitch(
        title: String,
        summary: String? = null,
        mutableStateFlow: StateFlow<Boolean>,
        onChangedEvent: (Boolean) -> Unit,
    ): SettingViewState.Switch {
        return SettingViewState.Switch(
            title = TextSettingTitle(title),
            summary = summary?.let { TextSettingSubtitle(it, maxLines = 2) },
            icon = null,
            checked = mutableStateFlow.value,
            onCheckedChange = { newValue ->
                onChangedEvent.invoke(newValue)
            },
            switchContentDescription = strings.switchContentDescription(
                title,
                mutableStateFlow.value,
            ),
            onClicked = {
                val newValue = !mutableStateFlow.value
                onChangedEvent.invoke(newValue)
            }
        )
    }

    fun createSettingSwitchMutable(
        title: String,
        summary: String? = null,
        mutableStateFlow: MutableStateFlow<Boolean>,
        onChanged: ((Boolean) -> Unit)? = { },
    ): SettingViewState.SwitchMutable {
        return SettingViewState.SwitchMutable(
            title = TextSettingTitle(title),
            summary = summary?.let { TextSettingSubtitle(it) },
            icon = null,
            mutableStateFlow = mutableStateFlow,
            onChanged = onChanged,
            switchContentDescription = strings.switchContentDescription(
                title,
                mutableStateFlow.value,
            ),
        )
    }

    private fun createSettingSwitchMutable(
        mutableStateFlow: MutableStateFlow<Boolean>,
        title: String,
        summary: String,
    ) = createSettingSwitchMutable(
        title = title,
        summary = summary,
        mutableStateFlow = mutableStateFlow,
    ) {
        mutableStateFlow.value = it
    }

    fun createOssLicenseViewState(ossLicense: OssLicense): SettingViewState {
        return createSettingDetail(
            title = ossLicense.name,
            summary = null,//ossLicense.licenseType,
            onClick = createUrlOnClick(ossLicense.licenseUrl),
        )
    }

//    val useAltThemeSwitch: SettingViewState.SwitchMutable = createSettingSwitchMutable(
//        title = "Use Alt Themes",
//        summary = "Invert background colors",
//        mutableStateFlow = appConfig.useAltTheme as MutableStateFlow<Boolean>,
//    )

    val adsEnabledSwitch: SettingViewState.SwitchMutable by lazy {
        createSettingSwitchMutable(
            title = "Show Ads",
            summary = "AdMob/affiliate ads on feed; reward ad when changing wallpaper",
            mutableStateFlow = appConfig.adsEnabled,
        )
    }

    val enableNativeUiRendering: SettingViewState.SwitchMutable by lazy {
        createSettingSwitchMutable(
            title = "Native UI rendering",
            summary = null,
            mutableStateFlow = appConfig.enableNativeUiRendering,
        )
    }

    val enableNativeIosCollectionScreen: SettingViewState.SwitchMutable by lazy {
        createSettingSwitchMutable(
            title = "Native Collection Screen",
            summary = null,
            mutableStateFlow = appConfig.enableNativeIosCollectionScreen,
        )
    }

    val enableFeedPaging: SettingViewState.SwitchMutable by lazy {
        createSettingSwitchMutable(
            title = "Feed Paging",
            summary = null,
            mutableStateFlow = appConfig.enableFeedPaging,
        )
    }

    val enableLogging: SettingViewState.SwitchMutable by lazy {
        createSettingSwitchMutable(
            title = "Enable Logging",
            summary = null,
            mutableStateFlow = appConfig.enableLogging,
        )
    }

    val entitlementSummaryState: SettingViewState.Detail
        get() = createSettingDetail(
            title = "Entitlements Summary",
            onClick = ViewEventHandler.createOnClick {
                alertManager.show(
                    AlertViewState(
                        title = "Entitlements Summary",
                        message = entitlementRepository.getEntitlementSummaryState()?.debugString
                            ?: "None found",
                        buttonPrimary = "OK",
                        buttonSecondary = "Reset All",
                        buttonSecondaryOnClick = ViewEventHandler.createOnClick { debugManager.resetEntitlements() },
                    )
                )
            },
        )

    private fun getShareUserIdText(userId: String): String {
        return "User ID: $userId\n" +
                "\n" +
                "https://console.firebase.google.com/project/obra-app/firestore/databases/-default-/data/~2Fusers~2F$userId\n"
    }

    fun userIdState(userId: String): SettingViewState.Detail {
        return createSettingDetail(
            title = "User ID",
            summary = userId,
            onClick = ViewEventHandler.createOnClick {
                systemShareManager.share(getShareUserIdText(userId))
            },
        )
    }

    val forceSignOut: SettingViewState.Detail by lazy {
        createSettingDetail(
            title = "Force sign out",
            onClick = ViewEventHandler.createOnClick { debugManager.forceSignOut() },
        )
    }

    val systemTimeZone: SettingViewState.Detail by lazy {
        createSettingDetail(
            title = "System Time Zone",
            summary = timeZoneRepository.getCurrentTimeZoneIdentifier(),
            onClick = null,
        )
    }

    val systemLanguages: SettingViewState.Detail by lazy {
        createSettingDetail(
            title = "System Languages",
            summary = languageRepository.systemLocaleIdentifiers?.joinToString() ?: "None found",
            onClick = null,
        )
    }

    private val currentImageBucket: ImageBucketSpec?
        get() = imageBucketManager.currentImageBucketSpec.value
    private val currentImageBucketDetail: String
        get() = currentImageBucket?.let {
            val widthScale = imageBucketManager.currentImageBucketWidthScale.value
            val heightScale = imageBucketManager.currentImageBucketHeightScale.value
            if (widthScale == 1f && heightScale == 1f) {
                "Exact bucket match! 👌"
            } else {
                "widthScale: ${widthScale.round(4)}, heightScale: ${heightScale.round(4)}"
            }
        } ?: ""
    val imageBucket: SettingViewState.Detail by lazy {
        createSettingDetail(
            title = "Image Bucket: ${imageBucketManager.currentImageBucketSpec.value?.label?.quote()}",
            summary = currentImageBucketDetail,
            onClick = null,
        )
    }

    fun networkState(networkConnectionState: NetworkConnectionState): SettingViewState.Detail {
        return createSettingDetail(
            title = "Network State",
            summary = networkConnectionState.name,
            onClick = null,
        )
    }

    val forceSingleRewardAd: SettingViewState.SwitchMutable  by lazy {
        createSettingSwitchMutable(
            title = "Force single reward ad",
            summary = "Resets session's ad watch count",
            mutableStateFlow = debugManager.useDebugRewardAdCount,
        )
    }

    val forceDebugBillingManager: SettingViewState.SwitchMutable  by lazy {
        createSettingSwitchMutable(
            title = "Force debug billing manager",
            summary = null,
            mutableStateFlow = debugManager.useDebugBillingManager,
        )
    }

//    val showPerformanceStats: SettingViewState.SwitchMutable = createSettingSwitchMutable(
//        title = "Perf Stats (unverified)",
//        summary = null,
//        mutableStateFlow = appConfig.showPerformanceStats,
//    )

    val resetAll: SettingViewState.Detail  by lazy {
        createSettingDetail(
            title = "Reset All",
            summary = "Reset onboarding/entitlements + sign out",
            onClick = ViewEventHandler.createOnClick { debugManager.resetAll() },
        )
    }

    val forceCrash: SettingViewState.Detail  by lazy {
        createSettingDetail(
            title = "Force Crash",
            summary = "Test crash reporting",
            onClick = ViewEventHandler.createOnClick { debugManager.forceCrash() },
        )
    }

    private fun ThemeTypeSpec.map() = ItemPreviewViewState<Any>(
        viewSpec = viewSpecFactory.themeSettingViewSpec,
        label = TextSettingButton(label),
        item = this,
        style = SettingViewState.ItemPreviewStyle.Button,
        shapeSpec = shapeSpecFactory.buttonShapeSpecDefault,
    )

    fun createThemeSetting(
        themeTypes: List<ThemeType>,
        currentThemeType: ThemeType,
        displayTitle: Boolean = true,
        onSelectedChanged: (Int) -> Unit,
    ) = ItemPreviewRowViewState(
        viewSpec = SettingViewSpec.SettingItemPreviewRowViewSpec(
            horizontalPadding = viewSpecArbitrator.settingItemPreviewRowHorizontalPadding,
            verticalPadding = viewSpecArbitrator.settingItemPreviewRowVerticalPadding,
        ),
        title = (if (displayTitle) {
            strings.theme
        } else {
            null
        })
            ?.let { TextSettingSubheading(it) },
        itemPreviews = themeTypes.map { themeManager.themeTypeSpecMap[it]!!.map() },
        currentIndex = themeTypes.indexOf(currentThemeType),
        onSelectedChanged = onSelectedChanged,
        scrolling = false,
    )

    private val licenseStateType: StateFlow<LicenseStateType>
        get() = licenseState.licenseStateType

    private val SubscriptionPlan?.lockedImage: Image?
        get() = this?.let {
            val licenseStateType = licenseStateType.value
            when (it) {
                SubscriptionPlan.PlusBasic -> {
                    if (licenseStateType == LicenseStateType.Unlicensed) {
                        imageRepository.appIconPreviewLocked
                    } else {
                        null
                    }
                }
                SubscriptionPlan.PlusUnlimited -> {
                    if (licenseStateType == LicenseStateType.Plus) {
                        null
                    } else {
                        imageRepository.appIconPreviewLocked
                    }
                }
            }
        }

    private val AppIconSpec.mapped
        get() = ItemPreviewViewState<Any>(
            viewSpec = viewSpecFactory.appIconSettingViewSpec,
            label = TextSettingSubtitle(label),
            item = SettingAppIconViewState(
                appIconPreviewSelected = this.appIconPreviewSelected,
                appIconPreviewUnselected = this.appIconPreviewUnselected,
                wallAppIcon = this.icon,
                lockedImage = this.subscriptionPlan.lockedImage,
            ),
            shapeSpec = shapeSpecFactory.buttonShapeSpecDefault,
        )

    private val currentAppIcon: AppIcon
        get() = userPreferences.appIcon.value

    val appIconSettingSelector: SettingViewState
        get() = createAppIconSetting(
            AppIcon.All,
            currentAppIcon,
            displayTitle = true,
            onSelectedAppIcon,
        )
    private val onSelectedAppIcon: (Int) -> Unit = { index ->
        val appIconSpec = appIconSpecFactory.getSpec(AppIcon.All[index])
        if (appIconSpec.subscriptionPlan.lockedImage != null) {
            appStateManager.navigateToPaywall(subscriptionPlan = appIconSpec.subscriptionPlan)
        } else {
            if (canShowChangeIconConfirm) {
                alertManager.show(
                    AlertViewStateOkCancel(
                        title = strings.changeAppIconTitle,
                        message = strings.changeAppIconMessage,
                        okOnClick = ViewEventHandler.createOnClick {
                            updateAppIcon(index)
                        },
                        okLabel = strings.confirm,
                        cancelOnClick = ViewEventHandler.NoOp,
                        cancelLabel = strings.cancel,
                    ),
                )
            } else {
                updateAppIcon(index)
            }
        }
    }

    private fun updateAppIcon(index: Int) {
        val appIcon = AppIcon.All[index]
        userPreferences.appIcon.value = appIcon
        appIconManager.setAppIcon(appIcon)
    }

    fun createAppIconSetting(
        appIcons: List<AppIcon>,
        currentAppIcon: AppIcon,
        displayTitle: Boolean = true,
        onSelectedChanged: (Int) -> Unit,
    ): SettingViewState {
        val edgeFadePadding = viewSpecArbitrator.settingItemPreviewRowHorizontalPadding / 2
        val edgeFadeWidth = viewSpecArbitrator.settingItemPreviewRowHorizontalPadding
        val edgeFadeHeight = viewSpecArbitrator.appIconSettingItemHeight
        return ItemPreviewRowViewState(
            viewSpec = viewSpecFactory.appIconSettingRowViewSpec,
            title = (if (displayTitle) {
                strings.appIcon
            } else {
                null
            })
                ?.let { TextSettingSubheading(it) },
            itemPreviews = appIcons.map { appIconSpecFactory.getSpec(it).mapped },
            currentIndex = appIcons.indexOf(currentAppIcon),
            onSelectedChanged = onSelectedChanged,
            scrolling = true,
            edgeFade = EdgeFadeViewState(
                image = imageRepository.waterfallGradientWhiteVertical,
                viewSpec = EdgeFadeViewSpec(
                    startPadding = edgeFadePadding,
                    endPadding = edgeFadePadding,
                    startWidth = edgeFadeWidth,
                    endWidth = edgeFadeWidth,
                    height = DpOptional(edgeFadeHeight),
                    tintColorToken = ColorToken.ThemeBackground
                ),
            )
        )
    }

    private val currentTheme: ThemeType
        get() = themeManager.themeType.value

    val themeSettingSelector: SettingViewState
        get() = createThemeSetting(
            allThemeTypes,
            currentTheme,
            displayTitle = true,
            onSelectedTheme,
        )

    private val allThemeTypes = themeManager.allThemeTypes
    private val onSelectedTheme: (Int) -> Unit = { index ->
        val theme = allThemeTypes[index]
        themeManager.setThemeType(theme)
    }

    val spacerSmall = SettingViewState.Spacer(height = 8.dp)
    val spacerNormal = SettingViewState.Spacer(height = 16.dp)
    val spacerSection = SettingViewState.Spacer(height = 32.dp)

    private val settingsFooterString: String
        get() = strings.settingsFooter
    val settingsFooter by lazy {
        SettingViewState.Footer(
            messages = listOf(
                TextCaption(settingsFooterString, textAlign = TextAlign.Center)
            ),
        )
    }

    fun createAcceptTermsSwitch(
        acceptedTerms: MutableStateFlow<Boolean>,
        showShimmer: Boolean,
    ): SettingViewState.Switch = createSettingSwitch(
        mutableStateFlow = acceptedTerms,
        title = strings.acceptTermsSwitchTitle,
        summary = strings.acceptTermsSwitchSummary,
        showShimmer = showShimmer,
    )

    fun createJoinNewsletterSwitch(
        subscribedToNewsletter: StateFlow<Boolean>,
        onChangedEvent: (Boolean) -> Unit,
    ): SettingViewState.Switch = createSettingSwitch(
        title = strings.joinNewsletterSwitchTitle,
        summary = strings.joinNewsletterSwitchSummary,
        mutableStateFlow = subscribedToNewsletter,
        onChangedEvent = onChangedEvent,
    )

    // The GDPR consent dialog
    fun createPrivacySettings(
        viewEventHandler: ViewEventHandler,
    ): SettingViewState.Detail = createSettingDetail(
        title = strings.privacySettings,
        onClick = viewEventHandler,
    )

    fun createReportUsageStatsSwitch(
        reportUsageStats: MutableStateFlow<Boolean>,
    ): SettingViewState.Switch = createSettingSwitch(
        mutableStateFlow = reportUsageStats,
        title = strings.reportUsageStatsSwitchTitle,
        summary = strings.reportUsageStatsSwitchSummary,
    )

    fun createReceiveNotificationsSwitch(
        receiveNotifications: StateFlow<Boolean>,
        onChangedEvent: (Boolean) -> Unit,
    ): SettingViewState.Switch = createSettingSwitch(
        title = strings.notifications,
        mutableStateFlow = receiveNotifications,
        onChangedEvent = onChangedEvent,
    )

    fun createNotificationsTapToGrantPermission(
        viewEventHandler: ViewEventHandler,
    ): SettingViewState.Detail = createSettingDetail(
        title = strings.notifications,
        summary = strings.notificationsSummary,
        onClick = viewEventHandler,
    )

    fun createRestorePurchases(
        viewEventHandler: ViewEventHandler,
    ): SettingViewState.Detail = createSettingDetail(
        title = strings.restorePurchases,
        onClick = viewEventHandler,
    )

    fun createManageSubscription(createOnClick: ViewEventHandler): SettingViewState {
        return createSettingDetail(
            title = strings.manageSubscription,
            onClick = createOnClick,
        )
    }

    fun createSignOutAccount(
        viewEventHandler: ViewEventHandler,
    ): SettingViewState.Detail = createSettingDetail(
        title = strings.signOut,
        onClick = ViewEventHandler.createOnClick {
            alertManager.show(
                AlertViewStateOkCancel(
                    title = strings.signOutTitle,
                    message = strings.signOutMessage,
                    okOnClick = viewEventHandler,
                    okLabel = strings.yes,
                    cancelOnClick = ViewEventHandler.NoOp,
                    cancelLabel = strings.no,
                ),
            )
        },
    )

    fun createDeleteAccount(
        viewEventHandler: ViewEventHandler,
    ): SettingViewState.Detail {
        val onClick = ViewEventHandler.createOnClick {
            alertManager.show(
                AlertViewStateOkCancel(
                    title = strings.deleteAccountConfirmDialogTitle,
                    message = strings.deleteAccountConfirmDialogMessage,
                    okOnClick = viewEventHandler,
                    okLabel = strings.yes,
                    cancelOnClick = ViewEventHandler.NoOp,
                    cancelLabel = strings.no,
                ),
            )
        }

        return createSettingDetail(
            title = strings.deleteAccount,
            onClick = onClick,
        )
    }

    val permissionShortcutsTitle  by lazy {
        createHeading("Request Permissions")
    }

    private val postNotificationsPermissionStatus: StateFlow<String> by lazy {
        systemPermissionManager.postNotificationPermissionStatus
            .map { it.name }
            .stateIn(coroutineScopeMain, SharingStarted.Eagerly, SystemPermissionStatus.Unknown.name)
    }
    val permissionPostNotifications: Flow<SettingViewState> by lazy {
        postNotificationsPermissionStatus
            .map { status ->
                createSettingDetail(
                    title = "Post Notifications",
                    summary = status,
                    onClick = ViewEventHandler.createOnClick {
                        systemPermissionManager.permissionGuardedAction(
                            systemPermissionType = SystemPermissionType.PostNotifications,
                            actionOnSuccess = {},
                            actionOnDenied = {},
                        )
                    },
                )
            }
    }

    val systemMediaPermissionStatus: StateFlow<String> by lazy {
        systemPermissionManager.systemMediaPermissionStatus
            .map { it.name }
            .stateIn(coroutineScopeMain, SharingStarted.Eagerly, SystemPermissionStatus.Unknown.name)
    }
    val permissionSystemMedia: Flow<SettingViewState> by lazy {
        systemMediaPermissionStatus.map { status ->
            createSettingDetail(
                title = "System Media",
                summary = status,
                onClick = ViewEventHandler.createOnClick {
                    systemPermissionManager.permissionGuardedAction(
                        systemPermissionType = SystemPermissionType.SystemMedia,
                        actionOnSuccess = {},
                        actionOnDenied = {},
                    )
                },
            )
        }
    }

    val openToSystemAppInfo: SettingViewState.Detail by lazy {
        createSettingDetail(
            title = "Open to System App Info",
            onClick = viewEventFactory.createNavigateToSystemAppInfo(),
        )
    }

    val requestReview: SettingViewState.Detail by lazy {
        createSettingDetail(
            title = "Request Review",
            summary = "(may or may not display)",
            onClick = ViewEventHandler.createOnClick {
                inAppReviewManager.requestReview()
            }
        )
    }

    val openToAppMarketplace: SettingViewState.Detail by lazy {
        createSettingDetail(
            title = strings.updateAppAction,
            onClick = viewEventFactory.createNavigateToAppMarketplace(),
        )
    }

    val screenShortcutsTitle by lazy {
        createHeading(
            title = "Screen shortcuts",
        )
    }

    fun createScreenShortcut(
        title: String,
        onClick: ViewEventHandler,
    ): SettingViewState.Detail = createSettingDetail(
        title = title,
        summary = null,
        onClick = onClick,
    )

    private fun createNavigateToScreen(
        onClick: () -> Unit,
    ) = ViewEventHandler.createOnClick {
        onClick.invoke()
    }

    private val wallpaperSingleScreenArgument: WallpaperShowcaseScreenArgument
        get() = WallpaperShowcaseScreenArgument(
            remixId = RemixId(name = "a~red_83b82b16"),
        )
    private val wallpaperCollectionScreenArgument: WallpaperShowcaseScreenArgument
        get() = WallpaperShowcaseScreenArgument(
            remixId = RemixId(name = "a~indigo_3680e4d9"),
        )
    private val folderScreenArgument: ScreenArgument.FolderScreenArgument
        get() = ScreenArgument.FolderScreenArgument(FolderId("f~justadded"))
    private val errorRewardAdScreenArgument: ScreenArgument.ErrorScreenArgument
        get() = ScreenArgument.ErrorScreenArgument(ErrorScreen.RewardAd(wallpaperId = WallpaperId(name = "a~artistname_0e732a6f"), errorMessage = "Error message"))
    private val artistScreenArgument: ArtistId
        get() = ArtistId("a~artistname")
    private val collectionScreenArgument: CollectionId
        get() = CollectionId("a~artistname~dunes")

    val wallpaperScreensTitle by lazy {
        createHeading(
            title = "Wallpaper screens:",
        )
    }
    private val openWallpaperSingleScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = " ↳ Wallpaper (Single) screen",
            onClick = createNavigateToScreen(
                viewEventFactory.createOnClickNavigateToScreen(
                    wallpaperSingleScreenArgument
                )
            ),
        )
    }
    private val openWallpaperGroupScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = " ↳ Wallpaper (Collection) screen",
            onClick = createNavigateToScreen(
                viewEventFactory.createOnClickNavigateToScreen(
                    wallpaperCollectionScreenArgument
                )
            ),
        )
    }
    private val openErrorRewardAdScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = " ↳ Reward Ad Error screen",
            onClick = createNavigateToScreen(
                viewEventFactory.createOnClickNavigateToScreen(
                    errorRewardAdScreenArgument
                )
            ),
        )
    }
    private val openArtistScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = "Artist screen",
            onClick = viewEventFactory.createNavigateToScreen(artistScreenArgument),
        )
    }
    val openArtistsScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = "All Artists screen (debug)",
            onClick = viewEventFactory.createNavigateToArtists(),
        )
    }
    private val openCollectionScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = "Collection screen",
            onClick = viewEventFactory.createNavigateToScreen(collectionScreenArgument),
        )
    }
    private val openFolderScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = "Seen on Folder screen",
            onClick = viewEventFactory.createNavigateToScreen(folderScreenArgument),
        )
    }
    private val paywallScreensTitle by lazy {
        createHeading(
            title = "Paywall screens:",
        )
    }
    private val openPaywallDefaultScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = " ↳ Default",
            onClick = viewEventFactory.createNavigateToScreen(
                PaywallScreenArgument.Arbitrated(autoTriggerPurchase = false, subscriptionExpired = false, subscriptionPlan = null),
            ),
        )
    }
    private val openPaywallNativeScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = " ↳ Native",
            onClick = viewEventFactory.createNavigateToScreen(
                PaywallScreenArgument.Internal(autoTriggerPurchase = false, subscriptionExpired = false, subscriptionPlan = null),
            ),
        )
    }
    private val openOssLicensesScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = "OSS Licenses screen",
            onClick = viewEventFactory.createNavigateToScreen(ScreenArgument.OssLicensesScreenArgument),
        )
    }
    private val openAccountScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = "Account screen",
            onClick = viewEventFactory.createNavigateToAccount(),
        )
    }
    private val openDataConsentScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = "Data Consent screen",
            onClick = viewEventFactory.createNavigateToDataConsent(),
        )
    }
    private val openSignUpScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = "Sign Up screen",
            onClick = viewEventFactory.createNavigateToSignUp(),
        )
    }

//    private val openStoriesScreen: SettingViewState = createScreenShortcut(
//        title = "Stories screen",
//        onClick = createNavigateToScreen(viewEventFactory.createOnClickNavigateToScreen(storiesScreenArgument)),
//    )
//    private val openSearchScreen: SettingViewState = createScreenShortcut(
//        title = "Search screen",
//        onClick = createNavigateToScreen(viewEventFactory.createOnClickNavigateToSearch()),
//    )
    val errorScreensTitle by lazy {
        createHeading(
            title = "Error screens:",
        )
    }
    private val openErrorAppUpdateRequiredScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = " ↳ App Update Required screen",
            onClick = viewEventFactory.createNavigateToError(ErrorScreen.AppUpdateRequired),
        )
    }
    private val openErrorDownloadFailedScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = " ↳ Download Failed screen",
            onClick = viewEventFactory.createNavigateToError(ErrorScreen.DownloadFailed("Error msg")),
        )
    }

    private val openErrorNetworkScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = " ↳ Network screen",
            onClick = viewEventFactory.createNavigateToError(ErrorScreen.Network()),
        )
    }
    private val openErrorPermissionSystemMediaDeniedAndroidScreen: SettingViewState.Detail by lazy {
        createScreenShortcut(
            title = " ↳ Permission System Media Access Denied (Android) screen",
            onClick = viewEventFactory.createNavigateToError(ErrorScreen.PermissionSystemMediaDeniedAndroid),
        )
    }
    private val openErrorPermissionSystemMediaDeniedIosScreen: SettingViewState.Detail by lazy {
        createScreenShortcut(
            title = " ↳ Permission System Media Access Denied (iOS) screen",
            onClick = viewEventFactory.createNavigateToError(ErrorScreen.PermissionSystemMediaDeniedIos),
        )
    }
    private val openErrorPurchaseScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = " ↳ Purchase screen",
            onClick = viewEventFactory.createNavigateToError(ErrorScreen.Purchase("")),
        )
    }
    private val openErrorSubscriptionExpiredScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = " ↳ Subscription Expired screen",
            onClick = viewEventFactory.createNavigateToError(ErrorScreen.SubscriptionExpired),
        )
    }
    private val openErrorTemplateScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = " ↳ [Template screen]",
            onClick = viewEventFactory.createNavigateToError(ErrorScreen.Template),
        )
    }
    val showcaseScreensTitle by lazy {
        createHeading(
            title = "Showcase/debug screens:",
        )
    }
    private val openAdsTypefaceScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = " ↳ Ads showcase",
            onClick = viewEventFactory.createNavigateToScreen(ShowcaseAdsScreenArgument),
        )
    }
    private val openShowcaseTypefaceScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = " ↳ Typeface showcase",
            onClick = viewEventFactory.createNavigateToScreen(ShowcaseTypefaceScreenArgument),
        )
    }
    private val openShowcaseTypefaceIosScreen: SettingViewState by lazy {
        createScreenShortcut(
            title = " ↳ Typeface showcase iOS",
            onClick = viewEventFactory.createNavigateToScreen(ShowcaseTypefaceIosScreenArgument),
        )
    }

    val openScreenShortcuts: List<SettingViewState> by lazy {
        listOfNotNull(
            openArtistScreen,
            openCollectionScreen,
            openFolderScreen,
            wallpaperScreensTitle,
            openWallpaperSingleScreen,
            openWallpaperGroupScreen,
            paywallScreensTitle,
            openPaywallDefaultScreen,
            openPaywallNativeScreen,
            openSignUpScreen,
            openDataConsentScreen,
            openAccountScreen,
            openOssLicensesScreen,
            errorScreensTitle,
            openErrorRewardAdScreen,
            openErrorTemplateScreen,
            openErrorAppUpdateRequiredScreen,
            openErrorDownloadFailedScreen,
            openErrorNetworkScreen,
            openErrorPermissionSystemMediaDeniedAndroidScreen,
            openErrorPermissionSystemMediaDeniedIosScreen,
            openErrorPurchaseScreen,
            openErrorSubscriptionExpiredScreen,
            showcaseScreensTitle,
            openAdsTypefaceScreen,
            openShowcaseTypefaceScreen,
            openShowcaseTypefaceIosScreen,
            openArtistsScreen,
        )
    }

    fun getByKey(key: String): SettingViewState {
        return when (key) {
//            settingKeyManager.getSettingKey(userPreferences.themeType) -> themeSettingSelector
//            settingKeyManager.getSettingKey(userPreferences.appIcon) -> appIconSettingSelector
            else -> {
                throw IllegalArgumentException("Unknown setting key: $key")
            }
        }
    }

    fun getByKeys(
        keys: List<String>,
    ) = keys.map { getByKey(it) }
}
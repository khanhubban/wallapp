package wallapp.content.state.debug

import kotlinx.coroutines.flow.StateFlow
import wallapp.account.AccountManager
import wallapp.content.state.feed.FeedScrollStateController
import wallapp.content.state.settings.SettingViewState
import wallapp.content.state.settings.SettingViewStateFactory
import wallapp.content.state.settings.SettingsViewState
import wallapp.data.content.ContentRepository
import wallapp.data.content.ContentResult.ConnectionsSummaryContentResult
import wallapp.network.NetworkConnectionState
import wallapp.network.NetworkState
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.pixel.toolbar.ToolbarViewState
import wallapp.resources.string.StringRepository
import wallapp.system.platform.PlatformFeature
import wallapp.text.TextToolbarTitle
import wallapp.theme.ColorToken
import wallapp.util.combine
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateRefresher
import wallapp.view.menu.MenuItemFactory
import wallapp.viewmodel.ViewModel

class DebugViewModel(
    accountManager: AccountManager,
    private val settingViewStateFactory: SettingViewStateFactory,
    contentRepository: ContentRepository,
    menuItemFactory: MenuItemFactory,
    strings: StringRepository,
    private val viewStateFactory: ViewStateFactory,
    viewStateRefresher: ViewStateRefresher,
    private val networkState: NetworkState,
) : ViewModel(), ScreenViewStateProvider {

    val toolbarViewState = ToolbarViewState(
        navigationIcon = menuItemFactory.back,
        title = menuItemFactory.centeredText(TextToolbarTitle(strings.debugSettings)),
        containerColorOverride = ColorToken.Transparent,
    )

    private fun List<Any>.flatten(): List<SettingViewState> = flatMap { item ->
        when (item) {
            is List<*> -> item.map { it as SettingViewState }
            is SettingViewState -> listOf(item)
            else -> throw IllegalArgumentException("Unknown type: ${item::class}")
        }
    }

    private val feedScrollStateController = FeedScrollStateController(viewModelScope)

    private fun createViewState(
        connectionsSummary: ConnectionsSummaryContentResult = ConnectionsSummaryContentResult.Empty,
        permissionPostNotifications: SettingViewState? = null,
        permissionSystemMedia: SettingViewState? = null,
        userId: String? = null,
        networkConnectionState: NetworkConnectionState = networkState.networkConnectionState.value,
    ): SettingsViewState {
        val connectionsViewState = viewStateFactory
            .createProfileConnectionsViewState(connectionsSummary)

        return SettingsViewState(
            toolbarViewState = toolbarViewState,
            settingViewStates = listOfNotNull(
                settingViewStateFactory.createViewStateWrapper(connectionsViewState),
                settingViewStateFactory.dividerFull,
                settingViewStateFactory.entitlementSummaryState,
                settingViewStateFactory.resetAll,
                settingViewStateFactory.forceSignOut,
                userId?.let { settingViewStateFactory.userIdState(it) },
                settingViewStateFactory.dividerFull,
                settingViewStateFactory.networkState(networkConnectionState),
                settingViewStateFactory.imageBucket,
                settingViewStateFactory.systemTimeZone,
                settingViewStateFactory.systemLanguages,
                settingViewStateFactory.dividerFull,
                settingViewStateFactory.openToAppMarketplace,
                settingViewStateFactory.openToSystemAppInfo,
                settingViewStateFactory.requestReview,
                settingViewStateFactory.dividerFull,
                settingViewStateFactory.permissionShortcutsTitle,
                permissionPostNotifications,
                permissionSystemMedia,
                settingViewStateFactory.dividerFull,
                settingViewStateFactory.screenShortcutsTitle,
                settingViewStateFactory.openScreenShortcuts,
                settingViewStateFactory.dividerFull,
//                settingViewStateFactory.useAltThemeSwitch,
                settingViewStateFactory.adsEnabledSwitch,
                settingViewStateFactory.forceSingleRewardAd,
                settingViewStateFactory.forceDebugBillingManager,
                settingViewStateFactory.enableFeedPaging,
//                if (PlatformFeature.CanShowPerformanceStats) { settingViewStateFactory.showPerformanceStats } else { null },
                settingViewStateFactory.enableLogging,
                if (PlatformFeature.IsIos) { settingViewStateFactory.enableNativeUiRendering } else { null },
                if (PlatformFeature.IsIos) { settingViewStateFactory.enableNativeIosCollectionScreen } else { null },
                settingViewStateFactory.dividerFull,
                settingViewStateFactory.forceCrash,
                settingViewStateFactory.spacerSection,
            ).flatten(),
            fullScreenToolbarOffset = true,
            feedScrollStateWrapper = feedScrollStateController.scrollStateWrapper,
        )
    }

    override val viewState: StateFlow<SettingsViewState> = combine(
        contentRepository.connectionsSummaryContent,
        settingViewStateFactory.permissionPostNotifications,
        settingViewStateFactory.permissionSystemMedia,
        viewStateRefresher.refresh,
        feedScrollStateController.lastScrollStateUpdateFlow,
        networkState.networkConnectionState,
        accountManager.signedInOrAnonymousUserId,
    ) { connectionsSummary, permissionPostNotifications, permissionSystemMedia, _, _, networkConnectionState, userId, ->
        createViewState(connectionsSummary, permissionPostNotifications, permissionSystemMedia, userId, networkConnectionState)
    }.stateIn(createViewState())

    override fun onCleared() {
        super.onCleared()
    }
}
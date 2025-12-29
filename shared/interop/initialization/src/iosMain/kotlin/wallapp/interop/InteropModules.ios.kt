package wallapp.interop

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import platform.UIKit.UIViewController
import wallapp.account.AccountManager
import wallapp.account.data.AccountDataRepository
import wallapp.ads.AdUnitIds
import wallapp.ads.initializerstate.AdInitializerState
import wallapp.ads.reward.RewardAdHandleManager
import wallapp.ads.reward.RewardAdPlaybackManager
import wallapp.app.AppViewModelFactory
import wallapp.appconfig.AppConfig
import wallapp.application.Application
import wallapp.billing.BillingStateManager
import wallapp.buildconfig.BuildConfig
import wallapp.deeplink.DeepLinkManager
import wallapp.image.cache.ImageDiskCacheForIos
import wallapp.image.cache.ImageDiskCacheForIosDefault
import wallapp.image.host.ImageHostManager
import wallapp.image.host.ImageHostUrlMapper
import wallapp.image.scaler.ImageScaler
import wallapp.image.seikoDiskCacheDefault
import wallapp.lifecycle.AppLifecycleManager
import wallapp.messaging.CloudMessagingManager
import wallapp.network.NetworkErrorBroadcaster
import wallapp.network.NetworkRefreshManager
import wallapp.network.NetworkRefreshTriggerBroadcaster
import wallapp.pixel.globaloverlay.GlobalOverlayManager
import wallapp.pixel.navigation.NavigationManager
import wallapp.pixel.view.RenderIos
import wallapp.pixel.view.SingleView
import wallapp.remoteconfig.data.RemoteConfigData
import wallapp.remotepaywall.RemotePaywallManager
import wallapp.resources.image.ImageRepository
import wallapp.resources.string.StringRepository
import wallapp.screen.ScreenArgument
import wallapp.signin.SignInProviderController
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.window.WindowFrameManager
import wallapp.system.window.WindowFrameManagerDefault
import wallapp.theme.SystemThemeIos
import wallapp.theme.ThemeManager
import wallapp.view.ViewSpecArbitrator

@Suppress("unused")
object InteropModulesIos: KoinComponent {

    val application: Application = get()

    val appConfig: AppConfig by lazy { get() }

    val deepLinkManager: DeepLinkManager by lazy { get() }

    val uiControllerManager: UiControllerManager by lazy { get<UiControllerManager>() }

    val systemTheme: SystemThemeIos by lazy { get<SystemThemeIos>() }

    val windowFrameManager: WindowFrameManagerDefault by lazy {
        get<WindowFrameManager>() as WindowFrameManagerDefault
    }

    fun mainViewController(screenArgument: ScreenArgument?): UIViewController =
        MainViewController(get(), screenArgument)

    fun composeViewController(singleView: SingleView): UIViewController = ComposeViewController(get(), singleView)

    val accountManager: AccountManager by lazy { get() }

    val accountDataRepository: AccountDataRepository by lazy { get() }

    val signInProviderController: SignInProviderController by lazy { get() }

    val navigationManager: NavigationManager by lazy { get() }

    val adInitializerState: AdInitializerState by lazy { get() }

    val appViewModelFactory: AppViewModelFactory by lazy { get() }

    val imageHostManager: ImageHostManager by inject()
    val imageHostUrlMapper: ImageHostUrlMapper by inject()
    val imageScaler: ImageScaler by inject()

    val themeManager: ThemeManager by lazy { get() }

    val render: RenderIos by lazy { RenderIos(get(), get(), get(), get()) }

    val remotePaywallManager: RemotePaywallManager by lazy { get() }

    val globalOverlayManager: GlobalOverlayManager by lazy { get() }

    val imageDiskCacheForIos: ImageDiskCacheForIos by lazy { ImageDiskCacheForIosDefault(seikoDiskCacheDefault) }

    val rewardAdHandleManager: RewardAdHandleManager by lazy { get() }

    val adUnitIds: AdUnitIds by lazy { get() }

    val cloudMessagingManager: CloudMessagingManager by lazy { get() }

    val buildConfig: BuildConfig by lazy { get() }

    val viewSpecArbitrator: ViewSpecArbitrator by lazy { get() }

    val billingStateManager: BillingStateManager by lazy { get() }

    val imageRepository: ImageRepository by lazy { get() }

    val appLifecycleManager: AppLifecycleManager by lazy { get() }

    val networkRefreshTriggerBroadcaster: NetworkRefreshTriggerBroadcaster by lazy { get() }

    val networkRefreshManager: NetworkRefreshManager by lazy { get() }

    val networkErrorBroadcaster: NetworkErrorBroadcaster by lazy { get() }

    val stringRepository: StringRepository by lazy { get() }

    val remoteConfigData: RemoteConfigData by lazy { get() }

    val rewardAdPlaybackManager: RewardAdPlaybackManager by lazy { get() }
}

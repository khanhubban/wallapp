package wallapp.di

import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import org.koin.core.scope.Scope
import wallapp.account.data.AccountDataManager
import wallapp.account.data.AccountDataManagerNoOp
import wallapp.account.data.AccountDataRepository
import wallapp.account.data.AccountDataRepositoryCombined
import wallapp.account.data.AccountDataRepositoryPreferences
import wallapp.ad.reward.RewardAdPlaybackManagerDefault
import wallapp.ads.initializerstate.AdInitializerState
import wallapp.ads.inline.support.GlobalFullScreenAdShowCallbacks
import wallapp.ads.inline.support.GlobalFullScreenAdShowCallbacksDefault
import wallapp.ads.inline.support.GlobalFullScreenAdShowCallbacksNoOp
import wallapp.appconfig.AppConfig
import wallapp.appconfig.AppConfigAltProcess
import wallapp.appconfig.AppConfigMainProcess
import wallapp.appicon.AppIconManager
import wallapp.appicon.AppIconManagerNoOp
import wallapp.appshortcuts.AppShortcutsManager
import wallapp.appshortcuts.AppShortcutsManagerNoOp
import wallapp.appstate.AppState
import wallapp.appstate.AppStateAltProcess
import wallapp.appstate.AppStateMainProcess
import wallapp.appversion.AppVersionUpgrader
import wallapp.appversion.AppVersionUpgraderNoOp
import wallapp.auth.firebase.FirebaseAuthManager
import wallapp.auth.firebase.FirebaseAuthManagerNoOp
import wallapp.auth.google.GoogleAuthManager
import wallapp.auth.google.GoogleAuthManagerNoOp
import wallapp.billing.revenuecat.RevenueCatInitializerDefault
import wallapp.billing.revenuecat.RevenueCatInitializerNoOp
import wallapp.billing.revenuecat.RevenueCatPublicKeys
import wallapp.billing.revenuecat.RevenueCatUserManager
import wallapp.billing.revenuecat.RevenueCatUserManagerNoOp
import wallapp.buildconfig.BuildConfig
import wallapp.content.model.Id
import wallapp.content.model.WallpaperDefaults
import wallapp.content.network.repository.NetworkContentRepository
import wallapp.content.network.repository.NetworkContentRepositoryDefault
import wallapp.content.network.repository.NetworkContentRepositoryUrlDownloader
import wallapp.coroutine.CoroutineContexts
import wallapp.coroutine.CoroutineScopes
import wallapp.data.content.ContentCacheConfig
import wallapp.data.content.ContentCacheConfigDefault
import wallapp.data.folder.FolderRepository
import wallapp.data.folder.FolderRepositoryDefault
import wallapp.data.folder.FolderRepositoryNetwork
import wallapp.data.model.ModelRepository
import wallapp.data.model.ModelRepositoryDefault
import wallapp.data.model.ModelRepositoryRaw
import wallapp.device.DeviceId
import wallapp.device.DeviceInfo
import wallapp.device.DeviceModel
import wallapp.device.createSystemDeviceInfo
import wallapp.download.FirebaseStorageDownloader
import wallapp.download.FirebaseStorageDownloaderNoOp
import wallapp.flavorconfig.FlavorConfig
import wallapp.flavorconfig.FlavorConfigDefault
import wallapp.graphics.color.ColorManager
import wallapp.image.generateSeikoImageLoader
import wallapp.image.loader.ImageLoaderSeiko
import wallapp.initializer.module.ModuleInitializer
import wallapp.initializer.module.ModuleInitializers
import wallapp.instantapp.InstantAppManager
import wallapp.interop.InteropBridge
import wallapp.license.LicenseCacheAltProcess
import wallapp.license.LicenseCacheMainProcess
import wallapp.license.LicenseCacheStub
import wallapp.license.cache.LicenseCache
import wallapp.license.controller.LicenseCheckController
import wallapp.license.controller.LicenseCheckControllerDefault
import wallapp.license.controller.LicenseCheckControllerNoOp
import wallapp.license.state.LicenseStateRepository
import wallapp.license.state.LicenseStateRepositoryAltProcess
import wallapp.license.state.LicenseStateRepositoryDefault
import wallapp.media.network.repository.NetworkMediaMapRepository
import wallapp.media.network.repository.NetworkMediaMapRepositoryCombined
import wallapp.media.network.repository.NetworkMediaMapRepositoryNetwork
import wallapp.mediamap.MediaMapRepository
import wallapp.mediamap.MediaMapRepositoryConfig
import wallapp.mediamap.MediaMapRepositoryConfigDefault
import wallapp.mediamap.MediaMapRepositoryDefault
import wallapp.navigation.CurrentScreenProvider
import wallapp.navigation.CurrentScreenProviderDefault
import wallapp.network.NetworkUserManager
import wallapp.network.NetworkUserManagerNoOp
import wallapp.network.httpclient.createNetworkHttpClient
import wallapp.pixel.typeface.TypefaceRepository
import wallapp.pixel.typeface.TypefaceRepositoryNoOp
import wallapp.pixel.view.UIKitFactory
import wallapp.pixel.view.UIKitFactoryNoOp
import wallapp.preferences.UserPreferences
import wallapp.preferences.UserPreferencesAltProcess
import wallapp.preferences.UserPreferencesMainProcess
import wallapp.prefs.DevicePreferenceStorage
import wallapp.prefs.DevicePreferenceStorageDefault
import wallapp.prefs.PreferenceStorage
import wallapp.prefs.PreferenceStorageDefault
import wallapp.process.Process
import wallapp.process.bridge.ProcessBridge
import wallapp.process.listener.ProcessListenerManager
import wallapp.process.preference.ProcessBridgePreferenceManager
import wallapp.process.preference.ProcessBridgePreferenceManagerDefault
import wallapp.process.preference.ProcessBridgePreferenceManagerStub
import wallapp.process.requireIsDefault
import wallapp.purchase.PurchasableRepository
import wallapp.purchase.PurchasableRepositoryDefault
import wallapp.remoteapi.RemoteEndpointsSpecRepository
import wallapp.remoteapi.RemoteEndpointsSpecRepositoryDefault
import wallapp.remoteconfig.ConfigValueRepository
import wallapp.remoteconfig.ConfigValueRepositoryPreset
import wallapp.remoteconfig.RemoteConfig
import wallapp.remoteconfig.RemoteConfigPreset
import wallapp.remoteconfig.data.RemoteConfigData
import wallapp.remoteconfig.data.RemoteConfigDataMock
import wallapp.remoteendpoint.RemoteApiEndpointRepository
import wallapp.remoteendpoint.RemoteApiEndpointRepositoryDefault
import wallapp.runmode.RunMode
import wallapp.runmode.isNotApp
import wallapp.runmode.isTest
import wallapp.search.network.repository.NetworkSearchContentRepository
import wallapp.search.network.repository.NetworkSearchContentRepositoryDefault
import wallapp.settings.AllSettings
import wallapp.system.window.WindowFrameManager
import wallapp.system.window.WindowFrameManagerDefault
import wallapp.system.window.WindowFrameManagerNoOp
import wallapp.util.MainThreadChecker
import wallapp.viewmodel.ViewModelFactoryCommon


@Suppress("UNUSED_PARAMETER")
abstract class FactoryCommon : FactoryBase {

    override var runMode: RunMode = RunMode.App
    override var multiProcessAllowed: Boolean? = null
    override var isDebug: Boolean? = null

    val liveWallpaperAllowed: Boolean
        get() = multiProcessAllowed == true

    // Used during the dagger to koin migration in cases where we need to override the default
    // behavior to keep running, but we want to keep a record of each of these cases.
    @Suppress("unused")
    var useCompatibility: Boolean = true

    protected fun canConfigureRevenueCat(scope: Scope): Boolean {
        val devicePreferenceStorage = scope.get<DevicePreferenceStorage>()
        if (devicePreferenceStorage.useDebugBillingManager.value) {
            return false
        }

        val buildConfig = scope.get<BuildConfig>()
        val publicApiKeyExists = RevenueCatPublicKeys.get(buildConfig.packageName) != null

        return publicApiKeyExists && scope.isDefaultProcess
    }

    protected fun canConfigureRevenueCatPaywalls(scope: Scope): Boolean {
        return canConfigureRevenueCat(scope)
    }

    fun allSettings(scope: Scope) = AllSettings(
        listOf(
            scope.get(NamedScope.CacheFileMediaMap),
            scope.get(NamedScope.DeviceSettings),
            scope.get(NamedScope.LicenseSettings),
            scope.get(NamedScope.UserSettings),
        )
    )

    private fun commonModuleInitializers(scope: Scope): List<ModuleInitializer> {
        return listOf(
            revenueCatInitializer(scope)
        )
    }

    abstract fun platformModuleInitializers(scope: Scope): List<ModuleInitializer>

    fun moduleInitializers(scope: Scope): ModuleInitializers {
        return ModuleInitializers(
            initializers = commonModuleInitializers(scope) + platformModuleInitializers(scope),
        )
    }

    override fun accountDataManager(scope: Scope): AccountDataManager {
        return AccountDataManagerNoOp
    }

    fun accountDataRepository(scope: Scope): AccountDataRepository {
        return scope.get<AccountDataRepositoryCombined>()
    }

    fun accountDataRepositoryCombined(scope: Scope): AccountDataRepositoryCombined {
        return AccountDataRepositoryCombined(
            accountDataDefaults = scope.get(),
            accountDataRepositoryLocal = scope.get<AccountDataRepositoryPreferences>(),
            accountDataRepositoryServer = accountDataRepositoryServer(scope),
            coroutineScopeMain = scope.get(NamedScope.CoroutineScopeMain),
        )
    }

    open fun accountDataRepositoryServer(scope: Scope): AccountDataRepository? {
        return null
    }

    fun adMobTestDeviceIds(scope: Scope): List<String> = emptyList()

    fun appConfig(scope: Scope) : AppConfig {
        val process: Process = scope.get()
        return if (process.isDefaultProcess) {
            scope.get<AppConfigMainProcess>()
        } else {
            scope.get<AppConfigAltProcess>()
        }
    }

    open fun appIconManager(scope: Scope): AppIconManager {
        return AppIconManagerNoOp
    }

    override fun appShortcutsManager(scope: Scope): AppShortcutsManager = AppShortcutsManagerNoOp()

    fun appState(scope: Scope) : AppState {
        val process: Process = scope.get()
        val appStateMainProcess: Lazy<AppStateMainProcess> = scope.get(NamedScope.LazyAppStateMainProcess)
        val appStateAltProcess: Lazy<AppStateAltProcess> = scope.get(NamedScope.LazyAppStateAltProcess)
        return if (process.isDefaultProcess) {
            appStateMainProcess.get()
        } else {
            appStateAltProcess.get()
        }
    }

    fun appVersionUpgrader(scope: Scope): AppVersionUpgrader {
        val buildConfig: BuildConfig = scope.get()
        val appState: AppState = scope.get()
        return AppVersionUpgraderNoOp(buildConfig, appState)
    }

    fun colorManager(): ColorManager = ColorManager()

    override fun configValueRepository(scope: Scope): ConfigValueRepository {
        return ConfigValueRepositoryPreset()
    }

    override fun contentCacheConfig(scope: Scope): ContentCacheConfig {
        return scope.get<ContentCacheConfigDefault>()
    }

    override fun coroutineScopeIo(scope: Scope): CoroutineScope {
        require(!runMode.isTest) { "Use TestScope for testing" }
        return CoroutineScope(scope.get<CoroutineContexts>().io)
    }

    override fun coroutineScopeMain(scope: Scope): CoroutineScope {
        require(!runMode.isTest) { "Use TestScope for testing" }
        return CoroutineScope(scope.get<CoroutineContexts>().main)
    }

    override fun coroutineScopeMainImmediate(scope: Scope): CoroutineScope {
        require(!runMode.isTest) { "Use TestScope for testing" }
        return CoroutineScope(scope.get<CoroutineContexts>().mainImmediate)
    }

    fun coroutineScopes(scope: Scope): CoroutineScopes {
        require(!runMode.isTest) { "Use TestScope for testing" }
        return CoroutineScopes(
            main = CoroutineScope(scope.get<CoroutineContexts>().main),
            mainImmediate = CoroutineScope(scope.get<CoroutineContexts>().mainImmediate),
            io = CoroutineScope(scope.get<CoroutineContexts>().io),
            prefetch = CoroutineScope(scope.get<CoroutineContexts>().prefetch),
        )
    }

    override fun currentScreenProvider(scope: Scope): CurrentScreenProvider =
        scope.get<CurrentScreenProviderDefault>()

    fun deviceInfo(scope: Scope): DeviceInfo {
        if (runMode != RunMode.App) {
            return DeviceInfo.Preset
        }
        return createSystemDeviceInfo()
    }

    fun deviceModel(scope: Scope): DeviceModel {
        return scope.get<DeviceInfo>().deviceModel
    }

    override fun devicePreferenceStorage(scope: Scope): DevicePreferenceStorage {
        val process: Process = scope.get()
        process.requireIsDefault("DevicePreferenceStorage")
        return scope.get<DevicePreferenceStorageDefault>()
    }

    override fun firebaseAuthManager(scope: Scope): FirebaseAuthManager {
        return FirebaseAuthManagerNoOp
    }

    override fun firebaseStorageDownloader(scope: Scope): FirebaseStorageDownloader {
        return FirebaseStorageDownloaderNoOp
    }

    fun flavorConfig(scope: Scope): FlavorConfig {
        val applicationId: String = scope.get(NamedScope.ApplicationId)
        val deviceId: DeviceId = scope.get()
        return FlavorConfigDefault(applicationId, deviceId)
    }

    override fun folderRepository(scope: Scope): FolderRepository {
        return FolderRepositoryDefault(
            folderRepositoryNetwork = scope.get<FolderRepositoryNetwork>(),
        )
    }

    fun globalFullScreenAdShowCallbacks(scope: Scope): GlobalFullScreenAdShowCallbacks =
        globalFullScreenAdShowCallbacks(scope.get(), scope.get(NamedScope.LazyGlobalFullScreenAdShowCallbacksDefault), scope.get(NamedScope.LazyGlobalFullScreenAdShowCallbacksNoOp))

    fun globalFullScreenAdShowCallbacks(
        adInitializerState: AdInitializerState,
        defaultGlobalCallbacks: Lazy<GlobalFullScreenAdShowCallbacksDefault>,
        noOpGlobalCallbacks: Lazy<GlobalFullScreenAdShowCallbacksNoOp>,
    ): GlobalFullScreenAdShowCallbacks = if (adInitializerState.initializeAds) {
        defaultGlobalCallbacks.get()
    } else {
        noOpGlobalCallbacks.get()
    }

    override fun googleAuthManager(scope: Scope): GoogleAuthManager {
        return GoogleAuthManagerNoOp
    }

    fun imageLoaderSeiko(scope: Scope): ImageLoaderSeiko {
        return ImageLoaderSeiko(generateSeikoImageLoader(scope.get(), scope.get()))
    }

    fun interopBridge(scope: Scope): InteropBridge {
        return InteropBridge()
    }

    fun licenseCache(scope: Scope) : LicenseCache {
        val process: Process = scope.get()
        val licenseCacheMainProcess: Lazy<LicenseCacheMainProcess> = scope.get(NamedScope.LazyLicenseCacheMainProcess)
        val licenseCacheAltProcess: Lazy<LicenseCacheAltProcess> = scope.get(NamedScope.LazyLicenseCacheAltProcess)
        val instantAppManager: InstantAppManager = scope.get()
        return when {
            instantAppManager.isInstantApp -> {
                LicenseCacheStub(coroutineScope = scope.get(NamedScope.CoroutineScopeMain))
            }
            process.isDefaultProcess -> {
                licenseCacheMainProcess.get()
            }
            else -> {
                licenseCacheAltProcess.get()
            }
        }
    }

    fun licenseCheckController(scope: Scope) : LicenseCheckController {
        val process: Process = scope.get()
        val licenseCheckControllerDefault: Lazy<LicenseCheckControllerDefault> = scope.get(NamedScope.LazyLicenseCheckControllerDefault)
        val licenseCheckControllerNoOp: Lazy<LicenseCheckControllerNoOp> = scope.get(NamedScope.LazyLicenseCheckControllerNoOp)
        return if (process.isDefaultProcess) {
            licenseCheckControllerDefault.get()
        } else {
            licenseCheckControllerNoOp.get()
        }
    }

    fun licenseStateRepository(scope: Scope): LicenseStateRepository {
        val process: Process = scope.get()
        val licenseStateRepositoryMainProcess: Lazy<LicenseStateRepositoryDefault> = Lazy { scope.get() }
        val licenseStateRepositoryAltProcess: Lazy<LicenseStateRepositoryAltProcess> = Lazy { scope.get() }
        return if (process.isDefaultProcess) {
            licenseStateRepositoryMainProcess.get()
        } else {
            licenseStateRepositoryAltProcess.get()
        }
    }

    fun mainThreadChecker(): MainThreadChecker {
        return MainThreadChecker(allowMainThreadOperations = false)
    }

    override fun mediaMapRepository(scope: Scope): MediaMapRepository {
        return scope.get<MediaMapRepositoryDefault>()
    }

    override fun mediaMapRepositoryConfig(scope: Scope): MediaMapRepositoryConfig {
        return scope.get<MediaMapRepositoryConfigDefault>()
    }

    override fun modelRepository(scope: Scope): ModelRepository {
        return ModelRepositoryDefault(
            modelRepositoryRaw = scope.get<ModelRepositoryRaw>(),
            mediaMapRepository = scope.get(),
            coroutineScopes = scope.get(),
        )
    }

    fun networkContentHttpClient(scope: Scope): HttpClient {
        val buildConfig: BuildConfig = scope.get()

        return createNetworkHttpClient(
            loggingEnabled = buildConfig.debug,
        )
    }

    override fun networkContentRepository(scope: Scope): NetworkContentRepository {
        return NetworkContentRepositoryDefault(
            repositoryNetwork = scope.get<NetworkContentRepositoryUrlDownloader>(),
        )
    }

    override fun networkMediaMapRepository(scope: Scope): NetworkMediaMapRepository {
        return NetworkMediaMapRepositoryCombined(
            scope.get<NetworkMediaMapRepositoryNetwork>(),
        )
    }

    fun networkSearchContentRepository(scope: Scope): NetworkSearchContentRepository {
        return NetworkSearchContentRepositoryDefault(
            repositoryNetwork = scope.get(),
        )
    }

    override fun networkUserManager(scope: Scope): NetworkUserManager {
        return NetworkUserManagerNoOp()
    }

    open fun processBridgePreferenceManager(scope: Scope): ProcessBridgePreferenceManager {
        val processBridge: ProcessBridge = scope.get()
        val processListenerManager: ProcessListenerManager = scope.get()
        val coroutineContexts: CoroutineContexts = scope.get()
        return if (runMode.isNotApp) {
            ProcessBridgePreferenceManagerStub()
        } else {
            ProcessBridgePreferenceManagerDefault(
                processBridge,
                processListenerManager,
                scope.get(NamedScope.CoroutineScopeMain),
            )
        }
    }

    override fun preferenceStorage(scope: Scope): PreferenceStorage {
        val process: Process = scope.get()
        process.requireIsDefault("PreferenceStorage")

        return PreferenceStorageDefault(
            scope.get(),
            scope.get(NamedScope.UserSettings),
            scope.get(),
            scope.get(NamedScope.CoroutineScopeMain),
        )
    }

    override fun purchasableRepository(scope: Scope): PurchasableRepository {
        return scope.get<PurchasableRepositoryDefault>()
    }

    override fun remoteConfig(scope: Scope): RemoteConfig {
        return RemoteConfigPreset()
    }

    override fun remoteConfigData(scope: Scope): RemoteConfigData {
        return RemoteConfigDataMock(scope.get())
    }

    override fun remoteEndpointsSpecRepository(scope: Scope): RemoteEndpointsSpecRepository {
//        return RemoteEndpointsSpecRepositoryStaging()
        return scope.get<RemoteEndpointsSpecRepositoryDefault>()
    }

    private fun revenueCatInitializer(scope: Scope): ModuleInitializer {
        return if (canConfigureRevenueCat(scope)) {
            scope.get<RevenueCatInitializerDefault>()
        } else {
            RevenueCatInitializerNoOp
        }
    }

    override fun revenueCatUserManager(scope: Scope): RevenueCatUserManager {
        return RevenueCatUserManagerNoOp
    }

    fun rewardAdPlaybackManagerDefault(scope: Scope): RewardAdPlaybackManagerDefault? {
        return rewardAdPlaybackManagerAdMob(scope)?.let {
            RewardAdPlaybackManagerDefault(
                config = scope.get(),
                rewardAdPlaybackManagerAdMob = it,
                rewardAdInternalPlaybackManager = scope.get(),
                scope.get(),
                scope.get(),
            )
        }
    }

    override fun typefaceRepository(scope: Scope): TypefaceRepository = TypefaceRepositoryNoOp

    fun userPreferences(scope: Scope) : UserPreferences {
        val process: Process = scope.get()
        val userPreferencesMainProcess: Lazy<UserPreferencesMainProcess> = scope.get(NamedScope.LazyUserPreferencesMainProcess)
        val userPreferencesAltProcess: Lazy<UserPreferencesAltProcess> = scope.get(NamedScope.LazyUserPreferencesAltProcess)
        return if (process.isDefaultProcess) {
            userPreferencesMainProcess.get()
        } else {
            userPreferencesAltProcess.get()
        }
    }

    open fun uiKitFactory(scope: Scope): UIKitFactory {
        return UIKitFactoryNoOp
    }

    fun viewModelFactoryCommon(scope: Scope): ViewModelFactoryCommon {
        return ViewModelFactoryCommon(scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(),  scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(), scope.get(NamedScope.LazyAppViewModelFactory), scope.get(NamedScope.LazyAppViewModel), scope.get(), scope.get(), scope.get())
    }

    fun wallpaperDefaults(scope: Scope): WallpaperDefaults {
        val appConfig: AppConfig = scope.get()
        return WallpaperDefaults(
            Id.RemixId(appConfig.defaultRemixId),
            Id.DesignId(appConfig.defaultDesignId),
        )
    }

    override fun windowFrameManager(scope: Scope): WindowFrameManager {
        return if (runMode.isNotApp) {
            WindowFrameManagerNoOp
        } else {
            WindowFrameManagerDefault(scope.get(), scope.get(NamedScope.CoroutineScopeMain))
        }
    }

    override fun remoteApiEndpointRepository(scope: Scope): RemoteApiEndpointRepository {
        return scope.get<RemoteApiEndpointRepositoryDefault>()
    }

    val Scope.isDefaultProcess: Boolean
        get() = this.get<Process>().isDefaultProcess
}

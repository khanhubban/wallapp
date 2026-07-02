package wallapp.remoteconfig

import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import wallapp.appversion.AppVersion
import wallapp.log.Log
import wallapp.remoteconfig.data.RemoteConfigData
import wallapp.remoteconfig.data.RemoteConfigEntry
import wallapp.remoteconfig.data.RemoteConfigEntry.AccountBackendEnabled
import wallapp.remoteconfig.data.RemoteConfigEntry.AccountGoogleSignInEnabled
import wallapp.remoteconfig.data.RemoteConfigEntry.AccountSignInAppleEnabled
import wallapp.remoteconfig.data.RemoteConfigEntry.AppUpdateMinimumAllowedAppVersion
import wallapp.remoteconfig.data.RemoteConfigEntry.CatalogVersion
import wallapp.remoteconfig.data.RemoteConfigEntry.ContentShowSingles
import wallapp.remoteconfig.data.RemoteConfigEntry.FeedAdsEnabled
import wallapp.remoteconfig.data.RemoteConfigEntry.HighlightArtist
import wallapp.remoteconfig.data.RemoteConfigEntry.HighlightCollectionOfTheWeek
import wallapp.remoteconfig.data.RemoteConfigEntry.HighlightJustAdded
import wallapp.remoteconfig.data.RemoteConfigEntry.HighlightMostPopular
import wallapp.remoteconfig.data.RemoteConfigEntry.HighlightWallpaperOfTheWeek
import wallapp.remoteconfig.data.RemoteConfigEntry.ImageHostName
import wallapp.remoteconfig.data.RemoteConfigEntry.RewardAdsEnableConsecutivePlays
import wallapp.remoteconfig.data.RemoteConfigEntry.RewardAdsEnabled
import wallapp.remoteconfig.data.RemoteConfigEntry.RewardAdsInternalProbability
import wallapp.remoteconfig.data.RemoteConfigEntry.RewardAdsMaxCountToUnlockSingle
import wallapp.remoteconfig.data.RemoteConfigEntry.RewardAdsUnlockWallpaperOnFailure
import wallapp.remoteconfig.data.RemoteConfigEntry.TranslationsEnabled
import wallapp.remoteconfig.data.RemoteConfigEntry.UpgradeEnableAnnualSubscription
import wallapp.remoteconfig.data.RemoteConfigEntry.UpgradeEnableAnySubscriptions
import wallapp.string.quote

class RemoteConfigDataDefault(
    private val remoteConfig: RemoteConfig,
    private val configValueRepository: ConfigValueRepository,
    private val coroutineScopeMain: CoroutineScope,
) : RemoteConfigData {

    override val accountBackendEnabled: StateFlow<Boolean> = AccountBackendEnabled.toStateFlow()
    override val accountSignInAppleEnabled: StateFlow<Boolean> = AccountSignInAppleEnabled.toStateFlow()
    override val accountSignInGoogleEnabled: StateFlow<Boolean> = AccountGoogleSignInEnabled.toStateFlow()
    private val appUpdateMinimumAllowedAppVersion_platformSpecific_data: StateFlow<String> = AppUpdateMinimumAllowedAppVersion.toStateFlow()
    override val appUpdateMinimumAllowedAppVersion_platformSpecific: StateFlow<AppVersion?> by lazy {
        appUpdateMinimumAllowedAppVersion_platformSpecific_data
            .map { AppVersion.fromExportString(it) }
            .stateIn(
                scope = coroutineScopeMain,
                started = SharingStarted.Eagerly,
                initialValue = null
            )
    }
    override val catalogVersion: StateFlow<String> = CatalogVersion.toStateFlow()
    override val contentShowSingles: StateFlow<Boolean> = ContentShowSingles.toStateFlow()
    override val feedAdsEnabled: StateFlow<Boolean> = FeedAdsEnabled.toStateFlow()
    override val highlightArtist: StateFlow<String> =  HighlightArtist.toStateFlow()
    override val highlightCollectionOfTheWeek: StateFlow<String> = HighlightCollectionOfTheWeek.toStateFlow()
    override val highlightJustAdded: StateFlow<String> = HighlightJustAdded.toStateFlow()
    override val highlightMostPopular: StateFlow<String> = HighlightMostPopular.toStateFlow()
    override val highlightWallpaperOfTheWeek: StateFlow<String> = HighlightWallpaperOfTheWeek.toStateFlow()
    override val imageHostName: StateFlow<String> = ImageHostName.toStateFlow()
    override val rewardAdsEnableConsecutivePlays: StateFlow<Boolean> = RewardAdsEnableConsecutivePlays.toStateFlow()
    override val rewardAdsEnabled: StateFlow<Boolean> = RewardAdsEnabled.toStateFlow()
    override val rewardAdsInternalProbability: StateFlow<Double> = RewardAdsInternalProbability.toStateFlow()
    override val rewardAdsMaxCountToUnlockSingle: StateFlow<Long> = RewardAdsMaxCountToUnlockSingle.toStateFlow()
    override val rewardAdsUnlockWallpaperOnFailure: StateFlow<Boolean> = RewardAdsUnlockWallpaperOnFailure.toStateFlow()
    override val translationsEnabled: StateFlow<Boolean> = TranslationsEnabled.toStateFlow()
    override val upgradeEnableAnnualSubscription: StateFlow<Boolean> = UpgradeEnableAnnualSubscription.toStateFlow()
    override val upgradeEnableAnySubscriptions: StateFlow<Boolean> = UpgradeEnableAnySubscriptions.toStateFlow()

    @FlowInterop.Enabled
    override val useInternalAdsForGDPR: StateFlow<Boolean> = RemoteConfigEntry.UseInternalAdsForGDPR.toStateFlow(
        configValueRepository.getBooleanFromConfig(RemoteConfigEntry.UseInternalAdsForGDPR)
    )
    override val uuid: StateFlow<String> = RemoteConfigEntry.Uuid.toStateFlow()
    override val testingParam: StateFlow<String> = RemoteConfigEntry.TestingParam.toStateFlow()

    private inline fun <reified T : Any> RemoteConfigEntry<T>.toStateFlow(defaultOverride: T? = null): StateFlow<T> {
        return remoteConfig.dataRefreshed
            .map { configValueRepository.get(this) }
            .onEach { Log.d("[RemoteConfig] ${key.quote()}: $it") }
            .stateIn(
                scope = coroutineScopeMain,
                started = SharingStarted.Eagerly,
                initialValue = defaultOverride ?: default
            )
    }
}
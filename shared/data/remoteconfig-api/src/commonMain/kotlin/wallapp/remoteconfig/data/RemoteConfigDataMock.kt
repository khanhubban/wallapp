package wallapp.remoteconfig.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.appversion.AppVersion

class RemoteConfigDataMock(
    provider: RemoteConfigDataDefaultsProvider,
) : RemoteConfigData {

    override val accountSignInAppleEnabled: StateFlow<Boolean>  =
        MutableStateFlow(provider.accountAppleSignInEnabled)
    override val accountBackendEnabled: StateFlow<Boolean> =
        MutableStateFlow(provider.accountBackendEnabled)
    override val accountSignInGoogleEnabled: StateFlow<Boolean> =
        MutableStateFlow(provider.accountGoogleSignInEnabled)
    override val appUpdateMinimumAllowedAppVersion_platformSpecific: StateFlow<AppVersion?> =
        MutableStateFlow(null)
    override val contentShowSingles: StateFlow<Boolean> =
        MutableStateFlow(provider.contentShowSingles)
    override val feedAdsEnabled: StateFlow<Boolean> =
        MutableStateFlow(provider.feedAdsEnabled)
    override val highlightArtist: StateFlow<String> =
        MutableStateFlow(provider.highlightArtist)
    override val highlightCollectionOfTheWeek: StateFlow<String> =
        MutableStateFlow(provider.highlightCollectionOfTheWeek)
    override val highlightJustAdded: StateFlow<String> =
        MutableStateFlow(provider.highlightJustAdded)
    override val highlightMostPopular: StateFlow<String> =
        MutableStateFlow(provider.highlightMostPopular)
    override val highlightWallpaperOfTheWeek: StateFlow<String> =
        MutableStateFlow(provider.highlightWallpaperOfTheWeek)
    override val imageHostName: StateFlow<String> =
        MutableStateFlow(provider.imageHostName)
    override val rewardAdsEnableConsecutivePlays: StateFlow<Boolean> =
        MutableStateFlow(provider.rewardAdsEnableConsecutivePlays)
    override val rewardAdsEnabled: StateFlow<Boolean> =
        MutableStateFlow(provider.rewardAdsEnabled)
    override val rewardAdsInternalProbability: StateFlow<Double> =
        MutableStateFlow(provider.rewardAdsInternalProbability)
    override val rewardAdsMaxCountToUnlockSingle: StateFlow<Long> =
        MutableStateFlow(provider.rewardAdsMaxCountToUnlockSingle)
    override val rewardAdsUnlockWallpaperOnFailure: StateFlow<Boolean> =
        MutableStateFlow(provider.rewardAdsUnlockWallpaperOnFailure)
    override val translationsEnabled: StateFlow<Boolean> =
        MutableStateFlow(provider.translationsEnabled)
    override val upgradeEnableAnnualSubscription: StateFlow<Boolean> =
        MutableStateFlow(provider.upgradeEnableAnnualSubscription)
    override val upgradeEnableAnySubscriptions: StateFlow<Boolean> =
        MutableStateFlow(provider.upgradeEnableAnySubscriptions)
    override val useInternalAdsForGDPR: StateFlow<Boolean> =
        MutableStateFlow(provider.useInternalAdsForGDPR)
    override val uuid: StateFlow<String> = MutableStateFlow(provider.uuid)
    override val testingParam: StateFlow<String> = MutableStateFlow(provider.testingParam)
}
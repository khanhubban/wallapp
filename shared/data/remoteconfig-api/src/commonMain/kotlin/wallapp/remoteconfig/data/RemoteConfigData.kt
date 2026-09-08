package wallapp.remoteconfig.data

import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.flow.StateFlow
import wallapp.appversion.AppVersion

interface RemoteConfigData {

    val accountBackendEnabled: StateFlow<Boolean>
    val accountSignInAppleEnabled: StateFlow<Boolean>
    val accountSignInGoogleEnabled: StateFlow<Boolean>
    val appUpdateMinimumAllowedAppVersion_platformSpecific: StateFlow<AppVersion?>
    val catalogVersion: StateFlow<String>
    val catalogVersionStaging: StateFlow<String>
    val contentShowSingles: StateFlow<Boolean>
    val feedAdsEnabled: StateFlow<Boolean>
    val highlightArtist: StateFlow<String>
    val highlightCollectionOfTheWeek: StateFlow<String>
    val highlightJustAdded: StateFlow<String>
    val highlightMostPopular: StateFlow<String>
    val highlightWallpaperOfTheWeek: StateFlow<String>
    val imageHostName: StateFlow<String>
    val rewardAdsEnableConsecutivePlays: StateFlow<Boolean>
    val rewardAdsEnabled: StateFlow<Boolean>
    val rewardAdsInternalProbability: StateFlow<Double>
    val rewardAdsMaxCountToUnlockSingle: StateFlow<Long>
    val rewardAdsUnlockWallpaperOnFailure: StateFlow<Boolean>
    val translationsEnabled: StateFlow<Boolean>
    val upgradeEnableAnnualSubscription: StateFlow<Boolean>
    val upgradeEnableAnySubscriptions: StateFlow<Boolean>
    @FlowInterop.Enabled
    val useInternalAdsForGDPR: StateFlow<Boolean>
    val uuid: StateFlow<String>
    val testingParam: StateFlow<String>
}

// Accessed via a property to somewhat mask the name, see #2234
val RemoteConfigData.unlockAllForSessionUuid: StateFlow<String>
    get() = uuid

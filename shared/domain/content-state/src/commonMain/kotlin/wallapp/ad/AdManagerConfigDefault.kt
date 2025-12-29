package wallapp.ad

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.license.state.LicenseState
import wallapp.license.state.isUnlicensed
import wallapp.privacymessaging.PrivacyMessagingManager

class AdManagerConfigDefault(
    licenseState: LicenseState,
    private val privacyMessagingManager: PrivacyMessagingManager,
    coroutineScopeMain: CoroutineScope,
) : AdManagerConfig {

    private val adsEnabled: StateFlow<Boolean> =
        licenseState.licenseStateType
            .map { licenseStateType -> licenseStateType.isUnlicensed() }
            .stateIn(coroutineScopeMain, started = SharingStarted.Eagerly, initialValue = false)

    override val feedAdsEnabled: StateFlow<Boolean> = MutableStateFlow(false)

    override val canRequestThirdPartyAds: Boolean
        get() = privacyMessagingManager.canRequestAds && adsEnabled.value
}
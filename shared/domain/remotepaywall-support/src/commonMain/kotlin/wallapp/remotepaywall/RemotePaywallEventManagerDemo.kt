package wallapp.remotepaywall

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import wallapp.coroutine.CoroutineScopeMain
import wallapp.entitlement.EntitlementRepository
import wallapp.license.state.LicenseStateType
import wallapp.license.state.isPlus
import wallapp.util.combine

class RemotePaywallEventManagerDemo(
    private val remotePaywallEventRepository: RemotePaywallEventRepository,
    remotePaywallManager: RemotePaywallManager,
    entitlementRepository: EntitlementRepository,
    @CoroutineScopeMain coroutineScopeMain: CoroutineScope,
) : RemotePaywallEventManager {

    private val paywallEvents: List<RemotePaywallEvent>?
        get() = listOfNotNull(
            remotePaywallEventRepository.demo1,
            remotePaywallEventRepository.demo2,
            remotePaywallEventRepository.demo3,
        )
            .ifEmpty { null }
    private val paywallEventIndex = MutableStateFlow(paywallEvents?.let { 0 })

    private val nextRemotePaywallEvent: StateFlow<RemotePaywallEvent?> =
        combine(
            entitlementRepository.licenseState,
            remotePaywallManager.supportsDynamicPaywall,
            paywallEventIndex,
        ) { licenseState: LicenseStateType, supportsDynamicPaywall: Boolean, paywallEventIndex ->
            val demoEvent = paywallEvents?.getOrNull(paywallEventIndex ?: 0)
            if (!licenseState.isPlus() && supportsDynamicPaywall) {
                demoEvent
            } else {
                null
            }
        }.stateIn(coroutineScopeMain, started = SharingStarted.Eagerly, initialValue = null)

    override fun arbitrateRemotePaywallEvent(suggested: RemotePaywallEvent?): RemotePaywallEvent? {
        if (suggested != null) {
            return suggested
        }

        val result = nextRemotePaywallEvent.value
        val demoEventIndex = paywallEventIndex.value
        if (result != null && demoEventIndex != null) {
            this.paywallEventIndex.value = (demoEventIndex + 1)
                .takeIf { it < (paywallEvents?.size ?: 0) } ?: 0
        }

        return result
    }
}
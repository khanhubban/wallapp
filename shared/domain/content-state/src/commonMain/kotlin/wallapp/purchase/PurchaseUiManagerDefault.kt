package wallapp.purchase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import wallapp.entitlement.EntitlementRepository
import wallapp.license.state.isLicensedAny

class PurchaseUiManagerDefault(
    private val entitlementRepository: EntitlementRepository,
    coroutineScopeMain: CoroutineScope,
) : PurchaseUiManager {

    private val hasPlus: Flow<Boolean?> get() = entitlementRepository.licenseState.map { it.isLicensedAny() }

    private val showPurchaseUi: Flow<Boolean> = hasPlus.map {
        if (it == null) {
            false
        } else {
            !it
        }
    }.stateIn(
        scope = coroutineScopeMain,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false,
    )

    override val showToolbarButton: Flow<Boolean>
        get() = showPurchaseUi
    override val showInAccount: Flow<Boolean>
        = flowOf(false)
//        get() = showPurchaseUi
}
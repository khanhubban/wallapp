package wallapp.entitlement

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import wallapp.content.model.Id
import wallapp.data.entitlement.EntitlementState
import wallapp.license.state.LicenseStateType

object EntitlementRepositoryNoOp : EntitlementRepository {
    override val licenseState: StateFlow<LicenseStateType> = MutableStateFlow(LicenseStateType.Unlicensed)

    override fun getEntitlementState(id: Id): Flow<EntitlementState> = flowOf(EntitlementState.Uninitialized)

    override fun setRewardEntitlement(id: Id) { }

    override fun getEntitlementSummaryState(): EntitlementSummaryState? = null
}
package wallapp.entitlement

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.Id
import wallapp.data.entitlement.EntitlementState
import wallapp.license.state.LicenseStateType

interface EntitlementRepository {

    val licenseState: StateFlow<LicenseStateType>

    fun getEntitlementState(id: Id): Flow<EntitlementState>

    fun setRewardEntitlement(id: Id)

    fun getEntitlementSummaryState(): EntitlementSummaryState?
}
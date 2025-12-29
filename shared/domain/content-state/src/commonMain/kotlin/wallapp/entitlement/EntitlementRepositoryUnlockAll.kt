package wallapp.entitlement

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.Id
import wallapp.content.model.Id.CategoryId
import wallapp.data.entitlement.EntitlementState
import wallapp.license.state.LicenseStateType
import wallapp.string.quote

class EntitlementRepositoryUnlockAll : EntitlementRepository {

    override val licenseState: StateFlow<LicenseStateType> = MutableStateFlow(LicenseStateType.Plus)

    private fun validateId(id: Id) {
        require(id !is CategoryId) { "Must use CollectionId instead for ${id.name.quote()}" }
    }

    private val entitlementStateMap: MutableMap<Id, MutableStateFlow<EntitlementState>> = mutableMapOf()
    private fun getEntitlementStateInternal(id: Id): MutableStateFlow<EntitlementState> {
        validateId(id)
        return entitlementStateMap.getOrPut(id) {
            MutableStateFlow(EntitlementState.SubscriberPlus)
        }
    }

    override fun getEntitlementState(id: Id): Flow<EntitlementState> {
        validateId(id)
        return getEntitlementStateInternal(id)
    }

    override fun setRewardEntitlement(id: Id) { }

    override fun getEntitlementSummaryState(): EntitlementSummaryState =
        EntitlementSummaryState.UnlockAll
}
package wallapp.content.state.debug

import kotlinx.coroutines.flow.MutableStateFlow

interface DebugManager {

    fun forceSignOut()

    fun resetAll()
    fun resetEntitlements()

    fun forceCrash()

    val useDebugRewardAdCount: MutableStateFlow<Boolean>
    val useDebugBillingManager: MutableStateFlow<Boolean>
}
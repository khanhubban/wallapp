package wallapp.content.state.debug

import kotlinx.coroutines.flow.MutableStateFlow

object DebugManagerNoOp : DebugManager {

    override fun forceSignOut() { }

    override fun resetAll() { }

    override fun resetEntitlements() { }

    override fun forceCrash() { }

    override val useDebugRewardAdCount: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val useDebugBillingManager: MutableStateFlow<Boolean> = MutableStateFlow(false)
}
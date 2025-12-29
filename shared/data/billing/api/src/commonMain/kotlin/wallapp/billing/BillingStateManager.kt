package wallapp.billing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.crashtracking.CrashTracking
import wallapp.crashtracking.CrashTrackingHolder
import wallapp.log.Log

interface BillingStateManager {

    val billingState: StateFlow<BillingState>

    fun onUserUpdating()

    fun onCustomerInfoUpdated()

    fun onLoginError(code: Int, description: String)

    fun onLogoutError(code: Int, description: String)

    fun onRestorePurchasesError(code: Int?, description: String)
}

class BillingStateManagerDefault : BillingStateManager {

    private val _billingState = MutableStateFlow<BillingState>(BillingState.NotInitialized)
    override val billingState: StateFlow<BillingState>
        get() = _billingState

    private val crashTracking: CrashTracking
        get() = CrashTrackingHolder.crashTracking

    override fun onUserUpdating() {
        Log.d("[BillingStateManager] onUserUpdating")
        _billingState.value = BillingState.Initializing
    }

    override fun onCustomerInfoUpdated() {
        Log.d("[BillingStateManager] onCustomerInfoUpdated")
        _billingState.value = BillingState.Initialized
    }

    override fun onLoginError(code: Int, description: String) {
        Log.e("[BillingStateManager] onLoginError: $code, $description")
        _billingState.value = BillingState.Error.LoginError(code, description)
        crashTracking.logNonFatalException(RuntimeException("RevenueCat login error: $description, code: $code"))
    }

    override fun onLogoutError(code: Int, description: String) {
        Log.e("[BillingStateManager] onLogoutError: $code, $description")
        _billingState.value = BillingState.Error.LogoutError(code, description)
        crashTracking.logNonFatalException(RuntimeException("RevenueCat logout error: $description, code: $code"))
    }

    override fun onRestorePurchasesError(code: Int?, description: String) {
        Log.e("[BillingStateManager] onRestorePurchasesError: $code, $description")
        _billingState.value = BillingState.Error.RestorePurchasesError(code, description)
        crashTracking.logNonFatalException(RuntimeException("RevenueCat restore purchases error: $description, code: $code"))
    }
}

sealed class BillingState {
    data object NotInitialized : BillingState()
    data object Initializing : BillingState()
    data object Initialized : BillingState()
    sealed class Error(open val code: Int?, open val description: String) : BillingState() {
        data class LoginError(override val code: Int, override val description: String) : Error(code, description)
        data class LogoutError(override val code: Int, override val description: String) : Error(code, description)
        data class RestorePurchasesError(override val code: Int?, override val description: String) : Error(code, description)
    }
}

fun BillingState.isInitialized(): Boolean {
    return this is BillingState.Initialized
}
package wallapp.billing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import wallapp.app.AppStateManager
import wallapp.content.state.error.ErrorScreen
import wallapp.log.Log

class BillingManagerErrorListenerDefault(
    private val appStateManager: AppStateManager,
    billingStateManager: BillingStateManager,
    coroutineScopeMain: CoroutineScope,
) : BillingManagerErrorListener {

    override fun onBillingError(titleSuffix: String?, message: String) {
        val titlePrefix = "RevenueCat Billing error"
        val title = if (titleSuffix != null) {
            "$titlePrefix - $titleSuffix"
        } else {
            titlePrefix
        }
        appStateManager.navigateToError(ErrorScreen.Purchase(errorMessage = "$title : $message"))
    }

    init {
        billingStateManager.billingState.onEach { billingState ->
            Log.d("[BillingManagerErrorListener] billingState: $billingState")
            if (billingState is BillingState.Error) {
                val code = billingState.code
                if (code != null) {
                    onBillingError(null, "${billingState.description}, code: ${billingState.code}")
                } else {
                    onBillingError(null, billingState.description)
                }
            }
        }.launchIn(coroutineScopeMain)
    }
}
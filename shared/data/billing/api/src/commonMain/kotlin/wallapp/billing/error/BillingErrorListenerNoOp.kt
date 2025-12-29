package wallapp.billing.error

import wallapp.billing.BillingResult
import wallapp.system.ui.controller.UiController


object BillingErrorListenerNoOp : BillingErrorListener() {
    override fun onConnectionError(billingResult: BillingResult) { }

    override fun onInitiatePurchaseError(billingResult: BillingResult, uiController: UiController) { }

    override fun onPurchasesUpdatedError(billingResult: BillingResult, uiController: UiController?) { }
}
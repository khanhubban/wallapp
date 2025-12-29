package wallapp.billing.error;

import wallapp.billing.BillingResult
import wallapp.system.ui.controller.UiController


abstract class BillingErrorListener {

    abstract fun onConnectionError(billingResult: BillingResult)

    abstract fun onInitiatePurchaseError(billingResult: BillingResult, uiController: UiController)

    abstract fun onPurchasesUpdatedError(billingResult: BillingResult, uiController: UiController?)
}

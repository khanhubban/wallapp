package com.android.billingclient.api


data class InternalPurchasesResult(
  val billingResult: BillingResult,
  val purchasesList: List<Purchase>?,
) {
  val responseCode: Int
    get() = billingResult.responseCode
}

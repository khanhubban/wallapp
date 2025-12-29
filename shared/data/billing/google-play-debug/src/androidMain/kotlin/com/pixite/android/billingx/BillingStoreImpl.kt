package com.pixite.android.billingx

import wallapp.billing.billingProductDescriptors
import wallapp.billing.createBillingResult
import wallapp.billing.sku.BillingProductId
import android.content.SharedPreferences
import androidx.core.content.edit
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.InternalPurchasesResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import org.json.JSONArray
import org.json.JSONObject

class BillingStoreImpl(private val prefs: SharedPreferences) : BillingStore(){

  companion object {
    internal const val KEY_PURCHASES = "dbc_purchases"
  }

  private val allSkuDetails = mutableListOf<SkuDetails>()
  private val allProductDetails = mutableListOf<ProductDetails>()

  override fun addProduct(productDetails: ProductDetails): BillingStore {
    allProductDetails.add(productDetails)
    return this
  }

  override fun addProduct(skuDetails: SkuDetails): BillingStore {
    allSkuDetails.add(skuDetails)
    return this
  }

  override fun getSkuDetails(params: QueryProductDetailsParams?): List<ProductDetails> {
    if (params == null) return emptyList()
    val descriptors = params.billingProductDescriptors ?: return emptyList()
    val productIds = descriptors.map { it.productId }
//    val productTypes = descriptors.map { it.productType.productType }.distinct()
//    require(productTypes.size <= 1) { "TODO: Update this code to work with IAPs AND subscriptions" }
//    return allProductDetails.filter {
//      productIds.contains(BillingProductId.from(it.productId)) && productTypes.contains(it.productType)
//    }
    return allProductDetails.filter {
      productIds.contains(BillingProductId.from(it.productId))
    }
  }

  override fun getSkuDetails(params: SkuDetailsParams?): List<SkuDetails> {
      return allSkuDetails
        .filter { params?.skusList?.contains(it.sku) == true && it.type == params.skuType }
  }

  override fun getPurchases(@ProductType productType: String?): InternalPurchasesResult {
    return InternalPurchasesResult(
      billingResult = createBillingResult(BillingClient.BillingResponseCode.OK),
      purchasesList = prefs.getString(KEY_PURCHASES, "[]")
          ?.toPurchaseList()
          ?.filter {
            if (productType != null) {
              it.signature.endsWith(productType)
            } else {
              true
            }
          },
    )
  }

  override fun addPurchase(purchase: Purchase): BillingStore {
    val allPurchases = JSONArray(prefs.getString(KEY_PURCHASES, "[]"))
    allPurchases.put(purchase.toJSONObject())
    prefs.edit(commit = true) {
      putString(KEY_PURCHASES, allPurchases.toString())
    }
    return this
  }

  override fun removePurchase(sku: String): BillingStore {
    val allPurchases: List<Purchase>? = prefs.getString(KEY_PURCHASES, "[]")?.toPurchaseList()
    val filtered = allPurchases?.filter { !it.skus.contains(sku) }
    val json = JSONArray()
    filtered?.forEach { json.put(it.toJSONObject()) }
    prefs.edit(commit = true) {
      putString(KEY_PURCHASES, json.toString())
    }
    return this
  }

  override fun clearPurchases(): BillingStore {
    prefs.edit(commit = true) {
      remove(KEY_PURCHASES)
    }
    return this
  }

  private fun Purchase.toJSONObject(): JSONObject =
      JSONObject().put("purchase", JSONObject(originalJson)).put("signature", signature)

  private fun JSONObject.toPurchase(): Purchase =
      Purchase(this.getJSONObject("purchase").toString(), this.getString("signature"))

  private fun SkuDetails.toJSONObject(): JSONObject = JSONObject(originalJson)

  private fun JSONObject.toSkuDetails(): SkuDetails = SkuDetails(toString())

  private fun String.toPurchaseList(): List<Purchase> {
    val list = mutableListOf<Purchase>()
    val array = JSONArray(this)
    (0 until array.length()).mapTo(list) { array.getJSONObject(it).toPurchase() }
    return list
  }
}
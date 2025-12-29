package com.pixite.android.billingx

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.InternalPurchasesResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams

abstract class BillingStore {

  companion object {
    private val lock = Any()
    internal var INSTANCE: BillingStore? = null

    fun defaultStore(context: Context): BillingStore {
      if (INSTANCE == null) {
        synchronized(lock) {
          if (INSTANCE == null) {
            INSTANCE = BillingStoreImpl(context
                .getSharedPreferences("dbx", AppCompatActivity.MODE_PRIVATE))
          }
        }
      }
      return INSTANCE!!
    }
  }

  abstract fun addProduct(productDetails: ProductDetails): BillingStore
  abstract fun addProduct(skuDetails: SkuDetails): BillingStore
  abstract fun getSkuDetails(params: QueryProductDetailsParams?): List<ProductDetails>
  abstract fun getSkuDetails(params: SkuDetailsParams?): List<SkuDetails>
  abstract fun getPurchases(@ProductType productType: String?): InternalPurchasesResult
  abstract fun addPurchase(purchase: Purchase): BillingStore
  abstract fun removePurchase(sku: String): BillingStore
  abstract fun clearPurchases(): BillingStore
}
package com.pixite.android.billingx

import wallapp.billing.BillingDebugReceiver
import wallapp.billing.BillingDebugReceiver.Companion.BILLING_DEBUG_RESPONSE_BUNDLE
import wallapp.billing.BillingDebugReceiver.Companion.BILLING_DEBUG_RESPONSE_CODE
import wallapp.billing.asResultBundle
import wallapp.billing.bestFormattedPrice
import wallapp.billing.play.debug.R
import wallapp.billing.toPurchase
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.QueryProductDetailsParams
import java.util.Date


class DebugBillingActivity : AppCompatActivity() {

  companion object {
//    internal const val REQUEST_SKU_TYPE = "request_sku_type"
    internal const val REQUEST_SKU = "request_sku"

    internal const val OLDER_PURCHASE_TIME_OFFSET = DateUtils.YEAR_IN_MILLIS * 4;
  }

  private lateinit var title: TextView
  private lateinit var description: TextView
  private lateinit var price: TextView
  private lateinit var buyButton: Button
//  private lateinit var buyOldButton: Button
  private lateinit var buyPendingButton: Button

  private lateinit var prefs: SharedPreferences
  @ProductType private lateinit var productType: String
  private lateinit var productDetails: ProductDetails

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_debug_billing)

    prefs = getSharedPreferences("dbx", MODE_PRIVATE)

    title = findViewById(R.id.title)
    description = findViewById(R.id.description)
    price = findViewById(R.id.price)
    buyButton = findViewById(R.id.buy)
//    buyOldButton = findViewById(R.id.buy_old)
    buyPendingButton = findViewById(R.id.buy_pending)

    val sku = intent.getStringExtra(REQUEST_SKU)!!
//    productType = intent.getStringExtra(REQUEST_SKU_TYPE)!!

    val products = listOf(
      QueryProductDetailsParams.Product.newBuilder()
        .setProductId(sku)
        .setProductType(productType)
        .build()
    )

    val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
      .setProductList(products)
      .build()

    val items = BillingStore.defaultStore(this)
      .getSkuDetails(queryProductDetailsParams)
      .associateBy { it.productId }

    val it = items[sku]
    if (it == null) {
      Log.e("DBX", "Unknown $productType sku: $sku")
      finish()
      return
    }

    productDetails = it
    title.text = productDetails.title
    description.text = productDetails.description
    price.text = productDetails.bestFormattedPrice

    buyButton.setOnClickListener {
      broadcastResult(BillingClient.BillingResponseCode.OK,
        productDetails.toPurchase(
          this.packageName,
          productType,
          PurchaseState.PURCHASED,
          purchaseTime = Date().time,
        ).asResultBundle(),
      )
      finish()
    }

//    buyOldButton.setOnClickListener {
//      broadcastResult(BillingClient.BillingResponseCode.OK,
//        productDetails.toPurchase(
//          this.packageName,
//          productType,
//          PurchaseState.PURCHASED,
//          purchaseTime = Date().time - OLDER_PURCHASE_TIME_OFFSET,
//        ).asResultBundle(),
//      )
//      finish()
//    }

    buyPendingButton.setOnClickListener {
      broadcastResult(BillingClient.BillingResponseCode.OK,
        productDetails.toPurchase(
          this.packageName,
          productType,
          PurchaseState.PENDING,
          Date().time,
        ).asResultBundle(),
      )
      finish()
    }
    window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
    window.addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
  }

  override fun onBackPressed() {
    broadcastUserCanceled()
    super.onBackPressed()
  }

  override fun onTouchEvent(event: MotionEvent?): Boolean {
    if (event?.action == MotionEvent.ACTION_OUTSIDE) {
      broadcastUserCanceled()
      finish()
      return true
    }
    return super.onTouchEvent(event)
  }

  private fun broadcastUserCanceled() {
    broadcastResult(BillingClient.BillingResponseCode.USER_CANCELED, Bundle())
  }

  private fun broadcastResult(responseCode: Int, resultBundle: Bundle) {
    val intent = Intent(this, BillingDebugReceiver::class.java)
    intent.putExtra(BILLING_DEBUG_RESPONSE_CODE, responseCode)
    intent.putExtra(BILLING_DEBUG_RESPONSE_BUNDLE, resultBundle)
    sendBroadcast(intent)
  }
}
package wallapp.billing

import android.os.Bundle
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.util.BillingHelper
import com.pixite.android.billingx.DebugBillingActivity
import org.json.JSONObject
import java.util.*

fun createBillingResult(@BillingClient.BillingResponseCode responseCode: Int): BillingResult {
    return BillingResult
        .newBuilder()
        .setResponseCode(responseCode)
        .build()
}

/**
 * As set via [DebugBillingActivity.SkuDetails.toPurchaseData].
 */
//@BillingClient.SkuType val Purchase.inferredSkuType: String
//    get() = when {
//        signature.endsWith(BillingClient.SkuType.INAPP) -> {
//            BillingClient.SkuType.INAPP
//        }
//        signature.endsWith(BillingClient.SkuType.SUBS) -> {
//            BillingClient.SkuType.SUBS
//        }
//        else -> {
//            throw RuntimeException("Unable to infer type from signature: $signature")
//        }
//    }

@BillingClient.ProductType val Purchase.inferredProductType: String
    get() = when {
        signature.endsWith(BillingClient.ProductType.INAPP) -> {
            BillingClient.ProductType.INAPP
        }
        signature.endsWith(BillingClient.ProductType.SUBS) -> {
            BillingClient.ProductType.SUBS
        }
        else -> {
            throw RuntimeException("Unable to infer type from signature: $signature")
        }
    }


fun Purchase.asResultBundle(): Bundle {
    return Bundle().apply {
        putInt(BillingHelper.RESPONSE_CODE, BillingClient.BillingResponseCode.OK)
        putStringArrayList(BillingHelper.RESPONSE_INAPP_PURCHASE_DATA_LIST, arrayListOf(originalJson))
        putStringArrayList(BillingHelper.RESPONSE_INAPP_SIGNATURE_LIST, arrayListOf(signature))
    }
}

/**
 * Work as per the decompiled [com.android.billingclient.api.Purchase.getPurchaseState] function.
 * See also https://github.com/android/play-billing-samples/issues/227.
 */
private fun convertPurchaseState(@PurchaseState purchaseState: Int): Int {
    if (purchaseState == PurchaseState.PENDING) {
        // Purchase.getPurchaseState() converts from 4 back to 2 (where 2 == PENDING)
        return 4
    }
    return 1
}

fun SkuDetails.toPurchase(
    packageName: String,
    @BillingClient.SkuType skuType: String,
    @PurchaseState purchaseState: Int,
    purchaseTime: Long,
    acknowledged: Boolean = false,
): Purchase {
    return toPurchase(
        sku,
        "debug-signature-$sku-$skuType",
        packageName,
        purchaseState,
        purchaseTime,
        acknowledged,
    )
}

fun ProductDetails.toPurchase(
    packageName: String,
    @BillingClient.SkuType skuType: String,
    @PurchaseState purchaseState: Int,
    purchaseTime: Long,
    acknowledged: Boolean = false,
): Purchase {
    return toPurchase(
        productId,
        "debug-signature-$productId-$skuType",
        packageName,
        purchaseState,
        purchaseTime,
        acknowledged,
    )
}

fun toPurchase(
    sku: String,
    signature: String,
    packageName: String,
    @PurchaseState purchaseState: Int,
    purchaseTime: Long,// = Date().time,
    acknowledged: Boolean = false,
    orderId: String? = "$sku-${purchaseTime}..0",
    purchaseToken: String = "0987654321-${purchaseTime}-$sku",
): Purchase {
    val json = """{"orderId":"$orderId","packageName":"$packageName","productId":"$sku","autoRenewing":true,"purchaseTime":"$purchaseTime","purchaseToken":"$purchaseToken","acknowledged":$acknowledged,"purchaseState":${convertPurchaseState(purchaseState)}}""".trimMargin()
    return Purchase(json, signature)
}

/**
 * There's no official API for creating a [ProductDetails] instance, so create via reflection.
 */
fun createProductDetails(json: JSONObject): ProductDetails {
    // There's no good
    val productDetailsClass = Class.forName("com.android.billingclient.api.ProductDetails")
    return productDetailsClass.getDeclaredConstructor(String::class.java).apply {
        isAccessible = true
    }.newInstance(json.toString()) as ProductDetails
}

package wallapp.billing

import wallapp.billing.account.AccountIdentifiers
import wallapp.billing.purchase.BILLING_PURCHASE_STATE_PENDING
import wallapp.billing.purchase.BILLING_PURCHASE_STATE_PURCHASED
import wallapp.billing.purchase.BILLING_PURCHASE_STATE_UNSPECIFIED_STATE
import wallapp.billing.sku.BillingProductId
import wallapp.billing.sku.BillingProductType
import wallapp.billing.sku.BillingSku
import wallapp.billing.sku.BillingSkuSpec
import android.annotation.SuppressLint
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.AccountIdentifiers as PlayAccountIdentifiers
import com.android.billingclient.api.BillingResult as PlayBillingResult

/**
 * Is this [BillingResult] something we want to alert the user about?
 */
fun isUserFacingError(playBillingResult: com.android.billingclient.api.BillingResult): Boolean {
    return when (playBillingResult.responseCode) {
        BillingResponseCode.FEATURE_NOT_SUPPORTED -> true
        BillingResponseCode.SERVICE_DISCONNECTED -> true
        BillingResponseCode.OK -> false
        BillingResponseCode.USER_CANCELED -> false
        BillingResponseCode.SERVICE_UNAVAILABLE -> true
        BillingResponseCode.BILLING_UNAVAILABLE -> true
        BillingResponseCode.ITEM_UNAVAILABLE -> true
        BillingResponseCode.DEVELOPER_ERROR -> true
        BillingResponseCode.ERROR -> true
        BillingResponseCode.ITEM_ALREADY_OWNED -> true
        BillingResponseCode.ITEM_NOT_OWNED -> true
        else -> true
    }
}

// IAB Helper error codes
private const val IABHELPER_ERROR_BASE = -1000

/**
 * Returns a human-readable description for the given [responseCode].
 *
 * It also includes the result code numerically.
 */
@SuppressLint("SwitchIntDef")
fun getResponseDescription(billingResult: com.android.billingclient.api.BillingResult): String {
    return if (billingResult.responseCode <= IABHELPER_ERROR_BASE) {
        when (billingResult.responseCode) {
            -1001 -> "-1001:Remote exception during initialization"
            -1002 -> "-1002:Bad response received"
            -1003 -> "-1003:Purchase signature verification failed"
            -1004 -> "-1004:Send intent failed"
            -1005 -> "-1005:User cancelled"
            -1006 -> "-1006:Unknown purchase response"
            -1007 -> "-1007:Missing token"
            -1008 -> "-1008:Unknown error"
            -1009 -> "-1009:Subscriptions not available"
            -1010 -> "-1010:Invalid consumption attempt"
            else -> "${billingResult.responseCode}:Unknown Helper Error"
        }
    } else {
        when (billingResult.responseCode) {
            BillingResponseCode.FEATURE_NOT_SUPPORTED ->
                "${BillingResponseCode.FEATURE_NOT_SUPPORTED}:Feature not supported"
            BillingResponseCode.SERVICE_DISCONNECTED ->
                "${BillingResponseCode.SERVICE_DISCONNECTED}:Service disconnected"
            BillingResponseCode.OK -> "${BillingResponseCode.OK}:OK"
            BillingResponseCode.USER_CANCELED ->
                "${BillingResponseCode.USER_CANCELED}:User Canceled"
            BillingResponseCode.SERVICE_UNAVAILABLE ->
                "${BillingResponseCode.SERVICE_UNAVAILABLE}:Service unavailable"
            BillingResponseCode.BILLING_UNAVAILABLE ->
                "${BillingResponseCode.BILLING_UNAVAILABLE}:Billing Unavailable"
            BillingResponseCode.ITEM_UNAVAILABLE ->
                "${BillingResponseCode.ITEM_UNAVAILABLE}:Item unavailable"
            BillingResponseCode.DEVELOPER_ERROR ->
                "${BillingResponseCode.DEVELOPER_ERROR}:Developer Error"
            BillingResponseCode.ERROR ->
                "${BillingResponseCode.ERROR}:Fatal error during the API action"
            BillingResponseCode.ITEM_ALREADY_OWNED ->
                "${BillingResponseCode.ITEM_ALREADY_OWNED}:Item Already Owned"
            BillingResponseCode.ITEM_NOT_OWNED ->
                "${BillingResponseCode.ITEM_NOT_OWNED}:Item not owned"
            else -> "${billingResult.responseCode}:Unknown"
        }
    }
}

fun PlayBillingResult.asBillingResult(): BillingResult =
    BillingResult(
        success = responseCode == BillingResponseCode.OK,
        responseCode = responseCode,
        isUserFacingError = isUserFacingError(this),
        getResponseDescription(this),
    )

fun asAccountIdentifiers(playAccountIdentifiers: PlayAccountIdentifiers) =
        AccountIdentifiers(
            playAccountIdentifiers.obfuscatedAccountId,
            playAccountIdentifiers.obfuscatedProfileId,
        )

fun asPurchaseState(@PurchaseState playPurchaseState: Int): Int = when(playPurchaseState) {
    PurchaseState.PURCHASED -> BILLING_PURCHASE_STATE_PURCHASED
    PurchaseState.PENDING -> BILLING_PURCHASE_STATE_PENDING
    PurchaseState.UNSPECIFIED_STATE -> BILLING_PURCHASE_STATE_UNSPECIFIED_STATE
    else -> throw IllegalArgumentException("Unhandled state: $playPurchaseState")
}

fun getPlayPurchaseStateDescription(@PurchaseState playPurchaseState: Int): String {
    return when(playPurchaseState) {
        PurchaseState.UNSPECIFIED_STATE -> "Unspecified"
        PurchaseState.PENDING -> "Pending"
        PurchaseState.PURCHASED -> "Purchased"
        else -> "Unknown ($playPurchaseState)"
    }
}

@ProductType val BillingProductType.productType: String
    get() = when(this) {
        BillingProductType.InApp -> ProductType.INAPP
        BillingProductType.Subscription -> ProductType.SUBS
    }

fun asBillingProductType(type: String): BillingProductType {
    return when (type) {
        ProductType.INAPP -> BillingProductType.InApp
        ProductType.SUBS -> BillingProductType.Subscription
        else -> throw IllegalArgumentException("Unhandled type: $type")
    }
}

fun ProductDetails.asBillingProduct(): BillingSku {
    return when (asBillingProductType(productType)) {
        BillingProductType.InApp -> {
            val oneTimePurchaseOfferDetails = oneTimePurchaseOfferDetails!!
            BillingSku(
                BillingProductType.InApp,
                BillingProductId.from(productId),
                title,
                description,
                priceLocalized = oneTimePurchaseOfferDetails.formattedPrice,
                priceNumerical = oneTimePurchaseOfferDetails.priceAmountMicros / 1000000f,
                priceCurrencyCode = oneTimePurchaseOfferDetails.priceCurrencyCode,
                nativeSku = this,
            )
        }
        BillingProductType.Subscription -> {
            TODO("Add support for Subscription type")
        }
    }
}

val ProductDetails.bestFormattedPrice: String
    get() = when (asBillingProductType(productType)) {
        BillingProductType.InApp -> {
            oneTimePurchaseOfferDetails!!.formattedPrice
        }
        BillingProductType.Subscription -> {
            TODO("Add support for Subscription type")
        }
    }

val BillingFlowParams.billingProductDescriptors: List<BillingSkuSpec>?
    get() {
        /**
         * NOTE: As of version 7.0.0 of the Billing library, there's no public API to fetch product
         * details, but this data is available via obfuscated functions. This likely requires
         * updating with each change of the Play Billing library. This is fine considering this code
         * doesn't run in release.
         */
        val items = zzg()
        return items.map { item ->
            if (item is BillingFlowParams.ProductDetailsParams) {
                val productDetails = item.zza()
                BillingSkuSpec(
//                    productType = asBillingProductType(productDetails.productType),
                    productId = BillingProductId.from(productDetails.productId),
                )
            } else {
                TODO("Add support for $item")
            }
        }.ifEmpty { null }
    }


val QueryProductDetailsParams.billingProductDescriptors: List<BillingSkuSpec>?
    get() {
        val items = zza()
        return items.map { item ->
            if (item is QueryProductDetailsParams.Product) {
                BillingSkuSpec(
//                    productType = asBillingProductType(item.zzb()),
                    productId = BillingProductId.from(item.zza()),
                )
            } else {
                TODO("Add support for $item")
            }
        }.ifEmpty { null }
    }

val QueryPurchasesParams.productType: String?
    get() {
        val productType = zza()
        if (productType.isEmpty()) {
            return null
        }
        return productType
    }


fun getBillingResponseCodeLabel(@BillingResponseCode responseCode: Int): String =
    when (responseCode) {
        BillingResponseCode.SERVICE_TIMEOUT -> "SERVICE_TIMEOUT"
        BillingResponseCode.FEATURE_NOT_SUPPORTED -> "FEATURE_NOT_SUPPORTED"
        BillingResponseCode.SERVICE_DISCONNECTED -> "SERVICE_DISCONNECTED"
        BillingResponseCode.OK -> "OK"
        BillingResponseCode.USER_CANCELED -> "USER_CANCELED"
        BillingResponseCode.SERVICE_UNAVAILABLE -> "SERVICE_UNAVAILABLE"
        BillingResponseCode.BILLING_UNAVAILABLE -> "BILLING_UNAVAILABLE"
        BillingResponseCode.ITEM_UNAVAILABLE -> "ITEM_UNAVAILABLE"
        BillingResponseCode.DEVELOPER_ERROR -> "DEVELOPER_ERROR"
        BillingResponseCode.ERROR -> "ERROR"
        BillingResponseCode.ITEM_ALREADY_OWNED -> "ITEM_ALREADY_OWNED"
        BillingResponseCode.ITEM_NOT_OWNED -> "ITEM_NOT_OWNED"
        else -> "[UnknownResponse_$responseCode]"
    }

val PlayBillingResult.debugLabel: String
    get() = "$responseCode / ${getBillingResponseCodeLabel(responseCode)} / ${getResponseDescription(this)} / $debugMessage"

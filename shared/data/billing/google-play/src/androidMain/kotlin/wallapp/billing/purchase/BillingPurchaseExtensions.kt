package wallapp.billing.purchase

import wallapp.billing.account.isNotEmpty
import com.android.billingclient.api.Purchase
import org.json.JSONArray
import org.json.JSONObject


fun BillingPurchasePlay.asPurchase(): Purchase {
    val json = JSONObject().apply {
        if (accountIdentifiers.isNotEmpty) {
            put("obfuscatedAccountId", accountIdentifiers?.obfuscatedAccountId)
            put("obfuscatedProfileId", accountIdentifiers?.obfuscatedProfileId)
        }
        put("developerPayload", developerPayload)
        put("orderId", orderId)
        put("packageName", packageName)
        put("purchaseState", purchaseState)
        put("purchaseTime", purchaseTime)
        put("purchaseToken", purchaseToken)
        put("quantity", quantity)
        put("productIds",
            JSONArray().apply {
                skuSpecs.forEachIndexed { index, skuDescriptor ->
                    put(index, skuDescriptor.productId)
                }
            }
        )
        put("acknowledged", isAcknowledged)
        put("autoRenewing", isAutoRenewing)
    }.toString(0)
    return Purchase(json, signature)
}

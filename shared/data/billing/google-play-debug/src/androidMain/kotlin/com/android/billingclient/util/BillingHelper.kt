package com.android.billingclient.util

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.Purchase
import org.json.JSONException

/** Helper methods for billing client.  */
object BillingHelper {
    // Keys for the responses from InAppBillingService
    const val RESPONSE_CODE = "RESPONSE_CODE"
    const val RESPONSE_GET_SKU_DETAILS_LIST = "DETAILS_LIST"
    const val RESPONSE_BUY_INTENT = "BUY_INTENT"

    // StringArrayList containing the list of SKUs
    const val RESPONSE_INAPP_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST"

    // StringArrayList containing the purchase information
    const val RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST"

    // StringArrayList containing the signatures of the purchase information
    const val RESPONSE_INAPP_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST"
    const val INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN"
    private const val TAG = "BillingHelper"

    // Keys for Purchase data parsing
    private const val RESPONSE_INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA"
    private const val RESPONSE_INAPP_SIGNATURE = "INAPP_DATA_SIGNATURE"

    /** Total number of cores of current device  */
    var NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()

    /**
     * Logs a verbose message
     *
     * @param tag Tag to be used inside logging
     * @param msg Message to log
     */
    fun logVerbose(tag: String?, msg: String?) {
        if (Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, msg!!)
        }
    }

    /**
     * Logs a warning message
     *
     * @param tag Tag to be used inside logging
     * @param msg Message to log
     */
    fun logWarn(tag: String?, msg: String?) {
        if (Log.isLoggable(tag, Log.WARN)) {
            Log.w(tag, msg!!)
        }
    }

    /** Retrieves a response code from the intent  */
    @BillingResponseCode
    fun getResponseCodeFromIntent(intent: Intent?, tag: String?): Int {
        return if (intent == null) {
            logWarn(TAG, "Got null intent!")
            BillingResponseCode.ERROR
        } else {
            getResponseCodeFromBundle(intent.getExtras(), tag)
        }
    }

    /** Retrieves a response code from the bundle  */
    @BillingResponseCode
    fun getResponseCodeFromBundle(bundle: Bundle?, tag: String?): Int {
        // Returning the error for null bundle
        if (bundle == null) {
            logWarn(tag, "Unexpected null bundle received!")
            return BillingResponseCode.ERROR
        }
        // Getting the responseCode to report
        val responseCode: Any? = bundle.get(RESPONSE_CODE)
        return if (responseCode == null) {
            logVerbose(
                tag,
                "getResponseCodeFromBundle() got null response code, assuming OK"
            )
            BillingResponseCode.OK
        } else if (responseCode is Int) {
            // noinspection WrongConstant
            responseCode
        } else {
            logWarn(
                tag, "Unexpected type for bundle response code: " + responseCode::javaClass.name
            )
            BillingResponseCode.ERROR
        }
    }

    /**
     * Gets a purchase data and signature (or lists of them) from the Bundle and returns the
     * constructed list of [Purchase]
     *
     * @param bundle The bundle to parse
     * @return New Purchase instance with the data extracted from the provided intent
     */
    fun extractPurchases(bundle: Bundle?): List<Purchase>? {
        if (bundle == null) {
            return null
        }
        val purchaseDataList: List<String>? = bundle.getStringArrayList(
            RESPONSE_INAPP_PURCHASE_DATA_LIST
        )
        val dataSignatureList: List<String>? = bundle.getStringArrayList(
            RESPONSE_INAPP_SIGNATURE_LIST
        )
        val resultList: MutableList<Purchase> = ArrayList<Purchase>()

        // If there were no lists of data, try to find single purchase data inside the Bundle
        if (purchaseDataList == null || dataSignatureList == null) {
            logWarn(TAG, "Couldn't find purchase lists, trying to find single data.")
            val purchaseData: String? = bundle.getString(RESPONSE_INAPP_PURCHASE_DATA)
            val dataSignature: String? = bundle.getString(RESPONSE_INAPP_SIGNATURE)
            val tmpPurchase: Purchase? = extractPurchase(purchaseData, dataSignature)
            if (tmpPurchase == null) {
                logWarn(TAG, "Couldn't find single purchase data as well.")
                return null
            } else {
                resultList.add(tmpPurchase)
            }
        } else {
            var i = 0
            while (i < purchaseDataList.size && i < dataSignatureList.size) {
                val tmpPurchase: Purchase? =
                    extractPurchase(purchaseDataList[i], dataSignatureList[i])
                if (tmpPurchase != null) {
                    resultList.add(tmpPurchase)
                }
                ++i
            }
        }
        return resultList
    }

    private fun extractPurchase(purchaseData: String?, signatureData: String?): Purchase? {
        if (purchaseData == null || signatureData == null) {
            logWarn(TAG, "Received a bad purchase data.")
            return null
        }
        var purchase: Purchase? = null
        try {
            purchase = Purchase(purchaseData, signatureData)
        } catch (e: JSONException) {
            logWarn(TAG, "Got JSONException while parsing purchase data: $e")
        }
        return purchase
    }
}

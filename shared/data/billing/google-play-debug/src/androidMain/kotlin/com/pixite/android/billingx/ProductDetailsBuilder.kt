package com.pixite.android.billingx

import wallapp.billing.createProductDetails
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.SkuDetails
import org.json.JSONObject

data class ProductDetailsBuilder(
    val productId: String,
    @ProductType val productType: String,
    val price: String,
    val priceAmountMicros: Long,
    val priceCurrencyCode: String,
    val title: String,
    val description: String,
    val subscriptionPeriod: String? = null,
    val freeTrialPeriod: String? = null,
    val introductoryPrice: String? = null,
    val introductoryPriceAmountMicros: String? = null,
    val introductoryPricePeriod: String? = null,
    val introductoryPriceCycles: String? = null,
) {

  fun build(): SkuDetails {
    val json = JSONObject()
        .put("productId", productId)
        .put("type", productType)
        .put("price", price)
        .put("price_amount_micros", priceAmountMicros)
        .put("price_currency_code", priceCurrencyCode)
        .put("title", title)
        .put("description", description)

    subscriptionPeriod?.let { json.put("subscriptionPeriod", subscriptionPeriod) }
    freeTrialPeriod?.let { json.put("freeTrialPeriod", freeTrialPeriod) }
    introductoryPrice?.let { json.put("introductoryPrice", introductoryPrice) }
    introductoryPriceAmountMicros?.let { json.put("introductoryPriceAmountMicros", introductoryPriceAmountMicros) }
    introductoryPricePeriod?.let { json.put("introductoryPricePeriod", introductoryPricePeriod) }
    introductoryPriceCycles?.let { json.put("introductoryPriceCycles", introductoryPriceCycles) }

    return SkuDetails(json.toString())
  }

    fun buildProductDetails(): ProductDetails {
        val json = JSONObject()
            .put("productId", productId)
            .put("type", productType)
            .put("price", price)
            .put("price_amount_micros", priceAmountMicros)
            .put("price_currency_code", priceCurrencyCode)
            .put("title", title)
            .put("description", description)

        if (productType == ProductType.INAPP) {
            json.put(
                "oneTimePurchaseOfferDetails",
                JSONObject()
                    .put("formattedPrice", price)
                    .put("priceAmountMicros", priceAmountMicros)
                    .put("priceCurrencyCode", priceCurrencyCode)
            )
        } else if (productId == ProductType.SUBS) {
            subscriptionPeriod?.let { json.put("subscriptionPeriod", subscriptionPeriod) }
            freeTrialPeriod?.let { json.put("freeTrialPeriod", freeTrialPeriod) }
            introductoryPrice?.let { json.put("introductoryPrice", introductoryPrice) }
            introductoryPriceAmountMicros?.let {
                json.put(
                    "introductoryPriceAmountMicros",
                    introductoryPriceAmountMicros
                )
            }
            introductoryPricePeriod?.let {
                json.put(
                    "introductoryPricePeriod",
                    introductoryPricePeriod
                )
            }
            introductoryPriceCycles?.let {
                json.put(
                    "introductoryPriceCycles",
                    introductoryPriceCycles
                )
            }
        }
        return createProductDetails(json)
    }
}
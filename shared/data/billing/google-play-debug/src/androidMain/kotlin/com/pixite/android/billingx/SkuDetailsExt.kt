package com.pixite.android.billingx

import com.android.billingclient.api.SkuDetails
import org.json.JSONObject

val SkuDetails.asOriginalJson: String
  get() = JSONObject().apply {
    put("productId", sku)
    put("type", type)
    put("price", price)
    put("price_amount_micros", priceAmountMicros)
    put("price_currency_code", priceCurrencyCode)
    put("title", title)
    put("description", description)

    put("subscriptionPeriod", subscriptionPeriod)
    put("freeTrialPeriod", freeTrialPeriod)
    put("introductoryPrice", introductoryPrice)
    put("introductoryPriceAmountMicros", introductoryPriceAmountMicros)
    put("introductoryPricePeriod", introductoryPricePeriod)
    put("introductoryPriceCycles", introductoryPriceCycles)
  }.toString()
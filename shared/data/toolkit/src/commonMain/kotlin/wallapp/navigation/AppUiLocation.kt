package wallapp.navigation

enum class AppUiLocation(val code: String) {
    FEATURE_METER_SCREEN("feature_meter_screen"),
    FEATURE_METER_OVERVIEW("feature_meter_overview"),
    FEATURE_METER_SETTINGS_ITEM("feature_meter_settings_item"),
    FEATURE_METER_FEED_ITEM("feature_meter_feed_item"),
    FEATURE_METER_SYSTEM_TIME_SETTINGS("feature_meter_system_time_settings"),
    UNLOCK_TIER_FEED_ITEM("unlock_tier_feed_item"),
    WALLPAPER_PREVIEW("wallpaper_preview"),
    FEED_ADMOB_AD("feed_admob_ad"),
    FEED_PROMO_AD("feed_promo_ad"),
    SHOP("shop"),
    INDIVIDUAL_SETTINGS_ITEM("individual_settings_item"),
    BUY_LICENSE_UPSELL("buy_license_upsell"),
    BUY_LICENSE_UPSELL_AUTO_REDIRECT("buy_license_upsell_auto_redirect"),
    PENDING_PURCHASE_FAILED("pending_purchase_failed"),
    ADD_CREDITS("add_credits"),
    UPGRADE_UPSELL("upgrade_upsell"),
    NOTIFICATION("notification"),
    SNACKBAR("snackbar"),
    UNKNOWN("unknown"),
}

fun asAppUiLocation(code: String?): AppUiLocation? {
    return AppUiLocation.entries.find { it.code == code }
}
package wallapp.prefs

import wallapp.content.model.WallpaperScreenTheme
import wallapp.preference.PreferenceInfo
import wallapp.system.version.SystemVersion


open class PreferenceDefaults(
    val preferenceDefaultsProvider: PreferenceDefaultsProvider,
    private val systemVersion: SystemVersion,
) {
    val String.platformVersionSuffix: String
        get() = "_${systemVersion.versionId}"

    val theme = PreferenceInfo("pref_theme", preferenceDefaultsProvider.themeType)
    val appIcon = PreferenceInfo("pref_app_icon2", preferenceDefaultsProvider.appIcon)
    val adsEnabled = PreferenceInfo("pref_ads_enabled", true)
    val staggeredFeed = PreferenceInfo("pref_staggered_feed", true)
    val allowFullWidthFeedItems = PreferenceInfo("pref_allow_full_width_feed_items", false)
    val pagedIndexScreen = PreferenceInfo("pref_paged_index_screen", false)
    val spotlightBottomSheet = PreferenceInfo("pref_spotlightBottomSheet", false)
    val spotlightShowsCollection = PreferenceInfo("pref_spotlight_shows_collection", false)
    val spotlightShowsOtherCollections = PreferenceInfo("pref_spotlight_shows_other_collections", false)
    val spotlightTheme = PreferenceInfo("pref_spotlight_theme", WallpaperScreenTheme.AppAccent.name)
    val enableIosNativeNavigation = PreferenceInfo("pref_enable_ios_native_navigation", true)
    val enableLogging = PreferenceInfo("pref_enable_logging", preferenceDefaultsProvider.enableLogging)

    val currentRemixId = PreferenceInfo("pref_current_wallpaper_item_id", "0")
    val currentDesignId = PreferenceInfo("pref_current_design_id", "0")
    val nextRemixId = PreferenceInfo("pref_next_wallpaper_item_id", "2")
    val currentPreviewRemixId = PreferenceInfo("pref_current_preview_wallpaper_item_id", "")

    val appInstallTime = PreferenceInfo("pref_app_install_time", preferenceDefaultsProvider.appInstallTime)
    val appInstallVersionName = PreferenceInfo("pref_app_install_version_name", preferenceDefaultsProvider.appInstallVersionName)
    val lastAppRunVersionName = PreferenceInfo("pref_last_app_run_version_name", "")
    val firebaseCloudMessagingToken = PreferenceInfo("pref_fcm_token", "")
    val favorites = PreferenceInfo("pref_favorites", "")
    val followings = PreferenceInfo("pref_followings", "")
    val purchases = PreferenceInfo("pref_purchases", "")
    val deviceInfo = PreferenceInfo("pref_device_info", "")
    val nextWallpaperUseFavorites = PreferenceInfo("pref_next_wallpaper_use_favorites", false)
    val wallpaperDownloadEvents = PreferenceInfo("pref_wallpaper_download_events", "")
    val exposeUpgradeScrollScreen = PreferenceInfo("pref_expose_upgrade_scroll_screen", true)
    val allWallpapersUnlocked = PreferenceInfo("hdfsgh", false)
    val isCurrentSystemWallpaperApp = PreferenceInfo("pref_is_current_system_wallpaper_app", false)
    val isCurrentSystemWallpaperAppRefreshTime = PreferenceInfo("pref_isCurrentSystemWallpaperAppRefreshTime", -1L)
    val lastHomeScreenWallpaperData = PreferenceInfo("pref_last_home_screen_wallpaper_data", "")
    val lastLockScreenWallpaperData = PreferenceInfo("pref_last_lock_screen_wallpaper_data", "")
    val lastNetworkRefreshTime = PreferenceInfo("pref_last_network_refresh_time", LAST_NETWORK_REFRESH_TIME_DEFAULT)
    val lastNetworkApiPath = PreferenceInfo("pref_last_network_api_path", "")
    val lastKnownDarkModeState = PreferenceInfo("pref_last_known_dark_mode_state", false)
    val bucketOverride = PreferenceInfo("pref_bucket_override", "")
    val appShowing = PreferenceInfo("pref_app_showing", false)
    val deepLinkRemixes = PreferenceInfo("pref_deep_link_remixes", "")
    open val defaultRemixId = PreferenceInfo("pref_default_remix_id", "<unset>")
    open val defaultDesignId = PreferenceInfo("pref_default_design_id", "<unset>")
    val acceptedTerms = PreferenceInfo("pref_accepted_terms", preferenceDefaultsProvider.acceptedTerms)
    val joinNewsletter = PreferenceInfo("pref_joinNewsletter", preferenceDefaultsProvider.joinNewsletter)
    val reportUsageStats = PreferenceInfo("pref_reportUsageStats", preferenceDefaultsProvider.reportUsageStats)
    val receiveNotifications = PreferenceInfo("pref_receiveNotifications", preferenceDefaultsProvider.receiveNotifications)
    val wallpaperHistory = PreferenceInfo("pref_wallpaper_history", "")
    val dataRefreshFromNetworkEvent = PreferenceInfo("pref_data_refresh_from_network_event", false)
    val rewardUnlockedWallpapers = PreferenceInfo("hgdgdfh", "")
    val showPerformanceStats = PreferenceInfo("pref_showPerformanceStats", false)
    open val cachedLicenseState = PreferenceInfo("7dfads", "-1")
    val debugLicenseState = PreferenceInfo("vdsvdsfgr", preferenceDefaultsProvider.debugLicenseState)
    val useDebugBillingManager = PreferenceInfo("fgadvadsff", preferenceDefaultsProvider.useDebugBillingManager)
    open val doubleLicenseCheckFinished = PreferenceInfo("2bsla", false)
    val firstRunOnboardingDismissed = PreferenceInfo("pref_firstRunOnboardingDismissed", preferenceDefaultsProvider.firstRunOnboardingDismissed)
    val homeOnboardingDismissed = PreferenceInfo("pref_homeOnboardingDismissed", preferenceDefaultsProvider.homeOnboardingDismissed)
    val simulateNetworkFailure = PreferenceInfo("pref_simulate_network_failure", false)
    val reviewFeedItemDismissedTime = PreferenceInfo("pref_review_feed_item_dismissed_time", REVIEW_FEED_ITEM_DISMISSED_TIME_DEFAULT)
    val reviewFeedItemDismissedCount = PreferenceInfo("pref_review_feed_item_dismissed_count", preferenceDefaultsProvider.reviewFeedItemDismissedCount)
    val licenseUserId = PreferenceInfo("7fadsf", "")
    val inAppReviewShownOnce = PreferenceInfo("pref_in_app_review_shown_once", false)
    val inAppReviewShownTime = PreferenceInfo("pref_in_app_review_shown_time", IN_APP_REVIEW_DEFAULT_TIME)
    val lastAppCloseTime = PreferenceInfo("pref_last_app_close_time", -1L)
    val lastWallpaperUsageTimeFuzzy = PreferenceInfo("pref_last_wallpaper_usage_time_fuzzy", -1L)
    val lastNotifyUserPurchasePendingOrderId = PreferenceInfo("pref_last_notify_purchase_pending_order_id", "")
    val pendingPurchaseOrderId = PreferenceInfo("pref_pending_purchase_order_id", "")
    val randomSeedIndex = PreferenceInfo("pref_random_seed_index", 0L)
    val systemPhotoStatusCache = PreferenceInfo("pref_system_photo_status_cache", "")
    val debugBillingData = PreferenceInfo("fdafasfaddfas", "")
    val useDebugRewardAdCount = PreferenceInfo("fadsfadsfadsfsd", false)
    val enableNativeUiRendering = PreferenceInfo("pref_enable_native_ui_rendering", true)
    val purchasesRestoredForId = PreferenceInfo("pref_purchases_restored_for_id", "")
    val imageFormatCodeInAppCompose = PreferenceInfo("pref_image_format_code_in_app_compose".platformVersionSuffix, "")
    val enableFeedPaging = PreferenceInfo("pref_enable_feed_paging", false)
    val recentlyViewedWallpaperIds = PreferenceInfo("pref_recently_viewed_wallpaper_ids", "")
    val recentlyViewedCollectionIds = PreferenceInfo("pref_recently_viewed_collection_ids", "")
    val recentlyViewedArtistIds = PreferenceInfo("pref_recently_viewed_artist_ids", "")
    val enableNativeIosCollectionScreen = PreferenceInfo("pref_enable_native_ios_collection_screen", false)
    val lastExpirationEpochTimeForWhichUserNotified = PreferenceInfo("pref_last_expired_subscription_entitlement_id", 0L)
    val lastExpiredSubscriptionNotifyCount = PreferenceInfo("pref_last_expired_subscription_notify_count", 0)
    val lastRequestReviewEpochTime = PreferenceInfo("pref_last_request_review_epoch_time", 0L)

    companion object {
        const val LAST_NETWORK_REFRESH_TIME_DEFAULT = 0L
        const val REVIEW_FEED_ITEM_DISMISSED_TIME_DEFAULT = -1L
        const val IN_APP_REVIEW_DEFAULT_TIME = -1L
    }
}

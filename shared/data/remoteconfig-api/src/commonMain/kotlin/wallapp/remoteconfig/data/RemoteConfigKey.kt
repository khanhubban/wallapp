package wallapp.remoteconfig.data

expect fun remoteConfigKeyPlatformSuffix(): String

enum class RemoteConfigKey(val key: String) {
    AccountBackendEnabled("account_backend_enabled"),
    AccountSignInAppleEnabled("account_sign_in_apple_enabled"),
    AccountSignInGoogleEnabled("account_sign_in_google_enabled"),
    AppUpdateMinimumAllowedAppVersion("app_update_minimum_allowed_app_version${remoteConfigKeyPlatformSuffix()}"),
    ContentShowSingles("content_show_singles"),
    FeedAdsEnabled("feed_ads_enabled"),
    HighlightArtist("highlight_artist"),
    HighlightCollectionOfTheWeek("highlight_collection_of_the_week"),
    HighlightJustAdded("highlight_just_added"),
    HighlightMostPopular("highlight_most_popular"),
    HighlightWallpaperOfTheWeek("highlight_wallpaper_of_the_week"),
    ImageHostName("image_host_name"),
    RewardAdsEnableConsecutivePlays("reward_ads_enable_consecutive_plays"),
    RewardAdsEnabled("reward_ads_enabled"),
    RewardAdsInternalProbability("reward_ads_internal_probability"),
    RewardAdsMaxCountToUnlockSingle("reward_ads_max_count_to_unlock_single"),
    RewardAdsUnlockWallpaperOnFailure("reward_ads_unlock_wallpaper_on_failure"),
    TranslationsEnabled("translations_enabled"),
    UpgradeEnableAnnualSubscription("upgrade_enable_annual_subscription"),
    UpgradeEnableAnySubscriptions("upgrade_enable_any_subscriptions"),
    UseInternalAdsForGDPR("use_internal_ads_for_gdpr"),
    /**
     * This is a special case item used as a back-door so that App Review can reliably enter the
     * app to test its content (hence the super-generic name). #2234.
     */
    Uuid("zln1yD5FJKLCzfkb"),

    TestingParam("testingParam"),
}
package wallapp.remoteconfig.data


open class RemoteConfigDataDefaultsProvider {

    open val accountAppleSignInEnabled: Boolean
        get() = true
    open val accountBackendEnabled: Boolean
        get() = true
    open val accountGoogleSignInEnabled: Boolean
        get() = true
    open val appUpdateMinimumAllowedAppVersion_platformSpecific: String
        get() = ""
    // Both environments start on the same catalog: the first prod publish writes the same bytes
    // staging already serves. Pointing this at the 99999999 demo catalog made every cold start
    // fetch 219 items it then discarded — the source of the MediaMap-entry-missing warning spam.
    open val catalogVersion: String
        get() = "20260709-06"
    open val catalogVersionStaging: String
        get() = "20260709-06"
    open val contentShowSingles: Boolean
        get() = true
    open val feedAdsEnabled: Boolean
        get() = true
    // Highlight IDs are resolved against the active catalog with no existence check
    // (ShowcaseRepositoryHighlightsConfigDefault wraps them in ArtistId/CategoryId/RemixId).
    // An ID absent from the catalog yields an empty showcase row, not an error, so these
    // defaults must name content that exists in the shipped catalog.
    open val highlightArtist: String
        get() = "stillscenes"
    open val highlightCollectionOfTheWeek: String
        get() = "stillscenes~featured"
    open val highlightJustAdded: String
        get() = highlightCollectionOfTheWeek
    open val highlightMostPopular: String
        get() = highlightCollectionOfTheWeek
    open val highlightWallpaperOfTheWeek: String
        get() = "stillscenes_0e59bd05"
    open val imageHostName: String
        get() = "example"
    open val rewardAdsEnableConsecutivePlays: Boolean
        get() = true
    open val rewardAdsEnabled: Boolean
        get() = true
    open val rewardAdsInternalProbability: Double
        get() = .35
    open val rewardAdsMaxCountToUnlockSingle: Long
        get() = 1
    open val rewardAdsUnlockWallpaperOnFailure: Boolean
        get() = false
    open val translationsEnabled: Boolean
        get() = true
    open val useInternalAdsForGDPR: Boolean
        get() = true
    open val uuid: String
        get() = ""
    open val upgradeEnableAnnualSubscription: Boolean
        get() = true
    open val upgradeEnableAnySubscriptions: Boolean
        get() = true

    open val useDimUnlicensed: Boolean
        get() = true
    open val useDimUpdateRequired: Boolean
        get() = true
    open val allowGesturesUnlicensed: Boolean
        get() = false

    open val featureMeterInitialLevel: Double
        get() = -1.0
    open val featureMeterDepletionPerDay: Double
        get() = -1.0
    open val featureMeterRewardAdLevelIncrease: Double
        get() = -1.0
    open val featureMeterRewardAdLevelIncreaseSurplus: Double
        get() = -1.0
    open val featureMeterMaxSurplusLevel: Double
        get() = -1.0
    open val featureMeterResetToDefaultLevelAfterDays: Double
        get() = 1.0     // A valid value so it can be used in [FeatureMeterManagerDynamic.resetForLimitedUse]
    open val featureMeterAmberMode: Boolean
        get() = false

    open val featureMeterWallpaperTierDiamond: Double
        get() = -1.0
    open val featureMeterWallpaperTierGold
        get() = -1.0
    open val featureMeterWallpaperTierSilver
        get() = -1.0
    open val featureMeterWallpaperTierBronze
        get() = -1.0
    open val featureMeterWallpaperTierBase
        get() = -1.0
    open val featureMeterAutoSwitchWallpaperSchedule
        get() = -1.0
    open val featureMeterAutoSwitchWallpaperDarkTheme
        get() = -1.0
    open val featureMeterSingleTapSpin
        get() = -1.0
    open val featureMeterGestures
        get() = -1.0
    open val featureMeterEffectBlur
        get() = -1.0
    open val featureMeterEffectDim
        get() = -1.0
    open val featureMeterFlickFX
        get() = -1.0
    open val featureMeterAnimateWallpaper
        get() = -1.0
    open val featureMeterDisableAppOpenAds
        get() = -1.0
    open val featureMeterDisableInterstitialAds
        get() = -1.0
    open val featureMeterDisableFeedVideoAds
        get() = -1.0
    open val featureMeterDisableFeedImageAds
        get() = -1.0

    open val testingParam
        get() = "default"
}
package wallapp.remoteconfig.data

import wallapp.remoteconfig.data.RemoteConfigDataDefaultsProviderDefault as Provider


sealed class RemoteConfigEntry<T : Any>(
    val key: String,
    val default: T,
    val allowEmptyValue: Boolean = false,
) {
    data object AccountBackendEnabled : RemoteConfigEntry<Boolean>(
        RemoteConfigKey.AccountBackendEnabled.key,
        Provider.accountBackendEnabled,
    )

    data object AccountSignInAppleEnabled : RemoteConfigEntry<Boolean>(
        RemoteConfigKey.AccountSignInAppleEnabled.key,
        Provider.accountAppleSignInEnabled,
    )

    data object AccountGoogleSignInEnabled : RemoteConfigEntry<Boolean>(
        RemoteConfigKey.AccountSignInGoogleEnabled.key,
        Provider.accountGoogleSignInEnabled,
    )

    data object AppUpdateMinimumAllowedAppVersion : RemoteConfigEntry<String>(
        RemoteConfigKey.AppUpdateMinimumAllowedAppVersion.key,
        Provider.appUpdateMinimumAllowedAppVersion_platformSpecific,
    )

    data object CatalogVersion : RemoteConfigEntry<String>(
        RemoteConfigKey.CatalogVersion.key,
        Provider.catalogVersion,
    )

    data object ContentShowSingles : RemoteConfigEntry<Boolean>(
        RemoteConfigKey.ContentShowSingles.key,
        Provider.contentShowSingles,
    )

    data object FeedAdsEnabled : RemoteConfigEntry<Boolean>(
        RemoteConfigKey.FeedAdsEnabled.key,
        Provider.feedAdsEnabled,
    )

    data object HighlightArtist : RemoteConfigEntry<String>(
        RemoteConfigKey.HighlightArtist.key,
        Provider.highlightArtist,
    )

    data object HighlightCollectionOfTheWeek : RemoteConfigEntry<String>(
        RemoteConfigKey.HighlightCollectionOfTheWeek.key,
        Provider.highlightCollectionOfTheWeek,
    )

    data object HighlightJustAdded : RemoteConfigEntry<String>(
        RemoteConfigKey.HighlightJustAdded.key,
        Provider.highlightJustAdded,
    )

    data object HighlightMostPopular : RemoteConfigEntry<String>(
        RemoteConfigKey.HighlightMostPopular.key,
        Provider.highlightMostPopular,
    )

    data object HighlightWallpaperOfTheWeek : RemoteConfigEntry<String>(
        RemoteConfigKey.HighlightWallpaperOfTheWeek.key,
        Provider.highlightWallpaperOfTheWeek,
    )

    data object ImageHostName : RemoteConfigEntry<String>(
        RemoteConfigKey.ImageHostName.key,
        Provider.imageHostName,
    )

    data object RewardAdsEnableConsecutivePlays : RemoteConfigEntry<Boolean>(
        RemoteConfigKey.RewardAdsEnableConsecutivePlays.key,
        Provider.rewardAdsEnableConsecutivePlays,
    )

    data object RewardAdsEnabled : RemoteConfigEntry<Boolean>(
        RemoteConfigKey.RewardAdsEnabled.key,
        Provider.rewardAdsEnabled,
    )

    data object RewardAdsInternalProbability : RemoteConfigEntry<Double>(
        RemoteConfigKey.RewardAdsInternalProbability.key,
        Provider.rewardAdsInternalProbability,
    )

    data object RewardAdsMaxCountToUnlockSingle : RemoteConfigEntry<Long>(
        RemoteConfigKey.RewardAdsMaxCountToUnlockSingle.key,
        Provider.rewardAdsMaxCountToUnlockSingle,
    )

    data object RewardAdsUnlockWallpaperOnFailure : RemoteConfigEntry<Boolean>(
        RemoteConfigKey.RewardAdsUnlockWallpaperOnFailure.key,
        Provider.rewardAdsUnlockWallpaperOnFailure,
    )

    data object TranslationsEnabled : RemoteConfigEntry<Boolean>(
        RemoteConfigKey.TranslationsEnabled.key,
        Provider.translationsEnabled,
    )

    data object UpgradeEnableAnnualSubscription : RemoteConfigEntry<Boolean>(
        RemoteConfigKey.UpgradeEnableAnnualSubscription.key,
        Provider.upgradeEnableAnnualSubscription,
    )

    data object UpgradeEnableAnySubscriptions : RemoteConfigEntry<Boolean>(
        RemoteConfigKey.UpgradeEnableAnySubscriptions.key,
        Provider.upgradeEnableAnySubscriptions,
    )

    data object UseInternalAdsForGDPR : RemoteConfigEntry<Boolean>(
        RemoteConfigKey.UseInternalAdsForGDPR.key,
        Provider.useInternalAdsForGDPR,
    )

    data object Uuid : RemoteConfigEntry<String>(
        RemoteConfigKey.Uuid.key,
        Provider.uuid,
    )

    data object TestingParam : RemoteConfigEntry<String>(
        RemoteConfigKey.TestingParam.key,
        Provider.testingParam,
    )

    companion object {
        fun asDefaultsArray(): Array<Pair<String, Any>> {
            return RemoteConfigKey.entries
                .map { it.toRemoteConfigEntry() }
                .map { it.key to it.default }
                .toTypedArray()
        }
    }
}

fun RemoteConfigKey.toRemoteConfigEntry(): RemoteConfigEntry<*> =
    when (this) {
        RemoteConfigKey.AccountBackendEnabled -> RemoteConfigEntry.AccountBackendEnabled
        RemoteConfigKey.AccountSignInAppleEnabled -> RemoteConfigEntry.AccountSignInAppleEnabled
        RemoteConfigKey.AccountSignInGoogleEnabled -> RemoteConfigEntry.AccountGoogleSignInEnabled
        RemoteConfigKey.AppUpdateMinimumAllowedAppVersion -> RemoteConfigEntry.AppUpdateMinimumAllowedAppVersion
        RemoteConfigKey.CatalogVersion -> RemoteConfigEntry.CatalogVersion
        RemoteConfigKey.ContentShowSingles -> RemoteConfigEntry.ContentShowSingles
        RemoteConfigKey.FeedAdsEnabled -> RemoteConfigEntry.FeedAdsEnabled
        RemoteConfigKey.HighlightArtist -> RemoteConfigEntry.HighlightArtist
        RemoteConfigKey.HighlightCollectionOfTheWeek -> RemoteConfigEntry.HighlightCollectionOfTheWeek
        RemoteConfigKey.HighlightJustAdded -> RemoteConfigEntry.HighlightJustAdded
        RemoteConfigKey.HighlightMostPopular -> RemoteConfigEntry.HighlightMostPopular
        RemoteConfigKey.HighlightWallpaperOfTheWeek -> RemoteConfigEntry.HighlightWallpaperOfTheWeek
        RemoteConfigKey.ImageHostName -> RemoteConfigEntry.ImageHostName
        RemoteConfigKey.RewardAdsEnableConsecutivePlays -> RemoteConfigEntry.RewardAdsEnableConsecutivePlays
        RemoteConfigKey.RewardAdsEnabled -> RemoteConfigEntry.RewardAdsEnabled
        RemoteConfigKey.RewardAdsInternalProbability -> RemoteConfigEntry.RewardAdsInternalProbability
        RemoteConfigKey.RewardAdsMaxCountToUnlockSingle -> RemoteConfigEntry.RewardAdsMaxCountToUnlockSingle
        RemoteConfigKey.RewardAdsUnlockWallpaperOnFailure -> RemoteConfigEntry.RewardAdsUnlockWallpaperOnFailure
        RemoteConfigKey.TranslationsEnabled -> RemoteConfigEntry.TranslationsEnabled
        RemoteConfigKey.UpgradeEnableAnnualSubscription -> RemoteConfigEntry.UpgradeEnableAnnualSubscription
        RemoteConfigKey.UpgradeEnableAnySubscriptions -> RemoteConfigEntry.UpgradeEnableAnySubscriptions
        RemoteConfigKey.UseInternalAdsForGDPR -> RemoteConfigEntry.UseInternalAdsForGDPR
        RemoteConfigKey.Uuid -> RemoteConfigEntry.Uuid
        RemoteConfigKey.TestingParam -> RemoteConfigEntry.TestingParam
    }


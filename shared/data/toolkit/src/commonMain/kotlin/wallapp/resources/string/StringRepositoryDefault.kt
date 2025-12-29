package wallapp.resources.string

import kotlinx.datetime.Instant
import wallapp.log.Log
import wallapp.resources.translation.TranslationRepository
import wallapp.string.fmt
import wallapp.string.quote

class StringRepositoryDefault(
    private val translationRepository: TranslationRepository,
    private val stringArbitrator: StringArbitrator,
) : StringRepository() {

    override fun get(key: String): String {
        return translationRepository.getString(key)
            ?: key.also {
                Log.w("No translation found for key: ${key.quote()}")
            }
    }

    override val billingErrorMessage1: String
        get() = billingErrorMessage1Format.fmt(storeName)

//    private val pricingManager: PricingManager by lazy { pricingManagerLazy.get() }

    fun artistPlural(count: Int): String {
        return if (count == 1) {
            artistSingular
        } else {
            artistPlural
        }
    }

    override val error: (String) -> String = { error -> errorString.fmt(error) }

    override fun getRewardCountdown(second: Long): String {
        return if (second > 1) {
            rewardCountdownPlural.fmt(second)
        } else {
            rewardCountdownSingle.fmt(second)
        }
    }

    override val storeName: String
        get() = stringArbitrator.storeName(appStore, playStore, store)
    override val updateAppAction: String
        get() = stringArbitrator.updateAppAction(openInAppStore, openInGooglePlay)
    override val networkErrorAction: String?
        get() = stringArbitrator.networkErrorAction(openNetworkSettings, openSettings)

    override fun date(date: Instant): String {
        return stringArbitrator.date(date)
    }

    override fun memberSince(createdAt: Instant): String {
        return memberSince.fmt(date(createdAt))
    }

    override fun plusPricePerMonth(localizedPrice: String): String =
        plusPricePerMonth.fmt(localizedPrice)

    override fun plusPricePerYear(localizedPrice: String): String =
        plusPricePerYear.fmt(localizedPrice)

    override fun plusPricePerYearPerMonth(localizedPrice: String): String =
        plusPricePerYearPerMonth.fmt(localizedPrice)

    override fun plusPricePerYearHighlight(annualDiscountPercentage: Int): String {
        return "${annualDiscountPercentage}${percentOff}"
    }

    override fun price(currencyCode: String, amount: String): String {
        return "$currencyCode$amount"
    }

    override fun wallpaperCount(wallpaperCount: Int): String {
        return "$wallpaperCount ${if (wallpaperCount == 1) wallpaper else wallpapers}"
    }

    override fun buyWallpaperCollectionPromo(wallpaperCount: Int): String {
//        return "Unlock all $wallpaperCount 4K wallpapers in this collection"
        return buyWallpaperCollectionPromo.fmt(wallpaperCount)
    }

    override fun plusStandardForPricePerPeriod(pricePerPeriod: String): String {
        return plusStandardForPricePerPeriodFormat.fmt(pricePerPeriod)
    }

    override fun plusUnlimitedForPricePerPeriod(pricePerPeriod: String): String {
        return plusUnlimitedForPricePerPeriodFormat.fmt(pricePerPeriod)
    }

    override fun downloadingWallpaperTitle(
        wallpaperName: String?,
        downloadDestination: String
    ): String {
        return downloadingWallpaperTitle.fmt(
            wallpaperName?.quote() ?: "",
            downloadDestination.quote()
        )
    }

    override fun successfulDownloadCount(successfulDownloadCount: Int): String {
        return "%d %s".fmt(
            successfulDownloadCount,
            if (successfulDownloadCount == 1) wallpaper else wallpapers
        )
    }

    override fun downloadingProgressCount(
        currentDownloadIndex: Int,
        totalDownloadCount: Int,
    ): String {
        return downloadingProgressCountTemplate.fmt(currentDownloadIndex, totalDownloadCount)
    }

    override fun downloadingProgressCountMessage(
        currentDownloadIndex: Int,
        totalDownloadCount: Int,
    ): String {
        return downloadingProgressCountMessage.fmt(currentDownloadIndex, totalDownloadCount)
    }


    override fun downloadStatusCombined(first: String, second: String): String {
        return "%s %s".fmt(first, second)
    }

    override fun signedOutOfAccount(accountName: String): String {
        return signedOutOfAccount.fmt(accountName)
    }

    override fun shareWallpaperCopy(
        collectionName: String,
        artistName: String,
        shareUrl: String,
    ): String {
        return shareWallpaperCopyWithArtistName.fmt(collectionName, artistName, shareUrl)
    }

    override fun shareWallpaperCopy(
        collectionName: String,
        shareUrl: String,
    ): String {
        return shareWallpaperCopy.fmt(collectionName, shareUrl)
    }

    override fun switchContentDescription(title: String, isChecked: Boolean): String {
        return switchContentDescription.fmt(title, if (isChecked) on else off)
    }

    override fun favoriteContentDescription(title: String, isFavorite: Boolean): String {
        return favoriteContentDescription.fmt(title, if (isFavorite) on else off)
    }

    override fun unfollowArtistTitle(artistName: String): String {
        return unfollowArtistTitle.fmt(artistName)
    }

    override val versionName: String
        get() = stringArbitrator.versionName
    override val copyrightAppWithYear: String
        get() = "$copyrightSansYear ${stringArbitrator.copyrightYear}"
    override val copyrightFullWithYear: String
        get() = "$copyright ${stringArbitrator.copyrightYear}"
}
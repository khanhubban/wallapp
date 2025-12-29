package wallapp.resources.string

import kotlinx.datetime.Instant
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.string.fmt


abstract class StringRepository : Strings {

    abstract val billingErrorMessage1: String
    abstract val storeName: String

    val followArbitrated: (shortLabel: Boolean) -> String = { if (it) follow else followArtist }

    abstract fun date(date: Instant): String

    abstract fun memberSince(createdAt: Instant): String

    val resolutionFullRes: (width: Int, height: Int) -> String =
        { width: Int, height: Int -> resolutionFullResFormat.fmt(width.toString(), height.toString()) }
    val resolutionHd: (width: Int, height: Int) -> String =
        { width: Int, height: Int -> resolutionHdFormat.fmt(width.toString(), height.toString()) }

    abstract val updateAppAction: String
    abstract val networkErrorAction: String?

    abstract val error: (String) -> String

    fun applySpacePrefix(text: String): String = " $text"
    fun applySpaceSeparator(string1: String, string2: String): String = "$string1 $string2"
    fun applyDotPrefix(text: String): String = "$dotCharacter $text"
    fun applyColonSuffix(text: String): String = "$text:"

    fun chooseAPlan(): String = chooseAPlan
    abstract fun getRewardCountdown(second: Long): String
    abstract fun plusPricePerMonth(localizedPrice: String): String
    abstract fun plusPricePerYear(localizedPrice: String): String
    abstract fun plusPricePerYearPerMonth(localizedPrice: String): String?
    abstract fun plusPricePerYearHighlight(annualDiscountPercentage: Int): String
    abstract fun price(currencyCode: String, amount: String): String
    abstract fun buyWallpaperCollectionPromo(wallpaperCount: Int): String
    abstract fun plusStandardForPricePerPeriod(pricePerPeriod: String): String
    abstract fun plusUnlimitedForPricePerPeriod(pricePerPeriod: String): String
    abstract fun wallpaperCount(wallpaperCount: Int): String

    abstract fun downloadingWallpaperTitle(
        wallpaperName: String?,
        downloadDestination: String
    ): String

    abstract fun downloadingProgressCount(
        currentDownloadIndex: Int,
        totalDownloadCount: Int,
    ): String

    abstract fun downloadingProgressCountMessage(
        currentDownloadIndex: Int,
        totalDownloadCount: Int,
    ): String

    abstract fun downloadStatusCombined(first: String, second: String): String

    abstract fun successfulDownloadCount(successfulDownloadCount: Int): String

    abstract fun signedOutOfAccount(accountName: String): String

    abstract fun shareWallpaperCopy(
        collectionName: String,
        artistName: String,
        shareUrl: String
    ): String

    abstract fun shareWallpaperCopy(collectionName: String, shareUrl: String): String
    abstract fun switchContentDescription(title: String, isChecked: Boolean): String
    abstract fun favoriteContentDescription(title: String, isFavorite: Boolean): String
    abstract fun unfollowArtistTitle(artistName: String): String

    abstract val versionName: String
    abstract val copyrightAppWithYear: String
    abstract val copyrightFullWithYear: String
    val settingsFooter: String
        get() = "$copyrightAppWithYear"

    val notificationContentUpdatesChannelId: String get() = "example.com.notification.channel.content_updates"
    val notificationDownloadWallpaperChannelId: String get() = "example.com.wallpapers.notification.channel.download"

    val deleteAccountConfirmDialogMessage: String
        get() = deleteAccountConfirmDialogMessage1 + "\n\n" + deleteAccountConfirmDialogMessage2

    val downloadedSize: (staticWallpaperSize: StaticWallpaperSize?) -> String
        get() = { staticWallpaperSize: StaticWallpaperSize? ->
            when (staticWallpaperSize) {
                null -> downloaded
                StaticWallpaperSize.FullResolution -> downloadedFullRes
                else -> downloadedHd
            }
        }
    val download:(staticWallpaperSize: StaticWallpaperSize) -> String
        get() = { staticWallpaperSize: StaticWallpaperSize ->
            when (staticWallpaperSize) {
                StaticWallpaperSize.FullResolution -> downloadFullRes
                else -> downloadHd
            }
        }
    val setWallpaper: (staticWallpaperSize: StaticWallpaperSize) -> String
        get() = { staticWallpaperSize: StaticWallpaperSize ->
            when (staticWallpaperSize) {
                StaticWallpaperSize.FullResolution -> setWallpaperFullRes
                else -> setWallpaperHd
            }
        }

    val homeOnboardingSummary: (currentFollowCount: Int, minFollowCount: Int) -> String = {
            currentFollowCount: Int, minFollowCount: Int ->
        if (currentFollowCount >= minFollowCount) {
            selectArtistYouLike
        } else {
            val remaining = minFollowCount - currentFollowCount
            selectRemainingArtistsForYourHomeFeed(remaining)
        }
    }
    val selectRemainingArtistsForYourHomeFeed: (remaining: Int) -> String = { remaining ->
        (if (remaining == 1) {
            selectRemainingArtistsForYourHomeFeedSingle
        } else {
            selectRemainingArtistsForYourHomeFeedPlural
        }).fmt(remaining)
    }

    val openInPhotos: (staticWallpaperSize: StaticWallpaperSize) -> String
        get() = { staticWallpaperSize: StaticWallpaperSize ->
            when (staticWallpaperSize) {
                StaticWallpaperSize.FullResolution -> openInPhotosFullRes
                else -> openInPhotosHd
            }
        }


}
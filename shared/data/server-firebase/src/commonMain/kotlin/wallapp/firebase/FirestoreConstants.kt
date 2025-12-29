package wallapp.firebase

object FirestoreConstants {

    object Collection {
        const val Users = "users"
        const val UnlockedWallpapers = "reward_unlocked_wallpapers"
        const val CreditsTransactions = "credits_transactions"
    }

    object Field {
        const val AccountDeleted = "accountDeleted"
        const val Currency = "currency"
        const val CurrentWallpaperIds = "currentWallpaperIds"
        const val DeviceInfo = "deviceInfo"
        const val Email = "email"
        const val EpochLastSeen = "epochLastSeen"
        const val EpochLastUpdated = "epochLastUpdated"
        const val FavoriteIds = "favoriteIds"
        const val Flags = "flags"
        const val FollowingIds = "followingIds"
        const val LoginType = "loginType"
        const val Newsletter = "newsletter"
        const val PurchaseRecords = "purchaseRecords"
        const val ReceiveNewsletter = "newsletter"
        const val ReceiveNotifications = "receiveNotifications"
        const val WallpaperDownloadEvents = "wallpaperDownloadEvents"
    }

}

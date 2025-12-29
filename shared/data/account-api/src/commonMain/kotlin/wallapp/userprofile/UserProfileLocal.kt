package wallapp.userprofile

data class UserProfileLocal(
    val userId: String,
    val email: String? = null,
    val isAnonymous: Boolean,
    val epochCreated: Long,
    val epochLastUpdated: Long,
    val epochLastSeen: Long,
    val loginType: LoginType,
    val favoriteIds: List<String>,
    val currentWallpaperIds: Map<String, String>,
    val followingIds: List<String>,
    val purchaseRecords: List<String>,
    val deviceInfo: String?,
    val currency: String?,
    val newsletter: Boolean,
    val flags: List<String>,
    val wallpaperDownloadEvents: List<String>,
    val receiveNotifications: Boolean,
    val accountDeleted: Boolean,
) {
    companion object {
        val Preset = UserProfileLocal(
            userId = "1234567890",
            email = "me@example.com",
            isAnonymous = true,
            epochCreated = 1111111111,
            epochLastUpdated = 1111111111,
            epochLastSeen = 1111111111,
            loginType = LoginType.Anonymous,
            favoriteIds = emptyList(),
            currentWallpaperIds = mapOf(),
            followingIds = emptyList(),
            purchaseRecords = emptyList(),
            deviceInfo = null,
            currency = null,
            newsletter = false,
            flags = emptyList(),
            wallpaperDownloadEvents = emptyList(),
            receiveNotifications = false,
            accountDeleted = false,
        )
    }
}

fun UserProfileRemote.toUserProfileLocal(): UserProfileLocal? {
    if (!isValid()) return null
    return UserProfileLocal(
        userId = userId!!,
        email = email,
        isAnonymous = isAnonymous,
        epochCreated = epochCreated!!,
        epochLastUpdated = epochLastUpdated!!,
        epochLastSeen = epochLastSeen!!,
        loginType = loginType?.toLoginType()!!,
        favoriteIds = favoriteIds ?: emptyList(),
        currentWallpaperIds = currentWallpaperIds ?: emptyMap(),
        followingIds = followingIds ?: emptyList(),
        purchaseRecords = purchaseRecords ?: emptyList(),
        deviceInfo = deviceInfo,
        currency = currency,
        newsletter = newsletter ?: false,
        flags = flags ?: emptyList(),
        wallpaperDownloadEvents = wallpaperDownloadEvents ?: emptyList(),
        receiveNotifications = receiveNotifications ?: false,
        accountDeleted = accountDeleted ?: false,
    )
}

fun UserProfileLocal.toUserProfileRemote(): UserProfileRemote {
    return UserProfileRemote(
        userId = userId,
        email = email,
        isAnonymous = isAnonymous,
        epochCreated = epochCreated,
        epochLastUpdated = epochLastUpdated,
        epochLastSeen = epochLastSeen,
        loginType = loginType.label,
        favoriteIds = favoriteIds,
        currentWallpaperIds = currentWallpaperIds,
        followingIds = followingIds,
        purchaseRecords = purchaseRecords,
        deviceInfo = deviceInfo,
        currency = currency,
        newsletter = newsletter,
        flags = flags,
        wallpaperDownloadEvents = wallpaperDownloadEvents,
        receiveNotifications = receiveNotifications,
        accountDeleted = accountDeleted,
    )
}

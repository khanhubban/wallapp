package wallapp.system.navigation

interface SystemNavigator {

    fun toUrl(url: String)

    fun toMailTo(recipients: List<String>, subject: String): Boolean

    fun toYouTubeVideo(youTubeVideoId: String)

    fun toPhotos(): Boolean

    fun toAppInfo(): Boolean

    /**
     * On Android, this will be the app's notification settings screen (which is a sub-screen
     * of the [toAppInfo] screen.
     *
     * On iOS, it's not possible to navigate to such a screen, so do the best available option
     * which is to [toAppInfo].
     */
    fun toSystemNotificationsSettings(): Boolean = toAppInfo()

    /**
     * "Marketplace" meaning the App Store for iOS and Google Play for Android.
     */
    fun toSystemMarketplace(appId: String): Boolean
    fun toSystemMarketplaceForCurrentApp(): Boolean

    fun toSystemSetAppAsLiveWallpaper(): Boolean

    fun toSystemNetworkSettings()
}
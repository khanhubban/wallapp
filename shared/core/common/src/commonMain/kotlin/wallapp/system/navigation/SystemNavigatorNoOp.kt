package wallapp.system.navigation


object SystemNavigatorNoOp : SystemNavigator {

    override fun toUrl(url: String) {}

    override fun toMailTo(recipients: List<String>, subject: String): Boolean = false

    override fun toYouTubeVideo(youTubeVideoId: String) {}

    override fun toPhotos() = false

    override fun toAppInfo(): Boolean = false

    override fun toSystemMarketplace(appId: String) = false
    override fun toSystemMarketplaceForCurrentApp() = false

    override fun toSystemSetAppAsLiveWallpaper(): Boolean = false
    override fun toSystemNetworkSettings() { /*NOOP*/ }
}
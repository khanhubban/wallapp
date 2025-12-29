package wallapp.system.navigation

import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UIKit.UIViewController
import wallapp.log.Logger
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.ui.controller.uiViewController

class SystemNavigatorIos(
    private val uiControllerManager: UiControllerManager,
) : SystemNavigator {

    companion object {
        val Log = Logger("SystemNavigatorIos")
    }

    private val uiViewController: UIViewController?
        get() = uiControllerManager.currentUiController?.uiViewController

    override fun toUrl(url: String) {
        val uiViewController = uiViewController
        if (uiViewController != null) {
            openUrlInSafariViewController(url, uiViewController)
        } else {
            openUrlInBrowser(url)
        }
    }

    override fun toMailTo(recipients: List<String>, subject: String): Boolean {
        return false
    }

    override fun toYouTubeVideo(youTubeVideoId: String) {

    }

    override fun toPhotos(): Boolean {
        // As of iOS 18, the NSURL.URLWithString("photos-redirect://") URL scheme that was
        // previously used no longer works. Furthermore, this is an app review risk, so remove
        // from the app entirely.
        return false
    }

    override fun toAppInfo(): Boolean {
        val url = NSURL(string = UIApplicationOpenSettingsURLString)
        return UIApplication.sharedApplication.openURL(url)
    }

    override fun toSystemMarketplaceForCurrentApp(): Boolean {
        // Retrieve the App Store ID from Info.plist
        val appStoreId = NSBundle.mainBundle.objectForInfoDictionaryKey("AppStoreID") as? String
        return if (appStoreId != null) {
            toSystemMarketplace(appStoreId)
        } else {
            Log.d("App Store ID not found in Info.plist")
            false
        }
    }

    override fun toSystemMarketplace(appId: String): Boolean {
        val appStoreUrl = "https://apps.apple.com/app/id$appId"
        val url = NSURL(string = appStoreUrl)
        return if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        } else {
            Log.d("toSystemMarketplace(): App Store not available")
            false
        }
    }

    override fun toSystemSetAppAsLiveWallpaper(): Boolean {
        return false
    }

    override fun toSystemNetworkSettings() {
        // iOS does not have a direct way to navigate to network settings or even the root settings.
        // Attempts to do so can lead to app review rejections. Seemingly the best available option
        // is to navigate to the app's settings screen. #2084.
        toAppInfo()
    }
}
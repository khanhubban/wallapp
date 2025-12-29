package wallapp.system.navigation

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import wallapp.activity.launchSystemWallpaperPicker
import wallapp.appconfig.AppPlatformConfig
import wallapp.appstore.PlayStoreDescriptor
import wallapp.buildconfig.BuildConfig
import wallapp.context.createSendEmailIntent
import wallapp.context.launchYouTubeVideo
import wallapp.context.loadUrlInStore
import wallapp.inappbrowser.InAppBrowserManager
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.ui.controller.currentActivity


class SystemNavigatorAndroid(
    private val context: Context,
    private val buildConfig: BuildConfig,
    private val uiControllerManager: UiControllerManager,
    private val inAppBrowserManager: InAppBrowserManager,
    private val packageManager: PackageManager,
    private val appPlatformConfig: AppPlatformConfig,
    private val playStoreDescriptor: PlayStoreDescriptor,
) : SystemNavigator {

    private val activity: Activity?
        get() = uiControllerManager.currentActivity

    override fun toUrl(url: String) {
        inAppBrowserManager.show(url)
    }

    override fun toMailTo(recipients: List<String>, subject: String): Boolean {
        val intent = createSendEmailIntent(recipients, subject)
        if (intent.resolveActivity(packageManager) != null) {
            activity?.startActivity(intent)
            return true
        }
        return false
    }

    override fun toYouTubeVideo(youTubeVideoId: String) {
        activity?.launchYouTubeVideo(youTubeVideoId)
    }

    override fun toPhotos() = false

    override fun toAppInfo(): Boolean {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", buildConfig.packageName, null)
        }

        val activity = activity ?: return false
        activity.startActivity(intent)
        return true
    }

    override fun toSystemNotificationsSettings(): Boolean {
//        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
//            data = Uri.fromParts("package", buildConfig.packageName, null)
//        }
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, buildConfig.packageName)
            }

        val activity = activity ?: return false
        activity.startActivity(intent)
        return true
    }

    override fun toSystemMarketplaceForCurrentApp(): Boolean {
        val currentAppId = buildConfig.packageName
        return toSystemMarketplace(currentAppId)
    }

    override fun toSystemMarketplace(appId: String): Boolean {
        return context.loadUrlInStore(this, playStoreDescriptor, appId)
    }

    override fun toSystemSetAppAsLiveWallpaper(): Boolean {
        val wallpaperServiceClassName = appPlatformConfig.wallpaperServiceClassName
            ?: return false

        return activity?.let { activity ->
            activity.launchSystemWallpaperPicker(ComponentName(activity, wallpaperServiceClassName))
            true
        } ?: false
    }

    override fun toSystemNetworkSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
        } else {
            Intent(Settings.ACTION_WIRELESS_SETTINGS)
        }
        activity?.startActivity(intent)
    }
}
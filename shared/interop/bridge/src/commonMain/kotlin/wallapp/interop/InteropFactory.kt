package wallapp.interop

import wallapp.ads.AdUnitIds
import wallapp.ads.reward.RewardAdCoordinator
import wallapp.auth.google.GoogleAuthCoordinatorForIos
import wallapp.billing.revenuecat.RevenueCatManager
import wallapp.billing.revenuecat.RevenueCatUserManager
import wallapp.buildconfig.BuildConfig
import wallapp.download.FirebaseStorageDownloadCoordinatorIos
import wallapp.download.UrlDownloadCoordinatorForIos
import wallapp.image.prefetch.ImagePrefetcher
import wallapp.inappbrowser.InAppBrowserDelegate
import wallapp.navigation.CurrentScreenCoordinatorIos
import wallapp.network.NetworkStateNative
import wallapp.pixel.view.UIKitFactory
import wallapp.privacymessaging.PrivacyMessagingManagerDelegateIos
import wallapp.system.photo.picker.SystemPhotoPicker
import wallapp.system.ui.controller.UiControllerManager
import wallapp.wallpaper.cache.BaseCache

interface InteropFactory {

    fun createAdUnitIds(): AdUnitIds
    fun createBaseCache(): BaseCache
    fun createBuildConfig(): BuildConfig
    fun createCurrentScreenCoordinator(): CurrentScreenCoordinatorIos
    fun createFirebaseStorageDownloadCoordinator(): FirebaseStorageDownloadCoordinatorIos
    fun createGoogleAuthCoordinatorForIos(): GoogleAuthCoordinatorForIos
    fun createImagePrefetcherEx(): ImagePrefetcher
    fun createInAppBrowserDelegate(): InAppBrowserDelegate
    fun createNetworkStateNative(): NetworkStateNative
    fun createPrivacyMessagingManagerDelegate(): PrivacyMessagingManagerDelegateIos
    fun createRevenueCatManager(): RevenueCatManager
    fun createRevenueCatUserManager(): RevenueCatUserManager
    fun createRewardAdCoordinator(): RewardAdCoordinator
    fun createSystemPhotoPicker(): SystemPhotoPicker
    fun createUIKitFactory(): UIKitFactory
    fun createUiControllerManager(): UiControllerManager
    fun createUrlDownloadCoordinator(): UrlDownloadCoordinatorForIos
}

private var interopFactory: InteropFactory? = null
fun registerInteropFactory(factory: InteropFactory) {
    interopFactory = factory
}
fun InteropFactory(): InteropFactory = interopFactory!!


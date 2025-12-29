package wallapp.system.platform

/**
 * There's no access to types here, so make do with flags, even if that means slightly uglier code.
 */
expect object PlatformFeature {

    fun init(application: Any?)

    // Use very sparingly - ideally use a more specific feature flag
    val IsAndroid: Boolean
    val IsDesktop: Boolean
    val IsIos: Boolean

    val SystemDarkTheme: Boolean

    val SystemBottomSheetsSupported: Boolean

    val CanSetStaticWallpaper: Boolean

    val LiveWallpaperSupported: Boolean

    val AppShortcutsSupported: Boolean

    val SystemZoomWallpaperSupported: Boolean

    val SystemAlertWindowRequiresPermission: Boolean

    val CurrentLiveWallpaperRequiresWorkaround: Boolean

    // True if the system can open the system photos app - false for iOS unfortunately.
    val CanOpenToSystemPhotosApp: Boolean

    val CanOpenToSystemAppMarketplace: Boolean

    val CanOpenAppsFromService: Boolean

    val CanLockDevice: Boolean

    val CanUseForegroundService: Boolean

    val DeviceProtectedStorageContext: Boolean

    val DeviceHasSensorHardware: Boolean

    val NativeBottomSheetUiSupported: Boolean

    val AnimatedImagesSupported: Boolean

    val ComposeAnimatedImagesSupported: Boolean

    val VideoPlaybackSupported: Boolean
    val VideoPlaybackBundledSupported: Boolean

    val ImageHashSupported: Boolean

    val ComposeRendersSystemBars: Boolean

    val PrecomposeNavigation: Boolean

    val FamilyPlanBillingSupported: Boolean

    val SignInWithAppleSupported: Boolean

    val SupportModalSheetBehaviour: Boolean

    val ShowFirstRunLogoSplash: Boolean

    val CanShowPerformanceStats: Boolean

    val CheckForRootScreenOnPop: Boolean

    val NativeManageSubscriptionSupported: Boolean
}
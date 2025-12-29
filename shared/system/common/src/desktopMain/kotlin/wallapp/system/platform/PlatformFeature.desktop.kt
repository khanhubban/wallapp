package wallapp.system.platform

actual object PlatformFeature {

    actual fun init(application: Any?) {
    }

    actual val IsAndroid: Boolean
        get() = false
    actual val IsDesktop: Boolean
        get() = true
    actual val IsIos: Boolean
        get() = false

    actual val SystemDarkTheme: Boolean
        get() = false
    actual val SystemBottomSheetsSupported: Boolean
        get() = false
    actual val CanSetStaticWallpaper: Boolean
        get() = false
    actual val LiveWallpaperSupported: Boolean
        get() = false
    actual val AppShortcutsSupported: Boolean
        get() = false
    actual val SystemZoomWallpaperSupported: Boolean
        get() = false
    actual val SystemAlertWindowRequiresPermission: Boolean
        get() = false
    actual val CurrentLiveWallpaperRequiresWorkaround: Boolean
        get() = false
    actual val CanOpenToSystemPhotosApp: Boolean
        get() = false
    actual val CanOpenToSystemAppMarketplace: Boolean
        get() = false
    actual val CanOpenAppsFromService: Boolean
        get() = false
    actual val CanLockDevice: Boolean
        get() = false
    actual val CanUseForegroundService: Boolean
        get() = false
    actual val DeviceProtectedStorageContext: Boolean
        get() = false
    actual val DeviceHasSensorHardware: Boolean
        get() = false
    actual val NativeBottomSheetUiSupported: Boolean
        get() = false
    actual val AnimatedImagesSupported: Boolean
        get() = false
    actual val ComposeAnimatedImagesSupported: Boolean
        get() = false
    actual val ImageHashSupported: Boolean
        get() = true
    actual val VideoPlaybackSupported: Boolean
        get() = false
    actual val VideoPlaybackBundledSupported: Boolean
        get() = false
    actual val ComposeRendersSystemBars: Boolean
        get() = true
    actual val PrecomposeNavigation: Boolean
        get() = true
    actual val FamilyPlanBillingSupported: Boolean
        get() = false
    actual val SignInWithAppleSupported: Boolean
        get() = false
    actual val SupportModalSheetBehaviour: Boolean
        get() = false

    actual val ShowFirstRunLogoSplash: Boolean
        get() = true
    actual val CanShowPerformanceStats: Boolean
        get() = false

    actual val CheckForRootScreenOnPop: Boolean
        get() = false

    actual val NativeManageSubscriptionSupported: Boolean
        get() = false
}
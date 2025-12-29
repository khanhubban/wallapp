package wallapp.system.platform

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.content.ContextCompat


actual object PlatformFeature {

    /**
     * Note: may be null during unit tests.
     */
    private var application: Application? = null

    private val sensorManager: SensorManager? by lazy {
        application?.let { ContextCompat.getSystemService(it, SensorManager::class.java) }
    }

    actual fun init(application: Any?) {
        this.application = application as? Application
    }

    actual val IsAndroid: Boolean
        get() = true
    actual val IsDesktop: Boolean
        get() = false
    actual val IsIos: Boolean
        get() = false

    @get:ChecksSdkIntAtLeast(api = 29)
    actual val SystemDarkTheme: Boolean
        get() = Build.VERSION.SDK_INT >= 29

    actual val SystemBottomSheetsSupported: Boolean
        get() = false

    @get:ChecksSdkIntAtLeast(api = 29)
    actual val SystemAlertWindowRequiresPermission: Boolean
        get() = Build.VERSION.SDK_INT >= 29

    @get:ChecksSdkIntAtLeast(api = 33)
    actual val CurrentLiveWallpaperRequiresWorkaround: Boolean
        get() = Build.VERSION.SDK_INT >= 33

    @get:ChecksSdkIntAtLeast(api = 25)
    actual val AppShortcutsSupported: Boolean
        get() = Build.VERSION.SDK_INT >= 25

    actual val CanSetStaticWallpaper: Boolean
        get() = true

    actual val LiveWallpaperSupported: Boolean
        get() = false

    @get:ChecksSdkIntAtLeast(api = 30)
    actual val SystemZoomWallpaperSupported: Boolean
        get() = Build.VERSION.SDK_INT >= 30

    actual val CanOpenToSystemPhotosApp: Boolean
        get() = true

    actual val CanOpenToSystemAppMarketplace: Boolean
        get() = true

    @get:ChecksSdkIntAtLeast(api = 31)
    actual val CanOpenAppsFromService: Boolean
        get() = Build.VERSION.SDK_INT >= 31

    @get:ChecksSdkIntAtLeast(api = 28)
    actual val CanLockDevice: Boolean
        get() = Build.VERSION.SDK_INT >= 28

    @get:ChecksSdkIntAtLeast(api = 23)
    actual val CanUseForegroundService: Boolean
        get() = Build.VERSION.SDK_INT >= 23

    @get:ChecksSdkIntAtLeast(api = 24)
    actual val DeviceProtectedStorageContext: Boolean
        get() = Build.VERSION.SDK_INT >= 24

    private val hasDeviceRotationSensor: Boolean by lazy {
        sensorManager?.getSensorList(Sensor.TYPE_ROTATION_VECTOR)?.isNotEmpty() ?: false
    }
    private val hasLinearAccelerationSensor: Boolean by lazy {
        sensorManager?.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION)?.isNotEmpty() ?: false
    }
    private val hasGravitySensor: Boolean by lazy {
        sensorManager?.getSensorList(Sensor.TYPE_GRAVITY)?.isNotEmpty() ?: false
    }
    actual val DeviceHasSensorHardware: Boolean by lazy {
        hasDeviceRotationSensor && hasGravitySensor && hasLinearAccelerationSensor
    }

    actual val NativeBottomSheetUiSupported: Boolean
        get() = false

    actual val AnimatedImagesSupported: Boolean
        get() = true

    actual val ComposeAnimatedImagesSupported: Boolean
        get() = true

    actual val ImageHashSupported: Boolean
        get() = true

    actual val VideoPlaybackSupported: Boolean
        get() = true

    actual val VideoPlaybackBundledSupported: Boolean
        get() = true

    actual val ComposeRendersSystemBars: Boolean
        get() = true

    actual val PrecomposeNavigation: Boolean
        get() = false

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
        get() = true

    actual val NativeManageSubscriptionSupported: Boolean
        get() = false
}
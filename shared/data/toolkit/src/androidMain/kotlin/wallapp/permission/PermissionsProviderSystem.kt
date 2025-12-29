package wallapp.permission

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import wallapp.log.Log
import wallapp.system.platform.PlatformFeature

class PermissionsProviderSystem(
    private val context: Context,
    private val packageManager: PackageManager,
    private val accessibilityServiceName: String,
) : PermissionsProvider {

    @SuppressLint("NewApi")
    override fun requiresSystemAlertWindowPermission(): Boolean {
        // As of Android 12, wallpaper apps can open apps, so no need for this permission.
        return if (PlatformFeature.CanOpenAppsFromService) {
            return false
        } else {
            PlatformFeature.SystemAlertWindowRequiresPermission && !Settings.canDrawOverlays(context)
        }
    }

    override fun requiresAccessibilityEnabled(): Boolean {
        if (accessibilityServiceName.isEmpty()) return true
        val accessibilityEnabled = try {
            Settings.Secure.getInt(context.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: Settings.SettingNotFoundException) {
            Log.e("Error finding setting, default accessibility to not found: %s", e.message)
        }

        val service = "${context.packageName}/$accessibilityServiceName"
        if (accessibilityEnabled == 1) {
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )?.apply {
                return !split(':').any { it.equals(service, ignoreCase = true) }
            }
        } else {
            Log.d("Accessibility disabled")
        }
        return true
    }

    override val hasStorageReadPermission: Boolean
        get() = hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)

    override val hasStorageWritePermission: Boolean
        get() = hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override val hasStoragePermissions: Boolean
        get() = hasStorageReadPermission && hasStorageWritePermission

    override fun hasWallpaperStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hasStorageReadPermission
        } else {
            true
        }
    }

    override fun hasQueryAllPackagesPermission(): Boolean {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            true
        } else {
            hasPermission(Manifest.permission.QUERY_ALL_PACKAGES)
        }
    }

    override val hasRequestInstallPackagePermission: Boolean
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                packageManager.canRequestPackageInstalls()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                hasPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES)
            }
            else -> {
                true
            }
        }

    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}
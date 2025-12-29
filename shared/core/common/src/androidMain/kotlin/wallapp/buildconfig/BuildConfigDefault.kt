package wallapp.buildconfig

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import wallapp.appversion.AppVersion
import wallapp.appversion.AppVersion.AppVersionAndroid

/**
 *
 */
class BuildConfigDefault(private val context: Context, override val debug: Boolean)
    : BuildConfig {

    private val packageInfo: PackageInfo? by lazy {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0)!!
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    val appInfo: ApplicationInfo? by lazy {
        try {
            context.packageManager.getApplicationInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    @Suppress("DEPRECATION")
    val appVersionCode: Long
        get() = packageInfo?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                it.longVersionCode
            } else {
                it.versionCode.toLong()
            }
        } ?: 0L

    val appVersionName: String by lazy {
        packageInfo?.versionName ?: "<unknown>"
    }

    override val appVersion: AppVersion by lazy {
        AppVersionAndroid(appVersionName, appVersionCode)
    }

    override val packageName: String by lazy {
        packageInfo?.packageName ?: ""
    }

    override val appName: String by lazy {
        appInfo?.loadLabel(context.packageManager)?.toString() ?: ""
    }

}
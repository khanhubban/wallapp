package wallapp.context

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.LocaleList
import android.os.UserManager
import android.telecom.TelecomManager
import android.util.TypedValue
import android.util.TypedValue.complexToDimensionPixelSize
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import wallapp.appstore.AppStoreDescriptor
import wallapp.log.Log
import wallapp.system.navigation.SystemNavigator
import java.util.Locale


@JvmOverloads
fun Context.toast(@StringRes stringId: Int, longLength: Boolean = true): Toast =
        toast(getString(stringId), longLength)

@JvmOverloads
fun Context.toast(string: String, longLength: Boolean = true): Toast {
    val toast = Toast.makeText(this, string, if (longLength) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
    toast.show()
    return toast
}

@JvmOverloads
fun Context.resolveAttributeColor(@AttrRes attributeId: Int,
                                  typedValue: TypedValue = TypedValue(),
                                  fallbackColor: Int = Color.TRANSPARENT): Int {
    return if (theme.resolveAttribute(attributeId, typedValue, true)) {
        typedValue.data
    } else {
        fallbackColor
    }
}

fun Context.resolveAttributeColorStateList(@AttrRes attributeId: Int): ColorStateList {
    val values = theme.obtainStyledAttributes(intArrayOf(attributeId))
    return requireNotNull(values.getColorStateList(0)).also {
        values.recycle()
    }
}

@JvmOverloads
fun Context.resolveAttributeDimension(@AttrRes attributeId: Int,
                                      typedValue: TypedValue = TypedValue()) : Int {
    return if (theme.resolveAttribute(attributeId, typedValue, true)) {
        complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
    } else {
        0
    }
}

@JvmOverloads
fun Context.resolveAttributeFloatValue(@AttrRes attributeId: Int, fallbackValue: Float = 0.0f): Float {
    val typedValue = TypedValue()
    return if (theme.resolveAttribute(attributeId, typedValue, true)) {
        typedValue.float
    } else {
        fallbackValue
    }
}

@JvmOverloads
fun Context.resolveAttributeIntValue(@AttrRes attributeId: Int, fallbackValue: Int = 0): Int {
    val typedValue = TypedValue()
    return if (theme.resolveAttribute(attributeId, typedValue, true)) {
        typedValue.data
    } else {
        fallbackValue
    }
}

fun Context.resolveAttributeDrawable(@AttrRes attributeId: Int): Drawable? {
    obtainStyledAttributes(intArrayOf(attributeId)).run {
        val drawable = getDrawable(0)
        recycle()
        return drawable
    }
}

/**
 * This method converts [dp] unit to equivalent device specific value in pixels.
 */
fun Context.dpToPx(dp: Float): Float {
    return dp * (resources.displayMetrics.densityDpi / 160f)
}


fun Context.loadApplicationIdInStore(
    systemNavigator: SystemNavigator,
    appStoreDescriptor: AppStoreDescriptor,
    applicationId: String,
    referrer: String? = null
): Boolean {
    return loadUrlInStore(systemNavigator, appStoreDescriptor, applicationId, referrer)
}

fun Context.loadUrlInStore(
    systemNavigator: SystemNavigator,
    appStoreDescriptor: AppStoreDescriptor,
    applicationId: String,
    referrer: String? = null,
): Boolean {
    val storeUrl = appStoreDescriptor.getStoreUrl(applicationId, referrer)
    val storeResolveInfo = getStoreViewResolveInfo(storeUrl, appStoreDescriptor.storeApplicationId)
    if (storeResolveInfo != null) {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(storeUrl)
            addFlags(FLAG_ACTIVITY_NEW_TASK)
            setClassName(storeResolveInfo.activityInfo.packageName, storeResolveInfo.activityInfo.name)
        })
    } else {
        systemNavigator.toUrl(storeUrl)
    }
    return true
}

fun Context.getStoreViewResolveInfo(storeUrl: String, storeApplicationId: String): ResolveInfo? {
    val queryIntent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = Uri.parse(storeUrl)
    }
    // Catch [Exception] to prevent issues with Instant Apps.
    return try {
        packageManager.queryIntentActivities(queryIntent,
                PackageManager.GET_RESOLVED_FILTER)
                .firstOrNull { it.activityInfo != null
                        && it.activityInfo.packageName.contains(storeApplicationId) }
    } catch (ignored: Exception) {
        null
    }
}

fun Context.viewUrl(url: String, addNewTaskFlag: Boolean = false) {
    startActivity(
        Intent(
            Intent.ACTION_VIEW, Uri.parse(url))
            .apply {
                if (addNewTaskFlag) {
                    addFlags(FLAG_ACTIVITY_NEW_TASK)
                }
            },
    )
}

fun Context.packageUri() : Uri = Uri.parse("package:$packageName")

fun Context.setLocale(locale: String) {
    setLocale(createLocale(locale))
}

@Suppress("DEPRECATION")
fun Context.setLocale(locale: Locale) {
    Locale.setDefault(locale)
    val config = Configuration()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        config.setLocales(LocaleList(locale))
    } else {
        config.locale = locale
    }
    applicationContext.resources.updateConfiguration(config, null)
}

fun createLocale(code: String): Locale {
    var lang = code
    var country = ""
    val index = code.indexOf("-")
    if (index > -1) {
        lang = code.substring(0, index)
        country = code.substring(index + 1, code.length)
        if (country.length == 3 && country[0] == 'r') {
            country = country.substring(1, 3)
        }
    }
    return Locale(lang, country)
}

fun Context.getTelecomManager(): TelecomManager {
    return getSystemService(Context.TELECOM_SERVICE) as TelecomManager
}

fun Context.getLauncherAppsService(): LauncherApps {
    return getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
}

fun Context.getUserManager(): UserManager {
    return getSystemService(Context.USER_SERVICE) as UserManager
}

//fun Context.openSystemAlertWindowPermissionSettings(
//    stringRepository: StringRepository,
//    usePackageUri: Boolean = true,
//) : Boolean {
//    var toast : Toast? = null
//    return try {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            toast = toast(R.string.grant_system_alert_window_permission_toast)
//            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
//                if (this !is Activity) {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                }
//                if (usePackageUri) {
//                    data = packageUri()
//                }
//            })
//            true
//        } else {
//            false
//        }
//    } catch (securityException: SecurityException) {
//        toast?.cancel()
//        openSystemAlertWindowSettingsOnException()
//    } catch (activityNotFoundException: ActivityNotFoundException) {
//        toast?.cancel()
//        if (usePackageUri) {
//            //Try once again without package Uri
//            openSystemAlertWindowPermissionSettings(stringRepository, false)
//        } else {
//            openSystemAlertWindowSettingsOnException()
//        }
//    }
//}

//private fun Context.openSystemAlertWindowSettingsOnException() : Boolean {
//    startActivity(Intent(Settings.ACTION_SETTINGS)
//        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//    toast(R.string.system_alert_window_security_exception_toast)
//    return false
//}

fun Context.startForegroundServiceSafely(intent: Intent): Boolean {
    return try {
        ContextCompat.startForegroundService(this, intent)
        true
    } catch (ex: SecurityException) {
        Log.e(ex, ex.localizedMessage)
        false
    } catch (ex: IllegalStateException) {
        Log.e(ex, ex.localizedMessage)
        false
    }
}

fun Context.findServiceComponentNames(intent: Intent): List<ComponentName>? {
    val serviceComponentNames = packageManager
        ?.queryIntentServices(intent, 0)
        ?.filter { it.serviceInfo != null }
        ?.map { ComponentName(it.serviceInfo.packageName, it.serviceInfo.name) }
    return if (serviceComponentNames?.isNotEmpty() == true) serviceComponentNames else null
}

fun Context.launchYouTubeVideo(youTubeVideoId: String) {
    try {
        this.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$youTubeVideoId")))
    } catch (ex: ActivityNotFoundException) {
        this.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$youTubeVideoId"))
        )
    }
}

fun createSendEmailIntent(recipients: List<String>, subject: String): Intent =
    Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, recipients.toTypedArray())
        putExtra(Intent.EXTRA_SUBJECT, subject)
    }
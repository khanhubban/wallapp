package wallapp.packagemanager

import android.content.Intent
import android.content.pm.PackageManager

/**
 * Returns the applicationId of the default launcher. Will return false if no
 * default launcher has been set (this happens when the user installs a new
 * launcher, and the user has yet to specify a new default).
 */
fun PackageManager.resolveDefaultLauncherApplicationId(): String? {
    resolveActivity(Intent("android.intent.action.MAIN")
                    .addCategory("android.intent.category.HOME"),
            PackageManager.MATCH_DEFAULT_ONLY)?.let {
        it.activityInfo?.packageName?.let { appId ->
            if (appId != "android") {
                return appId
            }
        }
    }
    return null
}
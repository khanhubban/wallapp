package wallapp.appicon

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import wallapp.buildconfig.BuildConfig
import wallapp.log.Logger
import wallapp.utils.stackTraceAsString


private val Log = Logger("AppIconManager")

class AppIconManagerAndroid(
    private val context: Context,
    private val buildConfig: BuildConfig,
) : AppIconManager {

    private val activityPrefix = "wallapp.activity"

    private fun getActivityName(appIcon: AppIcon): String {
        return when (appIcon) {
            AppIcon.Celebration -> "$activityPrefix.MainCelebration"
            AppIcon.Citrus -> "$activityPrefix.MainActivity" // default
            AppIcon.Ivory -> "$activityPrefix.MainIvory"
            AppIcon.Midnight -> "$activityPrefix.MainMidnight"
            AppIcon.PlusStandardDark -> "$activityPrefix.MainPlusStandardDark"
            AppIcon.PlusStandardLight -> "$activityPrefix.MainPlusStandardLight"
            AppIcon.PlusUnlimitedDark -> "$activityPrefix.MainPlusUnlimitedDark"
            AppIcon.PlusUnlimitedLight -> "$activityPrefix.MainPlusUnlimitedLight"
            AppIcon.Slate -> "$activityPrefix.MainSlate"
        }
    }

    override fun setAppIcon(appIcon: AppIcon) {
        context.enableComponent(appIcon)
    }

    private fun Context.enableComponent(enableAppIcon: AppIcon) {
        val componentPackage = buildConfig.packageName

        AppIcon.All.forEach { appIcon ->
            try {
                getActivityName(appIcon).let { componentNameString ->
                    val componentName = ComponentName(componentPackage, componentNameString)
                    val newState = if (enableAppIcon == appIcon) {
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    } else {
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    }
                    packageManager.setComponentEnabledSetting(
                        componentName,
                        newState,
                        PackageManager.DONT_KILL_APP,
                    )
                    Log.d("setComponentEnabledSetting(appIcon: $appIcon): $componentName, $newState")
                }

            } catch (exception: Exception) {
                Log.e(exception.stackTraceAsString())
            }
        }
    }
}





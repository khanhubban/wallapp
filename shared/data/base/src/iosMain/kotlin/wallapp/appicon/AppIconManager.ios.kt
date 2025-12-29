package wallapp.appicon

import platform.Foundation.NSError
import platform.UIKit.UIApplication
import platform.UIKit.setAlternateIconName
import platform.UIKit.supportsAlternateIcons
import wallapp.log.Logger

private val Log = Logger("AppIconManager")

class AppIconManagerIos : AppIconManager {

    fun getIconName(appIcon: AppIcon): String? {
        return when (appIcon) {
            AppIcon.Default, AppIcon.Citrus -> null//"AppIcon"
            AppIcon.Celebration -> "app_icon_celebration"
            AppIcon.Ivory -> "app_icon_ivory"
            AppIcon.Midnight -> "app_icon_midnight"
            AppIcon.PlusStandardDark -> "app_icon_plus_standard_dark"
            AppIcon.PlusStandardLight -> "app_icon_plus_standard_light"
            AppIcon.PlusUnlimitedDark -> "app_icon_plus_unlimited_dark"
            AppIcon.PlusUnlimitedLight -> "app_icon_plus_unlimited_light"
            AppIcon.Slate -> "app_icon_slate"
            else -> null
        }
    }

    private fun setAppIcon(label: String?) {
        val sharedApplication = UIApplication.sharedApplication
        if (sharedApplication.supportsAlternateIcons()) {
            Log.d("setAlternateIconName(): $label")
            sharedApplication.setAlternateIconName(label) { error: NSError? ->
                if (error != null) {
                    Log.d("An error occurred: $error")
                } else {
                    Log.d("The icon has been changed successfully: $label!")
                }
            }
        }
    }

    override fun setAppIcon(appIcon: AppIcon) {
        setAppIcon(getIconName(appIcon))
    }
}
